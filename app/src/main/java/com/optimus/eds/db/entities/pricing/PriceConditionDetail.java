package com.optimus.eds.db.entities.pricing;


import com.optimus.eds.db.converters.DecimalConverter;

import java.math.BigDecimal;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import static androidx.room.ForeignKey.CASCADE;


@Entity( foreignKeys = {  @ForeignKey(entity = PriceCondition.class
        ,parentColumns = "priceConditionId",
        childColumns = "priceConditionId",onDelete = CASCADE),

        @ForeignKey(entity = PriceBundle.class
                ,parentColumns = "bundleId",
                childColumns = "bundleId",onDelete = CASCADE)

},indices = {@Index("priceConditionId"),@Index("bundleId")})

public class PriceConditionDetail {

    @PrimaryKey
    @NonNull
    private Integer priceConditionDetailId;
    @NonNull
    @TypeConverters(DecimalConverter.class)
    private BigDecimal amount;
    @NonNull
    private Boolean isScale;

    private String validFrom;
    private String validTo;

    @NonNull
    private int type;


    private Boolean isDeleted;
    private Integer productId;
    private Integer productDefinitionId;
    private Integer outletId;
    private Integer routeId;
    private Integer distributionId;
    private Integer minimumQuantity=1;
    @TypeConverters(DecimalConverter.class)
    private BigDecimal maximumLimit;
    private Integer limitBy;
    private Double cartonAmount;
    private Integer packageId ;

    public Integer getPriceConditionDetailId() {
        return priceConditionDetailId;
    }

    public void setPriceConditionDetailId(Integer priceConditionDetailId) {
        this.priceConditionDetailId = priceConditionDetailId;
    }

    @NonNull
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NonNull BigDecimal amount) {
        this.amount = amount;
    }

    @NonNull
    public Boolean getScale() {
        return isScale;
    }

    public void setScale(@NonNull Boolean scale) {
        isScale = scale;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPriceConditionId() {
        return priceConditionId;
    }

    public void setPriceConditionId(int priceConditionId) {
        this.priceConditionId = priceConditionId;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
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

    public Integer getOutletId() {
        return outletId;
    }

    public void setOutletId(Integer outletId) {
        this.outletId = outletId;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public Integer getDistributionId() {
        return distributionId;
    }

    public void setDistributionId(Integer distributionId) {
        this.distributionId = distributionId;
    }

    public Integer getMinimumQuantity() {
        return (minimumQuantity==null||minimumQuantity==0)?1:minimumQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public Integer getBundleId() {
        return bundleId;
    }

    public void setBundleId(Integer bundleId) {
        this.bundleId = bundleId;
    }

    public BigDecimal getMaximumLimit() {
        return maximumLimit;
    }

    public void setMaximumLimit(BigDecimal maximumLimit) {
        this.maximumLimit = maximumLimit;
    }

    public Integer getLimitBy() {
        return limitBy;
    }

    public void setLimitBy(Integer limitBy) {
        this.limitBy = limitBy;
    }

    public Double getCartonAmount() {
        return cartonAmount;
    }

    public void setCartonAmount(Double cartonAmount) {
        this.cartonAmount = cartonAmount;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }


    /*ForeignKeys*/

/*
    @ForeignKey(entity = PriceCondition.class
            ,parentColumns = "priceConditionId",
            childColumns = "priceConditionId",onDelete = CASCADE)*/
    @NonNull
    private int priceConditionId;
/*
    @ForeignKey(entity = PriceBundle.class
            ,parentColumns = "bundleId",
            childColumns = "bundleId",onDelete = CASCADE)*/
    private Integer bundleId;
}
