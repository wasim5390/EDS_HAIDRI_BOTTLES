package com.optimus.eds.model;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.UnitPriceBreakDown;

import java.io.Serializable;
import java.util.List;

public class OrderDetailAndPriceBreakdown {

    @Embedded
   public OrderDetail orderDetail;

    @Relation(parentColumn = "pk_modid", entityColumn = "fk_modid", entity = CartonPriceBreakDown.class)
   public List<CartonPriceBreakDown> cartonPriceBreakDownList;


    @Relation(parentColumn = "pk_modid", entityColumn = "fk_modid", entity = UnitPriceBreakDown.class)
   public List<UnitPriceBreakDown> unitPriceBreakDownList;

    @Ignore
    public OrderDetailAndPriceBreakdown(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    public OrderDetailAndPriceBreakdown() {
    }


    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    public List<CartonPriceBreakDown> getCartonPriceBreakDownList() {
        return cartonPriceBreakDownList;
    }

    public List<UnitPriceBreakDown> getUnitPriceBreakDownList() {
        return unitPriceBreakDownList;
    }
}
