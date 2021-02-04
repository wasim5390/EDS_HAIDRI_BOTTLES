package com.optimus.eds.ui.customer_input;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.constraintlayout.solver.GoalRow;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.CustomerInput;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.OrderStatus;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.model.BaseResponse;
import com.optimus.eds.model.MasterModel;
import com.optimus.eds.model.OrderDetailAndPriceBreakdown;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.model.OrderResponseModel;
import com.optimus.eds.source.JobIdManager;
import com.optimus.eds.source.MerchandiseUploadService;
import com.optimus.eds.source.StatusRepository;
import com.optimus.eds.ui.order.OrderBookingRepository;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;
import com.optimus.eds.utils.NetworkManager;
import com.optimus.eds.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class CustomerInputViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> isSaving;
    private final MutableLiveData<String> msg;
    private final MutableLiveData<Boolean> orderSaved;
    private final MutableLiveData<Long> startUploadService;
    private final OrderBookingRepository orderRepository;
    private CustomerInputRepository customerInputRepository;
    private OutletDetailRepository outletDetailRepository;
    private StatusRepository statusRepository;
    private final CompositeDisposable disposable;
    private Long outletId;
    private MutableLiveData<OrderModel> orderModelLiveData;
    public CustomerInputViewModel(@NonNull Application application) {
        super(application);
        disposable = new CompositeDisposable();
        isSaving = new MutableLiveData<>();
        msg = new MutableLiveData<>();
        orderModelLiveData = new MutableLiveData<>();
        orderSaved = new MutableLiveData<>();
        startUploadService = new MutableLiveData<>();
        customerInputRepository = new CustomerInputRepository(application);
        outletDetailRepository = new OutletDetailRepository(application);
        statusRepository=StatusRepository.singleInstance(application);
        orderRepository = OrderBookingRepository.singleInstance(application);
    }


    public LiveData<Outlet> loadOutlet(Long outletId) {
        this.outletId = outletId;
        return customerInputRepository.getOutletById(outletId);
    }

    public void findOrder(Long outletId){

        Maybe<OrderModel> orderSingle = orderRepository.findOrder(outletId);
        Disposable orderDisposable = orderSingle
                .map(orderModel -> {
                    Crashlytics.setBool("Order_Exist_Before_Saving",orderModel!=null);
                    List<OrderDetail> orderDetails = new ArrayList<>();
                    for(OrderDetailAndPriceBreakdown orderDetail:orderModel.getOrderDetailAndCPriceBreakdowns()){
                        orderDetail.getOrderDetail().setCartonPriceBreakDown(orderDetail.getCartonPriceBreakDownList());
                        orderDetail.getOrderDetail().setUnitPriceBreakDown(orderDetail.getUnitPriceBreakDownList());
                        orderDetails.add(orderDetail.getOrderDetail());
                    }
                    orderModel.setOrderDetails(orderDetails);

                    return orderModel;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(this::onOrderLoadSuccess,this::error);
        disposable.add(orderDisposable);
    }


    public void saveOrder(String mobileNumber,String remarks,String cnic,String strn,String base64Sign , String deliveryDate){
        isSaving.postValue(true);
        Crashlytics.setBool("order_empty",orderModelLiveData.getValue()==null || orderModelLiveData.getValue().getOrder()==null);
        OrderModel orderModel = orderModelLiveData.getValue();
        if(orderModel!=null && orderModel.getOrder()!=null) {
            // @TODO have to change logic for livedata value as it gets null on some devices
            Order order = orderModel.getOrder();
            CustomerInput customerInput = new CustomerInput(outletId, order.getLocalOrderId(), mobileNumber, cnic, strn, remarks, base64Sign , deliveryDate);

            customerInputRepository.saveCustomerInput(customerInput)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(() -> {
                        postData(orderModel, customerInput);
                        scheduleMerchandiseJob(getApplication(), outletId, PreferenceUtil.getInstance(getApplication()).getToken());

                    });
        }else{
            findOrder(outletId);
        }

    }

    public void postData(OrderModel orderModel,CustomerInput customerInput){
        MasterModel data = generateOrder(orderModel,customerInput);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String finalJson = gson.toJson(data);
//        OrderStatus orderStatus = new OrderStatus(outletId,Constant.STATUS_PENDING_TO_SYNC,false,orderModel.getOrder().getPayable());
        OrderStatus orderStatus = new OrderStatus(outletId,Constant.STATUS_PENDING_TO_SYNC,false,orderModel.getOutlet().getLastOrder()!=null?orderModel.getOutlet().getLastOrder().getOrderTotal() : 0.0);
        orderStatus.setOutletVisitEndTime(Calendar.getInstance().getTimeInMillis());
        orderStatus.setData(finalJson);
        statusRepository.updateStatus(orderStatus);
        outletDetailRepository.updateOutletVisitStatus(outletId,Constant.STATUS_PENDING_TO_SYNC,false);
        outletDetailRepository.updateOutletCnic(outletId,customerInput.getMobileNumber(),customerInput.getCnic(),customerInput.getStrn());

        NetworkManager.getInstance().isOnline().subscribe((available, throwable) -> {
            Log.println(100,"Post Data:",outletId.toString());
            if (available){
                startUploadService.postValue(outletId);
            }
            isSaving.postValue(false);
            orderSaved.postValue(true);

        });
    }

    //**************** Post Order ****************/
    public MasterModel generateOrder(OrderModel orderModel,CustomerInput customerInput){

        OrderStatus status= statusRepository.findOrderStatus(outletId).subscribeOn(Schedulers.io()).blockingGet();

        MasterModel masterModel = new MasterModel();

        Order order = orderModel.getOrder();
        Gson gson  = new Gson();
        String json = gson.toJson(order);
        OrderResponseModel responseModel = gson.fromJson(json,OrderResponseModel.class);
        responseModel.setOrderDetails(orderModel.getOrderDetails());
        responseModel.setStartedDate(outletDetailRepository.getStartedDate());
        masterModel.setCustomerInput(customerInput);
        masterModel.setOrderModel(responseModel);
        masterModel.setStartedDate(outletDetailRepository.getStartedDate());
        masterModel.setLocation(orderModel.getOutlet().getVisitTimeLat(),orderModel.getOutlet().getVisitTimeLng());
        masterModel.setOutletId(order.getOutletId());
        masterModel.setOutletStatus(Constant.STATUS_CONTINUE); // 8 for order complete
        if(status!=null)
            masterModel.setOutletVisitTime(status.getOutletVisitStartTime()>0?status.getOutletVisitStartTime():null);
        masterModel.setOutletEndTime(Calendar.getInstance().getTimeInMillis());
        return masterModel;

    }

    // ************************************/


    // schedule
    public void scheduleMerchandiseJob(Context context,Long outletId,String token) {
        PersistableBundle extras = new PersistableBundle();
        extras.putLong(Constant.EXTRA_PARAM_OUTLET_ID,outletId);
        extras.putString(Constant.TOKEN, "Bearer "+token);
        ComponentName serviceComponent = new ComponentName(context, MerchandiseUploadService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JobIdManager.getJobId(JobIdManager.JOB_TYPE_MERCHANDISE_UPLOAD,outletId.intValue()), serviceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require any network
        builder.setOverrideDeadline(1000);
        builder.setMinimumLatency(1000);
        builder.setExtras(extras);
        JobScheduler jobScheduler = ContextCompat.getSystemService(context,JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }



    private void onOrderLoadSuccess(OrderModel order){
        orderModelLiveData.postValue(order);
    }


    private void error(Object throwable) {
        String errorBody = Constant.GENERIC_ERROR;
        if(throwable instanceof Throwable) {
            Throwable mThrowable = (Throwable) throwable;
            mThrowable.printStackTrace();
            errorBody = mThrowable.getMessage();
        }
        else{
            if(((MasterModel)throwable).getErrorCode()==2)
                errorBody =((MasterModel)throwable).getResponseMsg();

        }
        MasterModel baseResponse = new MasterModel();
        baseResponse.setResponseMsg(errorBody);
        baseResponse.setSuccess(false);
        msg.postValue(errorBody);
        orderSaved.postValue(false);
    }

    public LiveData<Boolean> isSaving() {
        return isSaving;
    }

    public LiveData<String> showMessage(){
        return msg;
    }

    public LiveData<Boolean> orderSaved(){
        return orderSaved;
    }

    public LiveData<OrderModel> order(){
        return orderModelLiveData;
    }

    public LiveData<Long> getStartUploadService() {
        return startUploadService;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();

    }

}
