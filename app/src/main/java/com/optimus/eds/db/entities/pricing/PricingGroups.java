package com.optimus.eds.db.entities.pricing;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PricingGroups {

    @PrimaryKey
    @NonNull
    private Integer pricingGroupId;
    private String pricingGroupName;
    @NonNull
    private Boolean status;


    public Integer getPricingGroupId() {
        return pricingGroupId;
    }

    public void setPricingGroupId(Integer pricingGroupId) {
        this.pricingGroupId = pricingGroupId;
    }

    public String getPricingGroupName() {
        return pricingGroupName;
    }

    public void setPricingGroupName(String pricingGroupName) {
        this.pricingGroupName = pricingGroupName;
    }

    @NonNull
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(@NonNull Boolean status) {
        this.status = status;
    }


}
