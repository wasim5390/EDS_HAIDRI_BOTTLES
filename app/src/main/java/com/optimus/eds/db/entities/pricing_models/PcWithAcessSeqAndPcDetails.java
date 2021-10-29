package com.optimus.eds.db.entities.pricing_models;

import com.optimus.eds.db.entities.pricing.PriceAccessSequence;
import com.optimus.eds.db.entities.pricing.PriceCondition;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class PcWithAcessSeqAndPcDetails {

    public PcWithAcessSeqAndPcDetails() {
    }


    @Embedded
    public PriceCondition priceCondition;

  /*  @Relation(parentColumn = "priceConditionId", entityColumn = "priceConditionId", entity = PriceConditionDetail.class)
    private List<PriceConditionDetail> priceConditionDetailList;*/


    @Relation(parentColumn = "accessSequenceId", entityColumn = "priceAccessSequenceId", entity = PriceAccessSequence.class)
    public List<PriceAccessSequence> accessSequence;

    public PriceCondition getPriceCondition() {
        return priceCondition;
    }

 /*   public List<PriceConditionDetail> getPriceConditionDetailList() {
        return priceConditionDetailList;
    }*/

  /*  public void setPriceConditionDetailList(List<PriceConditionDetail> priceConditionDetailList) {
        this.priceConditionDetailList = priceConditionDetailList;
    }*/

    public PriceAccessSequence getAccessSequence() {
        return accessSequence==null||accessSequence.isEmpty()?null:accessSequence.get(0);
    }

    public void setAccessSequence(List<PriceAccessSequence> accessSequence) {
        this.accessSequence = accessSequence;
    }
}
