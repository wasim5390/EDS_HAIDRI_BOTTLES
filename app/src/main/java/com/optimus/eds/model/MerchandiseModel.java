package com.optimus.eds.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.entities.Merchandise;

import androidx.room.ColumnInfo;

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

    @ColumnInfo(name = "statusId")
    public Integer statusId;

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }
}
