package com.optimus.eds.db.entities.pricing;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "Bundle")
public class PriceBundle {

    @PrimaryKey
    @NonNull
    private Integer bundleId;
    private String name;
    private String validFrom;
    private String validTo;

    private Integer entityGroupById;
    private Integer bundleMinimumQuantity;
    private Boolean isDeleted;

    /*ForeignKeys*/


    @ForeignKey(entity = PriceCondition.class
            ,parentColumns = "priceConditionId",
            childColumns = "priceConditionId",onDelete = CASCADE)
    @NonNull
    private Integer priceConditionId;


    public PriceBundle() {
    }



    @NonNull
    public Integer getBundleId() {
        return bundleId;
    }

    public void setBundleId(@NonNull Integer bundleId) {
        this.bundleId = bundleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public Integer getEntityGroupById() {
        return entityGroupById;
    }

    public void setEntityGroupById(Integer entityGroupById) {
        this.entityGroupById = entityGroupById;
    }

    public Integer getBundleMinimumQuantity() {
        return bundleMinimumQuantity;
    }

    public void setBundleMinimumQuantity(Integer bundleMinimumQuantity) {
        this.bundleMinimumQuantity = bundleMinimumQuantity;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    @NonNull
    public Integer getPriceConditionId() {
        return priceConditionId;
    }

    public void setPriceConditionId(@NonNull Integer priceConditionId) {
        this.priceConditionId = priceConditionId;
    }

}
