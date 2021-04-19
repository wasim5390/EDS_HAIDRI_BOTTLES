package com.optimus.eds.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class TargetVsAchievement {


    private Integer targetQuantity ;
    private Integer targetAmount ;
    private Double achievedQuantityPercentage ;
    private Integer mtdSaleQuantiy ;
    private Double achievedAmountPercentage ;
    private Double perDayRequiredSaleQuantiy ;
    private Double perDayRequiredSaleAmount ;

    public Integer getTargetQuantity() {
        return targetQuantity;
    }

    public void setTargetQuantity(Integer targetQuantity) {
        this.targetQuantity = targetQuantity;
    }

    public Integer getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(Integer targetAmount) {
        this.targetAmount = targetAmount;
    }

    public Double getAchievedQuantityPercentage() {
        return achievedQuantityPercentage;
    }

    public void setAchievedQuantityPercentage(Double achievedQuantityPercentage) {
        this.achievedQuantityPercentage = achievedQuantityPercentage;
    }

    public Double getAchievedAmountPercentage() {
        return achievedAmountPercentage;
    }

    public void setAchievedAmountPercentage(Double achievedAmountPercentage) {
        this.achievedAmountPercentage = achievedAmountPercentage;
    }

    public Double getPerDayRequiredSaleQuantity() {
        return perDayRequiredSaleQuantiy;
    }

    public void setPerDayRequiredSaleQuantity(Double perDayRequiredSaleQuantity) {
        this.perDayRequiredSaleQuantiy = perDayRequiredSaleQuantity;
    }

    public Double getPerDayRequiredSaleAmount() {
        return perDayRequiredSaleAmount;
    }

    public void setPerDayRequiredSaleAmount(Double perDayRequiredSaleAmount) {
        this.perDayRequiredSaleAmount = perDayRequiredSaleAmount;
    }

    public Integer getMtdSales() {
        return mtdSaleQuantiy;
    }

    public void setMtdSales(Integer mtdSales) {
        this.mtdSaleQuantiy = mtdSales;
    }
}
