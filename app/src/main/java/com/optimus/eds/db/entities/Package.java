package com.optimus.eds.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;


@Entity
public class Package {

    @PrimaryKey
    @SerializedName("productPackageId")
    public Long packageId;
    @SerializedName("productPackageName")
    public String packageName;

    public Package(Long packageId, String packageName) {
        this.packageId = packageId;
        this.packageName = packageName;
    }

    public Long getPackageId() {
        return packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public String toString() {
        return packageName ;
    }
}
