package com.optimus.eds.ui.reports;

import java.util.Objects;

public class Quantity{

    public Quantity(long productId) {
        this.itemId = productId;
    }

    public Quantity(long productId, float quantity) {
        this.itemId = productId;
        this.quantity = quantity;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    float quantity;
    Long itemId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity that = (Quantity) o;
        return itemId.equals(that.itemId) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, quantity);
    }


}
