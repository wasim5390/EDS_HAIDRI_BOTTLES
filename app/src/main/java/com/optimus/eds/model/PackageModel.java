package com.optimus.eds.model;


import com.optimus.eds.db.entities.Product;

import java.util.List;

public class PackageModel {

    List<Product> products;
    Long packageId;
    String packageName;

    public PackageModel(Long packageId,String pkgName,List<Product> products) {
        this.products = products;
        this.packageId = packageId;
        this.packageName = pkgName;
    }

    public Long getPackageId() {
        return packageId;
    }

    public String getPackageName() {
        return packageName;
    }
    public List<Product> getProducts() {
        return products;
    }
}
