package com.optimus.eds.db.entities;

import com.optimus.eds.db.converters.LookUpConverter;
import com.optimus.eds.model.AssetStatus;

import java.util.List;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class LookUp {

    @PrimaryKey(autoGenerate = true)
    private Integer lookUpId;

    @TypeConverters(LookUpConverter.class)
    List<AssetStatus> assetStatus ;

    public Integer getLookUpId() {
        return lookUpId;
    }

    public void setLookUpId(Integer lookUpId) {
        this.lookUpId = lookUpId;
    }

    public List<AssetStatus> getAssetStatus() {
        return assetStatus;
    }

    public void setAssetStatus(List<AssetStatus> assetStatus) {
        this.assetStatus = assetStatus;
    }
}
