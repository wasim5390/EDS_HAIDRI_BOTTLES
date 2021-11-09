package com.optimus.eds.model;

public class UdtProductQuantity {
    private Integer productDefinitionId;
    private Integer quantity;

    public UdtProductQuantity(Integer productDefinitionId, Integer quantity) {
        this.productDefinitionId = productDefinitionId;
        this.quantity = quantity;
    }

    public Integer getProductDefinitionId() {
        return productDefinitionId;
    }

    public void setProductDefinitionId(Integer productDefinitionId) {
        this.productDefinitionId = productDefinitionId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
