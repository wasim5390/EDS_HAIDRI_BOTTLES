
package com.optimus.eds.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity
public class Asset implements Serializable {

    @SerializedName("assetId")
    @PrimaryKey
    private Long mAssetId;

    @SerializedName("outletId")
    private Long outletId;

    @SerializedName("organizationId")
    private Long organizationId;
    @SerializedName("assetTypeMainId")
    private Long assetTypeMainId;

    @SerializedName("longitude")
    private Double longitude;
    @SerializedName("lattitude")
    private Double lattitude;

    @SerializedName("assetModel")
    private String mAssetModel;
    @SerializedName("assetModelId")
    private Integer mAssetModelId;
    @SerializedName("assetName")
    private String mAssetName;
    @SerializedName("assetNumber")
    private String mAssetNumber;
    @SerializedName("assetType")
    private String mAssetType;
    @SerializedName("assetTypeId")
    private Integer mAssetTypeId;
    @SerializedName("assignedDate")
    private Long mAssignedDate;
    @SerializedName("assignmentCode")
    private String mAssignmentCode;
    @SerializedName("cost")
    private Double mCost;
    @SerializedName("deposit")
    private Double mDeposit;
    @SerializedName("documentNumber")
    private String mDocumentNumber;
    @SerializedName("statusid ")
    private Integer statusid ;

    public void setAssetModel(String mAssetModel) {
        this.mAssetModel = mAssetModel;
    }


    public void setAssetName(String mAssetName) {
        this.mAssetName = mAssetName;
    }

    public void setAssetNumber(String mAssetNumber) {
        this.mAssetNumber = mAssetNumber;
    }

    public void setAssetType(String mAssetType) {
        this.mAssetType = mAssetType;
    }


    public void setAssignedDate(Long mAssignedDate) {
        this.mAssignedDate = mAssignedDate;
    }

    public void setAssignmentCode(String mAssignmentCode) {
        this.mAssignmentCode = mAssignmentCode;
    }

    public void setCost(Double mCost) {
        this.mCost = mCost;
    }

    public void setDeposit(Double mDeposit) {
        this.mDeposit = mDeposit;
    }

    public void setDocumentNumber(String mDocumentNumber) {
        this.mDocumentNumber = mDocumentNumber;
    }

    public void setExpiryDate(Long mExpiryDate) {
        this.mExpiryDate = mExpiryDate;
    }

    public void setReturnDate(Long mReturnDate) {
        this.mReturnDate = mReturnDate;
    }

    public void setSerialNumber(String mSerialNumber) {
        this.mSerialNumber = mSerialNumber;
    }

    public void setTransactionType(String mTransactionType) {
        this.mTransactionType = mTransactionType;
    }

    @SerializedName("expiryDate")
    private Long mExpiryDate;
    @SerializedName("returnDate")
    private Long mReturnDate;
    @SerializedName("barcode")
    private String mSerialNumber;
    @SerializedName("TransactionType")
    private String mTransactionType;


    public String getReason() {
        return reason==null?"":reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    @SerializedName("reason")
    private String reason;
    @SerializedName("verified")
    private Boolean verified;

    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }
    public Long getAssetId() {
        return mAssetId;
    }


    public Integer getAssetTypeId() {
        return mAssetTypeId;
    }
    public Integer getAssetModelId() {
        return mAssetModelId;
    }

    public void setAssetModelId(Integer mAssetModelId) {
        this.mAssetModelId = mAssetModelId;
    }
    public void setAssetTypeId(Integer mAssetTypeId) {
        this.mAssetTypeId = mAssetTypeId;
    }

    public String getAssetModel() {
        return mAssetModel;
    }

    public String getAssetName() {
        return mAssetName;
    }

    public String getAssetNumber() {
        return mAssetNumber;
    }

    public String getAssetType() {
        return mAssetType;
    }

    public Long getAssignedDate() {
        return mAssignedDate;
    }

