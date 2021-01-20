package com.optimus.eds.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Promotion {

    @PrimaryKey(autoGenerate = true)
    private Integer promotionId ;

    private Integer outletId ;
    private Integer priceConditionId ;
    private Integer detailId ;
    private String name ;
    private Double amount ;
    private String calculationType ;
    private Integer freeGoodId ;
    private String freeGoodName ;
    private String freeGoodSize ;
    private String size ;
    private String promoOrFreeGoodType ;
    private Integer freeGoodQuantity ;

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
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

    public Integer getDetailId() {
        return detailId;
    }

    public void setDetailId(Integer detailId) {
        this.detailId = detailId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(String calculationType) {
        this.calculationType = calculationType;
    }

    public Integer getFreeGoodId() {
        return freeGoodId;
    }

    public void setFreeGoodId(Integer freeGoodId) {
        this.freeGoodId = freeGoodId;
    }

    public String getFreeGoodName() {
        return freeGoodName;
    }

    public void setFreeGoodName(String freeGoodName) {
        this.freeGoodName = freeGoodName;
    }

    public String getFreeGoodSize() {
        return freeGoodSize;
    }

    public void setFreeGoodSize(String freeGoodSize) {
        this.freeGoodSize = freeGoodSize;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPromoOrFreeGoodType() {
        return promoOrFreeGoodType;
    }

    public void setPromoOrFreeGoodType(String promoOrFreeGoodType) {
        this.promoOrFreeGoodType = promoOrFreeGoodType;
    }

    public Integer getFreeGoodQuantity() {
        return freeGoodQuantity;
    }

    public void setFreeGoodQuantity(Integer freeGoodQuantity) {
        this.freeGoodQuantity = freeGoodQuantity;
    }
}
