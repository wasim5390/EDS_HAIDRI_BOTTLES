package com.optimus.eds.ui.order.pricing;

import com.optimus.eds.db.entities.pricing.PriceCondition;

public class PriceConditionWithAccessSequence extends PriceCondition {


    private String sequenceCode;
    private String sequenceName;
    private Integer priceAccessSequenceId;
    private Integer order;
    private Integer pricingLevelId;
    private String accessSequenceName ;
    private String accessSequenceCode ;
    private Integer accessSequenceOrder;
    private Integer conditionTypeId ;
    private Integer ChannelAttributeCount;
    private Integer OutletChannelAttribute;
    private Integer GroupAttributeCount;
    private Integer OutletGroupAttribute;
    private Integer VPOClassificationAttributeCount;
    private Integer OutletVPOClassificationAttributeCount;


    public String getAccessSequenceName() {
        return accessSequenceName;
    }

    public void setAccessSequenceName(String accessSequenceName) {
        this.accessSequenceName = accessSequenceName;
    }

    public String getAccessSequenceCode() {
        return accessSequenceCode;
    }

    public void setAccessSequenceCode(String accessSequenceCode) {
        this.accessSequenceCode = accessSequenceCode;
    }

    public Integer getAccessSequenceOrder() {
        return accessSequenceOrder;
    }

    public void setAccessSequenceOrder(Integer accessSequenceOrder) {
        this.accessSequenceOrder = accessSequenceOrder;
    }

    public Integer getConditionTypeId() {
        return conditionTypeId;
    }

    public void setConditionTypeId(Integer conditionTypeId) {
        this.conditionTypeId = conditionTypeId;
    }

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


    public Integer getChannelAttributeCount() {
        return ChannelAttributeCount != null ? ChannelAttributeCount : 0;
    }

    public void setChannelAttributeCount(Integer channelAttributeCount) {
        ChannelAttributeCount = channelAttributeCount;
    }

    public Integer getOutletChannelAttribute() {
        return OutletChannelAttribute != null ? OutletChannelAttribute : 0;
    }

    public void setOutletChannelAttribute(Integer outletChannelAttribute) {
        OutletChannelAttribute = outletChannelAttribute;
    }

    public Integer getGroupAttributeCount() {
        return GroupAttributeCount != null ? GroupAttributeCount : 0;
    }

    public void setGroupAttributeCount(Integer groupAttributeCount) {
        GroupAttributeCount = groupAttributeCount;
    }

    public Integer getOutletGroupAttribute() {
        return OutletGroupAttribute != null ? OutletGroupAttribute : 0;
    }

    public void setOutletGroupAttribute(Integer outletGroupAttribute) {
        OutletGroupAttribute = outletGroupAttribute;
    }

    public Integer getVPOClassificationAttributeCount() {
        return VPOClassificationAttributeCount != null ? VPOClassificationAttributeCount : 0;
    }

    public void setVPOClassificationAttributeCount(Integer VPOClassificationAttributeCount) {
        this.VPOClassificationAttributeCount = VPOClassificationAttributeCount;
    }

    public Integer getOutletVPOClassificationAttributeCount() {
        return OutletVPOClassificationAttributeCount != null ? OutletVPOClassificationAttributeCount : 0;
    }

    public void setOutletVPOClassificationAttributeCount(Integer outletVPOClassificationAttributeCount) {
        OutletVPOClassificationAttributeCount = outletVPOClassificationAttributeCount;
    }
}
