package com.optimus.eds.ui.customer_complaints.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created By apple on 5/1/19
 */
public class ComplaintReasonModel implements Parcelable {

    String reason;
    boolean isSelected;


    public ComplaintReasonModel(String reason,boolean isSelected) {
        this.reason = reason;
        this.isSelected = isSelected;
    }

    private ComplaintReasonModel(Parcel in) {
        reason = in.readString();
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof ComplaintReasonModel)) return false;
//
//        ComplaintReasonModel reasonModel = (ComplaintReasonModel) o;
//
//        if (isSelected() != reasonModel.isSelected()) return false;
//        return getReason() != null ? getReason().equals(reasonModel.getReason()) : reasonModel.getReason() == null;
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(reason);
    }


    public static final Creator<ComplaintReasonModel> CREATOR = new Creator<ComplaintReasonModel>() {
        @Override
        public ComplaintReasonModel createFromParcel(Parcel in) {
            return new ComplaintReasonModel(in);
        }

        @Override
        public ComplaintReasonModel[] newArray(int size) {
            return new ComplaintReasonModel[size];
        }
    };
}
