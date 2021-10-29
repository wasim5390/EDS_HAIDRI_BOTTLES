package com.optimus.eds.ui.order.free_goods;

public class prGetFreeGoods {

    public int FreeGoodGroupId;
    public int FreeGoodTypeId;
    public boolean IsBundle;
    public int ForEachQuantity;
    public int MaximumQuantity;
    public int FreeQuantityTypeId; //Primary, Optional

    public int getFreeGoodGroupId() {
        return FreeGoodGroupId;
    }

    public void setFreeGoodGroupId(int freeGoodGroupId) {
        FreeGoodGroupId = freeGoodGroupId;
    }

    public int getFreeGoodTypeId() {
        return FreeGoodTypeId;
    }

    public void setFreeGoodTypeId(int freeGoodTypeId) {
        FreeGoodTypeId = freeGoodTypeId;
    }

    public boolean isBundle() {
        return IsBundle;
    }

    public void setBundle(boolean bundle) {
        IsBundle = bundle;
    }

    public int getForEachQuantity() {
        return ForEachQuantity;
    }

    public void setForEachQuantity(int forEachQuantity) {
        ForEachQuantity = forEachQuantity;
    }

    public int getMaximumQuantity() {
        return MaximumQuantity;
    }

    public void setMaximumQuantity(int maximumQuantity) {
        MaximumQuantity = maximumQuantity;
    }

    public int getFreeQuantityTypeId() {
        return FreeQuantityTypeId;
    }

    public void setFreeQuantityTypeId(int freeQuantityTypeId) {
        FreeQuantityTypeId = freeQuantityTypeId;
    }


}
