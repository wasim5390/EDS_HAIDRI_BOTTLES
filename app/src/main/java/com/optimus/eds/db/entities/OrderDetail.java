package com.optimus.eds.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.converters.OrderDetailConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Order.class,
                        parentColumns = "pk_oid",
                        childColumns = "fk_oid",
                        onDelete = ForeignKey.CASCADE),

        }, indices = { @Index(value = "fk_oid")})

public class OrderDetail implements Cloneable{

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "pk_modid")
    @SerializedName("mobileOrderDetailId")
    public int orderDetailId;

    @SerializedName("productId")
    public Long mProductId;

    @NonNull @ColumnInfo(name = "fk_oid")
    @SerializedName("mobileOrderId")
    public Long mLocalOrderId;

    @ColumnInfo(name = "orderId")
    @SerializedName("orderId")
    public Long mOrderId;

    @SerializedName("packageId")
    public Long packageId;

    @SerializedName("unitOrderDetailId")
    @ColumnInfo(name = "unitOrderDetailId")
    public Long mUnitOrderDetailId;
    @SerializedName("cartonOrderDetailId")
    @ColumnInfo(name = "cartonOrderDetailId")
    public Long mCartonOrderDetailId;
    @SerializedName("productGroupId")
    public Long mProductGroupId;
    @SerializedName("unitFreeGoodGroupId")
    public Long unitFreeGoodGroupId;
    @SerializedName("cartonFreeGoodGroupId")
    public Long cartonFreeGoodGroupId;
    @SerializedName("unitFreeGoodDetailId")
    public Long unitFreeGoodDetailId;
    @SerializedName("cartonFreeGoodDetailId")
    public Long cartonFreeGoodDetailId;
    @SerializedName("unitFreeGoodExclusiveId")
    public Long unitFreeGoodExclusiveId;
    @SerializedName("cartonFreeGoodExclusiveId")
    public Long cartonFreeGoodExclusiveId;

    @SerializedName("productName")
    public String mProductName;
    @SerializedName("cartonQuantity")
    public Integer mCartonQuantity;
    @SerializedName("unitQuantity")
    public Integer mUnitQuantity;
    @SerializedName("avlUnitQuantity")
    public Integer avlUnitQuantity;
    @SerializedName("avlCartonQuantity")
    public Integer avlCartonQuantity;


    public Integer productTempDefinitionId; // this definitionId sets on PriceCalculation

    public Integer productTempQuantity;

    public int getProductTempDefId() {
        return productTempDefinitionId;
    }

    public void setProductTempDefId(int productDefinitionId) {
        this.productTempDefinitionId = productDefinitionId;
    }

    public int getProductTempQuantity() {
        return productTempQuantity;
    }

    public void setProductTempQuantity(int productTempQuantity) {
        this.productTempQuantity = productTempQuantity;
    }

    public Integer getUnitDefinitionId() {
        return unitDefinitionId;
    }

    public void setUnitDefinitionId(Integer unitDefinitionId) {
        this.unitDefinitionId = unitDefinitionId;
    }

    public Integer getCartonDefinitionId() {
        return cartonDefinitionId;
    }

    public void setCartonDefinitionId(Integer cartonDefinitionId) {
        this.cartonDefinitionId = cartonDefinitionId;
    }

    public Integer getActualUnitStock() {
        return actualUnitStock;
    }

    public void setActualUnitStock(Integer actualUnitStock) {
        this.actualUnitStock = actualUnitStock;
    }

    public Integer getActualCartonStock() {
        return actualCartonStock;
    }

    public void setActualCartonStock(Integer actualCartonStock) {
        this.actualCartonStock = actualCartonStock;
    }

    @SerializedName("unitDefinitionId")
    public Integer unitDefinitionId;
    @SerializedName("cartonDefinitionId")
    public Integer cartonDefinitionId;
    @SerializedName("actualUnitStock")
    public Integer actualUnitStock;
    @SerializedName("actualCartonStock")
    public Integer actualCartonStock;

    @SerializedName("cartonSize")
    public Integer cartonSize;

    @SerializedName("cartonCode")
    public String mCartonCode;
    @SerializedName("unitCode")
    public String mUnitCode;

    @SerializedName("unitPrice")
    public Double unitPrice;
    @SerializedName("cartonPrice")
    public Double cartonPrice;
    @SerializedName("unitTotalPrice")
    public Double unitTotalPrice;
    @SerializedName("cartonTotalPrice")
    public Double cartonTotalPrice;

    @SerializedName("payable")
    public Double total;
    @SerializedName("subtotal")
    public Double subtotal;
    @SerializedName("type")
    public String type; // either paid or free product

    @SerializedName("unitFreeQuantityTypeId")
    public Integer unitFreeQuantityTypeId; // 1. Primary ; 2. Optional {If Optional ask for quantity}

    @SerializedName("cartonFreeQuantityTypeId")
    public Integer cartonFreeQuantityTypeId; // 1. Primary ; 2. Optional {If Optional ask for quantity}

    @SerializedName("unitFreeGoodQuantity")
    public Integer unitFreeGoodQuantity; //Only applicable for Optional

    @SerializedName("cartonFreeGoodQuantity")
    public Integer cartonFreeGoodQuantity; //Only applicable for Optional

    @SerializedName("unitSelectedFreeGoodQuantity")
    public Integer unitSelectedFreeGoodQuantity;

    @SerializedName("cartonSelectedFreeGoodQuantity")
    public Integer cartonSelectedFreeGoodQuantity;

    @Ignore
    public Integer parentId; //In case of FOC, server will send the FOC row with parentId

    @SerializedName("cartonPriceBreakDown")
    @Ignore
    public List<CartonPriceBreakDown> cartonPriceBreakDown;

    @SerializedName("unitPriceBreakDown")
    @Ignore
    public List<UnitPriceBreakDown> unitPriceBreakDown;


    @TypeConverters(OrderDetailConverter.class)
    @SerializedName("cartonFreeGoods")
    public List<OrderDetail> cartonFreeGoods;

    @TypeConverters(OrderDetailConverter.class)
    @SerializedName("unitFreeGoods")
    public List<OrderDetail> unitFreeGoods;


    public OrderDetail() {
    }

    public OrderDetail(@NonNull Long mOrderId, @NonNull Long mProductId, Integer mCartonQuantity, Integer mUnitQuantity) {
        this.mLocalOrderId = mOrderId;
        this.mProductId = mProductId;
        this.mCartonQuantity = mCartonQuantity;
        this.mUnitQuantity = mUnitQuantity;
    }
    public void setLocalOrderId(Long mLocalOrderId){
        this.mLocalOrderId = mLocalOrderId;
    }
    public Long getLocalOrderId() {
        return mLocalOrderId;
    }
    public int getMobileOrderDetailId() {
        return orderDetailId;
    }
    public Long getOrderId() {
        return mOrderId;
    }

    public void setOrderId(Long mOrderId) {
        this.mOrderId = mOrderId;
    }

    public Long getProductId() {
        return mProductId;
    }

    public void setProductId(Long mProductId) {
        this.mProductId = mProductId;
    }

    public Integer getCartonQuantity() {
        return mCartonQuantity;
    }

    public void setCartonQuantity(Integer mCartonQuantity) {
        this.mCartonQuantity = mCartonQuantity;
    }

    public Integer getUnitQuantity() {
        return mUnitQuantity;
    }

    public void setUnitQuantity(Integer mUnitQuantity) {
        this.mUnitQuantity = mUnitQuantity;
    }
    public Integer getCartonSize() {
        return cartonSize;
    }

    public void setCartonSize(Integer cartonSize) {
        this.cartonSize = cartonSize;
    }

    public Integer getAvlUnitQuantity() {
        return avlUnitQuantity;
    }

    public Integer getAvlCartonQuantity() {
        return avlCartonQuantity;
    }

    public String getCartonCode() {
        return mCartonCode;
    }

    public void setCartonCode(String mCartonCode) {
        this.mCartonCode = mCartonCode;
    }

    public String getUnitCode() {
        return mUnitCode;
    }

    public void setUnitCode(String mUnitCode) {
        this.mUnitCode = mUnitCode;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String mProductName) {
        this.mProductName = mProductName;
    }

    public Long getProductGroupId() {
        return mProductGroupId;
    }

    public void setProductGroupId(Long mProductGroupId) {
        this.mProductGroupId = mProductGroupId;
    }

    public Long getUnitOrderDetailId() {
        return mUnitOrderDetailId;
    }

    public void setUnitOrderDetailId(Long mUnitOrderDetailId) {
        this.mUnitOrderDetailId = mUnitOrderDetailId;
    }

    public Long getCartonOrderDetailId() {
        return mCartonOrderDetailId;
    }

    public void setCartonOrderDetailId(Long mCartonOrderDetailId) {
        this.mCartonOrderDetailId = mCartonOrderDetailId;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getCartonPrice() {
        return cartonPrice;
    }

    public void setCartonPrice(Double cartonPrice) {
        this.cartonPrice = cartonPrice;
    }

    public Double getUnitTotalPrice() {
        return unitTotalPrice==null?0:unitTotalPrice;
    }

    public void setUnitTotalPrice(Double unitTotalPrice) {
        this.unitTotalPrice = unitTotalPrice;
    }

    public Double getCartonTotalPrice() {
        return cartonTotalPrice==null?0:cartonTotalPrice;
    }

    public void setCartonTotalPrice(Double cartonTotalPrice) {
        this.cartonTotalPrice = cartonTotalPrice;
    }
    public Integer getUnitFreeQuantityTypeId() {
        return unitFreeQuantityTypeId==null?-1:unitFreeQuantityTypeId;
    }

    public void setUnitFreeQuantityTypeId(Integer unitFreeQuantityTypeId) {
        this.unitFreeQuantityTypeId = unitFreeQuantityTypeId;
    }

    public Integer getCartonFreeQuantityTypeId() {
        return cartonFreeQuantityTypeId==null?-1:cartonFreeQuantityTypeId;
    }

    public void setCartonFreeQuantityTypeId(Integer cartonFreeQuantityTypeId) {
        this.cartonFreeQuantityTypeId = cartonFreeQuantityTypeId;
    }

    public Integer getUnitFreeGoodQuantity() {
        return unitFreeGoodQuantity;
    }

    public void setUnitFreeGoodQuantity(Integer freeGoodQuantity) {
        this.unitFreeGoodQuantity = freeGoodQuantity;
    }

    public Integer getCartonFreeGoodQuantity() {
        return cartonFreeGoodQuantity;
    }

    public void setCartonFreeGoodQuantity(Integer freeGoodQuantity) {
        this.cartonFreeGoodQuantity = freeGoodQuantity;
    }

    public Integer getSelectedCartonFreeGoodQuantity() {
        return cartonSelectedFreeGoodQuantity;
    }

    public void setSelectedCartonFreeGoodQuantity(Integer freeGoodQuantity) {
        this.cartonSelectedFreeGoodQuantity = freeGoodQuantity;
    }

    public Integer getSelectedUnitFreeGoodQuantity() {
        return unitSelectedFreeGoodQuantity;
    }

    public void setSelectedUnitFreeGoodQuantity(Integer freeGoodQuantity) {
        this.unitSelectedFreeGoodQuantity = freeGoodQuantity;
    }

    public Double getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }


    public void setAvlQty(Integer avlCartonQuantity,Integer avlUnitQuantity){
        this.avlCartonQuantity=avlCartonQuantity;
        this.avlUnitQuantity = avlUnitQuantity;
    }

    public String getQuantity(){
        Integer cQty = getCartonQuantity();
        Integer uQty = getUnitQuantity();
        return (cQty==null?0:cQty)+"/"+(uQty==null?0:uQty);
    }

    public String getWithoutUnitQuantity(){
        Integer cQty = getCartonQuantity();
        return String.valueOf(cQty==null?0:cQty);
    }

    public void setCartonPriceBreakDown(List<CartonPriceBreakDown> cartonPriceBreakDown) {
        this.cartonPriceBreakDown = cartonPriceBreakDown;
    }

    public void setUnitPriceBreakDown(List<UnitPriceBreakDown> unitPriceBreakDown) {
        this.unitPriceBreakDown = unitPriceBreakDown;
    }

    public List<CartonPriceBreakDown> getCartonPriceBreakDown() {
        return cartonPriceBreakDown;
    }

    public List<UnitPriceBreakDown> getUnitPriceBreakDown() {
        return unitPriceBreakDown;
    }

    public List<OrderDetail> getCartonFreeGoods() {
        return cartonFreeGoods==null?new ArrayList<>():cartonFreeGoods;
    }

    public void setUnitFreeGoods(List<OrderDetail> unitFreeGoods) {
        this.unitFreeGoods = unitFreeGoods;
    }

    public void setCartonFreeGoods(List<OrderDetail> cartonFreeGoods) {
        this.cartonFreeGoods = cartonFreeGoods;
    }

    public List<OrderDetail> getUnitFreeGoods() {
        return unitFreeGoods==null?new ArrayList<>():unitFreeGoods;
    }

    public Long getUnitFreeGoodGroupId() {
        return unitFreeGoodGroupId;
    }

    public void setUnitFreeGoodGroupId(Long unitFreeGoodGroupId) {
        this.unitFreeGoodGroupId = unitFreeGoodGroupId;
    }

    public Long getCartonFreeGoodGroupId() {
        return cartonFreeGoodGroupId;
    }

    public void setCartonFreeGoodGroupId(Long cartonFreeGoodGroupId) {
        this.cartonFreeGoodGroupId = cartonFreeGoodGroupId;
    }

    public Long getUnitFreeGoodDetailId() {
        return unitFreeGoodDetailId;
    }

    public void setUnitFreeGoodDetailId(Long unitFreeGoodDetailId) {
        this.unitFreeGoodDetailId = unitFreeGoodDetailId;
    }

    public Long getCartonFreeGoodDetailId() {
        return cartonFreeGoodDetailId;
    }

    public void setCartonFreeGoodDetailId(Long cartonFreeGoodDetailId) {
        this.cartonFreeGoodDetailId = cartonFreeGoodDetailId;
    }

    public Long getUnitFreeGoodExclusiveId() {
        return unitFreeGoodExclusiveId;
    }

    public void setUnitFreeGoodExclusiveId(Long unitFreeGoodExclusiveId) {
        this.unitFreeGoodExclusiveId = unitFreeGoodExclusiveId;
    }

    public Long getCartonFreeGoodExclusiveId() {
        return cartonFreeGoodExclusiveId;
    }

    public void setCartonFreeGoodExclusiveId(Long cartonFreeGoodExclusiveId) {
        this.cartonFreeGoodExclusiveId = cartonFreeGoodExclusiveId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetail that = (OrderDetail) o;
        return mProductId.equals(that.mProductId) &&
                mProductGroupId.equals(that.mProductGroupId);
    }

    public Long getPkgId() {
        return packageId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public void setPkgId(Long pkgId) {
        this.packageId = pkgId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mProductId, mProductGroupId);
    }

    public Object clone() throws
            CloneNotSupportedException
    {
        return super.clone();
    }
}
