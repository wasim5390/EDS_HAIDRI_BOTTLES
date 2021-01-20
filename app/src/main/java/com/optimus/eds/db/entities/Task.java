package com.optimus.eds.db.entities;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Task {
    @PrimaryKey
    @SerializedName("taskId")
    Integer taskId;
    @SerializedName("taskName")
    String taskName;
    @SerializedName("taskDate")
    String taskDate;
    @SerializedName("outletId")
    Long outletId;
    @SerializedName("completedDate")
    String completedDate;

    @SerializedName("status")
    Boolean status;

    public Task() {
    }

    @Ignore
    public Task(Long outletId,Integer taskId, String taskName, String taskDate, String completedDate, Boolean status) {
       this.outletId = outletId;
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDate = taskDate;
        this.completedDate = completedDate;
        this.status = status;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }
}
