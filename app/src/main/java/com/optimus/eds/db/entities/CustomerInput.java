package com.optimus.eds.db.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.annotation.NonNull;


@Entity(primaryKeys = {"outletId","orderId"})
public class CustomerInput {

    @NonNull
    private Long outletId;
    @NonNull
    private Long orderId;

   // private Long requiredDeliveryDate;
    private String mobileNumber;
    private String strn;
    private String remarks;
    private String signature;
    private String cnic;

    public String getStrn() {
        return strn;
    }


    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }
    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public CustomerInput() {
    }

    @Ignore
    public CustomerInput(@NonNull Long outletId,@NonNull Long orderId, String mobileNumber,String cnic, String strn,String remarks, String signature) {
        this.outletId = outletId;
        this.orderId = orderId;
        this.cnic = cnic;
        this.strn = strn;
       // this.requiredDeliveryDate = deliveryDate;
        this.mobileNumber = mobileNumber;
        this.remarks = remarks;
        this.signature = signature;
    }

    public void setStrn(String strn) {
        this.strn = strn;
    }


    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

 /*   public Long getRequiredDeliveryDate() {
        return requiredDeliveryDate;
    }

    public void setRequiredDeliveryDate(Long deliveryDate) {
        this.requiredDeliveryDate = deliveryDate;
    }*/

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }



}
