package com.optimus.eds.db.entities.pricing;

public class KeyValue {
    public Integer key;
    public String value;
    public String description;
    public Integer firstIntExtraField;
    public String firstStringExtraField;
    public Boolean defaultFlag;
    public Integer secondIntExtraField;
    public String secondStringExtraField;
    public Boolean hasError;
    public String errorMessage;
    public Integer quantity;
    public Integer minValue;
    public Integer maxValue;

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

    public Integer getQuantity() {
            return quantity;
    }

    public void setQuantity(Integer quantity) {
            this.quantity = quantity;
    }

    public Integer getMinValue() {
            return minValue;
    }

    public void setMinValue(Integer minValue) {
            this.minValue = minValue;
    }

    public Integer getMaxValue() {
            return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
            this.maxValue = maxValue;
    }
}