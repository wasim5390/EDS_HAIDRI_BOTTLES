package com.optimus.eds.ui.order.pricing;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ProductQuantity {

    @PrimaryKey
    public Integer ProductDefinitionId;
    public int Quantity;
    public Long packageId;


    public ProductQuantity(int productDefinitionId, int quantity , Long packageId) {
        ProductDefinitionId = productDefinitionId;
        Quantity = quantity;
        this.packageId = packageId;
    }

    public ProductQuantity() {
    }

    public void setProductDefinitionId(Integer productDefinitionId) {
        ProductDefinitionId = productDefinitionId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public int getProductDefinitionId() {
        return ProductDefinitionId;
    }

    public void setProductDefinitionId(int productDefinitionId) {
        ProductDefinitionId = productDefinitionId;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }


    /**
     * Two ProductQuantity are equal if their ProductDefinitionId is same.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ProductQuantity)
            return (this.ProductDefinitionId.equals(((ProductQuantity) obj).ProductDefinitionId));
        return false;
    }
}
