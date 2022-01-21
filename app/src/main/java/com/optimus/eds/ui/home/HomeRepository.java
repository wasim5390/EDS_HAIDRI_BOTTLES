package com.optimus.eds.ui.home;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.optimus.eds.BuildConfig;
import com.optimus.eds.Constant;
import com.optimus.eds.EdsApplication;
import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.CustomerDao;
import com.optimus.eds.db.dao.OrderDao;
import com.optimus.eds.db.dao.PriceConditionEntitiesDao;
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.db.dao.TaskDao;
import com.optimus.eds.db.entities.LookUp;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.OrderStatus;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Promotion;
import com.optimus.eds.db.entities.Task;
import com.optimus.eds.db.entities.pricing.PriceAccessSequence;
import com.optimus.eds.db.entities.pricing.PriceBundle;
import com.optimus.eds.db.entities.pricing.PriceCondition;
import com.optimus.eds.db.entities.pricing.PriceConditionClass;
import com.optimus.eds.db.entities.pricing.PriceConditionDetail;
import com.optimus.eds.db.entities.pricing.PriceConditionEntities;
import com.optimus.eds.db.entities.pricing.PriceConditionScale;
import com.optimus.eds.db.entities.pricing.PriceConditionType;
import com.optimus.eds.model.AppUpdateModel;
import com.optimus.eds.model.LogModel;
import com.optimus.eds.model.MasterModel;
import com.optimus.eds.model.PackageProductResponseModel;
import com.optimus.eds.model.PricingModel;
import com.optimus.eds.model.RouteOutletResponseModel;
import com.optimus.eds.model.WorkStatus;
import com.optimus.eds.source.API;
import com.optimus.eds.source.TokenResponse;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Response;

public class HomeRepository {

    private final String TAG = HomeRepository.class.getSimpleName();
    private static HomeRepository repository;
    private final PreferenceUtil preferenceUtil;

    private CustomerDao customerDao;
    private ProductsDao productsDao;
    private RouteDao routeDao;
    private TaskDao taskDao;
    private OrderDao orderDao;
    private PriceConditionEntitiesDao pricingDao;

    private MutableLiveData<Boolean> targetVsAchievement;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> msg;
    private MutableLiveData<Boolean> onDayStartLiveData;
    private API webService;
    private Executor executor;

    public static HomeRepository singleInstance(Application application, API api, Executor executor) {
        if (repository == null)
            repository = new HomeRepository(application, api, executor);
        return repository;
    }

    private HomeRepository(Application application, API api, Executor executor) {
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        preferenceUtil = PreferenceUtil.getInstance(application);
        productsDao = appDatabase.productsDao();
        routeDao = appDatabase.routeDao();
        taskDao = appDatabase.taskDao();
        customerDao = appDatabase.customerDao();
        orderDao = appDatabase.orderDao();
        isLoading = new MutableLiveData<>();
        pricingDao = appDatabase.priceConditionEntitiesDao();
        onDayStartLiveData = new MutableLiveData<>();
        msg = new MutableLiveData<>();
        webService = api;
        this.executor = executor;
        WorkStatus syncDate = preferenceUtil.getWorkSyncData();
//        onDayStartLiveData.postValue(syncDate.getDayStarted() != 0);

        // Added by Husnain
        targetVsAchievement = new MutableLiveData<>();

    }

    /**
     * Get User Token from server
     */
    public void getToken() {
        isLoading.setValue(true);
        String username = preferenceUtil.getUsername();
        String password = preferenceUtil.getPassword();
        webService.getToken("password", username, password)
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<TokenResponse>() {
                    @Override
                    public void onSuccess(TokenResponse tokenResponse) {
                        preferenceUtil.saveToken(tokenResponse.getAccessToken());
                        updateWorkStatus(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading.postValue(false);
                        if (e instanceof HttpException || e instanceof SocketTimeoutException)
                            msg.postValue(Constant.NETWORK_ERROR);
                        else
                            msg.postValue(e.getMessage());
                    }
                });

    }


