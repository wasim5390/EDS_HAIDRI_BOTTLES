package com.optimus.eds.ui.cash_memo;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.model.OrderDetailAndPriceBreakdown;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;
import com.optimus.eds.utils.Util;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.optimus.eds.Constant.PRIMARY;

public class CashMemoViewModel extends AndroidViewModel {

    public MutableLiveData<List<OrderDetail>> savedProducts;
    private CashMemoRepository repository;
    private OutletDetailRepository outletDetailRepository;
    private MutableLiveData<OrderModel> orderLiveData;


    public CashMemoViewModel(@NonNull Application application) {
        super(application);
        savedProducts = new MutableLiveData<>();
        orderLiveData = new MutableLiveData<>();
        repository = new CashMemoRepository(application);
        outletDetailRepository = new OutletDetailRepository(application);

    }

    protected LiveData<OrderModel> getOrder(Long outletId){

        repository.findOrder(outletId).map(orderModel -> {
           // List<OrderDetail> freeGoods = new ArrayList<>();
            float freeQty =0f;
            for(OrderDetailAndPriceBreakdown orderWithDetails:orderModel.getOrderDetailAndCPriceBreakdowns()){
                Integer unitFreeQty = orderWithDetails.getOrderDetail().getUnitFreeGoodQuantity();
                Integer cartonFreeQty = orderWithDetails.getOrderDetail().getCartonFreeGoodQuantity();

                if(orderWithDetails.getOrderDetail().getCartonFreeQuantityTypeId()==PRIMARY
                        ||orderWithDetails.getOrderDetail().getUnitFreeQuantityTypeId()==PRIMARY
                ){
                    cartonFreeQty=0;unitFreeQty=0;
                    for(OrderDetail freeItem :orderWithDetails.getOrderDetail().getCartonFreeGoods()){
                        cartonFreeQty += freeItem.getCartonQuantity();
                    }

                    for(OrderDetail freeItem :orderWithDetails.getOrderDetail().getUnitFreeGoods()){
                        unitFreeQty += freeItem.getUnitQuantity();
                    }
                }

                String freeQtyStr = Util.convertToDecimalQuantity(cartonFreeQty==null?0:cartonFreeQty,unitFreeQty==null?0:unitFreeQty);
                freeQty += Float.valueOf(freeQtyStr);

                //freeGoods.addAll(orderWithDetails.getOrderDetail().getCartonFreeGoods());
                // freeGoods.addAll(orderWithDetails.getOrderDetail().getUnitFreeGoods());
                orderWithDetails.getOrderDetail().setCartonPriceBreakDown(orderWithDetails.getCartonPriceBreakDownList());
                orderWithDetails.getOrderDetail().setUnitPriceBreakDown(orderWithDetails.getUnitPriceBreakDownList());

            }
            orderModel.setFreeAvailableQty(freeQty);
            // orderModel.setFreeGoods(freeGoods);
            return orderModel;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MaybeObserver<OrderModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(OrderModel order) {

                        orderLiveData.postValue(order);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return orderLiveData;
    }

    public void updateOrder(List<OrderDetailAndPriceBreakdown> orderDetailAndPriceBreakdowns){
        getOrderDetailObservable(orderDetailAndPriceBreakdowns)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderDetail>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(OrderDetail orderDetail) {
                        repository.updateOrderItem(orderDetail);
                        Log.e("CashMemoViewModel", "onNext: " + orderDetail.getProductName() );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("CashMemoViewModel", "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e("CashMemoViewModel", "All users emitted!");
                    }
                });
    }


    private Observable<OrderDetail> getOrderDetailObservable(List<OrderDetailAndPriceBreakdown> orderDetailAndPriceBreakdowns) {

        return Observable
                .create((ObservableOnSubscribe<OrderDetail>) emitter -> {
                    for (OrderDetailAndPriceBreakdown orderDetailAndPriceBreakdown : orderDetailAndPriceBreakdowns) {
                        if (!emitter.isDisposed()) {
                            emitter.onNext(orderDetailAndPriceBreakdown.getOrderDetail());
                        }
                    }

                    if (!emitter.isDisposed()) {
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io());
    }


    public LiveData<Outlet> loadOutlet(Long outletId) {
        return outletDetailRepository.getOutletById(outletId);
    }

}
