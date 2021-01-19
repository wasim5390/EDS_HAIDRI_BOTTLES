package com.optimus.eds.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Transaction;
import androidx.room.Update;

import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.UnitPriceBreakDown;
import com.optimus.eds.model.OrderModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface OrderDao {

    @Query("SELECT * FROM `Order` WHERE oid=:id")
    Order findOrderByOrderId(Long id);

    @Query("SELECT * FROM `Order` WHERE pk_oid=:id")
    Maybe<Order> findOrderById(Long id);

    @Query("SELECT * FROM OrderDetail where fk_oid=:orderId")
    Single<List<OrderDetail>> findOrderItemsByOrderId(Long orderId);


    @Query("SELECT * FROM 'Order' WHERE c_outletId IN (SELECT mOutletId From Outlet where mVisitStatus in (7,8))")
    @Transaction
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    Single<List<OrderModel>> findAllOrders();

    @Query("SELECT * FROM `Order`,Outlet" +
            " where c_outletId=:outletId AND mOutletId=:outletId")
    @Transaction
    Maybe<OrderModel> getOrderWithItems(long outletId);

    @Query("SELECT * FROM `Order` where orderStatus=:orderStatus")
    @Transaction
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    Flowable<List<OrderModel>> getPendingOrders(int orderStatus);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM `Order` where orderStatus=:orderStatus")
    @Transaction
    Single<List<OrderModel>> getPendingOrdersToUpload(int orderStatus);

    @Insert(onConflict = REPLACE)
    void insertOrder(Order order);

    @Insert(onConflict = REPLACE)
    void insertOrderItems(List<OrderDetail> orderItems);

    @Insert(onConflict = REPLACE)
    void insertOrderItem(OrderDetail orderItem);

    @Update
    void updateOrder(Order order);

    @Update
    void updateOrderItems(List<OrderDetail> orderItems);

    @Update
    void updateOrderItem(OrderDetail orderItem);

    @Insert(onConflict = REPLACE)
    void insertCartonPriceBreakDown(List<CartonPriceBreakDown> priceBreakDowns);

    @Insert(onConflict = REPLACE)
    void insertUnitPriceBreakDown(List<UnitPriceBreakDown> priceBreakDowns);

    @Query("Delete From 'Order'")
    void deleteAllOrders();

    @Query("Delete From 'OrderDetail' WHERE fk_oid=:orderId AND mProductGroupId=:groupId")
    void deleteOrderItemsByGroup(Long orderId,Long groupId);

    @Query("Delete From 'OrderDetail' WHERE fk_oid=:orderId")
    void deleteOrderItems(Long orderId);


}
