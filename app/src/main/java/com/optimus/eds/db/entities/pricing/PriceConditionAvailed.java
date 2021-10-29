package com.optimus.eds.db.entities.pricing;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PriceConditionAvailed {

    @PrimaryKey @NonNull
    private Integer priceConditionAvailedId;
    @NonNull
    private int outletId;
    @NonNull
    private int productDefinitionId;
    @NonNull
    private int productId;
    private Double amount;
    private Integer quantity;

    public Integer getPriceConditionAvailedId() {
        return priceConditionAvailedId;
    }

    public void setPriceConditionAvailedId(Integer priceConditionAvailedId) {
        this.priceConditionAvailedId = priceConditionAvailedId;
    }

    public int getOutletId() {
        return outletId;
    }

    public void setOutletId(int outletId) {
        this.outletId = outletId;
    }

    public int getProductDefinitionId() {
        return productDefinitionId;
    }

    public void setProductDefinitionId(int productDefinitionId) {
        this.productDefinitionId = productDefinitionId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Double getAmount() {
        return amount==null?0:amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int getQuantity() {
        return quantity==null?0:quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


}