    public String getAssignmentCode() {
        return mAssignmentCode;
    }
    public void setAssetId(Long mAssetId) {
        this.mAssetId = mAssetId;
    }
    public Double getCost() {
        return mCost;
    }

    public Double getDeposit() {
        return mDeposit;
    }

    public String getDocumentNumber() {
        return mDocumentNumber;
    }

    public Long getExpiryDate() {
        return mExpiryDate;
    }

    public Long getReturnDate() {
        return mReturnDate;
    }

    public String getSerialNumber() {
        return mSerialNumber==null?"":mSerialNumber;
    }

    public String getTransactionType() {
        return mTransactionType;
    }
    public Boolean getVerified() {
        return verified==null?false:verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Long getmAssetId() {
        return mAssetId;
    }

    public void setmAssetId(Long mAssetId) {
        this.mAssetId = mAssetId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getAssetTypeMainId() {
        return assetTypeMainId;
    }

    public void setAssetTypeMainId(Long assetTypeMainId) {
        this.assetTypeMainId = assetTypeMainId;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLattitude() {
        return lattitude;
    }

    public void setLattitude(Double lattitude) {
        this.lattitude = lattitude;
    }

    public String getmAssetModel() {
        return mAssetModel;
    }

    public void setmAssetModel(String mAssetModel) {
        this.mAssetModel = mAssetModel;
    }

    public Integer getmAssetModelId() {
        return mAssetModelId;
    }

    public void setmAssetModelId(Integer mAssetModelId) {
        this.mAssetModelId = mAssetModelId;
    }

    public String getmAssetName() {
        return mAssetName;
    }

    public void setmAssetName(String mAssetName) {
        this.mAssetName = mAssetName;
    }

    public String getmAssetNumber() {
        return mAssetNumber;
    }

    public void setmAssetNumber(String mAssetNumber) {
        this.mAssetNumber = mAssetNumber;
    }

    public String getmAssetType() {
        return mAssetType;
    }

    public void setmAssetType(String mAssetType) {
        this.mAssetType = mAssetType;
    }

    public Integer getmAssetTypeId() {
        return mAssetTypeId;
    }

    public void setmAssetTypeId(Integer mAssetTypeId) {
        this.mAssetTypeId = mAssetTypeId;
    }

    public Long getmAssignedDate() {
        return mAssignedDate;
    }

    public void setmAssignedDate(Long mAssignedDate) {
        this.mAssignedDate = mAssignedDate;
    }

    public String getmAssignmentCode() {
        return mAssignmentCode;
    }

    public void setmAssignmentCode(String mAssignmentCode) {
        this.mAssignmentCode = mAssignmentCode;
    }

    public Double getmCost() {
        return mCost;
    }

    public void setmCost(Double mCost) {
        this.mCost = mCost;
    }

    public Double getmDeposit() {
        return mDeposit;
    }

    public void setmDeposit(Double mDeposit) {
        this.mDeposit = mDeposit;
    }

    public String getmDocumentNumber() {
        return mDocumentNumber;
    }

    public void setmDocumentNumber(String mDocumentNumber) {
        this.mDocumentNumber = mDocumentNumber;
    }

    public Long getmExpiryDate() {
        return mExpiryDate;
    }

    public void setmExpiryDate(Long mExpiryDate) {
        this.mExpiryDate = mExpiryDate;
    }

    public Long getmReturnDate() {
        return mReturnDate;
    }

    public void setmReturnDate(Long mReturnDate) {
        this.mReturnDate = mReturnDate;
    }

    public String getmSerialNumber() {
        return mSerialNumber;
    }

    public void setmSerialNumber(String mSerialNumber) {
        this.mSerialNumber = mSerialNumber;
    }

    public String getmTransactionType() {
        return mTransactionType;
    }

    public void setmTransactionType(String mTransactionType) {
        this.mTransactionType = mTransactionType;
    }

    public Integer getStatusid() {
        return statusid;
    }

    public void setStatusid(Integer statusid) {
        this.statusid = statusid;
    }
}
