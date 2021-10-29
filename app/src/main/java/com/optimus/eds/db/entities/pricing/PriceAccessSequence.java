package com.optimus.eds.db.entities.pricing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PriceAccessSequence {
    @PrimaryKey
    @NonNull
    private Integer priceAccessSequenceId;
    private String sequenceCode;
    private String sequenceName;
    @NonNull
    private Integer order;
    @Nullable
    private Integer pricingLevelId;
    private Integer pricingTypeId;

    public Integer getPriceAccessSequenceId() {
        return priceAccessSequenceId;
    }

    public void setPriceAccessSequenceId(Integer priceAccessSequenceId) {
        this.priceAccessSequenceId = priceAccessSequenceId;
    }

    public String getSequenceCode() {
        return sequenceCode;
    }

    public void setSequenceCode(String sequenceCode) {
        this.sequenceCode = sequenceCode;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Nullable
    public Integer getPricingLevelId() {
        return pricingLevelId;
    }

    public void setPricingLevelId(@Nullable Integer pricingLevelId) {
        this.pricingLevelId = pricingLevelId;
    }

    public Integer getPricingTypeId() {
        return pricingTypeId;
    }

    public void setPricingTypeId(Integer pricingTypeId) {
        this.pricingTypeId = pricingTypeId;
    }
}
