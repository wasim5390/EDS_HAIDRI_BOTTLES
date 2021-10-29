package com.optimus.eds.ui.order.pricing;

import java.math.BigDecimal;

class PromoLimitDTO {

    private int PriceConditionDetailId;
    private Integer LimitBy;
    private BigDecimal MaximumLimit;
    private BigDecimal UnitPrice;

    public PromoLimitDTO()
    {
        UnitPrice = new BigDecimal(-1);
    }

    public PromoLimitDTO(int priceConditionDetailId,Integer limitBy,BigDecimal maximumLimit)
    {
        UnitPrice = new BigDecimal(-1);
        this.PriceConditionDetailId = priceConditionDetailId;
        this.LimitBy = limitBy;
        this.MaximumLimit = maximumLimit;
    }

    public void setPriceConditionDetailId(int priceConditionDetailId) {
        PriceConditionDetailId = priceConditionDetailId;
    }

    public void setLimitBy(Integer limitBy) {
        LimitBy = limitBy;
    }

    public void setMaximumLimit(BigDecimal maximumLimit) {
        MaximumLimit = maximumLimit;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        UnitPrice = unitPrice;
    }

    public int getPriceConditionDetailId() {
        return PriceConditionDetailId;
    }

    public Integer getLimitBy() {
        return LimitBy;
    }

    public BigDecimal getMaximumLimit() {
        return MaximumLimit;
    }

    public BigDecimal getUnitPrice() {
        return UnitPrice;
    }


}
