package com.optimus.eds.db.entities.pricing;

import com.optimus.eds.db.converters.FreeGoodDetailsConverter;
import com.optimus.eds.db.converters.FreeGoodEntityDetailsConverter;
import com.optimus.eds.db.converters.FreeGoodGroupsConverter;

import java.util.List;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class FreeGoodMasters {

    @PrimaryKey(autoGenerate = false)
    private Integer freeGoodMasterId;
    private String name;
    public Boolean isActive ;
    public Boolean isDeleted ;
    public Boolean isBundle ;
    public Integer accessSequenceId ;
    public String accessSequenceText ;
    @TypeConverters(FreeGoodGroupsConverter.class)
    public List<FreeGoodGroups> freeGoodGroups ;
    @TypeConverters(FreeGoodDetailsConverter.class)
    public List<FreeGoodDetails> freeGoodDetails ;
    @TypeConverters(FreeGoodEntityDetailsConverter.class)
    public List<FreeGoodEntityDetails> freeGoodEntityDetails;


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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Integer getAccessSequenceId() {
        return accessSequenceId;
    }

    public void setAccessSequenceId(Integer accessSequenceId) {
        this.accessSequenceId = accessSequenceId;
    }

    public String getAccessSequenceText() {
        return accessSequenceText;
    }

    public void setAccessSequenceText(String accessSequenceText) {
        this.accessSequenceText = accessSequenceText;
    }

    public List<FreeGoodGroups> getFreeGoodGroups() {
        return freeGoodGroups;
    }

    public void setFreeGoodGroups(List<FreeGoodGroups> freeGoodGroups) {
        this.freeGoodGroups = freeGoodGroups;
    }

    public List<FreeGoodDetails> getFreeGoodDetails() {
        return freeGoodDetails;
    }

    public void setFreeGoodDetails(List<FreeGoodDetails> freeGoodDetails) {
        this.freeGoodDetails = freeGoodDetails;
    }

    public List<FreeGoodEntityDetails> getFreeGoodEntityDetails() {
        return freeGoodEntityDetails;
    }

    public void setFreeGoodEntityDetails(List<FreeGoodEntityDetails> freeGoodEntityDetails) {
        this.freeGoodEntityDetails = freeGoodEntityDetails;
    }

    public Boolean getBundle() {
        return isBundle;
    }

    public void setBundle(Boolean bundle) {
        isBundle = bundle;
    }
}
