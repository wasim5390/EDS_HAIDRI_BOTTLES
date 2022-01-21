package com.optimus.eds.source;

import android.app.IntentService;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;

import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.model.OrderDetailAndPriceBreakdown;

import com.optimus.eds.ui.order.OrderBookingRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class ProductUpdateService extends IntentService implements Constant {

    private final String iTAG = ProductUpdateService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public ProductUpdateService() {
        super("ProductUpdateService");
    }


    public static void startProductsUpdateService(Context context, Long outletId) {

        try {
            Intent intent = new Intent(context, ProductUpdateService.class);
            intent.putExtra(EXTRA_PARAM_OUTLET_ID,outletId);
            context.startService(intent);
        }catch (Exception e){
            e.getLocalizedMessage();
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final Long outletId = intent.getExtras().getLong(EXTRA_PARAM_OUTLET_ID);
        if(outletId!=null)
        updateProductCounter(outletId);
    }

    private void updateProductCounter(Long outletId){
        OrderBookingRepository.singleInstance(getApplication()).findOrder(outletId)
                .map(orderModel -> {
                    List<OrderDetail> orderDetails = new ArrayList<>();
                    for (OrderDetailAndPriceBreakdown orderDetail : orderModel.getOrderDetailAndCPriceBreakdowns()) {
                        orderDetails.add(orderDetail.getOrderDetail());
                    }
                    orderModel.setOrderDetails(orderDetails);

                    return orderModel;
                }).toObservable()
                .flatMap(orderModel -> getOrderDetailObservable(orderModel.getOrderDetails()))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(orderDetail -> {
                    findProduct(orderDetail);
                });



    }

    private void findProduct(OrderDetail orderItem){

        Single<Product> productSingle = OrderBookingRepository.singleInstance(getApplication()).findProductById(orderItem.getProductId());
        productSingle.map(product -> {
            Integer cartonQty = orderItem.getCartonQuantity()==null?0:orderItem.getCartonQuantity();
            Integer unitQty = orderItem.getUnitQuantity()==null?0:orderItem.getUnitQuantity();
            Integer productCartonStockInHand = product.getCartonStockInHand()==null?0:product.getCartonStockInHand();
            Integer productUnitStockInHand = product.getUnitStockInHand()==null?0:product.getUnitStockInHand();
            product.setCartonStockInHand(productCartonStockInHand-cartonQty);
            product.setUnitStockInHand(productUnitStockInHand-unitQty);
            return product;
        }).flatMapCompletable(
                product -> OrderBookingRepository.singleInstance(getApplication())
                        .updateProduct(product)).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io()).subscribe(() -> {
            Log.i(iTAG,"onComplete");
        },this::error);

    }

    private Observable<OrderDetail> getOrderDetailObservable(List<OrderDetail> orderDetail) {

        return Observable
                .create((ObservableOnSubscribe<OrderDetail>) emitter -> {
                    for (OrderDetail mOrderDetail : orderDetail) {
                        if (!emitter.isDisposed()) {
                            emitter.onNext(mOrderDetail);
                        }
                    }

                    if (!emitter.isDisposed()) {
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io());
    }


    private void error(Throwable throwable) {

        throwable.printStackTrace();
        String errorBody = throwable.getMessage();

    }


}
