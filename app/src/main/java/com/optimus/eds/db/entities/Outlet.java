
package com.optimus.eds.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity(tableName = "Outlet")
public class Outlet implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "mOutletId")
    @SerializedName("outletId")
    private Long mOutletId;
    @SerializedName("routeId")
    private Long mRouteId;
    @SerializedName("outletCode")
    private String mOutletCode;
    @SerializedName("outletName")
    private String mOutletName;
    @SerializedName("channelName")
    private String mChannelName;
    @SerializedName("location")
    private String mLocation;
    @SerializedName("visitFrequency")
    private Integer mVisitFrequency;
    @SerializedName("visitDay")
    private Integer mVisitDay;
    @SerializedName("planned")
    private Integer planned;

    @SerializedName("sequenceNumber")
    private Integer sequenceNumber;
    @SerializedName("address")
    private String mAddress;
    @SerializedName("latitude")
    private Double mLatitude;
    @SerializedName("longitude")
    private Double mLongitude;
    private Double visitTimeLat;
    private Double visitTimeLng;

    @SerializedName("lastSaleDate")
    private Long mLastSaleDate;
    @SerializedName("lastSaleQuantity")
    private String mLastSaleQuantity;
    @SerializedName("availableCreditLimit")
    private Double mAvailableCreditLimit;
    @SerializedName("outstandingCreditLimit")
    private Double mOutstandingCredit;
    @SerializedName("lastSale")
    private Double mLastSale;
    @SerializedName("visitStatus")
    private Integer mVisitStatus; // [1-8] Order status for Outlet
    @SerializedName("cnic")
    private String cnic;
    @SerializedName("strn")
    private String strn;

    @SerializedName("mobileNumber")
    private String mobileNumber;

    private Boolean isAssetsScennedInTheLastMonth;

    private Boolean synced;
    public Boolean getSynced() {
        return synced;
    }

    public void setSynced(Boolean synced) {
        this.synced = synced;
    }

    public String getStrn() {
        return strn==null?"":strn;
    }

    public void setStrn(String strn) {
        this.strn = strn;
    }

    public String getCnic() {
        return cnic==null?"":cnic;
    }

    public void setCnic(String mCnic) {
        this.cnic = mCnic;
    }

    public String getMobileNumber() {
         return mobileNumber==null?"":mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }


    @Ignore
    private Double mTotalAmount;

    public Long getRouteId() {
        return mRouteId;
    }

    public void setRouteId(Long mRouteId) {
        this.mRouteId = mRouteId;
    }
    public void setOutletId(Long mOutletId) {
        this.mOutletId = mOutletId;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public void setAvailableCreditLimit(Double mAvailableCreditLimit) {
        this.mAvailableCreditLimit = mAvailableCreditLimit;
    }

    public void setLastSale(Double mLastSale) {
        this.mLastSale = mLastSale;
    }

    public void setChannelName(String mChannelName) {
        this.mChannelName = mChannelName;
    }

    public void setLastSaleDate(Long mLastSaleDate) {
        this.mLastSaleDate = mLastSaleDate;
    }

    public void setLastSaleQuantity(String mLastSaleQuantity) {
        this.mLastSaleQuantity = mLastSaleQuantity;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setLatitude(Double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public void setLongitude(Double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public void setOutletCode(String mOutletCode) {
        this.mOutletCode = mOutletCode;
    }

    public void setOutletName(String mOutletName) {
        this.mOutletName = mOutletName;
    }

    public void setOutstandingCredit(Double mOutstandingCredit) {
        this.mOutstandingCredit = mOutstandingCredit;
    }

    public void setTotalAmount(Double mTotalAmount) {
        this.mTotalAmount = mTotalAmount;
    }

    public void setVisitDay(Integer mVisitDay) {
        this.mVisitDay = mVisitDay;
    }

    public void setVisitFrequency(Integer mVisitFrequency) {
        this.mVisitFrequency = mVisitFrequency;
    }

    public void setVisitStatus(Integer mVisitStatus) {
        this.mVisitStatus = mVisitStatus;
    }

    public String getAddress() {
        return mAddress;
    }


    public Double getAvailableCreditLimit() {
        return mAvailableCreditLimit;
    }

    public String getChannelName() {
        return mChannelName;
    }

    public Long getLastSaleDate() {
        return mLastSaleDate;
    }

    public String getLastSaleQuantity() {
        return mLastSaleQuantity;
    }

    public Double getLastSale() {
        return mLastSale;
    }

    public String getLastSaleString() {
        return mLastSale==null?"":String.valueOf(mLastSale);
    }


    public Double getLatitude() {
        return mLatitude;
    }

    public String getLocation() {
        return mLocation;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public String getOutletCode() {
        return mOutletCode;
    }

    public Long getOutletId() {
        return mOutletId;
    }

    public String getOutletName() {
        return mOutletName;
    }

    public Double getOutstandingCredit() {
        return mOutstandingCredit;
    }

    public Double getTotalAmount() {
        return mTotalAmount==null?0.0:mTotalAmount;
    }

    public Integer getVisitDay() {
        return mVisitDay;
    }

    public Integer getVisitFrequency() {
        return mVisitFrequency;
    }

    public Integer getVisitStatus() {
        return mVisitStatus==null?0:mVisitStatus;
    }

    public Double getVisitTimeLat() {
        return visitTimeLat;
    }

    public void setVisitTimeLat(Double visitTimeLat) {
        this.visitTimeLat = visitTimeLat;
    }

    public Double getVisitTimeLng() {
        return visitTimeLng;
    }

    public void setVisitTimeLng(Double visitTimeLng) {
        this.visitTimeLng = visitTimeLng;
    }
    public Integer getPlanned() {
        return planned;
    }

    public void setPlanned(Integer planned) {
        this.planned = planned;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber==null?0:sequenceNumber;
    }
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Outlet && ((Outlet) obj).getOutletId() == getOutletId();
    }

    public Boolean getAssetsScennedInTheLastMonth() {
        return isAssetsScennedInTheLastMonth;
    }

    public void setAssetsScennedInTheLastMonth(Boolean assetsScennedInTheLastMonth) {
        isAssetsScennedInTheLastMonth = assetsScennedInTheLastMonth;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
