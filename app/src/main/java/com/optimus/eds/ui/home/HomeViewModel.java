package com.optimus.eds.ui.home;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.OrderStatus;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.model.AppUpdateModel;
import com.optimus.eds.model.WorkStatus;
import com.optimus.eds.source.RetrofitHelper;
import com.optimus.eds.source.UploadOrdersService;
import com.optimus.eds.ui.AppUpdater;
import com.optimus.eds.ui.route.outlet.OutletListRepository;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;

import java.io.IOException;
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


    public HomeViewModel(@NonNull Application application) {
        super(application);
        ExecutorService executors = Executors.newSingleThreadExecutor();
        repository = HomeRepository.singleInstance(application,RetrofitHelper.getInstance().getApi(),executors);
        disposable = new CompositeDisposable();
        endDayLiveData = new MutableLiveData<>();
        appUpdateLiveData = new MutableLiveData<>();


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
        when.postValue(syncDate.getDayStarted()==Constant.DAY_END);
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
