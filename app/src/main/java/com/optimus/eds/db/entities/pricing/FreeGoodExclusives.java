package com.optimus.eds.db.entities.pricing;

import com.optimus.eds.db.converters.KeyValueConverter;

import java.util.List;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class FreeGoodExclusives {
    @PrimaryKey(autoGenerate = false)
    public Integer freeGoodExclusiveId;
    public Integer freeGoodGroupId ;
    public Integer productId ;
    public String productCode ;
    public String productName ;
    public Integer productDefinitionId ;
    public Integer quantity ;
    public Integer maximumFreeGoodQuantity ;
    //public int OfferTypeId { get; set; }
    public String offerType ;
    public String status; //New, Edit/Existing, Delete
    @TypeConverters(KeyValueConverter.class)
    public List<KeyValue> productDefinitions ;
    public Boolean isDeleted ;

    public Integer getFreeGoodExclusiveId() {
        return freeGoodExclusiveId;
    }

    public void setFreeGoodExclusiveId(Integer freeGoodExclusiveId) {
        this.freeGoodExclusiveId = freeGoodExclusiveId;
    }

    public Integer getFreeGoodGroupId() {
        return freeGoodGroupId;
    }

    public void setFreeGoodGroupId(Integer freeGoodGroupId) {
        this.freeGoodGroupId = freeGoodGroupId;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getMaximumFreeGoodQuantity() {
        return maximumFreeGoodQuantity;
    }

    public void setMaximumFreeGoodQuantity(Integer maximumFreeGoodQuantity) {
        this.maximumFreeGoodQuantity = maximumFreeGoodQuantity;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<KeyValue> getProductDefinitions() {
        return productDefinitions;
    }

    public void setProductDefinitions(List<KeyValue> productDefinitions) {
        this.productDefinitions = productDefinitions;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
