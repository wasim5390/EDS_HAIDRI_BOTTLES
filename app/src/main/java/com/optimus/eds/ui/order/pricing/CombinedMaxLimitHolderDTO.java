package com.optimus.eds.ui.order.pricing;

public class CombinedMaxLimitHolderDTO {

    private Integer priceConditionId;
    private Integer packageId;
    private Double availedAmount;
    private Integer availedQuantity;
    //public bool IsCombinedLimitDefinedInTheSystem { get; set; }
    /// <summary>
    /// if an order contains 5 products , and a priceCondition is applicable on all the products,
    /// [IsPriceConditionAppliedOnTheFirstItem] will be true for the calculation of first product, and false for the rest of four.
    /// the main purpose of this field is to make sure a single price-condition is added once in the list (not multiple times).
    /// </summary>
    private Boolean isPriceConditionAppliedForTheFirstItem;

    public Integer getPriceConditionId() {
        return priceConditionId;
    }

    public void setPriceConditionId(Integer priceConditionId) {
        this.priceConditionId = priceConditionId;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public Double getAvailedAmount() {
        return availedAmount;
    }

    public void setAvailedAmount(Double availedAmount) {
        this.availedAmount = availedAmount;
    }

    public Integer getAvailedQuantity() {
        return availedQuantity;
    }

    public void setAvailedQuantity(Integer availedQuantity) {
        this.availedQuantity = availedQuantity;
    }

    public Boolean getPriceConditionAppliedForTheFirstItem() {
        return isPriceConditionAppliedForTheFirstItem;
    }

    public void setPriceConditionAppliedForTheFirstItem(Boolean priceConditionAppliedForTheFirstItem) {
        isPriceConditionAppliedForTheFirstItem = priceConditionAppliedForTheFirstItem;
    }
}
