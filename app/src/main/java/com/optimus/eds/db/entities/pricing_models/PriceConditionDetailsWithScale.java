package com.optimus.eds.db.entities.pricing_models;

import com.optimus.eds.db.entities.pricing.PriceConditionDetail;
import com.optimus.eds.db.entities.pricing.PriceConditionScale;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class PriceConditionDetailsWithScale {

    public PriceConditionDetailsWithScale() {
    }

    @Embedded
    public PriceConditionDetail priceConditionDetail;



    @Relation(parentColumn = "priceConditionDetailId", entityColumn = "priceConditionDetailId", entity = PriceConditionScale.class)
    private List<PriceConditionScale> priceConditionScaleList;

    public PriceConditionDetail getPriceConditionDetail() {
        return priceConditionDetail;
    }

    public void setPriceConditionDetail(PriceConditionDetail priceConditionDetail) {
        this.priceConditionDetail = priceConditionDetail;
    }

    public List<PriceConditionScale> getPriceConditionScaleList() {
        return priceConditionScaleList;
    }

    public void setPriceConditionScaleList(List<PriceConditionScale> priceConditionScaleList) {
        this.priceConditionScaleList = priceConditionScaleList;
    }
}
