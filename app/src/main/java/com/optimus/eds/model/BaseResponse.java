package com.optimus.eds.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sidhu on 4/11/2018.
 */

public class BaseResponse implements Serializable {


    public BaseResponse(Boolean success, String errorMessage, Integer errorCode) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public BaseResponse() {
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    private Boolean success;
    private String errorMessage;
    private Integer errorCode;

    public void setResponseMsg(String responseMsg) {
        this.errorMessage = responseMsg;
    }
    public String getResponseMsg() {
        return errorMessage;
    }
    public Boolean isSuccess() {
        return success==null?true:success;
    }
    public Integer getErrorCode() {
        return errorCode==null?1:errorCode;
    }
    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

}
