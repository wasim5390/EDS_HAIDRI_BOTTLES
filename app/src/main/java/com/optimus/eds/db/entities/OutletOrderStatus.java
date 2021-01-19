package com.optimus.eds.db.entities;

import androidx.room.Embedded;


public class OutletOrderStatus {
    @Embedded
    public OrderStatus orderStatus;
    @Embedded
    public Outlet outlet;
}
//    SELECT Outlet.*, OrderStatus.* FROM Outlet left JOIN OrderStatus ON Outlet.mOutletId = OrderStatus.outletId