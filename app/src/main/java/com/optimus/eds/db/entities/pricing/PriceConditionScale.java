package com.optimus.eds.db.entities.pricing;

import com.optimus.eds.db.converters.DecimalConverter;

import java.math.BigDecimal;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(foreignKeys = {
        @ForeignKey(entity = PriceConditionDetail.class,
                parentColumns = "priceConditionDetailId",
                childColumns = "priceConditionDetailId",
                onDelete = ForeignKey.CASCADE
        )},indices = @Index(value = {"priceConditionDetailId","priceConditionScaleId"}))
public class PriceConditionScale {

    @PrimaryKey @NonNull
    private Integer priceConditionScaleId;
    @NonNull
    private Integer from;
    @NonNull
    @TypeConverters(DecimalConverter.class)
    private BigDecimal amount;
    @NonNull
    private int priceConditionDetailId;

    private Double cartonAmount ;

    public Integer getPriceConditionScaleId() {
        return priceConditionScaleId;
    }

    public void setPriceConditionScaleId(Integer priceConditionScaleId) {
        this.priceConditionScaleId = priceConditionScaleId;
    }

    @NonNull
    public Integer getFrom() {
        return from;
    }

    public void setFrom(@NonNull Integer from) {
        this.from = from;
    }

    @NonNull
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NonNull BigDecimal amount) {
        this.amount = amount;
    }

    public int getPriceConditionDetailId() {
        return priceConditionDetailId;
    }

    public void setPriceConditionDetailId(int priceConditionDetailId) {
        this.priceConditionDetailId = priceConditionDetailId;
    }

    public Double getCartonAmount() {
        return cartonAmount;
    }

    public void setCartonAmount(Double cartonAmount) {
        this.cartonAmount = cartonAmount;
    }
}
