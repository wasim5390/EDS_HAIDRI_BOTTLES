package com.optimus.eds.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Outlet.class,
                        parentColumns = "mOutletId",
                        childColumns = "outletId",onDelete = ForeignKey.CASCADE),
        }, indices = { @Index(value = "outletId")
})
public class OrderStatus {
    @NonNull
    @PrimaryKey
    private long outletId;

    private Long orderId;

    private Long outletVisitEndTime;
    private Long outletVisitStartTime;
    private int status;
    private Double orderAmount;
    @ColumnInfo(name = "sync")
    private Boolean synced;
    private String data;

    public OrderStatus() {
    }


    @Ignore
    public OrderStatus(long outletId, int status, boolean synced,Double orderAmount) {
        this.outletId = outletId;
        this.status = status;
        this.synced = synced;
        this.orderAmount = orderAmount;
    }

    public String getData() {
        return data;
    }

    public void setData(String workWithData) {
        this.data = workWithData;
    }


    public Boolean getSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getOutletId() {
        return outletId;
    }

    public void setOutletId(long outletId) {
        this.outletId = outletId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Double getOrderAmount() {
        return orderAmount != null ? orderAmount : 0;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Long getOutletVisitStartTime() {
        return outletVisitStartTime;
    }

    public void setOutletVisitStartTime(Long outletVisitStartTime) {
        this.outletVisitStartTime = outletVisitStartTime;
    }

    public Long getOutletVisitEndTime() {
        return outletVisitEndTime;
    }

    public void setOutletVisitEndTime(Long outletVisitEndTime) {
        this.outletVisitEndTime = outletVisitEndTime;
    }
}