    public Integer priceConditionClassValidation(){
        return pricingDao.priceConditionClassValidation().subscribeOn(Schedulers.io()).blockingGet();
    }

    public Integer priceConditionValidation(){
        return pricingDao.priceConditionValidation().subscribeOn(Schedulers.io()).blockingGet();
    }

    public Integer priceConditionTypeValidation(){
        return pricingDao.priceConditionTypeValidation().subscribeOn(Schedulers.io()).blockingGet();
    }

    /**
     * Fetch current day Routes/Outlets
     *
     * @param onDayStart {True for onDayStart, False for Download click}
     */
    public void fetchTodayData(boolean onDayStart) {

        List<Long> outletIds = new ArrayList<>();

        executor.execute(() -> {
            try {
                Response<RouteOutletResponseModel> response = webService.loadTodayRouteOutlets().execute();
                if (response.isSuccessful()) {
                    if (!response.body().isSuccess()) {

                        if (response.body().getResponseMsg() != null)
                            msg.postValue(response.body().getResponseMsg());
                        else
                            msg.postValue(Constant.GENERIC_ERROR);
                        return;
                    }
                    //
                    FirebaseCrashlytics.getInstance().log("dist_id" + response.body().getDistributionId() + "");
                    FirebaseCrashlytics.getInstance().log(response.body().getEmployeeName());

                    if (response.body() != null && response.body().getDistributionId() != null)
                        preferenceUtil.saveDistributionId(response.body().getDistributionId());
                    preferenceUtil.saveConfig(response.body().getConfiguration());
                    deleteAllRoutesAssets()
                            .andThen(deleteAllOutlets(onDayStart))
                            .andThen(Completable.fromAction(() -> {
                                if (onDayStart) {
                                    routeDao.deleteAllMerchandise();
                                    customerDao.deleteAllCustomerInput();
                                    taskDao.deleteAllTask();
                                    routeDao.deleteAllPromotion();
                                    routeDao.deleteAllLookUp();

                                    // remove Pricing
//                                    pricingDao.deleteAllPriceConditionClasses();
//                                    pricingDao.deleteAllPricingAreas();
//                                    deleteAllPricing();
                                }
                            }))

                            .andThen(Completable.fromAction(() -> {
                                routeDao.insertRoutes(response.body().getRouteList());

//                                Added by Husnain
                                PreferenceUtil.getInstance(EdsApplication.getContext()).setHideCustomerInfo(response.body().getSystemConfiguration().getHideCustomerInfoInOrderingApp());
                                PreferenceUtil.getInstance(EdsApplication.getContext()).setPunchOrderInUnits(response.body().getSystemConfiguration().getCanNotPunchOrderInUnits());
                                PreferenceUtil.getInstance(EdsApplication.getContext()).setTargetAchievement(new Gson().toJson(response.body().getTargetVsAchievement()));
                                if (response.body().getDeliveryDate() != null)
                                    PreferenceUtil.getInstance(EdsApplication.getContext()).setDeliveryDate(response.body().getDeliveryDate());
                            }))
                            .andThen(Completable.fromAction(() -> {
                                routeDao.insertOutlets(response.body().getOutletList());

                                for (Outlet outlet : response.body().getOutletList()) {
                                    outletIds.add(outlet.getOutletId());
                                }
                            }))
                            .andThen(Completable.fromAction(() -> {
                                routeDao.insertAssets(response.body().getAssetList());
                            }))
                            .andThen(insertTasks(response.body().getTasksList()))
                            .andThen(insertPromotion(response.body().getPromosAndFOC()))
                            .andThen(insertLookUp(response.body().getLookUp()))
                            .andThen(Completable.fromAction(() -> {
                                long mobileOrderId = 1;
                                for (Order order : response.body().getOrders()) {
                                    if (!outletIds.contains(order.outletId))
                                        continue;
                                    order.setLocalOrderId(mobileOrderId);
                                    orderDao.insertOrder(order);
                                    mobileOrderId++;
                                }
                            })) // added By Husanin
                            .andThen(Completable.fromAction(() -> { // added By Husanin
                                for (Order order : response.body().getOrders()) {
                                    if (!outletIds.contains(order.outletId))
                                        continue;

                                    OrderStatus orderStatus = new OrderStatus();
                                    orderStatus.setOrderId(order.getOrderId());
                                    orderStatus.setOutletId(order.getOutletId());

                                    MasterModel masterModel = new MasterModel();
                                    masterModel.setOutletId(order.getOutletId());
                                    masterModel.setOutletStatus(8);

                                    orderStatus.setSynced(false);
                                    orderStatus.setData(new Gson().toJson(masterModel));
                                    orderStatus.setStatus(8);
                                    orderStatus.setOrderAmount(order.getPayable());
                                    orderStatus.setOutletVisitEndTime(0L);
                                    orderStatus.setOutletVisitStartTime(0L);


                                    orderDao.insertOrderStatus(orderStatus);
                                }
                            }))
                            .andThen(Completable.fromAction(() -> { // added By Husanin
                                long mobileOrderId = 1;
                                for (Order order : response.body().getOrders()) {
                                    if (!outletIds.contains(order.outletId))
                                        continue;
                                    for (OrderDetail orderDetail : order.getOrderDetails()) {

                                        orderDetail.setLocalOrderId(mobileOrderId);
                                        orderDao.insertOrderItem(orderDetail);
                                    }
                                    mobileOrderId++;
                                }
                            }))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.single()).subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onComplete() {
                            // if(onDayStart)
                            //     loadPricing();
                            targetVsAchievement.postValue(PreferenceUtil.getInstance(EdsApplication.getContext()).getTargetAchievement() != null);
                        }

                        @Override
                        public void onError(Throwable e) {
                            isLoading.postValue(false);
                            Log.e(TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    });

                } else {
                    isLoading.postValue(false);
                    msg.postValue(Constant.GENERIC_ERROR);
                }

            } catch (IOException e) {
                isLoading.postValue(false);
                e.printStackTrace();
                Log.e(TAG, e.getMessage() + "");
                msg.postValue(Constant.GENERIC_ERROR);
            }


        });


