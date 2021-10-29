package com.optimus.eds.db.entities.pricing;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class PricingLevels {

    @SerializedName("pricingLevelId")
    @PrimaryKey
    private Integer pricingLevelId;
    @SerializedName("value")
    @Nullable
    private String value;

    public PricingLevels() {
    }
    @Ignore
    public PricingLevels(Integer pricingLevelId, @Nullable String value) {
        this.pricingLevelId = pricingLevelId;
        this.value = value;
    }


    public Integer getPricingLevelId() {
        return pricingLevelId;
    }

    public boolean isPricingNull(){
        return pricingLevelId==null;
    }

    public void setPricingLevelId(Integer pricingLevelId) {
        this.pricingLevelId = pricingLevelId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
