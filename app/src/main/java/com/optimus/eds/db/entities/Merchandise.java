package com.optimus.eds.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.ui.merchandize.MerchandiseImage;

import java.io.Serializable;
import java.util.List;

/**
 * Created By sidhu on 4/23/19
 */

@Entity(tableName = "Merchandise")
public class Merchandise implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "outletId")
    public Long outletId;

    @ColumnInfo(name = "remarks")
    private String remarks;

    @ColumnInfo(name = "merchandiseImages")
    private List<MerchandiseImage> merchandiseImages;

    @ColumnInfo(name = "assets")
    @SerializedName("assets")
    private List<Asset> assetList;


    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public List<MerchandiseImage> getMerchandiseImages() {
        return merchandiseImages;
    }

    public void setMerchandiseImages(List<MerchandiseImage> merchandiseImages) {
        this.merchandiseImages = merchandiseImages;
    }

    public List<Asset> getAssetList() {
        return assetList;
    }

    public void setAssetList(List<Asset> assetList) {
        this.assetList = assetList;
    }

    public String getRemarks() {
        return remarks==null?"":remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
