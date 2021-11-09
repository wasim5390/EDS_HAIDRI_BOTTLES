package com.optimus.eds.ui.order.free_goods;
import com.optimus.eds.ui.order.pricing.Message;

import java.util.List;

import androidx.room.Ignore;

public class FreeGoodOutputDTO {

    public Integer freeGoodGroupId ;
    public Integer freeGoodDetailId ;
    public Integer freeGoodExclusiveId;
    public Integer productId;
    public String productName;
    public String productCode;
    public Integer productDefinitionId;
    public String productSize;
    public Boolean isDefault;
    public String definitionCode;
    public Integer stockInHand;
    public Integer maximumFreeGoodQuantity;
    public Integer freeGoodQuantity;
    public Integer freeGoodTypeId;  //Inclusive=1/Exclusive=2
    public Integer finalFreeGoodsQuantity;  //FreeGoodsQuantity If StockInHand > FreeGoodsQuantity ELSE StockInHand OR MaxQuantity
    public Integer qualifiedFreeGoodQuantity; //FreeGood quantity which the order deserve.
    //list of message
    @Ignore
    public List<Message> messages;
    public Integer parentId ;
    public Integer forEachQuantity;
    public Boolean isBundle;
    public Integer freeQuantityTypeId; //Primary, Optional

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

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getProductDefinitionId() {
        return productDefinitionId;
    }

    public void setProductDefinitionId(int productDefinitionId) {
        this.productDefinitionId = productDefinitionId;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        this.isDefault = aDefault;
    }

    public String getDefinitionCode() {
        return definitionCode;
    }

    public void setDefinitionCode(String definitionCode) {
        this.definitionCode = definitionCode;
    }

    public Integer getStockInHand() {
        return stockInHand;
    }

    public void setStockInHand(Integer stockInHand) {
        this.stockInHand = stockInHand;
    }

    public Integer getMaximumFreeGoodQuantity() {
        return maximumFreeGoodQuantity;
    }

    public void setMaximumFreeGoodQuantity(Integer maximumFreeGoodQuantity) {
        this.maximumFreeGoodQuantity = maximumFreeGoodQuantity;
    }

    public int getFreeGoodQuantity() {
        return freeGoodQuantity;
    }

    public void setFreeGoodQuantity(int freeGoodQuantity) {
        this.freeGoodQuantity = freeGoodQuantity;
    }

    public int getFreeGoodTypeId() {
        return freeGoodTypeId;
    }

    public void setFreeGoodTypeId(int freeGoodTypeId) {
        this.freeGoodTypeId = freeGoodTypeId;
    }

    public int getFinalFreeGoodsQuantity() {
        return finalFreeGoodsQuantity;
    }

    public void setFinalFreeGoodsQuantity(int finalFreeGoodsQuantity) {
        this.finalFreeGoodsQuantity = finalFreeGoodsQuantity;
    }

    public int getQualifiedFreeGoodQuantity() {
        return qualifiedFreeGoodQuantity;
    }

    public void setQualifiedFreeGoodQuantity(int qualifiedFreeGoodQuantity) {
        this.qualifiedFreeGoodQuantity = qualifiedFreeGoodQuantity;
    }
//
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getForEachQuantity() {
        return forEachQuantity;
    }

    public void setForEachQuantity(int forEachQuantity) {
        this.forEachQuantity = forEachQuantity;
    }

    public boolean isBundle() {
        return isBundle;
    }

    public void setBundle(boolean bundle) {
        this.isBundle = bundle;
    }

    public int getFreeQuantityTypeId() {
        return freeQuantityTypeId;
    }

    public void setFreeQuantityTypeId(int freeQuantityTypeId) {
        this.freeQuantityTypeId = freeQuantityTypeId;
    }
}
