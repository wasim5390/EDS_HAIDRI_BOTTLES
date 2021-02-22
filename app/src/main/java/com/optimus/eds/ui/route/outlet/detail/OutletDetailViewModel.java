package com.optimus.eds.ui.route.outlet.detail;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import com.google.gson.GsonBuilder;
import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.Promotion;
import com.optimus.eds.model.Configuration;
import com.optimus.eds.db.entities.OrderStatus;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.model.MasterModel;
import com.optimus.eds.model.OrderResponseModel;
import com.optimus.eds.model.PackageProductResponseModel;
import com.optimus.eds.source.StatusRepository;

import java.util.Calendar;
import java.util.List;


public class OutletDetailViewModel extends AndroidViewModel {

    private final OutletDetailRepository repository;
    private final StatusRepository statusRepository;
    private MutableLiveData<Boolean> startUploadService;
    private final MutableLiveData<Location> outletNearbyPos;
    private final MutableLiveData<Boolean> uploadStatus;


    private int outletStatus=1;
    private Outlet outlet;


    public OutletDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new OutletDetailRepository(application);
        statusRepository = StatusRepository.singleInstance(application);
        uploadStatus = new MutableLiveData<>();
        outletNearbyPos = new MutableLiveData<>();
        startUploadService = new MutableLiveData<>();
        repository.loadProductsFromServer();

    }


    public LiveData<Outlet> findOutlet(Long outletId){
        return repository.getOutletById(outletId);
    }

    public void setOutlet(Outlet outlet){
        this.outlet = outlet;
    }


    // schedule
    public void uploadStatus(Long outletId,Location location, Long visitDateTime,Long visitEndTime,String reason) {

        MasterModel masterModel = new MasterModel();
        masterModel.setOutletId(outletId);
        masterModel.setOutletStatus(outletStatus);
        masterModel.setReason(reason);
        masterModel.setStartedDate(repository.getStartedDate());
        OrderResponseModel order = new OrderResponseModel();
        order.setStartedDate(repository.getStartedDate());
        masterModel.setOrderModel(order);
        masterModel.setOutletVisitTime(visitDateTime);
        masterModel.setOutletEndTime(visitEndTime);
        masterModel.setLocation(location.getLatitude(),location.getLongitude());
        String finalJson = new GsonBuilder().setPrettyPrinting().create().toJson(masterModel);
        Log.i("JSON:: ",finalJson);
        statusRepository.findOrderStatus(outletId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(status -> {
            status.setData(finalJson);
            statusRepository.update(status);
            startUploadService.postValue(true);

        });





/*        PersistableBundle extras = new PersistableBundle();
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_ID,outletId);
        extras.putInt(Constant.EXTRA_PARAM_OUTLET_STATUS_ID,outletStatus);
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_VISIT_TIME,visitDateTime);
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_VISIT_END_TIME,visitEndTime);
        extras.putDouble(Constant.EXTRA_PARAM_PRESELLER_LAT,location.getLatitude());
        extras.putDouble(Constant.EXTRA_PARAM_PRESELLER_LNG,location.getLongitude());
        extras.putString(Constant.EXTRA_PARAM_OUTLET_REASON_N_ORDER,reason);
        extras.putString(Constant.TOKEN, "Bearer "+token);
        ComponentName serviceComponent = new ComponentName(context, MasterDataUploadService.class);
        int jobId = outletId.intValue();//JobIdManager.getJobId(JobIdManager.JOB_TYPE_MASTER_UPLOAD,outletId.intValue());
        JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require any network
        builder.setExtras(extras);
        builder.setMinimumLatency(1000);
        builder.setPersisted(true);
        JobScheduler jobScheduler = ContextCompat.getSystemService(context,JobScheduler.class);
        jobScheduler.cancel(jobId);
        NetworkManager.getInstance().isOnline().observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe((aBoolean, throwable) -> {
                    if(aBoolean)
                        builder.setOverrideDeadline(0);
                    Objects.requireNonNull(jobScheduler).schedule(builder.build());
                });*/

    }




    public void updateOutletStatusCode(int code){
        outletStatus = code;
    }

    public void updateOutletVisitEndTime(Long outletId,Long time){

        statusRepository.updateStatusOutletEndTime(time,outletId);
    }

    public Configuration getConfiguration(){
        return repository.getConfiguration();
    }

    public void onNextClick(Location currentLocation,Long outletVisitStartTime) {

        Location outletLocation = new Location("Outlet Location");
        outletLocation.setLatitude(outlet.getLatitude());
        outletLocation.setLongitude(outlet.getLongitude());
        double distance = currentLocation.distanceTo(outletLocation);

        Configuration configuration = getConfiguration();
        Integer minReqDistance = configuration.getGeoFenceMinRadius();
        if(configuration.getGeoFenceRequired() && distance>=minReqDistance && outletStatus<=2)
            outletNearbyPos.postValue(outletLocation);
        else
        {
            outlet.setVisitTimeLat(currentLocation.getLatitude());
            outlet.setVisitTimeLng(currentLocation.getLongitude());
            outlet.setVisitStatus(outletStatus);
            outlet.setSynced(false);
            outlet.setZeroSaleOutlet(false);
            outlet.setStatusId(outletStatus);

            OrderStatus orderStatus = new OrderStatus(outlet.getOutletId(),outletStatus,false,0.0);
            orderStatus.setOutletVisitEndTime(Calendar.getInstance().getTimeInMillis());
            orderStatus.setOutletVisitStartTime(outletVisitStartTime);
            statusRepository.insertStatus(orderStatus);
            repository.updateOutlet(outlet);
            uploadStatus.postValue(outletStatus != 1);
        }


    }

    public void postEmptyCheckout(boolean noOrderFromBooking,Long outletId,Long outletVisitStartTime,Long outletVisitEndTime){
        if(noOrderFromBooking) {
            outletStatus = Constant.STATUS_NO_ORDER_FROM_BOOKING; // 6 means no order from booking view
            if(outlet==null)
               outlet = repository.getOutletByIdSingle(outletId).subscribeOn(Schedulers.io()).blockingGet();
            outlet.setSynced(false);
            outlet.setVisitStatus(outletStatus);
            repository.updateOutlet(outlet); // TODO remove this if below successful
            OrderStatus status = new OrderStatus(outlet.getOutletId(),outletStatus,false,0.0);
            // TODO here synced=> false is not working properly
            status.setOutletVisitStartTime(outletVisitStartTime);
            status.setOutletVisitEndTime(outletVisitEndTime);
            statusRepository.insertStatus(status);
            uploadStatus.postValue(true);

        }
    }

    public LiveData<List<Promotion>> getPromos(Long outletId){
        return repository.getPromotionByOutletId(outletId);
    }

    public LiveData<Boolean> startUploadService(){
        return startUploadService;
    }

    public LiveData<Boolean> getUploadStatus() {
        return uploadStatus;
    }

    public LiveData<PackageProductResponseModel> stockLoaded(){
        return repository.stockLoaded();
    }

    public LiveData<Boolean> isLoading(){
        return repository.isLoading();
    }

    public LiveData<Boolean> viewTasks(){
        MutableLiveData<Boolean> tasksLiveData = new MutableLiveData<>();
        tasksLiveData.postValue(getConfiguration().taskExists());

        return tasksLiveData;
    }

    public LiveData<Location> getOutletNearbyPos() {
        return outletNearbyPos;
    }


}
