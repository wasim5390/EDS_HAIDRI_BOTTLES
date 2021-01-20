package com.optimus.eds.ui.route.outlet;

import android.app.Application;
import androidx.lifecycle.LiveData;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import com.optimus.eds.Constant;
import com.optimus.eds.db.AppDatabase;

import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.db.entities.OrderStatus;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.OutletOrderStatus;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.ui.order.OrderBookingRepository;

import java.util.List;

public class OutletListRepository  extends OrderBookingRepository {

    private final RouteDao routeDao;

    private static OutletListRepository repository;

    public static OutletListRepository getInstance(Application application){
        if(repository==null)
            repository =  new OutletListRepository(application);
        return repository;
    }

    private OutletListRepository(Application application){
        super(application);
        routeDao = AppDatabase.getDatabase(application).routeDao();
    }



    public Single<List<Outlet>> getOutlets(Long routeId,int outletType){
        //1 for get All planned outlet calls
        return routeDao.findAllOutletsForRoute(routeId,outletType);
    }

    public Flowable<List<Outlet>> getOutletsWithNoVisits(){
        // get All planned outlet calls
        return routeDao.findOutletsWithPendingTasks(1);
    }

    public Single<List<OrderStatus>> getOrderStatus(){
        // get All planned outlet calls
        return routeDao.findPendingOrderToSync(false);
    }

    public Single<List<Outlet>> getUnsyncedOutlets(List<Long> outlets){
        // get All planned outlet calls
        return routeDao.findOutletsWithPendingOrderToSync(outlets);
    }

    public LiveData<List<Route>> getRoutes(){
        return routeDao.findAllRoutes();
    }

    public int getPjpCount() {
        return routeDao.getPjpCount();
    }

    public List<OutletOrderStatus> getCompletedCount() {
        return routeDao.getVisitedOutletCount();
    }
    public List<OutletOrderStatus> getProductiveCount() {
        return routeDao.getProductiveOutletCount();
    }
    public List<OutletOrderStatus> getPendingCount() {
        return routeDao.getPendingOutletCount();
    }

    public Observable<List<OutletOrderStatus>> getProductiveOutlets() {
        return routeDao.getProductiveOutlets();
    }

    public Observable<List<OutletOrderStatus>> getVisitedOutlets() {
        return routeDao.getVisitedOutlets();
    }

    public Observable<List<OutletOrderStatus>> getPendingOutlets() {
        return routeDao.getPendingOutlets();
    }


}
