package com.optimus.eds.db.entities.pricing;

import java.util.List;

public class GroupFreeGoodDetails {
    public Integer id ;
    public Integer freeGoodGroupId ;
    public Integer productId ;
    public String productName;
    public Integer productDefinitionId ;
    public String productCode ;
    public String productSize ;
    public Integer freeGoodQuantity ;
    public Integer minimumQuantity;
    public Integer maximumQuantity ;
    public Boolean isActive ;
    public Boolean isDeleted ;
    public List<KeyValue> productDefinitions ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public Integer getFreeGoodQuantity() {
        return freeGoodQuantity;
    }

    public void setFreeGoodQuantity(Integer freeGoodQuantity) {
        this.freeGoodQuantity = freeGoodQuantity;
    }

    public Integer getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public Integer getMaximumQuantity() {
        return maximumQuantity;
    }

    public void setMaximumQuantity(Integer maximumQuantity) {
        this.maximumQuantity = maximumQuantity;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public List<KeyValue> getProductDefinitions() {
        return productDefinitions;
    }

    public void setProductDefinitions(List<KeyValue> productDefinitions) {
        this.productDefinitions = productDefinitions;
    }
}
