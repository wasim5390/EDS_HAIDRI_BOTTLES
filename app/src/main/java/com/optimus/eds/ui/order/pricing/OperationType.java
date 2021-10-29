package com.optimus.eds.ui.order.pricing;

public enum OperationType {

    PLUS(1),MINUS(2);
    private final int type;
    OperationType(int type)
    {
        this.type = type;
    }
}
