package com.optimus.eds.ui.reports;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.model.OrderDetailAndPriceBreakdown;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.model.ReportModel;

import com.optimus.eds.ui.order.OrderManager;
import com.optimus.eds.ui.route.outlet.OutletListRepository;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class ReportsViewModel extends AndroidViewModel {


    private OutletListRepository repository;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<ReportModel> summaryMutable;
    private ReportModel reportModel;

    private List<Quantity> orderDetailList; // this list is sku based
    private List<Quantity> confirmedOrderDetailList;
    private Double total=0.0,confirmedTotal=0.0;
    private Float carton=0f,confirmedCarton=0f;
    private Long unit = 0l,confirmedUnits=0l;
    private int totalOrder = 0;
    private int pjpCount = 0;
    private int pendingCount = 0;
    private int completedCount = 0;
    private int productiveCount = 0;
    private int confirmedOrderCount = 0;
    private float totalSku = 0;
    public ReportsViewModel(@NonNull Application application) {
        super(application);
        repository = OutletListRepository.getInstance(application);
        orderDetailList = new ArrayList<>();
        confirmedOrderDetailList = new ArrayList<>();
        reportModel = new ReportModel();
        summaryMutable = new MutableLiveData<>();
    }

    public void getPjpCount(){

        AsyncTask.execute(() -> {
            pjpCount = repository.getPjpCount();
            completedCount = repository.getCompletedCount().size();
            productiveCount = repository.getProductiveCount().size();
            pendingCount = repository.getPendingCount().size();
            reportModel.setCounts(pjpCount,completedCount,productiveCount , pendingCount);
            summaryMutable.postValue(reportModel);

        });

    }
    public void getReport(){

        getPjpCount();
        Observable<List<OrderModel>> orderListObservable = repository.getOrders().toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation());
        disposable.add(orderListObservable
                .flatMap((Function<List<OrderModel>, ObservableSource<OrderModel>>) orderModelList -> {
                    totalOrder = orderModelList.size();
                    return Observable.fromIterable(orderModelList);
                })
                .subscribeWith(new DisposableObserver<OrderModel>() {
                    @Override
                    public void onNext(OrderModel orderDetail) {
                        boolean isOrderSynced=false;
                        FirebaseCrashlytics.getInstance().setCustomKey("order_empty_in_reports",orderDetail.getOrder()==null);
                        FirebaseCrashlytics.getInstance().setCustomKey("order_payable_empty_in_reports",orderDetail.getOrder()==null||orderDetail.getOrder().getPayable()==null);
                        Double price = orderDetail.getOrder().getPayable();
                        total+=price;
                        if(orderDetail.getOrder().getOrderStatus()==1) {
                            isOrderSynced=true;
                            confirmedTotal += price;
                            confirmedOrderCount+=1;
                        }
                        totalSku =totalSku +  orderDetail.getOrderDetailAndCPriceBreakdowns().size();
                        for(OrderDetailAndPriceBreakdown detailAndPriceBreakdown:orderDetail.getOrderDetailAndCPriceBreakdowns())
                        {
                            OrderDetail orderItem = detailAndPriceBreakdown.getOrderDetail();
                            Integer cQty = orderItem.getCartonQuantity();
                            Integer uQty = orderItem.getUnitQuantity();
                            cQty= cQty!=null?cQty:0;
                            uQty = uQty!=null?uQty:0;
//                            totalSku = orderDetail.getOrderDetailAndCPriceBreakdowns().size();


                            float quantity =   OrderManager.instance()
                                    .calculateQtyInCartons(orderItem.getCartonSize(),uQty,cQty);
                            Quantity qty = new Quantity(orderItem.getProductId(),quantity);

                            if(orderDetailList.contains(new Quantity(orderItem.getProductId()))){
                                int pos = orderDetailList.indexOf(new Quantity(orderItem.getProductId()));
                                Quantity savedItem = orderDetailList.get(pos);
                                float newQty = savedItem.getQuantity()+quantity;
//                                totalSku = totalSku + quantity;
                                orderDetailList.get(pos).setQuantity(newQty);
                            }else{
//                                totalSku = totalSku + quantity;
                                orderDetailList.add(qty);
                            }

                            if(isOrderSynced) {
                                Quantity confirmQty = new Quantity(orderItem.getProductId(),quantity);
                                if(confirmedOrderDetailList.contains(new Quantity(orderItem.getProductId()))){
                                    int pos = confirmedOrderDetailList.indexOf(new Quantity(orderItem.getProductId()));
                                    Quantity savedItem = confirmedOrderDetailList.get(pos);
                                    float newQty = savedItem.getQuantity()+quantity;
                                    confirmedOrderDetailList.get(pos).setQuantity(newQty);
                                }else{
                                    confirmedOrderDetailList.add(confirmQty);
                                }
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ReportsViewModel",e.getMessage());
                        summaryMutable.postValue(setSummary(total,confirmedTotal,carton,confirmedCarton,totalOrder,confirmedOrderCount, totalSku)); // orderDetailList.size()
                    }

                    @Override
                    public void onComplete() {
                        for (Quantity item: orderDetailList) {
                            carton+=item.getQuantity()!=null?item.getQuantity():0;
                        }

                        for (Quantity item: confirmedOrderDetailList) {
                            confirmedCarton+=item.getQuantity()!=null?item.getQuantity():0;
                        }

                        summaryMutable.postValue(setSummary(total,confirmedTotal,carton,confirmedCarton,totalOrder,confirmedOrderCount, totalSku)); // orderDetailList.size()
                    }
                }));


    }

    private ReportModel setSummary(Double total,Double confirmedTotal,Float carton,Float confirmedCartons,int totalOrder,int confirmedOrder,float totalSku){ //skuSize

        reportModel.setTotalSale(total);
        reportModel.setTotalSaleConfirm(confirmedTotal);
        reportModel.setCarton(carton);
        reportModel.setCartonConfirm(confirmedCartons);
//        reportModel.setSkuSize(skuSize);
        reportModel.setTotalSku(totalSku);
        reportModel.setTotalOrders(totalOrder);
        reportModel.setTotalConfirmOrders(confirmedOrder);
        return reportModel;
    }



    public LiveData<ReportModel> getSummary(){
        return summaryMutable;
    }

}
