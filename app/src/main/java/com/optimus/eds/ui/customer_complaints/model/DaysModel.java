package com.optimus.eds.ui.customer_complaints.model;

/**
 * Created By apple on 5/2/19
 */
public class DaysModel {

    String name;
    private boolean selected;

    public DaysModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
