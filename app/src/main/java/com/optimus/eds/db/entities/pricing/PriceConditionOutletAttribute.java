package com.optimus.eds.db.entities.pricing;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PriceConditionOutletAttribute {

    @PrimaryKey(autoGenerate = false)
    public Integer priceConditionOutletAttributeId ;
    public Integer priceConditionId ;
    public Integer channelId ;
    public Integer vpoClassificationId ;
    public Integer outletGroupId;
    public Integer outletGroup2Id;
    public Integer outletGroup3Id ;
    public Integer bundleId ;
    public Integer freeGoodId ;

    public Integer getPriceConditionOutletAttributeId() {
        return priceConditionOutletAttributeId;
    }

    public void setPriceConditionOutletAttributeId(Integer priceConditionOutletAttributeId) {
        this.priceConditionOutletAttributeId = priceConditionOutletAttributeId;
    }

    public Integer getPriceConditionId() {
        return priceConditionId;
    }

    public void setPriceConditionId(Integer priceConditionId) {
        this.priceConditionId = priceConditionId;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getVpoClassificationId() {
        return vpoClassificationId;
    }

    public void setVpoClassificationId(Integer vpoClassificationId) {
        this.vpoClassificationId = vpoClassificationId;
    }

    public Integer getOutletGroupId() {
        return outletGroupId;
    }

    public void setOutletGroupId(Integer outletGroupId) {
        this.outletGroupId = outletGroupId;
    }

    public Integer getOutletGroup2Id() {
        return outletGroup2Id;
    }

    public void setOutletGroup2Id(Integer outletGroup2Id) {
        this.outletGroup2Id = outletGroup2Id;
    }

    public Integer getOutletGroup3Id() {
        return outletGroup3Id;
    }

    public void setOutletGroup3Id(Integer outletGroup3Id) {
        this.outletGroup3Id = outletGroup3Id;
    }

    public Integer getBundleId() {
        return bundleId;
    }

    public void setBundleId(Integer bundleId) {
        this.bundleId = bundleId;
    }

    public Integer getFreeGoodId() {
        return freeGoodId;
    }

    public void setFreeGoodId(Integer freeGoodId) {
        this.freeGoodId = freeGoodId;
    }
}
