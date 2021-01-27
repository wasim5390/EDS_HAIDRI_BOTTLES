package com.optimus.eds.db.entities;

import androidx.room.Embedded;


public class OutletOrderStatus {
    @Embedded
    public OrderStatus orderStatus;
    @Embedded
    public Outlet outlet;
}