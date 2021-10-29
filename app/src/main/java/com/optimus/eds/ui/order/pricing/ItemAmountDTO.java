package com.optimus.eds.ui.order.pricing;

public class ItemAmountDTO {

    public boolean isMaxLimitReached() {
        return IsMaxLimitReached;
    }

    public void setMaxLimitReached(boolean maxLimitReached) {
        IsMaxLimitReached = maxLimitReached;
    }

    public double getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        TotalPrice = totalPrice;
    }

    public double getBlockPrice() {
        return BlockPrice;
    }

    public void setBlockPrice(double blockPrice) {
        BlockPrice = blockPrice;
    }

    public boolean IsMaxLimitReached;
    public double TotalPrice;
    public double BlockPrice;
}
