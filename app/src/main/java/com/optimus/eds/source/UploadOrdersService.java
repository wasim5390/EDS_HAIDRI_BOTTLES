package com.optimus.eds.source;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.OrderStatus;
import com.optimus.eds.model.MasterModel;
import com.optimus.eds.ui.order.OrderBookingRepository;
import com.optimus.eds.ui.route.outlet.OutletListRepository;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;
import com.optimus.eds.utils.NotificationUtil;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.optimus.eds.Constant.EXTRA_PARAM_OUTLET_ID;

public class UploadOrdersService extends IntentService {

    private final String iTAG = UploadOrdersService.class.getSimpleName();
    private static final String ACTION_SINGLE_ORDER_UPLOAD = "com.optimus.eds.source.action.SINGLE_ORDER_UPLOAD";
    private static final String ACTION_MULTIPLE_ORDER_UPLOAD = "com.optimus.eds.source.action.MULTIPLE_ORDER_UPLOAD";

    private int jobId;

    private int remainingTasks=0;
    private int totalTasks=0;

    private OutletDetailRepository outletDetailRepository;
    private StatusRepository repository;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public UploadOrdersService() {
        super("UploadOrdersService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        repository = StatusRepository.singleInstance(getApplication());
        outletDetailRepository = new OutletDetailRepository(getApplication());
    }

    public static void startUploadService(Context context,Long outletId) {

        Intent intent = new Intent(context, UploadOrdersService.class);
        intent.setAction(ACTION_SINGLE_ORDER_UPLOAD);
        intent.putExtra(EXTRA_PARAM_OUTLET_ID,outletId);
        context.startService(intent);
    }

    public static void startSyncOrdersService(Context context) {

        Intent intent = new Intent(context, UploadOrdersService.class);
        intent.setAction(ACTION_MULTIPLE_ORDER_UPLOAD);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action){
                case ACTION_SINGLE_ORDER_UPLOAD:
                    Long outletId = intent.getExtras().getLong(EXTRA_PARAM_OUTLET_ID);
                    if(outletId!=null)
                        handleSingleOrder(outletId);
                    break;
                case ACTION_MULTIPLE_ORDER_UPLOAD:
                    handleMultipleSyncOrder();
                    break;
            }

        }
    }

    private void handleMultipleSyncOrder() {
        String TAG = "Upload Multiple Orders:";
        NotificationUtil.getInstance(getApplicationContext()).showNotification();
        OutletListRepository.getInstance(getApplication()).getOrderStatus().toObservable()
                .concatMapIterable(orderStatuses -> {
                    remainingTasks=totalTasks=orderStatuses.size();
                    return orderStatuses;
                })
                .concatMap(orderStatus -> saveOrderObservable(orderStatus))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.single())
                .subscribe(new DisposableObserver<MasterModel>() {
                    @Override
                    public void onNext(MasterModel response) {
                        Log.i(TAG,"OnNext");
                        remainingTasks = remainingTasks-1;
                        if(remainingTasks>0)
                            NotificationUtil.getInstance(getApplicationContext()).updateNotificationProgress(((float)remainingTasks/totalTasks),10);
                        onUpload(response,response.getOutletId(),response.getOutletStatus());
                    }

                    @Override
                    public void onError(Throwable e){
                        if(e instanceof OrderException) {
                            OrderException exception = (OrderException)e;
                            int status = exception.model.getOutletStatus();
                            NotificationUtil.getInstance(getApplicationContext()).cancelUpload(10,exception.model.getOutletId(),status);
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
                        Log.i(TAG,"OnComplete");
                        Toast.makeText(getApplicationContext(), "All orders uploaded", Toast.LENGTH_SHORT).show();
                        NotificationUtil.getInstance(getApplicationContext()).finishUpload(10);
                    }
                });
    }

    private void handleSingleOrder(Long outletId) {
        OrderStatus status = repository.findOrderStatus(outletId)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .blockingGet();
        String json = status.getData() != null ? status.getData() : ""; // Added Check By Husnain
        MasterModel masterModel = new Gson().fromJson(json,MasterModel.class);



        uploadMasterData(masterModel,status.getStatus());

    }

    private void uploadMasterData(MasterModel masterModel,int statusId) {

        String masterModelGson = new Gson().toJson(masterModel);

        Log.d("MaterModel" , masterModel.latitude + " " + masterModel.longitude);

        RetrofitHelper.getInstance().getApi().saveOrder(masterModel)
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(response->{
            onUpload(response,masterModel.getOutletId(),statusId);
        },throwable -> error(throwable));
    }

    private void onUpload(MasterModel orderResponseModel,Long outletId,Integer statusId) {
        if(!orderResponseModel.isSuccess()){
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
                                    ProductUpdateService.startProductsUpdateService(getApplicationContext(),orderResponseModel.getOutletId());
                                },
                                throwable -> {error(throwable);});
            }else{
                outletDetailRepository.updateOutletVisitStatus(outletId,statusId,true);
                repository.updateStatus(new OrderStatus(outletId,statusId,true,0.0));
                outletDetailRepository.updateOutlet(statusId , outletId);

            }
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_ORDER_UPLOAD);
        intent.putExtra("Response", orderResponseModel);
        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(intent);

    }

    private void updateOutletTaskStatus(Long outletId,Double amount){
        outletDetailRepository.updateOutletVisitStatus(outletId,Constant.STATUS_COMPLETED,true); // 8 for completed task
        repository.updateStatus(new OrderStatus(outletId,Constant.STATUS_COMPLETED,true,amount));
        outletDetailRepository.updateOutlet(Constant.STATUS_COMPLETED , outletId);
    }


    private Observable saveOrderObservable(final OrderStatus orderStatus) {

        Gson gson = new Gson();
        MasterModel masterModel = gson.fromJson(orderStatus.getData(),MasterModel.class);

        return RetrofitHelper.getInstance().getApi().saveOrder(masterModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).toObservable()
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
                        return model;
                    }else{
                        throw new OrderException(model);
                    }

                });
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
        MasterModel baseResponse = new MasterModel();
        baseResponse.setResponseMsg(errorBody);
        baseResponse.setSuccess(false);
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_ORDER_UPLOAD);
        intent.putExtra("Response", baseResponse);
        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(intent);
    }

    private class OrderException extends Exception{
        MasterModel model;
        public OrderException(MasterModel model) {
            super(model.getResponseMsg());
            this.model= model;
        }

    }

}
