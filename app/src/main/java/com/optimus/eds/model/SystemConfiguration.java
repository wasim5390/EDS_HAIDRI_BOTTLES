package com.optimus.eds.model;

public class SystemConfiguration {

    private Boolean canNotPunchOrderInUnits ;
    private Boolean hideCustomerInfoInOrderingApp ;
    private String productFilter ;
    private String productView ;

    public Boolean getCanNotPunchOrderInUnits() {
        return canNotPunchOrderInUnits;
    }

    public void setCanNotPunchOrderInUnits(Boolean canNotPunchOrderInUnits) {
        this.canNotPunchOrderInUnits = canNotPunchOrderInUnits;
    }

    public Boolean getHideCustomerInfoInOrderingApp() {
        return hideCustomerInfoInOrderingApp;
    }

    public void setHideCustomerInfoInOrderingApp(Boolean hideCustomerInfoInOrderingApp) {
        this.hideCustomerInfoInOrderingApp = hideCustomerInfoInOrderingApp;
    }

    public String getProductFilter() {
        return productFilter;
    }

    public void setProductFilter(String productFilter) {
        this.productFilter = productFilter;
    }

    public String getProductView() {
        return productView;
    }

    public void setProductView(String productView) {
        this.productView = productView;
    }
}
