package com.optimus.eds.db.entities.pricing;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.converters.FreeGoodExclusivesConverter;

import java.util.List;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class FreeGoodDetails {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("freeGoodDetailId")
    @Expose
    private Integer freeGoodDetailId;
    @SerializedName("freeGoodMasterId")
    @Expose
    private Integer freeGoodMasterId;
    @SerializedName("productId")
    @Expose
    private Integer productId;
    @SerializedName("productCode")
    @Expose
    private String productCode;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("productDefinitionId")
    @Expose
    private Integer productDefinitionId;
    @SerializedName("typeId")
    @Expose
    private Integer typeId;
    @SerializedName("typeText")
    @Expose
    private String typeText;
    @SerializedName("minimimQuantity")
    @Expose
    private Integer minimimQuantity;
    @SerializedName("forEachQuantity")
    @Expose
    private Integer forEachQuantity;
    @SerializedName("freeGoodQuantity")
    @Expose
    private Integer freeGoodQuantity;
    @SerializedName("freeGoodGroupId")
    @Expose
    private Integer freeGoodGroupId;
    @SerializedName("maximumFreeGoodQuantity")
    @Expose
    private Integer maximumFreeGoodQuantity;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("endDate")
    @Expose
    private String endDate;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("isDifferentProduct")
    @Expose
    private Boolean isDifferentProduct;

    @TypeConverters(FreeGoodExclusivesConverter.class)
    @SerializedName("freeGoodExclusives")
    @Expose
    private List<FreeGoodExclusives> freeGoodExclusives;

    public Integer getFreeGoodDetailId() {
        return freeGoodDetailId;
    }

    public void setFreeGoodDetailId(Integer freeGoodDetailId) {
        this.freeGoodDetailId = freeGoodDetailId;
    }

    public Integer getFreeGoodMasterId() {
        return freeGoodMasterId;
    }

    public void setFreeGoodMasterId(Integer freeGoodMasterId) {
        this.freeGoodMasterId = freeGoodMasterId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getProductDefinitionId() {
        return productDefinitionId;
    }

    public void setProductDefinitionId(Integer productDefinitionId) {
        this.productDefinitionId = productDefinitionId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeText() {
        return typeText;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    public Integer getMinimimQuantity() {
        return minimimQuantity;
    }

    public void setMinimimQuantity(Integer minimimQuantity) {
        this.minimimQuantity = minimimQuantity;
    }

    public Integer getForEachQuantity() {
        return forEachQuantity;
    }

    public void setForEachQuantity(Integer forEachQuantity) {
        this.forEachQuantity = forEachQuantity;
    }

    public Integer getFreeGoodQuantity() {
        return freeGoodQuantity;
    }

    public void setFreeGoodQuantity(Integer freeGoodQuantity) {
        this.freeGoodQuantity = freeGoodQuantity;
    }

    public Integer getMaximumFreeGoodQuantity() {
        return maximumFreeGoodQuantity;
    }

    public void setMaximumFreeGoodQuantity(Integer maximumFreeGoodQuantity) {
        this.maximumFreeGoodQuantity = maximumFreeGoodQuantity;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsDifferentProduct() {
        return isDifferentProduct;
    }

    public void setIsDifferentProduct(Boolean isDifferentProduct) {
        this.isDifferentProduct = isDifferentProduct;
    }

    public List<FreeGoodExclusives> getFreeGoodExclusives() {
        return freeGoodExclusives;
    }

    public void setFreeGoodExclusives(List<FreeGoodExclusives> freeGoodExclusives) {
        this.freeGoodExclusives = freeGoodExclusives;
    }

    public Integer getFreeGoodGroupId() {
        return freeGoodGroupId;
    }

    public void setFreeGoodGroupId(Integer freeGoodGroupId) {
        this.freeGoodGroupId = freeGoodGroupId;
    }
}
