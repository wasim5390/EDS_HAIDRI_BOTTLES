package com.optimus.eds.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.reactivex.annotations.Nullable;

@Entity
public class Product implements Serializable {


    @PrimaryKey @ColumnInfo(name = "pk_pid")
    @SerializedName("productId")
    public Long id;
    @SerializedName("productName")
    public String name;
    @SerializedName("productDescription")
    public String productDescription;
    @SerializedName("productCode")
    public String productCode;
    @SerializedName("productGroupId")
    public Long productGroupId;
    @SerializedName("productGroupName")
    public String productGroupName;
    @SerializedName("productPackageId")
    public Long pkgId;
    @SerializedName("packageName")
    public String packageName;
    @SerializedName("productBrandId")
    public Long productBrandId;
    @SerializedName("brandName")
    public String brandName;
    @SerializedName("productFlavorId")
    public Long productFlavorId;
    @SerializedName("flavorName")
    public String flavorName;
    @SerializedName("unitCode")
    public String unitCode;
    @SerializedName("cartonCode")
    public String cartonCode;
    @SerializedName("unitQuantity")
    public Integer unitQuantity;
    @SerializedName("cartonQuantity")
    public Integer cartonQuantity;
    @SerializedName("unitSizeForDisplay")
    public String unitSizeForDisplay;
    @SerializedName("cartonSizeForDisplay")
    public String cartonSizeForDisplay;


    @SerializedName("unitStockInHand")
    public Integer unitStockInHand;
    @SerializedName("cartonStockInHand")
    public Integer cartonStockInHand;

    @SerializedName("unitDefinitionId")
    public Integer unitDefinitionId;
    @SerializedName("cartonDefinitionId")
    public Integer cartonDefinitionId;
    @SerializedName("actualUnitStock")
    public Integer actualUnitStock;
    @SerializedName("actualCartonStock")
    public Integer actualCartonStock;


    @Ignore
    public Integer qtyCarton,qtyUnit;
    @Ignore
    public Integer avlStockUnit,avlStockCarton;


    public Product(Long id, Long pkgId, String name) {
        this.id = id;
        this.pkgId = pkgId;
        this.name = name;
    }

    public Integer getQtyUnit() {
        return qtyUnit==null||qtyUnit==0?null:qtyUnit;
    }

    public Integer getQtyCarton() {
        return qtyCarton==null||qtyCarton==0?null:qtyCarton;
    }

    public void setQty(Integer carton, Integer unit) {
        this.qtyCarton = carton;
        this.qtyUnit = unit;
    }

    public void setAvlStock(Integer carton,Integer unit) {
        this.avlStockCarton = carton;
        this.avlStockUnit = unit;
    }

    public void setUnit(Integer unit) {
        this.qtyUnit = unit;
    }

    public void setCarton(Integer carton) {
        this.qtyCarton = carton;
    }


    public Integer getAvlStockUnit() {
        return avlStockUnit;
    }

    public Integer getAvlStockCarton() {
        return avlStockCarton;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProductDescription() {
        return productDescription;
    }
    public Long getPkgId() {
        return pkgId;
    }

    public String getProductCode() {
        return productCode;
    }

    public Long getProductGroupId() {
        return productGroupId;
    }

    public String getProductGroupName() {
        return productGroupName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Long getProductBrandId() {
        return productBrandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public Long getProductFlavorId() {
        return productFlavorId;
    }

    public String getFlavorName() {
        return flavorName;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public String getCartonCode() {
        return cartonCode;
    }

    public Integer getUnitQuantity() {
        return unitQuantity;
    }

    public Integer getCartonQuantity() {
        return cartonQuantity;
    }

    public String getUnitSizeForDisplay() {
        return unitSizeForDisplay;
    }

    public String getCartonSizeForDisplay() {
        return cartonSizeForDisplay;
    }

    public Integer getUnitStockInHand() {
        return unitStockInHand;
    }

    public Integer getCartonStockInHand() {
        return cartonStockInHand;
    }


    public boolean isProductSelected(){
        return (getQtyCarton()!=null || getQtyUnit()!=null &&( getQtyUnit()>0 || getQtyCarton()>0));
    }

    public void setUnitStockInHand(Integer unitStockInHand) {
        this.unitStockInHand = unitStockInHand;
    }

    public void setCartonStockInHand(Integer cartonStockInHand) {
        this.cartonStockInHand = cartonStockInHand;
    }


    public Integer getUnitDefinitionId() {
        return unitDefinitionId;
    }

    public Integer getCartonDefinitionId() {
        return cartonDefinitionId;
    }

    public Integer getActualUnitStock() {
        return actualUnitStock;
    }

    public Integer getActualCartonStock() {
        return actualCartonStock;
    }

    @Override
    public String toString() {
        return String.valueOf(getQtyCarton());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj!=null && obj instanceof Product){
            if(((Product) obj).getId()==getId()) return true;
        }
        return false;
    }
}
