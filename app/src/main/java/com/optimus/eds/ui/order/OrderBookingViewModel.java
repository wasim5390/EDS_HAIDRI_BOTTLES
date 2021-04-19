package com.optimus.eds.ui.order;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optimus.eds.Constant;

import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.ProductGroup;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.model.OrderResponseModel;
import com.optimus.eds.model.PackageModel;
import com.optimus.eds.source.API;
import com.optimus.eds.source.RetrofitHelper;

import com.optimus.eds.ui.route.outlet.detail.OutletDetailRepository;
import com.optimus.eds.utils.NetworkManager;
import com.optimus.eds.utils.Util;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.reactivex.Completable;

import io.reactivex.CompletableObserver;
import io.reactivex.Maybe;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;


public class OrderBookingViewModel extends AndroidViewModel {

    private final CompositeDisposable disposable;
    private final OrderBookingRepository repository;
    private final OutletDetailRepository outletDetailRepository;
    private final MutableLiveData<List<PackageModel>> mutablePkgList;
    private MutableLiveData<List<ProductGroup>> productGroupList;
    private final MutableLiveData<Boolean> isSaving;
    private final MutableLiveData<String> msg;
    private final MutableLiveData<Boolean> orderSaved;
    private LiveData<List<Package>> packages;
    private MutableLiveData<Boolean> noOrder;


    private Long outletId;
    private Integer distributionId;
    private OrderModel order = null;
    private final API webservice;
    private final static String TAG = OrderBookingViewModel.class.getName();


    public OrderBookingViewModel(@NonNull Application application) {
        super(application);
        disposable = new CompositeDisposable();
        webservice = RetrofitHelper.getInstance().getApi();
        repository = OrderBookingRepository.singleInstance(application);
        outletDetailRepository = new OutletDetailRepository(application);
        mutablePkgList = new MutableLiveData<>();
        msg = new MutableLiveData<>();
        isSaving = new MutableLiveData<>();
        orderSaved = new MutableLiveData<>();
        noOrder = new MutableLiveData<>();
        onScreenCreated();
    }

    private void onScreenCreated() {
        isSaving.setValue(true);
        noOrder.setValue(false);
        productGroupList = repository.findAllGroups();
        packages = repository.findAllPackages();

    }

    private void setOrder(OrderModel order) {
        this.order = order;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
        findOrder(outletId);
    }

    public void setDistributionId(Integer distributionId) {
        this.distributionId = distributionId;
    }

    private void findOrder(Long outletId) {
//        Maybe<OrderModel> orderSingle = repository.findOrder(outletId);
//        Disposable orderDisposable = orderSingle.observeOn(Schedulers.io())
//                .subscribeOn(Schedulers.io()).subscribe(this::setOrder, this::onError);
//        disposable.add(orderDisposable);

        OrderModel orderModel = repository.findOrder(outletId).blockingGet();
        setOrder(orderModel);
    }


    public void filterProductsByGroup(Long groupId) {
//        Single<List<Product>> allProductsByGroup = repository.findAllProductsByGroup(groupId);
        Single<List<Product>> allProductsByPackage = repository.findAllProductsByPackageId(groupId);
        if (order == null) {
            disposable.add(allProductsByPackage.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onProductsLoaded));
            return;
        }

        Single<List<OrderDetail>> allAddedProducts = repository.getOrderItems(order.getOrder().getLocalOrderId());
        Single<List<Product>> zippedSingleSource = Single.zip(allProductsByPackage, allAddedProducts, this::updatedProducts);