        executor.execute(() -> {

            Observable<PackageProductResponseModel> stockObservable = webService.loadTodayPackageProduct();
            stockObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(response -> {
                if (response.isSuccess() || !response.getPackageList().isEmpty()) {

                    loadPricing();

                    AsyncTask.execute(() -> {
                        productsDao.deleteAllPackages();
                        productsDao.deleteAllProductGroups();
                        productsDao.deleteAllProducts();
                        productsDao.insertProductGroups(response.getProductGroups());
                        productsDao.insertPackages(response.getPackageList());
                        productsDao.insertProducts(response.getProductList());
                    });

                } else {
                    isLoading.postValue(false);
                    msg.postValue(response.getResponseMsg() != null ? response.getResponseMsg() : "Unable to refresh stock");
                }
            }, throwable -> {
                throwable.printStackTrace();
                msg.postValue(Constant.GENERIC_ERROR);
                isLoading.postValue(false);
            });


        });

    }


    public Completable deleteAllRoutesAssets() {
        return Completable.fromAction(() -> {
            routeDao.deleteAllRoutes();
            routeDao.deleteAllAssets();

        });
    }

    private Completable insertTasks(List<Task> tasks) {
        return Completable.fromAction(() -> {
            //AsyncTask.execute(() -> taskDao.insertTasks(generateTasks()));
            AsyncTask.execute(() -> taskDao.insertTasks(tasks));

        });
    }


    private Completable insertPromotion(List<Promotion> promotions) {
        return Completable.fromAction(() -> {
            //AsyncTask.execute(() -> taskDao.insertTasks(generateTasks()));
            AsyncTask.execute(() -> routeDao.insertPromotion(promotions));

        });
    }

    private Completable insertLookUp(LookUp lookUp) {
        return Completable.fromAction(() -> {
            //AsyncTask.execute(() -> taskDao.insertTasks(generateTasks()));
            AsyncTask.execute(() -> routeDao.insertLookUp(lookUp));

        });
    }

    // Added By Husnain
    private Completable insertOrder(List<Order> order) {
        return Completable.fromAction(() -> {
            //AsyncTask.execute(() -> taskDao.insertTasks(generateTasks()));
//            AsyncTask.execute(() -> orderDao.insertOrders(order));
            orderDao.insertOrders(order);

        });
    }

    private Completable insertOrderDetail(List<OrderDetail> orderDetails) {
        return Completable.fromAction(() -> {
            //AsyncTask.execute(() -> taskDao.insertTasks(generateTasks()));
            AsyncTask.execute(() -> orderDao.insertOrderItems(orderDetails));

        });
    }

    public Completable deleteAllOutlets(boolean onStartDay) {
        if (onStartDay)
            return Completable.fromAction(() -> routeDao.deleteAllOutlets());
        return Completable.complete();
    }

    public Completable deleteAllMerchandise() {
        return Completable.fromAction(() -> routeDao.deleteAllMerchandise());
    }

    public Completable deleteAllCustomerInput() {
        return Completable.fromAction(() -> customerDao.deleteAllCustomerInput());
    }


    /**
     * Save Work status on server {Day started/ Day end}
     *
     * @param isStart {True for dayStart, False for dayEnd}
     */
    public void updateWorkStatus(boolean isStart) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("operationTypeId", isStart ? 1 : 2);
        map.put("appVersion", BuildConfig.VERSION_CODE);
        webService.updateStartEndStatus(map).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<LogModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(LogModel logModel) {
                        if (logModel.isSuccess()) {
                            WorkStatus status = preferenceUtil.getWorkSyncData();
                            status.setDayStarted(isStart ? 1 : 0);
                            status.setSyncDate(logModel.getStartDay());
                            preferenceUtil.saveWorkSyncData(status);
                            onDayStartLiveData.postValue(isStart);
                            if (isStart) {
                                fetchTodayData(true);
                            } else {
                                isLoading.postValue(false);
                            }
                        } else {
                            isLoading.postValue(false);

                            msg.postValue(logModel.getErrorMessage());
//                            msg.postValue(logModel.getErrorCode() == 2 ? logModel.getResponseMsg() : Constant.GENERIC_ERROR);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading.postValue(false);
                        msg.postValue(e.getMessage());
                    }
                });
    }

    public Completable deleteAllPricing(){


        return   Completable.fromAction(()->        pricingDao.deleteAllPriceConditionClasses()        )
                .andThen(Completable.fromAction(()->pricingDao.deleteAllPricingAreas()                 )   )
                .andThen(Completable.fromAction(()->pricingDao.deleteAllPriceConditionEntities()       ) )
                .andThen(Completable.fromAction(()->pricingDao.deleteAllPriceBundles()                   ))
                .andThen(Completable.fromAction(()->pricingDao.deletePriceCondition()                   ) )
                .andThen(Completable.fromAction(()->pricingDao.deletePriceConditionTypes()              ))
                .andThen(Completable.fromAction(()->pricingDao.deletePriceConditionScale()             ) )
                .andThen(Completable.fromAction(()->pricingDao.deletePriceAccessSequence()              ))
                .andThen(Completable.fromAction(()->pricingDao.deletePriceConditionOutletAttribute()    ))
                .andThen(Completable.fromAction(()->pricingDao.deleteFreeGoodMasters()                  ))
                .andThen(Completable.fromAction(()->pricingDao.deleteFreeGoodGroups()))
                .andThen(Completable.fromAction(()->pricingDao.deleteFreePriceConditionOutletAttribute() ))
                .andThen(Completable.fromAction(()->pricingDao.deleteFreeGoodDetails()                   ))
                .andThen(Completable.fromAction(()->pricingDao.deleteFreeGoodExclusives()                 ))
                .andThen(Completable.fromAction(()->pricingDao.deleteFreeGoodEntityDetails()             ))
                .andThen(Completable.fromAction(()->pricingDao.deleteOutletAvailedFreeGoods()            ))
                .andThen(Completable.fromAction(()->pricingDao.deleteOutletAvailedPromotion()             ));

    }

    /**
     * Pricing Start
     */
    Completable insertPriceConditionClasses(List<PriceConditionClass> priceConditionClasses){
        return   Completable.fromAction(() -> {
            pricingDao.insertPriceConditionClasses(priceConditionClasses);
        });
    }
    Completable insertConditionTypes(List<PriceConditionType> priceConditionTypes){
        return   Completable.fromAction(() -> {
            pricingDao.insertPriceConditionType(priceConditionTypes);
        });
    }

    Completable insertConditions(List<PriceCondition> priceConditions){
        return   Completable.fromAction(() -> {
            pricingDao.insertPriceCondition(priceConditions);
        });
    }

    Completable insertAccessSequence(List<PriceAccessSequence> priceAccessSequences){
        return   Completable.fromAction(() -> {
            pricingDao.insertPriceAccessSequence(priceAccessSequences);
        });
    }

    Completable insertConditionDetails(List<PriceConditionDetail> priceConditionDetails){
        return   Completable.fromAction(() -> {
            pricingDao.insertPriceConditionDetail(priceConditionDetails);
        });
    }

    Completable insertPriceBundle(List<PriceBundle> bundles){
        return   Completable.fromAction(() -> {
            pricingDao.insertPriceBundles(bundles);
        });
    }

    Completable insertPriceConditionEntities(List<PriceConditionEntities> entities){
        return   Completable.fromAction(() -> {
            pricingDao.insertPriceConditionEntities(entities);
        });
    }

    Completable insertPriceConditionScale(List<PriceConditionScale> scales){
        return   Completable.fromAction(() -> {
            pricingDao.insertPriceConditionScales(scales);
        });
    }
    /**** End Pricing ***/


    public void loadPricing() {

        executor.execute(() -> {
            try {
                isLoading.postValue(true);
                Observable<PricingModel> pricingModelResponse = webService.loadPricing();
                pricingModelResponse.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(response -> {
                        AsyncTask.execute(() -> {

                            pricingDao.deleteAllPriceConditionClasses();
                            pricingDao.deleteAllPricingAreas();
                            pricingDao.deleteAllPriceConditionEntities();
                            pricingDao.deleteAllPriceBundles();
                            pricingDao.deletePriceCondition();
                            pricingDao.deletePriceConditionTypes();
                            pricingDao.deletePriceConditionScale();
                            pricingDao.deletePriceAccessSequence();
                            pricingDao.deletePriceConditionOutletAttribute();
                            pricingDao.deleteFreeGoodMasters();
                            pricingDao.deleteFreeGoodGroups();
                            pricingDao.deleteFreePriceConditionOutletAttribute();
                            pricingDao.deleteFreeGoodDetails();
                            pricingDao.deleteFreeGoodExclusives();
                            pricingDao.deleteFreeGoodEntityDetails();
                            pricingDao.deleteOutletAvailedFreeGoods();
                            pricingDao.deleteOutletAvailedPromotion();

                            pricingDao.insertPriceConditionClasses(response.getPriceConditionClasses());
                            pricingDao.insertPriceConditionType(response.getPriceConditionTypes());
                            pricingDao.insertPriceAccessSequence(response.getPriceAccessSequences());
                            pricingDao.insertPriceCondition(response.getPriceConditions());
                            pricingDao.insertPriceBundles(response.getPriceBundles());
                            pricingDao.insertPriceConditionDetail(response.getPriceConditionDetails());
                            pricingDao.insertPriceConditionEntities(response.getPriceConditionEntities());
                            pricingDao.insertPriceConditionScales(response.getPriceConditionScales());
                            pricingDao.insertPriceConditionOutletAttributes(response.getPriceConditionOutletAttribute());
                            pricingDao.insertOutletAvailedPromotions(response.getOutletAvailedPromotions());


                            if (response.getFreeGoodsWrapper() != null){

                                pricingDao.insertFreeGoodMasters(response.freeGoodsWrapper.getFreeGoodMasters());
                                pricingDao.insertFreeGoodGroups(response.freeGoodsWrapper.getFreeGoodGroups());
                                pricingDao.insertFreePriceConditionOutletAttributes(response.freeGoodsWrapper.getPriceConditionOutletAttributes());
                                pricingDao.insertFreeGoodDetails(response.freeGoodsWrapper.getFreeGoodDetails());
                                pricingDao.insertFreeGoodExclusives(response.freeGoodsWrapper.getFreeGoodExclusives());
                                pricingDao.insertFreeGoodEntityDetails(response.freeGoodsWrapper.getFreeGoodEntityDetails());
                                pricingDao.insertOutletAvailedFreeGoods(response.freeGoodsWrapper.getOutletAvailedFreeGoods());
                            }

                            msg.postValue("Pricing Loaded Successfully!");

                            isLoading.postValue(false);

                        });

                }, throwable -> {
                    throwable.printStackTrace();
                    msg.postValue(Constant.GENERIC_ERROR);
                    isLoading.postValue(false);
                });
//                Response<PricingModel> response = webService.loadPricing().execute();
//                if(response.isSuccessful()){
//                    PricingModel pricingModel = response.body();
//                    deleteAllPricing()
//                            .andThen(insertPriceConditionClasses(pricingModel.getPriceConditionClasses()))
//                            .andThen(insertConditionTypes(pricingModel.getPriceConditionTypes()).delay(200, TimeUnit.MILLISECONDS))
//                            .andThen(insertAccessSequence(pricingModel.getPriceAccessSequences()).delay(200, TimeUnit.MILLISECONDS))
//                            .andThen(insertConditions(pricingModel.getPriceConditions()))
//                            .andThen(insertPriceBundle(pricingModel.getPriceBundles()))
//                            .andThen(insertConditionDetails(pricingModel.getPriceConditionDetails()))
//                            .andThen(insertPriceConditionEntities(pricingModel.getPriceConditionEntities()))
//                            .andThen(insertPriceConditionScale(pricingModel.getPriceConditionScales()))
//                            .observeOn(Schedulers.io())
//                            .subscribeOn(Schedulers.io())
//                            .subscribe(new CompletableObserver() {
//                                @Override
//                                public void onSubscribe(Disposable d) {
//
//                                }
//
//                                @Override
//                                public void onComplete() {
//                                    msg.postValue("Pricing Loaded Successfully!");
//                                }
//
//                                @Override
//                                public void onError(Throwable e) {
//                                    Log.e(TAG,e.getMessage());
//                                    msg.postValue(Constant.GENERIC_ERROR);
//                                }
//                            });
//
//                }
//                else{
//                    msg.postValue(response.errorBody().string());
//                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,e.getMessage()+"");
                msg.postValue(Constant.GENERIC_ERROR);
                isLoading.postValue(false);
            }

        });
    }


    public Single<AppUpdateModel> updateApp() {
        return webService.checkAppUpdate();
    }


    public MutableLiveData<Boolean> mLoading() {
        return isLoading;
    }

    public MutableLiveData<Boolean> startDay() {
        return onDayStartLiveData;
    }

    public MutableLiveData<Boolean> getTargetVsAchievement() {
        return targetVsAchievement;
    }

    public MutableLiveData<String> getError() {
        return msg;
    }
}
