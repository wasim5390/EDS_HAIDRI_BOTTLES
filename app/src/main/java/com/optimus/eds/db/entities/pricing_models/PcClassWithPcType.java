package com.optimus.eds.db.entities.pricing_models;

import com.optimus.eds.db.entities.pricing.PriceConditionClass;
import com.optimus.eds.db.entities.pricing.PriceConditionType;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class PcClassWithPcType {
    public PcClassWithPcType() {
    }

    @Embedded
    public PriceConditionClass priceConditionClass;

    @Relation(parentColumn = "priceConditionClassId", entityColumn = "priceConditionClassId", entity = PriceConditionType.class)
    private List<PcTypeWithPc> pcTypeWithPcList;


    public List<PcTypeWithPc> getPcTypeWithPcList() {
        return pcTypeWithPcList;
    }

    public void setPcTypeWithPcList(List<PcTypeWithPc> priceConditionList) {
        this.pcTypeWithPcList = priceConditionList;
    }




    public PriceConditionClass getPriceConditionClass() {
        return priceConditionClass;
    }
}
