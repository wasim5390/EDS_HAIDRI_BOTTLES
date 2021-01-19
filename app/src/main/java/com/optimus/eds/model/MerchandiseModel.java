package com.optimus.eds.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.entities.Merchandise;

public class MerchandiseModel  {

    public MerchandiseModel(Merchandise merchandise) {
        this.merchandise = merchandise;
    }

    public void setMerchandise(Merchandise merchandise) {
        this.merchandise = merchandise;
    }

    public Merchandise getMerchandise() {
        return merchandise;
    }

    @Expose
    @SerializedName("merchandise")
    private Merchandise merchandise;

}
