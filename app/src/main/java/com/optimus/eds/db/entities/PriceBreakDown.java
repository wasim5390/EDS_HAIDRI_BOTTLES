package com.optimus.eds.db.entities;


public class PriceBreakDown extends UnitPriceBreakDown{


    public PriceBreakDown(int priceConditionId, String priceConditionType) {
        setPriceConditionId(priceConditionId);
        setPriceConditionType(priceConditionType);
    }

    public Integer getPriceConditionClassOrder() {
        if (mPriceConditionClassOrder==null||mPriceConditionClassOrder < 1)
            this.mPriceConditionClassOrder = getPriceConditionId();
        return mPriceConditionClassOrder;
    }

    //Depends only on account number
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + mPriceConditionId;
        return result;
    }

    //Compare only account numbers
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PriceBreakDown other = (PriceBreakDown) obj;
        if (mPriceConditionId != other.mPriceConditionId)
            return false;
        return true;
    }
}
