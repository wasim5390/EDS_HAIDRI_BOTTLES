package com.optimus.eds.model;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.db.entities.Asset;
import com.optimus.eds.db.entities.LookUp;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Promotion;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.db.entities.Task;

import java.util.ArrayList;
import java.util.List;

public class RouteOutletResponseModel extends BaseResponse{

    @SerializedName("routes")
    List<Route> routeList;
    @SerializedName("outlets")
    List<Outlet> outletList;
    @SerializedName("assets")
    List<Asset> assetList;

    @SerializedName("distributionId")
    Integer distributionId;
    @SerializedName("employeeName")
    String employeeName;

    @SerializedName("configuration")
    Configuration configuration;

    @SerializedName("targetVSAchievement")
    TargetVsAchievement targetVsAchievement ;

    @SerializedName("systemConfiguration")
    SystemConfiguration systemConfiguration ;

    @SerializedName("promosAndFOC")
    List<Promotion> promosAndFOC;

    @SerializedName("tasks")
    List<Task> tasks;

    @SerializedName("lookup")
    LookUp lookUp;

    @SerializedName("orders")
    List<Order> orders;

    @SerializedName("deliveryDate")
    Long deliveryDate;

    public List<Route> getRouteList() {
        return routeList==null?new ArrayList<>():routeList;
    }

    public List<Outlet> getOutletList() {
        return outletList==null?new ArrayList<>():outletList;
    }

    public List<Asset> getAssetList() {
        return assetList;
    }

    public List<Task> getTasksList(){
        return tasks==null?new ArrayList<>():tasks;
    }

    public Integer getDistributionId() {
        return distributionId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public TargetVsAchievement getTargetVsAchievement() {
        return targetVsAchievement;
    }

    public void setTargetVsAchievement(TargetVsAchievement targetVsAchievement) {
        this.targetVsAchievement = targetVsAchievement;
    }

    public List<Promotion> getPromosAndFOC() {
        return promosAndFOC;
    }

    public void setPromosAndFOC(List<Promotion> promosAndFOC) {
        this.promosAndFOC = promosAndFOC;
    }

    public void setRouteList(List<Route> routeList) {
        this.routeList = routeList;
    }

    public void setOutletList(List<Outlet> outletList) {
        this.outletList = outletList;
    }

    public void setAssetList(List<Asset> assetList) {
        this.assetList = assetList;
    }

    public void setDistributionId(Integer distributionId) {
        this.distributionId = distributionId;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public SystemConfiguration getSystemConfiguration() {
        return systemConfiguration;
    }

    public void setSystemConfiguration(SystemConfiguration systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public LookUp getLookUp() {
        return lookUp;
    }

    public void setLookUp(LookUp lookUp) {
        this.lookUp = lookUp;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Long getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Long deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
