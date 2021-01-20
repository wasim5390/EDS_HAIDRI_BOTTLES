package com.optimus.eds.model;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.BaseModel;

public class AppUpdateModel extends BaseModel {


    public String file;
    public Long version;
    public Long date;
    @SerializedName("error_msg")
    public String msg;

    public String getFile() {
        return file;
    }

    public Long getVersion() {
        return version;
    }

    public Long getDate() {
        return date;
    }

    public String getMsg() {
        return msg;
    }
}
