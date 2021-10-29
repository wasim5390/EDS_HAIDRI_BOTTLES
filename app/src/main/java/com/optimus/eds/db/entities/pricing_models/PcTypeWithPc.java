package com.optimus.eds.db.entities.pricing_models;

import com.optimus.eds.db.entities.pricing.PriceCondition;
import com.optimus.eds.db.entities.pricing.PriceConditionType;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class PcTypeWithPc {

    public PcTypeWithPc() {
    }



    @Embedded
    public PriceConditionType priceConditionType;

    @Relation(parentColumn = "priceConditionTypeId", entityColumn = "priceConditionTypeId", entity = PriceCondition.class)
    private List<PcWithAcessSeqAndPcDetails> priceConditionList;

    public List<PcWithAcessSeqAndPcDetails> getPriceConditionList() {
        return priceConditionList;
    }

    public void setPriceConditionList(List<PcWithAcessSeqAndPcDetails> priceConditionList) {
        this.priceConditionList = priceConditionList;
    }

    public PriceConditionType getPriceConditionType() {
        return priceConditionType;
    }
}
