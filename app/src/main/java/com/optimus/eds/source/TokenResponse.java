
package com.optimus.eds.source;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.model.BaseResponse;

import java.io.Serializable;


public class TokenResponse implements Serializable {

    @SerializedName("access_token")
    private String mAccessToken;
    @SerializedName("expires_in")
    private Long mExpiresIn;
    @SerializedName("token_type")
    private String mTokenType;
    private Boolean success=false;
    @SerializedName("error_description")
    private String errorMessage;

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

    public Long getExpiresIn() {
        return mExpiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        mExpiresIn = expiresIn;
    }

    public String getTokenType() {
        return mTokenType;
    }

    public void setTokenType(String tokenType) {
        mTokenType = tokenType;
    }
    public Boolean isSuccess() {
        return success==null?false:success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