        Disposable homeDisposable = zippedSingleSource
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onProductsLoaded, this::onError);
        disposable.add(homeDisposable);


    }


    @NotNull
    private List<Product> updatedProducts(List<Product> filteredProducts, List<OrderDetail> addedProducts) {

        for (Product product : filteredProducts) {
            for (OrderDetail orderDetail : addedProducts) {
                if (Objects.equals(product.getId(), orderDetail.getProductId())) {
                    product.setQty(orderDetail.getCartonQuantity(), orderDetail.getUnitQuantity());
                    product.setAvlStock(orderDetail.getAvlCartonQuantity(), orderDetail.getAvlUnitQuantity());

                }
            }
        }


        return filteredProducts;
    }


    public void addOrder(List<Product> orderItems, Long pkgId, boolean sendToServer) {
        Completable.create(e -> {
            if (order == null) {
                Order order = new Order(outletId);
                repository.createOrder(order);
            } else {
//                repository.deleteOrderItemsByGroup(order.getOrder().getLocalOrderId(),groupId);
                repository.deleteOrderItemsByPackage(order.getOrder().getLocalOrderId(), pkgId);
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onComplete() {
                addOrderItems(orderItems, sendToServer);
            }

            @Override
            public void onError(Throwable e) {
                msg.postValue(e.getMessage());
            }
        });


    }

    @SuppressLint("CheckResult")
    private void addOrderItems(List<Product> orderItems, boolean sendToServer) {

        repository.findOrder(outletId)
                .flatMapCompletable(
                        orderModel -> modifyOrderDetails(orderModel, orderItems))
                .andThen(repository.findOrder(outletId))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(orderModel -> onInsertedInDb(orderModel, sendToServer), this::onError);
    }


    private void updateOrder(OrderModel order) {

        if (order != null && order.getOrder() != null && order.isSuccess() && order.getOrderDetails() != null) {
            if (order.getOrder().getPayable() != null) {

                setOrder(order);

                if (order.order != null){
                    if (order.order.serverOrderId != null) {
                        for (int i = 0; i < order.getOrderDetails().size(); i++) {
                            order.order.getOrderDetails().get(i).setOrderId(order.order.serverOrderId);

                            if ( order.order.getOrderDetails().get(i).getCartonFreeGoods() != null){

                                for (int j=0; j<order.order.getOrderDetails().get(i).getCartonFreeGoods().size(); j++){
                                    order.order.getOrderDetails().get(i).getCartonFreeGoods().get(j).setOrderId(order.order.serverOrderId);
                                }
                            }

                        }
                    }

                }

                Completable orderUpdateCompletable = repository.updateOrder(order.getOrder());
                Completable removeOrderItems = repository.deleteOrderItems(order.getOrder().getLocalOrderId());
                Completable insertOrderItems = repository.addOrderItems(order.getOrderDetails());
                // Completable updateOrderItems = repository.updateOrderItems(order.getOrderDetails());


                Completable insertBreakdown = Completable.fromAction(() -> {
                    for (OrderDetail orderDetail : order.getOrderDetails()) {
                        if (!Util.isListEmpty(orderDetail.getUnitPriceBreakDown()))
                            repository.addOrderUnitPriceBreakDown(orderDetail.getUnitPriceBreakDown());
                        if (!Util.isListEmpty(orderDetail.getCartonPriceBreakDown()))
                            repository.addOrderCartonPriceBreakDown(orderDetail.getCartonPriceBreakDown());
                    }
                });

                orderUpdateCompletable
                        .andThen(Completable.fromAction(() -> System.out.println("Update Order finished")))
                        .andThen(removeOrderItems)
                        .andThen(Completable.fromAction(() -> System.out.println("Remove Order Items finished")))
                        .andThen(insertOrderItems).andThen(Completable.fromAction(() -> System.out.println("Insert Order Items")))
                        .andThen(insertBreakdown).andThen(Completable.fromAction(() -> System.out.println("Insert Breakdown")))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.single())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                disposable.add(d);
                            }

                            @Override
                            public void onComplete() {
                                orderSaved.postValue(true);
                                isSaving.postValue(false);
                                // msg.postValue("Order Saved Successfully");
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                orderSaved.postValue(false);
                                isSaving.postValue(false);
                                msg.postValue(e.getMessage());
                            }
                        });
            }

        } else {
            msg.postValue(Constant.PRICING_ERROR);
            isSaving.postValue(false);
        }

    }


    private void onInsertedInDb(OrderModel orderModel, boolean sendToServer) {

        repository.getOrderItems(orderModel.getOrder().getLocalOrderId())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(orderDetails -> {
                    if (orderDetails.isEmpty() && sendToServer) {
                        noOrder.postValue(true);
                        return;
                    } else
                        noOrder.postValue(false);

                    if (order != null)
                        if (order.order != null)
                            if (order.order.serverOrderId != null) {
                                for (int i = 0; i < orderDetails.size(); i++) {

                                    orderDetails.get(i).setOrderId(order.order.serverOrderId);
                                }
                            }

                    orderModel.setOrderDetails(orderDetails);
                    setOrder(orderModel);
                    if (sendToServer) {
                        composeOrderForServer();
                    }
                });


    }

    private void onProductsLoaded(List<Product> products) {
        mutablePkgList.postValue(repository.packageModel(packages.getValue(), products));
        isSaving.postValue(false);
    }

    private void onError(Throwable throwable) throws IOException {
        throwable.printStackTrace();
        String errorBody = throwable.getMessage();
        if (throwable instanceof HttpException) {
            HttpException error = (HttpException) throwable;
            if (error.code() == 500)
                errorBody = Constant.GENERIC_ERROR;
            else
                errorBody = error.response().errorBody().string();
        }
        if (throwable instanceof IOException) {
            errorBody = "Please check your internet connection";
        }

        msg.postValue(errorBody);
        isSaving.postValue(false);

    }

    private Completable modifyOrderDetails(OrderModel order, List<Product> orderProducts) {

        List<OrderDetail> orderDetails = new ArrayList<>(orderProducts.size());
        for (Product product : orderProducts) {
            OrderManager.OrderQuantity orderQuantity = OrderManager.instance().calculateOrderQty(product.getCartonQuantity(), product.getQtyUnit(), product.getQtyCarton());
            OrderDetail orderDetail = new OrderDetail(order.getOrder().getLocalOrderId(), product.getId(), orderQuantity.getCarton(), orderQuantity.getUnits());
            orderDetail.setAvlQty(product.getAvlStockCarton(), product.getAvlStockUnit());
            orderDetail.setCartonCode(product.getCartonCode());
            orderDetail.setUnitCode(product.getUnitCode());
            orderDetail.setProductName(product.getName());
            orderDetail.setActualCartonStock(product.getActualCartonStock());
            orderDetail.setActualUnitStock(product.getActualUnitStock());
            orderDetail.setUnitDefinitionId(product.getUnitDefinitionId());
            orderDetail.setCartonDefinitionId(product.getCartonDefinitionId());
            orderDetail.setProductGroupId(product.getProductGroupId());
            orderDetail.setType(Constant.ProductType.PAID);
            orderDetail.setCartonSize(product.getCartonQuantity());
            orderDetail.setPkgId(product.getPkgId());
            orderDetails.add(orderDetail);
        }
        order.setOrderDetails(orderDetails);
        return repository.addOrderItems(orderDetails);
        // return order;
    }

    protected List<Product> filterOrderProducts(Map<String, Section> sectionHashMap) {
        List<Product> productList = new ArrayList<>();

        for (Map.Entry<String, Section> entry : sectionHashMap.entrySet()) {
            PackageSection section = (PackageSection) entry.getValue();
            List<Product> products = section.getList();
            for (Product product : products) {
                if (product.isProductSelected()) {
                    productList.add(product);
                }
            }

        }

        return productList;
    }

    private void composeOrderForServer() {


        if (order != null) {
            isSaving.postValue(true);

            Order mOrder = new Order(order.getOrder().getOutletId());
            mOrder.setRouteId(order.getOutlet().getRouteId());
            mOrder.setVisitDayId(order.getOutlet().getVisitDay());
            if (order.getOrder().orderStatus == null)
            mOrder.setOrderStatus(Constant.ORDER_CREATED); //2 created
            else
            mOrder.setOrderStatus(order.getOrder().orderStatus); // Server Side Status
            mOrder.setLocalOrderId(order.getOrder().getLocalOrderId());
            mOrder.serverOrderId = order.getOrder().serverOrderId;
            mOrder.setLatitude(order.getOutlet().getLatitude());
            mOrder.setLongitude(order.getOutlet().getLongitude());
//            mOrder = order.order;


            order.setOrder(mOrder);


            Gson gson = new Gson();
            String json = gson.toJson(mOrder);
            OrderResponseModel responseModel = gson.fromJson(json, OrderResponseModel.class);
            responseModel.setOrderDetails(order.getOrderDetails());
            responseModel.setDistributionId(distributionId);
            NetworkManager.getInstance().isOnline().subscribe((aBoolean, throwable) -> {
                if (!aBoolean) {

                    msg.postValue(Constant.NETWORK_ERROR);
                    isSaving.postValue(false);
                } else {
                    disposable.add(calculateFromServer(responseModel));
                }
            });
        }

    }

    private Disposable calculateFromServer(OrderResponseModel responseModel) {
        return webservice.calculatePricing(responseModel)
                .map(orderResponseModel -> {
                    OrderModel orderModel = new OrderModel();
                    String orderString = new Gson().toJson(orderResponseModel);
                    Order order = new Gson().fromJson(orderString, Order.class);
                    orderModel.setOrderDetails(orderResponseModel.getOrderDetails());
                    orderModel.setOrder(order);
                    orderModel.setOutlet(this.order.getOutlet());
                    orderModel.setSuccess(orderResponseModel.isSuccess());


                    if (orderModel.order != null)
                        if (orderModel.order.serverOrderId != null) {
                            if ( orderResponseModel.getOrderDetails() != null)
                                for (int i = 0; i < orderResponseModel.getOrderDetails().size(); i++) {

                                    orderModel.getOrderDetails().get(i).setOrderId(orderModel.getOrder().serverOrderId);
                                }
                        }


                    return orderModel;
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::updateOrder, this::onError);
    }


    private int getOrderTotalQty(List<OrderDetail> orderDetails) {
        int totalQuantity = 0;
        for (OrderDetail orderDetail : orderDetails) {
            int cartonQty = orderDetail.getCartonQuantity() == null ? 0 : orderDetail.getCartonQuantity();
            int unitQty = orderDetail.getUnitQuantity() == null ? 0 : orderDetail.getUnitQuantity();
            totalQuantity += (cartonQty + unitQty);
        }
        return totalQuantity;
    }


    public LiveData<List<PackageModel>> getProductList() {
        return mutablePkgList;
    }

    public LiveData<List<ProductGroup>> getProductGroupList() {
        return productGroupList;
    }

    public LiveData<List<Package>> getPackages() {
        return packages;
    }

    public LiveData<Outlet> loadOutlet(Long outletId) {
        return outletDetailRepository.getOutletById(outletId);
    }

    public LiveData<Boolean> isSaving() {
        return isSaving;
    }

    public LiveData<String> showMessage() {
        return msg;
    }

    public LiveData<Boolean> orderSaved() {
        return orderSaved;
    }

    public LiveData<Boolean> noOrderTaken() {
        return noOrder;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();

    }
}
