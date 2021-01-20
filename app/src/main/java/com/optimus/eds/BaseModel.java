package com.optimus.eds;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BaseModel implements Serializable {

    @SerializedName("success")
    private Boolean mSuccess;

    public Boolean getSuccess() {
        return mSuccess;
    }
}
