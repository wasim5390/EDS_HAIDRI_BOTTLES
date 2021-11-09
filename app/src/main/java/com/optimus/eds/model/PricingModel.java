package com.optimus.eds.model;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.BaseModel;
import com.optimus.eds.db.entities.OutletAvailedPromotion;
import com.optimus.eds.db.entities.pricing.FreeGoodsWrapper;
import com.optimus.eds.db.entities.pricing.PriceAccessSequence;
import com.optimus.eds.db.entities.pricing.PriceBundle;
import com.optimus.eds.db.entities.pricing.PriceCondition;
import com.optimus.eds.db.entities.pricing.PriceConditionClass;
import com.optimus.eds.db.entities.pricing.PriceConditionDetail;
import com.optimus.eds.db.entities.pricing.PriceConditionEntities;
import com.optimus.eds.db.entities.pricing.PriceConditionOutletAttribute;
import com.optimus.eds.db.entities.pricing.PriceConditionScale;
import com.optimus.eds.db.entities.pricing.PriceConditionType;

import java.util.ArrayList;
import java.util.List;

public class PricingModel extends BaseModel {

    @SerializedName("priceConditionClass")
    public List<PriceConditionClass> priceConditionClasses;
    @SerializedName("priceConditionType")
    public List<PriceConditionType> priceConditionTypes;
    @SerializedName("priceCondition")
    public List<PriceCondition> priceConditions;


    @SerializedName("priceBundle")
    public List<PriceBundle> priceBundles;
    @SerializedName("priceConditionEntity")
    public List<PriceConditionEntities> priceConditionEntities;
    @SerializedName("priceConditionDetail")
    public List<PriceConditionDetail> priceConditionDetails;

    @SerializedName("priceConditionScale")
    public List<PriceConditionScale> priceConditionScales;

    @SerializedName("priceAccessSequence")
    public List<PriceAccessSequence> priceAccessSequences;

    @SerializedName("outletAvailedPromotion")
    public List<OutletAvailedPromotion> outletAvailedPromotions;

    @SerializedName("priceConditionOutletAttribute")
    public List<PriceConditionOutletAttribute> priceConditionOutletAttribute;

    @SerializedName("freeGoodsWrapper")
    public FreeGoodsWrapper freeGoodsWrapper;

    public List<PriceConditionClass> getPriceConditionClasses() {
        return (priceConditionClasses != null) ? priceConditionClasses : new ArrayList<>();
    }

    public List<PriceConditionType> getPriceConditionTypes() {
        return (priceConditionTypes != null) ? priceConditionTypes : new ArrayList<>();
    }

    public List<PriceCondition> getPriceConditions() {
        return (priceConditions != null) ? priceConditions : new ArrayList<>();
    }

    public List<PriceBundle> getPriceBundles() {
        return  (priceBundles != null) ? priceBundles : new ArrayList<>();
    }


    public List<PriceConditionEntities> getPriceConditionEntities() {
        return   (priceConditionEntities != null) ? priceConditionEntities : new ArrayList<>();
    }


    public List<PriceConditionDetail> getPriceConditionDetails() {
        return   (priceConditionDetails != null) ? priceConditionDetails : new ArrayList<>();
    }


    public List<PriceConditionScale> getPriceConditionScales() {

        return   (priceConditionScales != null) ? priceConditionScales : new ArrayList<>();
    }


    public List<PriceAccessSequence> getPriceAccessSequences() {
        return   (priceAccessSequences != null) ? priceAccessSequences : new ArrayList<>();
    }

    public void setPriceConditionClasses(List<PriceConditionClass> priceConditionClasses) {
        this.priceConditionClasses = priceConditionClasses;
    }

    public void setPriceConditionTypes(List<PriceConditionType> priceConditionTypes) {
        this.priceConditionTypes = priceConditionTypes;
    }

    public void setPriceConditions(List<PriceCondition> priceConditions) {
        this.priceConditions = priceConditions;
    }

    public void setPriceBundles(List<PriceBundle> priceBundles) {
        this.priceBundles = priceBundles;
    }

    public void setPriceConditionEntities(List<PriceConditionEntities> priceConditionEntities) {
        this.priceConditionEntities = priceConditionEntities;
    }

    public void setPriceConditionDetails(List<PriceConditionDetail> priceConditionDetails) {
        this.priceConditionDetails = priceConditionDetails;
    }

    public void setPriceConditionScales(List<PriceConditionScale> priceConditionScales) {
        this.priceConditionScales = priceConditionScales;
    }

    public void setPriceAccessSequences(List<PriceAccessSequence> priceAccessSequences) {
        this.priceAccessSequences = priceAccessSequences;
    }

    public List<OutletAvailedPromotion> getOutletAvailedPromotions() {
        return  (outletAvailedPromotions != null) ? outletAvailedPromotions : new ArrayList<>();
    }

    public void setOutletAvailedPromotions(List<OutletAvailedPromotion> outletAvailedPromotions) {
        this.outletAvailedPromotions = outletAvailedPromotions;
    }

    public List<PriceConditionOutletAttribute> getPriceConditionOutletAttribute() {
        return ( priceConditionOutletAttribute  != null ) ? priceConditionOutletAttribute : new ArrayList<>();
    }

    public void setPriceConditionOutletAttribute(List<PriceConditionOutletAttribute> priceConditionOutletAttribute) {
        this.priceConditionOutletAttribute = priceConditionOutletAttribute;
    }

    public FreeGoodsWrapper getFreeGoodsWrapper() {
        return freeGoodsWrapper;
    }

    public void setFreeGoodsWrapper(FreeGoodsWrapper freeGoodsWrapper) {
        this.freeGoodsWrapper = freeGoodsWrapper;
    }
}
