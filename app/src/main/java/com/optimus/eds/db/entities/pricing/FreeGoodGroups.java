package com.optimus.eds.db.entities.pricing;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.converters.FreeGoodExclusivesConverter;
import com.optimus.eds.db.converters.GroupFreeGoodDetailConverter;

import java.util.List;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class FreeGoodGroups {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("freeGoodMasterId")
    @Expose
    private Integer freeGoodMasterId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("typeId")
    @Expose
    private Integer typeId;
    @SerializedName("minimumQuantity")
    @Expose
    private Integer minimumQuantity;
    @SerializedName("forEachQuantity")
    @Expose
    private Integer forEachQuantity;
    @SerializedName("maximumQuantity")
    @Expose
    private Integer maximumQuantity;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("isDeleted")
    @Expose
    private Boolean isDeleted;
    @SerializedName("isDifferentProduct")
    @Expose
    private Boolean isDifferentProduct;
    @SerializedName("freeQuantity")
    @Expose
    private Integer freeQuantity;
    @SerializedName("freeQuantityTypeId")
    @Expose
    private Integer freeQuantityTypeId;

    @TypeConverters(GroupFreeGoodDetailConverter.class)
    @SerializedName("groupFreeGoodDetails")
    @Expose
    private List<GroupFreeGoodDetails> groupFreeGoodDetails;
    @TypeConverters(FreeGoodExclusivesConverter.class)
    @SerializedName("freeGoodExclusives")
    @Expose
    private List<FreeGoodExclusives> freeGoodExclusives = null;


    // for query purpose ( appliedFreeGoodGroups )


    Integer outletChannelAttributeCount ;

    Integer channelAttributeCount ;

    Integer outletGroupAttributeCount ;

    Integer groupAttributeCount ;

    Integer outletVPOAttributeCount ;

    Integer vpoAttributeCount ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFreeGoodMasterId() {
        return freeGoodMasterId;
    }

    public void setFreeGoodMasterId(Integer freeGoodMasterId) {
        this.freeGoodMasterId = freeGoodMasterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public Integer getForEachQuantity() {
        return forEachQuantity;
    }

    public void setForEachQuantity(Integer forEachQuantity) {
        this.forEachQuantity = forEachQuantity;
    }

    public Integer getMaximumQuantity() {
        return maximumQuantity;
    }

    public void setMaximumQuantity(Integer maximumQuantity) {
        this.maximumQuantity = maximumQuantity;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }



    public Integer getFreeQuantity() {
        return freeQuantity;
    }

    public void setFreeQuantity(Integer freeQuantity) {
        this.freeQuantity = freeQuantity;
    }

    public Integer getFreeQuantityTypeId() {
        return freeQuantityTypeId;
    }

    public void setFreeQuantityTypeId(Integer freeQuantityTypeId) {
        this.freeQuantityTypeId = freeQuantityTypeId;
    }

    public List<GroupFreeGoodDetails> getGroupFreeGoodDetails() {
        return groupFreeGoodDetails;
    }

    public void setGroupFreeGoodDetails(List<GroupFreeGoodDetails> groupFreeGoodDetails) {
        this.groupFreeGoodDetails = groupFreeGoodDetails;
    }

    public List<FreeGoodExclusives> getFreeGoodExclusives() {
        return freeGoodExclusives;
    }

    public void setFreeGoodExclusives(List<FreeGoodExclusives> freeGoodExclusives) {
        this.freeGoodExclusives = freeGoodExclusives;
    }


    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean getDifferentProduct() {
        return isDifferentProduct;
    }

    public void setDifferentProduct(Boolean differentProduct) {
        isDifferentProduct = differentProduct;
    }

    public Integer getOutletChannelAttributeCount() {
        return outletChannelAttributeCount != null ? outletChannelAttributeCount : 0;
    }

    public void setOutletChannelAttributeCount(Integer outletChannelAttributeCount) {
        this.outletChannelAttributeCount = outletChannelAttributeCount;
    }

    public Integer getChannelAttributeCount() {
        return channelAttributeCount != null ? channelAttributeCount : 0;
    }

    public void setChannelAttributeCount(Integer channelAttributeCount) {
        this.channelAttributeCount = channelAttributeCount;
    }

    public Integer getOutletGroupAttributeCount() {
        return outletGroupAttributeCount != null ? outletGroupAttributeCount : 0;
    }

    public void setOutletGroupAttributeCount(Integer outletGroupAttributeCount) {
        this.outletGroupAttributeCount = outletGroupAttributeCount;
    }

    public Integer getGroupAttributeCount() {
        return groupAttributeCount != null ? groupAttributeCount : 0;
    }

    public void setGroupAttributeCount(Integer groupAttributeCount) {
        this.groupAttributeCount = groupAttributeCount;
    }

    public Integer getOutletVPOAttributeCount() {
        return outletVPOAttributeCount != null ? outletVPOAttributeCount : 0;
    }

    public void setOutletVPOAttributeCount(Integer outletVPOAttributeCount) {
        this.outletVPOAttributeCount = outletVPOAttributeCount;
    }

    public Integer getVpoAttributeCount() {
        return vpoAttributeCount != null ? vpoAttributeCount : 0;
    }

    public void setVpoAttributeCount(Integer vpoAttributeCount) {
        this.vpoAttributeCount = vpoAttributeCount;
    }
}
