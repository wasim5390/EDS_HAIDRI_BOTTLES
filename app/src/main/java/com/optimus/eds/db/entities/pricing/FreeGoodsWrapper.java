package com.optimus.eds.db.entities.pricing;

import java.util.List;

public class FreeGoodsWrapper {

    List<FreeGoodMasters> freeGoodMasters ;
    List<FreeGoodGroups> freeGoodGroups ;
    List<FreePriceConditionOutletAttributes> priceConditionOutletAttributes ;
    List<FreeGoodDetails> freeGoodDetails ;
    List<FreeGoodExclusives> freeGoodExclusives ;
    List<FreeGoodEntityDetails> freeGoodEntityDetails ;
    List<OutletAvailedFreeGoods> outletAvailedFreeGoods ;

    public List<FreeGoodMasters> getFreeGoodMasters() {
        return freeGoodMasters;
    }

    public void setFreeGoodMasters(List<FreeGoodMasters> freeGoodMasters) {
        this.freeGoodMasters = freeGoodMasters;
    }

    public List<FreeGoodGroups> getFreeGoodGroups() {
        return freeGoodGroups;
    }

    public void setFreeGoodGroups(List<FreeGoodGroups> freeGoodGroups) {
        this.freeGoodGroups = freeGoodGroups;
    }

    public List<FreePriceConditionOutletAttributes> getPriceConditionOutletAttributes() {
        return priceConditionOutletAttributes;
    }

    public void setPriceConditionOutletAttributes(List<FreePriceConditionOutletAttributes> priceConditionOutletAttributes) {
        this.priceConditionOutletAttributes = priceConditionOutletAttributes;
    }

    public List<FreeGoodDetails> getFreeGoodDetails() {
        return freeGoodDetails;
    }

    public void setFreeGoodDetails(List<FreeGoodDetails> freeGoodDetails) {
        this.freeGoodDetails = freeGoodDetails;
    }

    public List<FreeGoodExclusives> getFreeGoodExclusives() {
        return freeGoodExclusives;
    }

    public void setFreeGoodExclusives(List<FreeGoodExclusives> freeGoodExclusives) {
        this.freeGoodExclusives = freeGoodExclusives;
    }

    public List<FreeGoodEntityDetails> getFreeGoodEntityDetails() {
        return freeGoodEntityDetails;
    }

    public void setFreeGoodEntityDetails(List<FreeGoodEntityDetails> freeGoodEntityDetails) {
        this.freeGoodEntityDetails = freeGoodEntityDetails;
    }

    public List<OutletAvailedFreeGoods> getOutletAvailedFreeGoods() {
        return outletAvailedFreeGoods;
    }

    public void setOutletAvailedFreeGoods(List<OutletAvailedFreeGoods> outletAvailedFreeGoods) {
        this.outletAvailedFreeGoods = outletAvailedFreeGoods;
    }
}
