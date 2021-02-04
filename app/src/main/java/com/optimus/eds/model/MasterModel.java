package com.optimus.eds.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.entities.CustomerInput;
import com.optimus.eds.db.entities.Merchandise;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.utils.Util;

import androidx.room.Ignore;

public class MasterModel extends BaseResponse {

    @Expose
    private Long outletId;
    @Expose
    private Integer outletStatus;

    @Expose
    private String reason;

    @Expose
    public Long outletVisitTime; // when user opens outlet

    @Expose
    public Long outletEndTime; // when user completes order for that outlet

    public String outletOpenTime;

    public String outletCloseTime;

    @Expose
    public Double latitude;

    @Expose
    public Double longitude;

    @Expose
    private CustomerInput customerInput;
    @Expose
    private OrderResponseModel order;

    private Long startedDate;

    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public Integer getOutletStatus() {
        return outletStatus;
    }

    public void setOutletStatus(Integer outletStatus) {
        this.outletStatus = outletStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public OrderResponseModel getOrderModel() {
        return order;
    }

    public void setOrderModel(OrderResponseModel responseModel) {
        this.order = responseModel;
    }

    public void setCustomerInput(CustomerInput customerInput) {
        this.customerInput = customerInput;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Long getOutletVisitTime() {
        return outletVisitTime;
    }

    public void setOutletVisitTime(Long outletVisitTime) {
        this.outletVisitTime = outletVisitTime;
        this.outletOpenTime = Util.formatDate("HH:mm" , outletVisitTime);
    }

    public Long getOutletEndTime() {
        return outletEndTime;
    }

    public void setOutletEndTime(Long outletEndTime) {
        this.outletEndTime = outletEndTime;
        this.outletCloseTime = Util.formatDate("HH:mm" , outletEndTime);
    }



    public Double getLongitude() {
        return longitude;
    }



    public void setLocation(Double latitude,Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public void setStartedDate(Long startedDate) {
        this.startedDate = startedDate;
    }

    public Long getStartedDate() {
        return startedDate;
    }
}
