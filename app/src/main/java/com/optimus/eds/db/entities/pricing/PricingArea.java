package com.optimus.eds.db.entities.pricing;


import com.google.gson.annotations.SerializedName;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class PricingArea {

    @SerializedName("PricingAreaId")
    @PrimaryKey
    private Integer pricingAreaId;
    @SerializedName("Code")
    @Nullable
    private String code;
    @SerializedName("Order")
    private int order;
    @SerializedName("Name")
    private String name;

    @SerializedName("IsActive")
    private boolean isActive;

    public PricingArea() {
    }
    @Ignore
    public PricingArea(Integer PricingAreaId, @Nullable String Code,int order,String name,boolean isActive) {
        this.pricingAreaId = PricingAreaId;
        this.code = Code;
        this.order = order;
        this.name = name;
        this.isActive = isActive;
    }

    public Integer getPricingAreaId() {
        return pricingAreaId;
    }

    @Nullable
    public String getCode() {
        return code;
    }

    public int getOrder() {
        return order;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setPricingAreaId(Integer pricingAreaId) {
        this.pricingAreaId = pricingAreaId;
    }

    public void setCode(@Nullable String code) {
        this.code = code;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
