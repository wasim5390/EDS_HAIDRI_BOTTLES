package com.optimus.eds.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class OutletAvailedPromotion {

    @PrimaryKey
    private Integer id ;
    private Integer outletId  ;
    private Integer priceConditionId   ;
    private Integer priceConditionDetailId   ;
    private Integer quantity   ;
    private Double amount   ;
    private Integer productId   ;
    private Integer productDefinitionId   ;
    private Integer packageId   ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOutletId() {
        return outletId;
    }

    public void setOutletId(Integer outletId) {
        this.outletId = outletId;
    }

    public Integer getPriceConditionId() {
        return priceConditionId;
    }

    public void setPriceConditionId(Integer priceConditionId) {
        this.priceConditionId = priceConditionId;
    }

    public Integer getPriceConditionDetailId() {
        return priceConditionDetailId;
    }

    public void setPriceConditionDetailId(Integer priceConditionDetailId) {
        this.priceConditionDetailId = priceConditionDetailId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductDefinitionId() {
        return productDefinitionId;
    }

    public void setProductDefinitionId(Integer productDefinitionId) {
        this.productDefinitionId = productDefinitionId;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }
}
