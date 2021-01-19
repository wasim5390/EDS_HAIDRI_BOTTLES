package com.optimus.eds.ui.route.outlet.detail;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.optimus.eds.db.AppDatabase;

import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.db.dao.TaskDao;
import com.optimus.eds.db.entities.LookUp;
import com.optimus.eds.db.entities.Promotion;
import com.optimus.eds.model.Configuration;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Task;
import com.optimus.eds.model.PackageProductResponseModel;
import com.optimus.eds.source.API;
import com.optimus.eds.source.RetrofitHelper;
import com.optimus.eds.utils.PreferenceUtil;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class OutletDetailRepository {
    private final  static  String TAG = OutletDetailRepository.class.getName();
    private final RouteDao routeDao;
    private final ProductsDao productsDao;
    private final TaskDao taskDao;
    private final PreferenceUtil preferenceUtil;

    MutableLiveData<PackageProductResponseModel> loaded;
    MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> msg ;
    private final API webservice;

    public OutletDetailRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        preferenceUtil = PreferenceUtil.getInstance(application);
        webservice = RetrofitHelper.getInstance().getApi();
        routeDao = appDatabase.routeDao();
        taskDao = appDatabase.taskDao();
        productsDao = appDatabase.productsDao();
        msg = new MutableLiveData<>();
        loaded = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
    }


    public Configuration getConfiguration(){
        return preferenceUtil.getConfig();
    }

    public Long getStartedDate(){
        return preferenceUtil.getWorkSyncData().getSyncDate();
    }

    public LiveData<Outlet> getOutletById(Long outletId){
        return routeDao.findOutletById(outletId);
    }

    public Single<Outlet> getOutletByIdSingle(Long outletId){
        return routeDao.findOutletByIdSingle(outletId);
    }

    public LiveData<List<Promotion>> getPromotionByOutletId(Long outletId){
        return routeDao.getPromotionByOutletId(outletId);
    }

    public LiveData<LookUp> getLookUpData(){
        return routeDao.getLookUpData();
    }

    public LiveData<List<Task>> getTasksByOutletId(Long outletId){
        return taskDao.getTaskByOutletId(outletId);
    }

    public void  updateOutletVisitStatus(Long outletId, Integer visitStatus,boolean synced){
        AsyncTask.execute(() -> routeDao.updateOutletVisitStatus(outletId,visitStatus,synced));
    }

    public void  updateOutletCnic(Long outletId,String mobileNumber, String cnic,String strn){
        AsyncTask.execute(() -> routeDao.updateOutletCnic(outletId,mobileNumber,cnic,strn));
    }

    public void updateOutlet(Outlet outlet){
        AsyncTask.execute(() -> routeDao.updateOutlet(outlet));
    }

    public void updateTask(Task task){
        AsyncTask.execute(() -> {
            Completable completable = Completable.fromAction(() -> {
                taskDao.updateTask(task);
            });
            completable.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(() -> {
                        msg.postValue("Task Updated!");
                    });
        });
    }

    public void loadProductsFromServer(){
        isLoading.setValue(false);
        Observable<PackageProductResponseModel> stockObservable =webservice.loadTodayPackageProduct();
        stockObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse,this::handleError);
    }

    private void handleResponse(PackageProductResponseModel response) {
        if(response.isSuccess()){
            AsyncTask.execute(() -> {
                productsDao.deleteAllProducts();
                productsDao.deleteAllProductGroups();
                productsDao.insertProducts(response.getProductList());
                productsDao.insertProductGroups(response.getProductGroups());
                loaded.postValue(response);
            });


        }else{
            msg.postValue(response.getResponseMsg()!=null?response.getResponseMsg():"Unable to Load Stock");
        }
        isLoading.postValue(true);
    }


    private void handleError(Throwable t) {
        msg.postValue(t.getMessage());
        isLoading.postValue(true);

    }
    public MutableLiveData<String> showMsg() {
        return msg;
    }
    public LiveData<PackageProductResponseModel> stockLoaded() {
        return loaded;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

}
