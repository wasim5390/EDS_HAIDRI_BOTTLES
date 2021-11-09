package com.optimus.eds.db.entities.pricing;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FreeGoodEntityDetails {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("freeGoodEntityDetailId")
    @Expose
    private Integer freeGoodEntityDetailId;
    @SerializedName("freeGoodMasterId")
    @Expose
    private Integer freeGoodMasterId;
    @SerializedName("outletId")
    @Expose
    private Integer outletId;
    @SerializedName("routeId")
    @Expose
    private Integer routeId;
    @SerializedName("distributionId")
    @Expose
    private Integer distributionId;
    @SerializedName("channelId")
    @Expose
    private Integer channelId;
    @SerializedName("entityCode")
    @Expose
    private String entityCode;
    @SerializedName("entityText")
    @Expose
    private String entityText;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("channel")
    @Expose
    private String channel;

    public Integer getFreeGoodEntityDetailId() {
        return freeGoodEntityDetailId;
    }

    public void setFreeGoodEntityDetailId(Integer freeGoodEntityDetailId) {
        this.freeGoodEntityDetailId = freeGoodEntityDetailId;
    }

    public Integer getFreeGoodMasterId() {
        return freeGoodMasterId;
    }

    public void setFreeGoodMasterId(Integer freeGoodMasterId) {
        this.freeGoodMasterId = freeGoodMasterId;
    }

    public Integer getOutletId() {
        return outletId;
    }

    public void setOutletId(Integer outletId) {
        this.outletId = outletId;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public Integer getDistributionId() {
        return distributionId;
    }

    public void setDistributionId(Integer distributionId) {
        this.distributionId = distributionId;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public String getEntityText() {
        return entityText;
    }

    public void setEntityText(String entityText) {
        this.entityText = entityText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
