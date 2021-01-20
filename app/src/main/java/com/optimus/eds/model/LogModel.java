
package com.optimus.eds.model;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.BaseActivity;

public class LogModel extends BaseResponse {

    @SerializedName("distributionId")
    private Long mDistributionId;

    @SerializedName("operationTypeId")
    private Long mOperationTypeId;
    @SerializedName("salesmanId")
    private Long mSalesmanId;
    @SerializedName("startDay")
    private Long mStartDay;

    public Long getDistributionId() {
        return mDistributionId;
    }

    public void setDistributionId(Long distributionId) {
        mDistributionId = distributionId;
    }

    public Long getOperationTypeId() {
        return mOperationTypeId;
    }

    public void setOperationTypeId(Long operationTypeId) {
        mOperationTypeId = operationTypeId;
    }

    public Long getSalesmanId() {
        return mSalesmanId;
    }

    public void setSalesmanId(Long salesmanId) {
        mSalesmanId = salesmanId;
    }

    public Long getStartDay() {
        return mStartDay;
    }

    public void setStartDay(Long startDay) {
        mStartDay = startDay;
    }


}
