package com.optimus.eds.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = OrderDetail.class,
                        parentColumns = "pk_modid",
                        childColumns = "fk_modid",
                        onDelete = ForeignKey.CASCADE),


        }, indices = { @Index(value = "fk_modid")})
public class    UnitPriceBreakDown {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pk_upbd")
    public Integer mId;
    @SerializedName("orderId")
    public Integer mOrderId;

    @ColumnInfo(name = "fk_modid")
    @SerializedName("mobileOrderDetailId")
    public Integer mOrderDetailId;


    @SerializedName("priceCondition")
    public String mPriceCondition;

    @SerializedName("priceConditionType") //Will contain label of pricing like Retail Price, Discount
    public String mPriceConditionType;
    public void setPriceCondition(String mPriceCondition) {
        this.mPriceCondition = mPriceCondition;
    }

    public void setPriceConditionClass(String mPriceConditionClass) {
        this.mPriceConditionClass = mPriceConditionClass;
    }

    @SerializedName("priceConditionClass")
    public String mPriceConditionClass;
    @SerializedName("priceConditionClassOrder")
    public Integer mPriceConditionClassOrder;



    @SerializedName("priceConditionClassId")
    public Integer mPriceConditionClassId;
    @SerializedName("priceConditionId")
    public Integer mPriceConditionId;
    @SerializedName("priceConditionDetailId")
    public Integer mPriceConditionDetailId;



    @SerializedName("accessSequence")
    public String mAccessSequence;



    @SerializedName("unitPrice")
    public Float mUnitPrice;



    @SerializedName("blockPrice")
    public Double mBlockPrice;

    public void setAccessSequence(String mAccessSequence) {
        this.mAccessSequence = mAccessSequence;
    }
    public void setPriceConditionId(Integer mPriceConditionId) {
        this.mPriceConditionId = mPriceConditionId;
    }

    public void setUnitPrice(Float mUnitPrice) {
        this.mUnitPrice = mUnitPrice;
    }
    public void setTotalPrice(Double mTotalPrice) {
        this.mTotalPrice = mTotalPrice;
    }

    public void setCalculationType(Integer mCalculationType) {
        this.mCalculationType = mCalculationType;
    }
    public void setBlockPrice(Double mBlockPrice) {
        this.mBlockPrice = mBlockPrice;
    }

    public void addBlockPrice(Double mBlockPrice) {
        this.mBlockPrice += mBlockPrice;
    }

    @SerializedName("totalPrice")
    public Double  mTotalPrice;
    @SerializedName("calculationType")
    public Integer mCalculationType;
    @SerializedName("outletId")
    public Integer outletId;
    @SerializedName("productId")
    public Integer mProductId;
    @SerializedName("productDefinitionId")
    public Integer mProductDefinitionId;
    @SerializedName("isMaxLimitReached")
    public Boolean isMaxLimitReached;
    @SerializedName("maximumLimit")
    public Double maximumLimit;
    @SerializedName("alreadyAvailed")
    public Double mAlreadyAvailed;
    @SerializedName("limitBy")
    public Integer mLimitBy;


    public void setOrderId(Integer mOrderId) {
        this.mOrderId = mOrderId;
    }

    public void setOrderDetailId(Integer mOrderDetailId) {
        this.mOrderDetailId = mOrderDetailId;
    }

    public void setPriceConditionType(String mPriceConditionType) {
        this.mPriceConditionType = mPriceConditionType;
    }
    public Double getMaximumLimit() {
        return maximumLimit;
    }
    public Integer getId() {
        return mId;
    }

    public Integer getOrderId() {
        return mOrderId;
    }

    public Integer getOrderDetailId() {
        return mOrderDetailId;
    }

    public String getPriceCondition() {
        return mPriceCondition;
    }

    public Float getUnitPrice() {
        return mUnitPrice;
    }

    public Double getBlockPrice() {
        return mBlockPrice;
    }

    public Double getTotalPrice() {
        return mTotalPrice;
    }

    public String getPriceConditionType() {
        return mPriceConditionType;
    }

    public Integer getPriceConditionId() {
        return mPriceConditionId;
    }

    public Integer getLimitBy() {
        return mLimitBy;
    }

    public void setMaximumLimit(Double maximumLimit) {
        this.maximumLimit = maximumLimit;
    }

    public void setAlreadyAvailed(Double mAlreadyAvailed) {
        this.mAlreadyAvailed = mAlreadyAvailed;
    }

    public void setLimitBy(Integer mLimitBy) {
        this.mLimitBy = mLimitBy;
    }
    public Double getAlreadyAvailed() {
        return mAlreadyAvailed;
    }
    public void setPriceConditionDetailId(Integer mPriceConditionDetailId) {
        this.mPriceConditionDetailId = mPriceConditionDetailId;
    }

    public Integer getPriceConditionDetailId() {
        return mPriceConditionDetailId;
    }

    public Integer getPriceConditionClassOrder() {
        return mPriceConditionClassOrder==null?0:mPriceConditionClassOrder;
    }

    public void setPriceConditionClassOrder(Integer mPriceConditionClassOrder) {
        this.mPriceConditionClassOrder = mPriceConditionClassOrder;
    }

    public Integer getmPriceConditionClassId() {
        return mPriceConditionClassId;
    }

    public void setmPriceConditionClassId(Integer mPriceConditionClassId) {
        this.mPriceConditionClassId = mPriceConditionClassId;
    }

    /**
     * Two PriceBreakdowns are equal if their priceConditionId is same.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  UnitPriceBreakDown)
            return (this.mPriceConditionId.equals(((UnitPriceBreakDown) obj).mPriceConditionId));
        return (this.mPriceConditionId.equals(((CartonPriceBreakDown) obj).mPriceConditionId));
    }

    public String getmPriceConditionClass() {
        return mPriceConditionClass;
    }
}

