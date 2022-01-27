
package com.optimus.eds.model;

import com.google.gson.annotations.Expose;

public class Configuration {

    @Expose
    private Boolean endDayOnPjpCompletion;
    @Expose
    private Integer geoFenceMinRadius;
    @Expose
    private Boolean geoFenceRequired;

    @Expose
    private Boolean taskExists;


    public Configuration() {
    }

    public void setEndDayOnPjpCompletion(Boolean endDayOnPjpCompletion) {
        this.endDayOnPjpCompletion = endDayOnPjpCompletion;
    }

    public void setGeoFenceMinRadius(Integer geoFenceMinRadius) {
        this.geoFenceMinRadius = geoFenceMinRadius;
    }

    public void setGeoFenceRequired(Boolean geoFenceRequired) {
        this.geoFenceRequired = geoFenceRequired;
    }

    public Boolean getEndDayOnPjpCompletion() {
        return endDayOnPjpCompletion!=null?endDayOnPjpCompletion:true;
    }

    public Integer getGeoFenceMinRadius() {
        return geoFenceMinRadius==null?50:geoFenceMinRadius;
    }

    public Boolean getGeoFenceRequired() {
        return geoFenceRequired==null?false:geoFenceRequired;
    }

    public boolean taskExists() {
        return taskExists==null?false:taskExists;
    }


}
