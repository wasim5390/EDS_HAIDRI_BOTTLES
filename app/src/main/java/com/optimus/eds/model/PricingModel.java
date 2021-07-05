package com.optimus.eds.model;

public class PricingModel {


    double tradePrice = 0.0 , promos = 0.0 , discounts = 0.0, tax = 0.0;

    public Double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(Double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public void setTradePrice(double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public double getPromos() {
        return promos;
    }

    public void setPromos(double promos) {
        this.promos = promos;
    }

    public double getDiscounts() {
        return discounts;
    }

    public void setDiscounts(double discounts) {
        this.discounts = discounts;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }
}
