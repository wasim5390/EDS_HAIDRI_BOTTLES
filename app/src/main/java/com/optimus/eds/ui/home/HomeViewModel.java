package com.optimus.eds.ui.home;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import com.bugfender.sdk.Bugfender;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.OrderStatus;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.model.AppUpdateModel;
import com.optimus.eds.model.MasterModel;
import com.optimus.eds.model.WorkStatus;
import com.optimus.eds.source.JobIdManager;
import com.optimus.eds.source.MerchandiseUploadService;
import com.optimus.eds.source.ProductUpdateService;
import com.optimus.eds.source.RetrofitHelper;
import com.optimus.eds.source.StatusRepository;
import com.optimus.eds.source.UploadOrdersService;
import com.optimus.eds.ui.AppUpdater;
import com.optimus.eds.ui.order.OrderBookingRepository;
import com.optimus.eds.ui.route.outlet.OutletListRepository;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;
import com.optimus.eds.utils.NotificationUtil;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class HomeViewModel extends AndroidViewModel {

    private final String TAG = HomeViewModel.class.getSimpleName();
    private final HomeRepository repository;
    private final CompositeDisposable disposable;

    private MutableLiveData<Boolean> endDayLiveData;
    private MutableLiveData<AppUpdateModel> appUpdateLiveData;

    private int remainingTasks=0;
    private int totalTasks=0;
    private OutletDetailRepository outletDetailRepository;
    private StatusRepository statusRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        ExecutorService executors = Executors.newSingleThreadExecutor();
        repository = HomeRepository.singleInstance(application,RetrofitHelper.getInstance().getApi(),executors);
        statusRepository = StatusRepository.singleInstance(getApplication());
        disposable = new CompositeDisposable();
        endDayLiveData = new MutableLiveData<>();
        appUpdateLiveData = new MutableLiveData<>();
        outletDetailRepository = new OutletDetailRepository(getApplication());


    }

    public MutableLiveData<Boolean> onStartDay(){
        return repository.startDay();
    }

    public void download(){
        isLoading().postValue(true);
        repository.fetchTodayData(true);
    }

    public void startDay(){
        // PreferenceUtil.getInstance(getApplication()).clearAllPreferences();
        repository.getToken();
    }

    public void updateDayEndStatus(){
        isLoading().postValue(true);
        repository.updateWorkStatus(false);
    }

    public MutableLiveData<Boolean> getEndDayLiveData() {
        return endDayLiveData;
    }



    public Observable<List<Outlet>> findOutletsWithPendingTasks() {
        return OutletListRepository.getInstance(getApplication()).getOutletsWithNoVisits()
                .toObservable().subscribeOn(Schedulers.computation());
    }

    public Observable<List<Outlet>> findAllOutlets() {
        return OutletListRepository.getInstance(getApplication()).getAllOutlet()
                .toObservable().subscribeOn(Schedulers.computation());
    }

    public void pushOrdersToServer(){

        List<OrderStatus> count =  OutletListRepository.getInstance(getApplication()).getOrderStatus()
                .subscribeOn(Schedulers.single()).blockingGet();
        if(count.size()<1) {
            getErrorMsg().postValue("Updated!");
            return;
        }

        UploadOrdersService.startSyncOrdersService(getApplication());

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
        disposable.dispose();
    }


    public Integer priceConditionClassValidation(){
        return repository.priceConditionClassValidation();
    }

    public Integer priceConditionValidation(){
        return repository.priceConditionValidation();
    }

    public Integer priceConditionTypeValidation(){
        return repository.priceConditionTypeValidation();
    }


    public MutableLiveData<Boolean> isLoading() {
        return repository.mLoading();
    }

    public MutableLiveData<Boolean> getTargetVsAchievement() {
        return repository.getTargetVsAchievement();
    }

    public MutableLiveData<String> getErrorMsg() {
        return repository.getError();
    }




    public LiveData<Boolean> dayStarted(){
        MutableLiveData<Boolean> when = new MutableLiveData<>();
        WorkStatus syncDate = PreferenceUtil.getInstance(getApplication()).getWorkSyncData();
        when.postValue(syncDate.getDayStarted()!=0);
        return when;
    }

    public LiveData<Boolean> dayEnded(){
        MutableLiveData<Boolean> when = new MutableLiveData<>();
        WorkStatus syncDate = PreferenceUtil.getInstance(getApplication()).getWorkSyncData();
        when.postValue(syncDate.getDayStarted().equals(Constant.DAY_END));
        //when.postValue(DateUtils.isToday(syncDate));
        return when;
    }

    public LiveData<AppUpdateModel> appUpdateLiveData(){
        return appUpdateLiveData;
    }

    public void checkDayEnd(){
        Long lastSyncDate = PreferenceUtil.getInstance(getApplication()).getWorkSyncData().getSyncDate();

        if( !Util.isDateToday(lastSyncDate)){
            onStartDay().postValue(false);
        }else{
            onStartDay().postValue(true);
        }
    }

    public void checkAppUpdate(){

        repository.updateApp().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(appUpdateModel -> {
                    if(appUpdateModel.getSuccess()){
                        boolean newVersionFound =AppUpdater.getInstance().apkChanged(appUpdateModel);
                        if(!newVersionFound)
                            isLoading().postValue(false);
                        appUpdateLiveData.postValue(appUpdateModel);
                        // isLoading().postValue(false);
                    }else{
                        getErrorMsg().postValue(appUpdateModel.getMsg());
                        isLoading().postValue(false);
                    }
                },this::onErrorCallback);

    }


    public void handleMultipleSyncOrder() {
        isLoading().postValue(true);
        String TAG = "Upload Multiple Orders:";
//        NotificationUtil.getInstance(getApplication().getApplicationContext()).showNotification();
        OutletListRepository.getInstance(getApplication()).getOrderStatus().toObservable()
                .concatMapIterable(orderStatuses -> {

                    FirebaseCrashlytics.getInstance().log(new Gson().toJson(orderStatuses));
                    FirebaseCrashlytics.getInstance().setCustomKey("orderStatuses" , new Gson().toJson(orderStatuses));

                    remainingTasks=totalTasks=orderStatuses.size();
                    return orderStatuses;
                }).concatMap(orderStatus ->
                    saveOrderObservable(orderStatus).delay(2 , TimeUnit.SECONDS)
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.single())
                .subscribe(new DisposableObserver<MasterModel>() {


                    @Override
                    public void onNext(MasterModel response) {
                        Log.i(TAG,"OnNext");
                        remainingTasks = remainingTasks-1;

//                        if(remainingTasks>0)
//                            NotificationUtil.getInstance(getApplication().getApplicationContext()).updateNotificationProgress(((float)remainingTasks/totalTasks),10);
                        onUpload(response,response.getOutletId(),response.getOutletStatus());
                    }
                    @Override
                    public void onError(Throwable e){

                        isLoading().postValue(false);
                        Toast.makeText(getApplication(), e.getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();
                        if(e instanceof OrderException) {
                            OrderException exception = (OrderException)e;
                            if (exception != null){
                                Integer status = exception.model.getOutletStatus();
//                                if (status != null)
//                                    NotificationUtil.getInstance(getApplication().getApplicationContext()).cancelUpload(10,exception.model.getOutletId(),status);
                            }
                        }else{
                            try {
                                error(e);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onComplete() {

                        new Handler().postDelayed(() -> {
                            Log.i(TAG,"OnComplete");
                            isLoading().postValue(false);
                            Toast.makeText(getApplication().getApplicationContext(), "All orders uploaded", Toast.LENGTH_SHORT).show();
//
                        }, 60000);
//                        Log.i(TAG,"OnComplete");
//                        isLoading().postValue(false);
//                        Toast.makeText(getApplication().getApplicationContext(), "All orders uploaded", Toast.LENGTH_SHORT).show();
//                        NotificationUtil.getInstance(getApplication().getApplicationContext()).finishUpload(10);
                    }
                });
    }

    private void onUpload(MasterModel orderResponseModel,Long outletId,Integer statusId) {


        if(!orderResponseModel.isSuccess()){
            isLoading().postValue(false);
            try {
                error(orderResponseModel);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }


        if(orderResponseModel !=null )
            if( orderResponseModel.getOrderModel()!=null) {
                orderResponseModel.setCustomerInput(null);
                orderResponseModel.getOrderModel().setOrderDetails(null);
                OrderBookingRepository.singleInstance(getApplication())
                        .findOrderById(orderResponseModel.getOrderModel().getMobileOrderId()).map(order -> {
                    orderResponseModel.getOrderModel().setPayable(order.getPayable());
                    order.setOrderStatus(orderResponseModel.getOrderModel().getOrderStatusId());
                    return order;
                }).flatMapCompletable(order -> OrderBookingRepository.singleInstance(getApplication())
                        .updateOrder(order)).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                                    Log.i("UploadOrdersService", "Order Status Updated");
                                    updateOutletTaskStatus(orderResponseModel.getOutletId(),orderResponseModel.getOrderModel().getPayable());
                                    ProductUpdateService.startProductsUpdateService(getApplication().getApplicationContext(),orderResponseModel.getOutletId());
                                },
                                throwable -> {error(throwable);});
            }else{
                outletDetailRepository.updateOutletVisitStatus(outletId,statusId,true);
                statusRepository.updateStatus(new OrderStatus(outletId,statusId,true,0.0));
                outletDetailRepository.updateOutlet(statusId , outletId);
            }

    }

    private void updateOutletTaskStatus(Long outletId,Double amount){
        outletDetailRepository.updateOutletVisitStatus(outletId,Constant.STATUS_COMPLETED,true); // 8 for completed task
        statusRepository.updateStatus(new OrderStatus(outletId,Constant.STATUS_COMPLETED,true,amount));
        outletDetailRepository.updateOutlet(Constant.STATUS_COMPLETED , outletId);
    }

    private void error(Object throwable) throws IOException {
        String errorBody;
        if(throwable instanceof Throwable) {
            Throwable mThrowable = (Throwable) throwable;
            mThrowable.printStackTrace();
            errorBody = mThrowable.getMessage();
            if (throwable instanceof HttpException) {
                HttpException error = (HttpException) throwable;
                errorBody = error.response().errorBody().string();
            }
            if (throwable instanceof SocketTimeoutException
                    || throwable instanceof SocketException
            ) {
                errorBody = Constant.NETWORK_ERROR;
            }
        }else{
            errorBody =((MasterModel)throwable).getResponseMsg();

        }
        isLoading().postValue(false);
        Toast.makeText(getApplication(), errorBody+"", Toast.LENGTH_SHORT).show();
    }


    private Observable saveOrderObservable(final OrderStatus orderStatus) {

        Gson gson = new Gson();
        MasterModel masterModel = gson.fromJson(orderStatus.getData(),MasterModel.class);

        if (masterModel != null)
            Bugfender.d("OutletId Multiple Request", masterModel.getOutletId() +"  " + PreferenceUtil.getInstance(getApplication().getApplicationContext()).getUsername())    ;


        return RetrofitHelper.getInstance().getApi().saveOrder(masterModel)
                .observeOn(AndroidSchedulers.mainThread()).toObservable()
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> {
                    MasterModel model = new MasterModel();
                    model.setSuccess(false);
                    model.setResponseMsg("Unable to Save Order");
                    model.setOutletId(orderStatus.getOutletId());
                    model.setOutletStatus(orderStatus.getStatus());
                    return model;
                })
                .map(model -> {
                    if(model.isSuccess()) {
                        model.setOutletId(masterModel.getOutletId());
                        model.setOutletStatus(orderStatus.getStatus());
                        scheduleMerchandiseJob(getApplication().getApplicationContext() , model.getOutletId() , PreferenceUtil.getInstance(getApplication()).getToken() , model.getOutletStatus());
                        return model;
                    }else{

//                        Toast.makeText(getApplication().getApplicationContext(), model.getErrorMessage() +"", Toast.LENGTH_SHORT).show();
                        MasterModel errorModel = new MasterModel();
                        errorModel.setSuccess(false);
                        errorModel.setResponseMsg(model.getErrorMessage());
                        errorModel.setOutletId(orderStatus.getOutletId());
                        errorModel.setOutletStatus(orderStatus.getStatus());
                        return errorModel;
                    }

                });
    }

    private class OrderException extends Exception{
        MasterModel model;
        public OrderException(MasterModel model) {
            super(model.getResponseMsg());
            this.model= model;
        }

    }


    // schedule
    public void scheduleMerchandiseJob(Context context,Long outletId,String token , Integer statusId) {

        if (outletId == null)
            return;
        PersistableBundle extras = new PersistableBundle();
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_ID,outletId);
        extras.putString(Constant.TOKEN, "Bearer "+token);
        extras.putInt("statusId", statusId);
        ComponentName serviceComponent = new ComponentName(context, MerchandiseUploadService.class);
        JobInfo.Builder builder = new JobInfo.Builder(outletId.intValue(), serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require any network
        builder.setOverrideDeadline(1000);
        builder.setMinimumLatency(1000);
        builder.setExtras(extras);
        JobScheduler jobScheduler = ContextCompat.getSystemService(context,JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }




    private void onErrorCallback(Throwable throwable) {
        throwable.printStackTrace();
        String errorBody = throwable.getMessage();
        if (throwable instanceof HttpException){
            HttpException error = (HttpException)throwable;
            try {
                errorBody = error.response().errorBody().string();
            } catch (IOException e) {
                errorBody = "Please check your internet connection";
            }
        }
        if (throwable instanceof IOException){
            errorBody = "Please check your internet connection";
        }

        getErrorMsg().postValue(errorBody);
        isLoading().postValue(false);

    }
}
