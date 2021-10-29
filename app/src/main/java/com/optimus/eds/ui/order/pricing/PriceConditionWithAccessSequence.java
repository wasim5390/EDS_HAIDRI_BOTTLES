package com.optimus.eds.ui.order.pricing;

import com.optimus.eds.db.entities.pricing.PriceCondition;

public class PriceConditionWithAccessSequence extends PriceCondition {


    private String sequenceCode;
    private String sequenceName;
    private Integer priceAccessSequenceId;
    private Integer order;
    private Integer pricingLevelId;

    public Integer getBundleId() {
        return bundleId;
    }

    public void setBundleId(Integer bundleId) {
        this.bundleId = bundleId;
    }

    private Integer bundleId;


    public void setSequenceCode(String sequenceCode) {
        this.sequenceCode = sequenceCode;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public void setPriceAccessSequenceId(Integer priceAccessSequenceId) {
        this.priceAccessSequenceId = priceAccessSequenceId;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public void setPricingLevelId(Integer pricingLevelId) {
        this.pricingLevelId = pricingLevelId;
    }



    public String getSequenceCode() {
        return sequenceCode;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public Integer getPriceAccessSequenceId() {
        return priceAccessSequenceId;
    }

    public Integer getOrder() {
        return order;
    }

    public Integer getPricingLevelId() {
        return pricingLevelId;
    }


}
