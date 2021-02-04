package com.optimus.eds.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastOrder {

    @SerializedName("routeId")
    @Expose
    private Integer routeId;
    @SerializedName("outletId")
    @Expose
    private Integer outletId;
    @SerializedName("orderId")
    @Expose
    private Integer orderId;
    @SerializedName("orderTotal")
    @Expose
    private Double orderTotal;
    @SerializedName("orderQuantity")
    @Expose
    private Double orderQuantity;
    @SerializedName("orderDetails")
    @Expose
    private List<OrderDetail> orderDetails = null;

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public Integer getOutletId() {
        return outletId;
    }

    public void setOutletId(Integer outletId) {
        this.outletId = outletId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Double getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(Double orderQuantity) {
        this.orderQuantity = orderQuantity;
    }
}
