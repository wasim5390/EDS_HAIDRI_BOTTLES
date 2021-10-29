package com.optimus.eds.ui.order.pricing;

import com.optimus.eds.db.entities.UnitPriceBreakDown;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

public class PriceOutputDTO {

   private BigDecimal TotalPrice;
    private Collection<UnitPriceBreakDown> PriceBreakdown;

    public Collection<Message> Messages;

    public PriceOutputDTO() {
        PriceBreakdown = new ArrayList<>();
        Messages = new ArrayList<>();
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        TotalPrice = totalPrice;
    }

    public void setPriceBreakdown(Collection<UnitPriceBreakDown> priceBreakdown) {
        PriceBreakdown = priceBreakdown;
    }
    public BigDecimal getTotalPrice() {
        return TotalPrice;
    }

    public Collection<UnitPriceBreakDown> getPriceBreakdown() {
        return PriceBreakdown;
    }

    public Collection<Message> getMessages() {
        return Messages;
    }
}
