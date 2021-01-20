package com.optimus.eds.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AssetStatus {
    @SerializedName("key")
    @Expose
    private Integer key;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("firstIntExtraField")
    @Expose
    private Integer firstIntExtraField;
    @SerializedName("firstStringExtraField")
    @Expose
    private String firstStringExtraField;
    @SerializedName("defaultFlag")
    @Expose
    private Boolean defaultFlag;
    @SerializedName("secondIntExtraField")
    @Expose
    private Integer secondIntExtraField;
    @SerializedName("secondStringExtraField")
    @Expose
    private String secondStringExtraField;
    @SerializedName("hasError")
    @Expose
    private Boolean hasError;
    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFirstIntExtraField() {
        return firstIntExtraField;
    }

    public void setFirstIntExtraField(Integer firstIntExtraField) {
        this.firstIntExtraField = firstIntExtraField;
    }

    public String getFirstStringExtraField() {
        return firstStringExtraField;
    }

    public void setFirstStringExtraField(String firstStringExtraField) {
        this.firstStringExtraField = firstStringExtraField;
    }

    public Boolean getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(Boolean defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public Integer getSecondIntExtraField() {
        return secondIntExtraField;
    }

    public void setSecondIntExtraField(Integer secondIntExtraField) {
        this.secondIntExtraField = secondIntExtraField;
    }

    public String getSecondStringExtraField() {
        return secondStringExtraField;
    }

    public void setSecondStringExtraField(String secondStringExtraField) {
        this.secondStringExtraField = secondStringExtraField;
    }

    public Boolean getHasError() {
        return hasError;
    }

    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return value.toString() ;
    }

    public AssetStatus(String value , Integer key) {
        this.value = value;
        this.key = key;
    }
}
