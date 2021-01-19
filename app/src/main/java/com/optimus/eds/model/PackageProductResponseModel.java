package com.optimus.eds.model;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.ProductGroup;

import java.util.List;

public class PackageProductResponseModel extends BaseResponse {

    @SerializedName("productPackages")
    public List<Package> packageList;

    @SerializedName("productGroups")
    public List<ProductGroup> productGroups;

    @SerializedName("products")
    public List<Product> productList;


    public List<Package> getPackageList() {
        return packageList;
    }

    public List<ProductGroup> getProductGroups() {
        return productGroups;
    }

    public List<Product> getProductList() {
        return productList;
    }
}
