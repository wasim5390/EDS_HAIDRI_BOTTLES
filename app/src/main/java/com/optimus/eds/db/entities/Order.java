    package com.optimus.eds.db.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.converters.PriceBreakdownConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity (foreignKeys =
@ForeignKey(
        entity = Outlet.class,
        parentColumns = "mOutletId",
        childColumns = "c_outletId", onDelete = ForeignKey.CASCADE), indices = {@Index(value = "c_outletId")})

public class Order implements Serializable {

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "pk_oid")
    @SerializedName("mobileOrderId")
    public Long id;
    @SerializedName("orderId")
    public Long serverOrderId;

    @SerializedName("outletId")
    @ColumnInfo(name = "c_outletId")
    public Long outletId;
    @SerializedName("routeId")
    public Long routeId;

    @SerializedName("code")
    public String code;

    @SerializedName("orderStatusId")
    public Integer orderStatus; // {1. Confirmed, 2. Created, 3. Cancelled}

    @SerializedName("visitDayId")
    public int visitDayId;

    @SerializedName("latitude")
    public Double latitude;
    @SerializedName("longitude")
    public Double longitude;

    @SerializedName("subtotal")
    public Double subTotal;
    @SerializedName("payable")
    public Double payable;

    @SerializedName("orderDate")
    public Long orderDate;
    @SerializedName("deliveryDate")
    public Long deliveryDate;

    @SerializedName("distributionId")
    private Long distributionId;

    @SerializedName("priceBreakDown")
    @TypeConverters(PriceBreakdownConverter.class)
    public List<UnitPriceBreakDown> priceBreakDown; // invoice level breakdown


    public Long getDistributionId() {
        return distributionId;
    }

    public void setDistributionId(Long distributionId) {
        this.distributionId = distributionId;
    }

    public List<UnitPriceBreakDown> getPriceBreakDown() {
        return priceBreakDown==null?new ArrayList<>():priceBreakDown;
    }

    public void setPriceBreakDown(List<UnitPriceBreakDown> priceBreakDown) {
        this.priceBreakDown = priceBreakDown;
    }

    public Order(Long outletId) {
        this.outletId = outletId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }
    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public Long getLocalOrderId() {
        return id;
    }

    public void setLocalOrderId(Long mOrderId) {
        this.id = mOrderId;
    }

    public Long getOrderId() {
        return serverOrderId;
    }

    public void setOrderId(Long serverOrderId) {
        this.serverOrderId = serverOrderId;
    }

    public int getOrderStatus() {
        return orderStatus==null?0:orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getVisitDayId() {
        return visitDayId;
    }

    public void setVisitDayId(int visitDayId) {
        this.visitDayId = visitDayId;
    }
    public Double getLatitude() {
        return latitude==null?0:latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude==null?0:longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Double getPayable() {
        return payable==null?0.0:payable;
    }

    public void setPayable(Double payable) {
        this.payable = payable;
    }


    public Long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Long orderDate) {
        this.orderDate = orderDate;
    }

    public Long getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Long deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
