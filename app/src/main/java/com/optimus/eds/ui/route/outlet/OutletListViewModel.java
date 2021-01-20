package com.optimus.eds.ui.route.outlet;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.optimus.eds.Constant;
import com.optimus.eds.db.entities.OrderStatus;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.OutletOrderStatus;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.source.StatusRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class OutletListViewModel extends AndroidViewModel {
    private final OutletListRepository repository;
    private final StatusRepository statusRepository;
    private final MutableLiveData<List<Outlet>> outletList;
    private final MutableLiveData<List<OutletOrderStatus>> outletOrderList;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> errorMsg;
    private final List<Outlet> allOutlets;
    private final CompositeDisposable disposable;

    private int visitedCount = 0;
    private int productiveCount = 0;
    private int pendingCount = 0;

    private MutableLiveData<HashMap<String , Integer>> countingMutable = new MutableLiveData<>();

    public OutletListViewModel(@NonNull Application application) {
        super(application);
        repository = OutletListRepository.getInstance(application);
        statusRepository = StatusRepository.singleInstance(application);
        outletOrderList = new MutableLiveData<>();
        outletList = new MutableLiveData<>();
        disposable = new CompositeDisposable();
        errorMsg = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        allOutlets = new ArrayList<>();


    }



    public LiveData<List<Route>> getRoutes() {
       return repository.getRoutes();
    }

    public boolean nonPjpExist(Long routeId){
       return  !repository.getOutlets(routeId,0).subscribeOn(Schedulers.io()).blockingGet().isEmpty();
    }


    public void loadOutletsFromDb(Long routeId,boolean isPjp){
        isLoading.postValue(true);
        ConnectableObservable<List<Outlet>> outletObservable = getOutlets(routeId,isPjp).toObservable().replay();
        disposable.add(
                outletObservable
                        .subscribeOn(Schedulers.single())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<List<Outlet>>() {

                            @Override
                            public void onNext(List<Outlet> outlets) {
                                // Refreshing list
                                allOutlets.clear();
                                allOutlets.addAll(outlets);
                                outletList.postValue(allOutlets);
                            }

                            @Override
                            public void onError(Throwable e) {
                                errorMsg.postValue(e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        }));

        ///////////// below connect-able observer is used to fetch order's amount for outlet
        disposable.add(
                outletObservable
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap((Function<List<Outlet>, ObservableSource<Outlet>>) Observable::fromIterable)
                        .flatMap((Function<Outlet, ObservableSource<Outlet>>) this::getOrderObservable)
                        .subscribeWith(new DisposableObserver<Outlet>() {

                            @Override
                            public void onNext(Outlet outlet) {
                                int position = allOutlets.indexOf(outlet);

                                if (position == -1) {
                                    return;
                                }

                                allOutlets.set(position, outlet);
                                outletList.postValue(allOutlets);

                            }

                            @Override
                            public void onError(Throwable e) {
                                isLoading.postValue(false);
                                errorMsg.postValue(e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                            }
                        }));

        // Calling connect to start emission
        outletObservable.connect();
    }



    private Single<List<Outlet>> getOutlets(Long routeId, boolean isPjp) {
        return repository.getOutlets(routeId,isPjp?1:0)
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Outlet> getOrderObservable(final Outlet outlet) {
        return statusRepository.findOrderStatus(outlet.getOutletId())
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(orderStatus -> {
                    outlet.setVisitStatus(orderStatus.getStatus());
                    outlet.setSynced(orderStatus.getSynced());
                        outlet.setTotalAmount(orderStatus.getOrderAmount());

                    return outlet;
                });

    }

    public LiveData<Boolean> orderTaken(Long outletId){
        MutableLiveData<Boolean> orderAlreadyTaken = new MutableLiveData<>();
        statusRepository.findOrderStatus(outletId).toSingle().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread()).subscribe(orderModel -> orderAlreadyTaken.postValue(orderModel.getStatus() >6), throwable -> {
            if(throwable instanceof NoSuchElementException)
                orderAlreadyTaken.postValue(false);
            else onError(throwable);
        });
        return orderAlreadyTaken;
    }

    public void getVisitedOutlets(){

        disposable.add(repository.getVisitedOutlets()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(outletOrderList::postValue));
    }

    public void getProductiveOutlets(){

        disposable.add(repository.getProductiveOutlets()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(outletOrderList::postValue));
    }

    public void getPendingOutlets(){

        disposable.add(repository.getPendingOutlets()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(outletOrderList::postValue));
    }


    public LiveData<List<Outlet>> getOutletList(){
        return outletList;
    }

    public void getPjpCount(){

        AsyncTask.execute(() -> {
             visitedCount= repository.getCompletedCount().size();
            pendingCount = repository.getPendingCount().size();
            productiveCount = repository.getProductiveCount().size();

            HashMap<String , Integer> countingHashMap = new HashMap<>();
            countingHashMap.put("pendingCount" , pendingCount);
            countingHashMap.put("visitedCount" , visitedCount);
            countingHashMap.put("productiveCount" , productiveCount);
            countingMutable.postValue(countingHashMap);
        });

    }

    public List<OutletOrderStatus> getPendingOutletCount(){
        return repository.getPendingCount();
    }

    public List<OutletOrderStatus> getVisitedOutletCount(){
        return repository.getCompletedCount();
    }
    public List<OutletOrderStatus> getProductiveOutletCount(){
        return repository.getProductiveCount();
    }


    public LiveData<List<OutletOrderStatus>> getOutletOrderStatus(){return outletOrderList;}

    public MutableLiveData<Boolean> isLoading() {
        return isLoading;
    }

    private void onError(Throwable throwable) {
        isLoading.postValue(false);
        errorMsg.setValue(throwable.getMessage());
    }

    public MutableLiveData<HashMap<String, Integer>> getCountingMutable() {
        return countingMutable;
    }
}
