
package com.optimus.eds.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.UnitPriceBreakDown;

public class OrderResponseModel extends BaseResponse{

    @Expose
    private String code;
    @Expose
    private Long deliveryDate;
    @Expose
    private Integer distributionId;
    @Expose
    private Double latitude;
    @Expose
    private Double longitude;
    @Expose
    private Long mobileOrderId;
    @Expose
    private Long orderDate;
    @Expose
    private List<OrderDetail> orderDetails;

    @Expose
    private List<UnitPriceBreakDown> priceBreakDown; // invoice level breakdown
    @Expose
    private Long orderId;
    @Expose
    private Integer orderStatusId;
    @Expose
    private String orderStatusText;
    @Expose
    private Integer outletId;
    @Expose
    private Double payable;
    @Expose
    private Integer routeId;
    @Expose
    private Long salesmanId;
    @Expose
    private Double subtotal;

    @Expose
    private Long visitDayId;

    @Expose
    private Integer outletStatus;

    @Expose
    private Long startedDate;




    public void setOutletStatus(Integer outletStatus) {
        this.outletStatus = outletStatus;
    }

    public Integer getOutletStatus() {
        return outletStatus;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Long deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Integer getDistributionId() {
        return distributionId;
    }

    public void setDistributionId(Integer distributionId) {
        this.distributionId = distributionId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getMobileOrderId() {
        return mobileOrderId;
    }

    public void setMobileOrderId(Long mobileOrderId) {
        this.mobileOrderId = mobileOrderId;
    }

    public Long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Long orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(Integer orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public String getOrderStatusText() {
        return orderStatusText;
    }

    public void setOrderStatusText(String orderStatusText) {
        this.orderStatusText = orderStatusText;
    }

    public Integer getOutletId() {
        return outletId;
    }

    public void setOutletId(Integer outletId) {
        this.outletId = outletId;
    }

    public Double getPayable() {
        return payable;
    }

    public void setPayable(Double payable) {
        this.payable = payable;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }
    public void setPriceBreakDown(List<UnitPriceBreakDown> priceBreakDown) {
        this.priceBreakDown = priceBreakDown;
    }
    public Long getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(Long salesmanId) {
        this.salesmanId = salesmanId;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Long getVisitDayId() {
        return visitDayId;
    }

    public void setVisitDayId(Long visitDayId) {
        this.visitDayId = visitDayId;
    }

    public List<UnitPriceBreakDown> getPriceBreakDown() {
        return priceBreakDown;
    }

    public void setStartedDate(Long startedDate) {
        this.startedDate = startedDate;
    }

    public Long getStartedDate() {
        return startedDate;
    }

}
