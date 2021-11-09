package com.optimus.eds.model;

import java.util.List;

public class PrIsConditionBundle {

    private List<UdtProductQuantity> productList;
    private Integer conditionTypeId;
    private Integer productDefinitionId ;
    private Integer organizationId ;
    private Integer outletId  ;
    private String orderDate  ;

    public PrIsConditionBundle(List<UdtProductQuantity> productList, Integer conditionTypeId, Integer productDefinitionId, Integer organizationId, Integer outletId, String orderDate) {
        this.productList = productList;
        this.conditionTypeId = conditionTypeId;
        this.productDefinitionId = productDefinitionId;
        this.organizationId = organizationId;
        this.outletId = outletId;
        this.orderDate = orderDate;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public List<UdtProductQuantity> getProductList() {
        return productList;
    }

    public void setProductList(List<UdtProductQuantity> productList) {
        this.productList = productList;
    }

    public Integer getConditionTypeId() {
        return conditionTypeId;
    }

    public void setConditionTypeId(Integer conditionTypeId) {
        this.conditionTypeId = conditionTypeId;
    }

    public Integer getProductDefinitionId() {
        return productDefinitionId;
    }

    public void setProductDefinitionId(Integer productDefinitionId) {
        this.productDefinitionId = productDefinitionId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getOutletId() {
        return outletId;
    }

    public void setOutletId(Integer outletId) {
        this.outletId = outletId;
    }
}
