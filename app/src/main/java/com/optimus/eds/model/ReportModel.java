
package com.optimus.eds.model;


public class ReportModel {

    private Float mCarton;
    private Float mCartonConfirm;
    private Double mGrandTotal;
    private Double mGrandTotalConfirm;
    private Integer mSkuSize;
    private Float totalSku;
    private Integer totalOrders;
    private Integer totalConfirmOrders;


    private Integer pjpCount, completedOutletsCount,productiveOutletCount , pendingCount;

    public Float getCarton() {
        return mCarton==null?0f:mCarton;
    }

    public void setCarton(Float carton) {
        mCarton = carton;
    }

    public Integer getTotalConfirmOrders() {
        return totalConfirmOrders;
    }

    public void setTotalConfirmOrders(Integer totalConfirmOrders) {
        this.totalConfirmOrders = totalConfirmOrders;
    }

    public Float getCartonConfirm() {
        return mCartonConfirm==null?0:mCartonConfirm;
    }

    public void setCartonConfirm(Float carton) {
        mCartonConfirm = carton;
    }

    public Double getTotalAmount() {
        return mGrandTotal==null?0:mGrandTotal;
    }

    public void setTotalSale(Double totalAmount) {
        mGrandTotal = totalAmount;
    }

    public Double getTotalAmountConfirm() {
        return mGrandTotalConfirm==null?0:mGrandTotalConfirm;
    }

    public void setTotalSaleConfirm(Double totalAmount) {
        mGrandTotalConfirm = totalAmount;
    }

    public Integer getTotalOrders() {
        return totalOrders==null?0:totalOrders;
    }

    public Integer getSkuSize() {
         return mSkuSize==null?0:mSkuSize;
    }

    public float getAvgSkuSize(){
        if(getProductiveOutletCount()<1)
            return 0;
        int skuSize = getSkuSize();
        return (float)skuSize/ getProductiveOutletCount();
    }

    public Double getDropSize(){
        if(getProductiveOutletCount()<1)
            return 0.0;
        return getTotalAmount()/ getProductiveOutletCount();
    }

    public void setSkuSize(int mSkuSize) {
        this.mSkuSize = mSkuSize;
    }

    public Integer getPjpCount() {
        return pjpCount==null?0:pjpCount;
    }

    public void setPjpCount(Integer pjpCount) {
        this.pjpCount = pjpCount;
    }

    public void setCounts(Integer pjpCount,Integer completedTaskCount, Integer productiveOutletCount , Integer pendingCount) {
        this.pjpCount = pjpCount;
        this.completedOutletsCount = completedTaskCount;
        this.productiveOutletCount = productiveOutletCount;
        this.pendingCount = pendingCount ;
    }

    public Integer getCompletedOutletsCount() {
        return completedOutletsCount ==null?0: completedOutletsCount;
    }

    public void setCompletedOutletsCount(Integer completedTaskCount) {
        this.completedOutletsCount = completedTaskCount;
    }

    public Integer getProductiveOutletCount() {
        return productiveOutletCount==null?0:productiveOutletCount;
    }

    public Float getTotalSku() {
        return totalSku != null ? totalSku : 0;
    }

    public void setTotalSku(Float totalSku) {
        this.totalSku = totalSku;
    }

    public void setProductiveOutletCount(Integer productiveOutletCount) {
        this.productiveOutletCount = productiveOutletCount;
    }

    public Integer getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(Integer pendingCount) {
        this.pendingCount = pendingCount;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

}
