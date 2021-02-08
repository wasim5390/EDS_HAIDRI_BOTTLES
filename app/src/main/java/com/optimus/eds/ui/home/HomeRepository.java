package com.optimus.eds.ui.home;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.optimus.eds.Constant;
import com.optimus.eds.EdsApplication;
import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.CustomerDao;
import com.optimus.eds.db.dao.OrderDao;
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
import com.optimus.eds.model.AppUpdateModel;
import com.optimus.eds.model.LogModel;
import com.optimus.eds.model.MasterModel;
import com.optimus.eds.model.PackageProductResponseModel;
import com.optimus.eds.model.RouteOutletResponseModel;
import com.optimus.eds.model.WorkStatus;
import com.optimus.eds.source.API;
import com.optimus.eds.source.TokenResponse;
import com.optimus.eds.utils.PreferenceUtil;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

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

    private final String TAG=HomeRepository.class.getSimpleName();
    private static HomeRepository repository;
    private final PreferenceUtil preferenceUtil;

    private CustomerDao customerDao;
    private ProductsDao productsDao;
    private RouteDao routeDao;
    private TaskDao taskDao;
    private OrderDao orderDao;


    private MutableLiveData<Boolean> targetVsAchievement;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> msg;
    private MutableLiveData<Boolean> onDayStartLiveData;
    private API webService;
    private Executor executor;

    public static HomeRepository singleInstance(Application application, API api, Executor executor){
        if(repository==null)
            repository = new HomeRepository(application,api,executor);
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
        onDayStartLiveData = new MutableLiveData<>();
        msg = new MutableLiveData<>();
        webService = api;
        this.executor = executor;
        WorkStatus syncDate = preferenceUtil.getWorkSyncData();
        onDayStartLiveData.postValue(syncDate.getDayStarted()!=0);

        // Added by Husnain
        targetVsAchievement = new MutableLiveData<>();

    }

    /**
     * Get User Token from server
     */
    public void getToken(){
        isLoading.setValue(true);
        String username = preferenceUtil.getUsername();
        String password = preferenceUtil.getPassword();
        webService.getToken("password",username,password)
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<TokenResponse>() {
                    @Override
                    public void onSuccess(TokenResponse tokenResponse) {
                        preferenceUtil.saveToken(tokenResponse.getAccessToken());
                        Crashlytics.setUserName(username);

                        updateWorkStatus(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading.postValue(false);
                        if(e instanceof HttpException || e instanceof SocketTimeoutException)
                            msg.postValue(Constant.NETWORK_ERROR);
                        else
                            msg.postValue(e.getMessage());
                    }
                });

    }

    /**
     * Fetch current day Routes/Outlets
     * @param onDayStart {True for onDayStart, False for Download click}
     */
    public void fetchTodayData(boolean onDayStart){

        executor.execute(() -> {
            try {
                Response<RouteOutletResponseModel> response = webService.loadTodayRouteOutlets().execute();
                if(response.isSuccessful()){
                    if(!response.body().isSuccess())
                    {

                        if(response.body().getResponseMsg()!=null)
                            msg.postValue(response.body().getResponseMsg());
                        else
                            msg.postValue(Constant.GENERIC_ERROR);
                        return;
                    }
                    //
                    Crashlytics.setString("dist_id",response.body().getDistributionId()+"");
                    Crashlytics.setUserIdentifier(response.body().getEmployeeName());
                    if(response.body() != null)
                        preferenceUtil.saveDistributionId(response.body().getDistributionId());
                    preferenceUtil.saveConfig(response.body().getConfiguration());
                    deleteAllRoutesAssets()
                            .andThen(deleteAllOutlets(onDayStart))
                            .andThen(Completable.fromAction(() -> {
                                if(onDayStart)
                                {
                                    routeDao.deleteAllMerchandise();
                                    customerDao.deleteAllCustomerInput();
                                    taskDao.deleteAllTask();
                                    routeDao.deleteAllPromotion();
                                    routeDao.deleteAllLookUp();
                                }
                            }))

                            .andThen(Completable.fromAction(() -> {
                                routeDao.insertRoutes(response.body().getRouteList());

//                                Added by Husnain
                                PreferenceUtil.getInstance(EdsApplication.getContext()).setHideCustomerInfo(response.body().getSystemConfiguration().getHideCustomerInfoInOrderingApp());
                                PreferenceUtil.getInstance(EdsApplication.getContext()).setPunchOrderInUnits(response.body().getSystemConfiguration().getCanNotPunchOrderInUnits());
                                PreferenceUtil.getInstance(EdsApplication.getContext()).setTargetAchievement(new Gson().toJson(response.body().getTargetVsAchievement()));
                            })).andThen(Completable.fromAction(() ->  routeDao.insertOutlets(response.body().getOutletList())))
                            .andThen(Completable.fromAction(() -> { routeDao.insertAssets(response.body().getAssetList());}))
                            .andThen(insertTasks(response.body().getTasksList()))
                            .andThen(insertPromotion(response.body().getPromosAndFOC()))
                            .andThen(insertLookUp(response.body().getLookUp()))
                            .andThen(Completable.fromAction(() -> {
                                long mobileOrderId = 1;
                                for (Order order: response.body().getOrders()){

                                    order.setLocalOrderId(mobileOrderId);
                                    orderDao.insertOrder(order);
                                    mobileOrderId++;
                                }
                            })) // added By Husanin
                            .andThen(Completable.fromAction(() -> { // added By Husanin
                                for (Order order : response.body().getOrders()){


                                    OrderStatus orderStatus = new OrderStatus();
                                    orderStatus.setOrderId(order.getOrderId());
                                    orderStatus.setOutletId(order.getOutletId());

                                    MasterModel masterModel = new MasterModel();
                                    masterModel.setOutletId(order.getOutletId());
                                    masterModel.setOutletStatus(8);

                                    orderStatus.setSynced(false);
                                    orderStatus.setData(new Gson().toJson(masterModel));
                                    orderStatus.setStatus(8);


                                    orderDao.insertOrderStatus(orderStatus);
                                }
                            }))
                            .andThen(Completable.fromAction(() -> { // added By Husanin
                                long mobileOrderId = 1;
                                for (Order order : response.body().getOrders()){
                                    for (OrderDetail orderDetail : order.getOrderDetails()){

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
                            Log.e(TAG,e.getMessage());
                            e.printStackTrace();
                        }
                    });

                }
                else{
                    msg.postValue(Constant.GENERIC_ERROR);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,e.getMessage()+"");
                msg.postValue(Constant.GENERIC_ERROR);
            }


        });



        executor.execute(() -> {

            Observable<PackageProductResponseModel> stockObservable = webService.loadTodayPackageProduct();
            stockObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(response -> {
                if (response.isSuccess() || !response.getPackageList().isEmpty()) {
                    AsyncTask.execute(() -> {
                        productsDao.deleteAllPackages();
                        productsDao.deleteAllProductGroups();
                        productsDao.deleteAllProducts();
                        productsDao.insertProductGroups(response.getProductGroups());
                        productsDao.insertPackages(response.getPackageList());
                        productsDao.insertProducts(response.getProductList());
                    });


                } else {
                    msg.postValue(response.getResponseMsg() != null ? response.getResponseMsg() : "Unable to refresh stock");
                }
                isLoading.postValue(false);
            }, throwable -> {
                throwable.printStackTrace();
                msg.postValue(Constant.GENERIC_ERROR);
                isLoading.postValue(false);
            });


        });
    }


    public Completable deleteAllRoutesAssets(){
        return Completable.fromAction(()->{
            routeDao.deleteAllRoutes();
            routeDao.deleteAllAssets();

        });
    }

    private Completable insertTasks(List<Task> tasks){
        return Completable.fromAction(()->{
            //AsyncTask.execute(() -> taskDao.insertTasks(generateTasks()));
            AsyncTask.execute(() -> taskDao.insertTasks(tasks));

        });
    }


    private Completable insertPromotion(List<Promotion> promotions){
        return Completable.fromAction(()->{
            //AsyncTask.execute(() -> taskDao.insertTasks(generateTasks()));
            AsyncTask.execute(() -> routeDao.insertPromotion(promotions));

        });
    }

    private Completable insertLookUp(LookUp lookUp){
        return Completable.fromAction(()->{
            //AsyncTask.execute(() -> taskDao.insertTasks(generateTasks()));
            AsyncTask.execute(() -> routeDao.insertLookUp(lookUp));

        });
    }
    // Added By Husnain
    private Completable insertOrder(List<Order> order){
        return Completable.fromAction(()->{
            //AsyncTask.execute(() -> taskDao.insertTasks(generateTasks()));
//            AsyncTask.execute(() -> orderDao.insertOrders(order));
            orderDao.insertOrders(order);

        });
    }

    private Completable insertOrderDetail(List<OrderDetail> orderDetails){
        return Completable.fromAction(()->{
            //AsyncTask.execute(() -> taskDao.insertTasks(generateTasks()));
            AsyncTask.execute(() -> orderDao.insertOrderItems(orderDetails));

        });
    }

    public Completable deleteAllOutlets(boolean onStartDay){
        if(onStartDay)
            return Completable.fromAction(()->routeDao.deleteAllOutlets());
        return Completable.complete();
    }

    public Completable deleteAllMerchandise(){
        return Completable.fromAction(()->routeDao.deleteAllMerchandise());
    }

    public Completable deleteAllCustomerInput(){
        return Completable.fromAction(() -> customerDao.deleteAllCustomerInput());
    }



    /**
     * Save Work status on server {Day started/ Day end}
     * @param isStart {True for dayStart, False for dayEnd}
     */
    public void updateWorkStatus(boolean isStart){
        HashMap<String, Integer> map = new HashMap<>();
        map.put("operationTypeId",isStart?1:2);
        webService.updateStartEndStatus(map).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<LogModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(LogModel logModel) {
                        isLoading.postValue(false);
                        if(logModel.isSuccess()){
                            WorkStatus status = preferenceUtil.getWorkSyncData();
                            status.setDayStarted(1);
                            status.setSyncDate(logModel.getStartDay());
                            preferenceUtil.saveWorkSyncData(status);
                            onDayStartLiveData.postValue(isStart);
                            if(isStart) {
                                fetchTodayData(isStart);
                            }
                        }else {
                            msg.postValue(logModel.getErrorCode()==2?logModel.getResponseMsg(): Constant.GENERIC_ERROR);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading.postValue(false);
                        msg.postValue(e.getMessage());
                    }
                });
    }

    public Single<AppUpdateModel> updateApp(){
        return webService.checkAppUpdate();
    }


    public MutableLiveData<Boolean> mLoading() {
        return isLoading;
    }

    public MutableLiveData<Boolean> startDay(){
        return onDayStartLiveData;
    }

    public MutableLiveData<Boolean> getTargetVsAchievement() {
        return targetVsAchievement;
    }

    public MutableLiveData<String> getError() {
        return msg;
    }
}
