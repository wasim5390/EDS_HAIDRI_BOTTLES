package com.optimus.eds.source;

import android.app.Application;
import android.os.AsyncTask;

import com.optimus.eds.db.dao.OrderStatusDao;
import com.optimus.eds.db.entities.OrderStatus;
import com.optimus.eds.ui.order.OrderBookingRepository;

import io.reactivex.Maybe;

public class StatusRepository extends OrderBookingRepository {

    private OrderStatusDao orderStatusDao;
    private static StatusRepository repository;

    public StatusRepository(Application application) {
        super(application);
        orderStatusDao = appDatabase.orderStatusDao();
    }

    public static StatusRepository singleInstance(Application application){
        if(repository==null)
            repository = new StatusRepository(application);
        return repository;
    }

    public Maybe<OrderStatus> findOrderStatus(Long outletId){
        return orderStatusDao.findOutletOrderStatus(outletId);
    }

    public void insertStatus(OrderStatus status){
        AsyncTask.execute(() -> orderStatusDao.insertStatus(status));

    }

    public void updateStatus(OrderStatus status){
        AsyncTask.execute(() -> orderStatusDao.updateStatus(status.getStatus(),status.getOutletId(),status.getSynced(),status.getOrderAmount(),status.getData(),status.getOutletVisitEndTime()));
    }

    public void update(OrderStatus status){
        AsyncTask.execute(() -> orderStatusDao.update(status));
    }

    public void deleteAllStatus(){
        AsyncTask.execute(() -> orderStatusDao.deleteAllStatus());
    }

    public void updateStatusOutletEndTime(Long endTime,Long outletId){
        AsyncTask.execute(() -> orderStatusDao.updateStatusVisitEndTime(endTime,outletId));
    }

    public void updateStatusOutletStartTime(Long startTime,Long outletId){
        AsyncTask.execute(() -> orderStatusDao.updateStatusVisitStartTime(startTime,outletId));
    }
}
