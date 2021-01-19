package com.optimus.eds.db.dao;

import com.optimus.eds.db.entities.OrderStatus;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Maybe;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface OrderStatusDao {

    @Query("SELECT * FROM OrderStatus WHERE outletId=:id")
    Maybe<OrderStatus> findOutletOrderStatus(Long id);

    @Insert(onConflict = REPLACE)
    void insertStatus(OrderStatus status);

    @Query("UPDATE OrderStatus Set status=:status, sync=:sync,orderAmount=:amount,outletVisitEndTime=:endTime, data=:jsonData WHERE outletId=:outletId")
    void updateStatus(Integer status, Long outletId,boolean sync,Double amount,String jsonData,Long endTime);

    @Update
    void update(OrderStatus status);

    @Query("UPDATE OrderStatus Set outletVisitEndTime=:time WHERE outletId=:outletId")
    void updateStatusVisitEndTime(Long time, Long outletId);

    @Query("Update OrderStatus Set outletVisitStartTime=:time WHERE outletId=:outletId")
    void updateStatusVisitStartTime(Long time, Long outletId);

    @Query("DELETE FROM OrderStatus")
    void deleteAllStatus();

}
