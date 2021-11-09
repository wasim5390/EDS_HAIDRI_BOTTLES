package com.optimus.eds.db.entities.pricing;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class OutletAvailedFreeGoods {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("outletId")
    @Expose
    private Integer outletId;
    @SerializedName("freeGoodGroupId")
    @Expose
    private Integer freeGoodGroupId;
    @SerializedName("freeGoodDetailId")
    @Expose
    private Integer freeGoodDetailId;
    @SerializedName("freeGoodExclusiveId")
    @Expose
    private Integer freeGoodExclusiveId;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("productId")
    @Expose
    private Integer productId;
    @SerializedName("productDefinitionId")
    @Expose
    private Integer productDefinitionId;
    @SerializedName("orderId")
    @Expose
    private Integer orderId;
    @SerializedName("invoiceId")
    @Expose
    private Integer invoiceId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOutletId() {
        return outletId;
    }

    public void setOutletId(Integer outletId) {
        this.outletId = outletId;
    }

    public Integer getFreeGoodGroupId() {
        return freeGoodGroupId;
    }

    public void setFreeGoodGroupId(Integer freeGoodGroupId) {
        this.freeGoodGroupId = freeGoodGroupId;
    }

    public Integer getFreeGoodDetailId() {
        return freeGoodDetailId;
    }

    public void setFreeGoodDetailId(Integer freeGoodDetailId) {
        this.freeGoodDetailId = freeGoodDetailId;
    }

    public Integer getFreeGoodExclusiveId() {
        return freeGoodExclusiveId;
    }

    public void setFreeGoodExclusiveId(Integer freeGoodExclusiveId) {
        this.freeGoodExclusiveId = freeGoodExclusiveId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductDefinitionId() {
        return productDefinitionId;
    }

    public void setProductDefinitionId(Integer productDefinitionId) {
        this.productDefinitionId = productDefinitionId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }
}
