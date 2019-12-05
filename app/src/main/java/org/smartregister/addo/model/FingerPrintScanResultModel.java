package org.smartregister.addo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FingerPrintScanResultModel implements Parcelable {

    private String name;
    private String gender;
    private String village;

    public FingerPrintScanResultModel(String name, String gender, String village) {
        this.name = name;
        this.gender = gender;
        this.village = village;
    }

    protected FingerPrintScanResultModel(Parcel in) {
        name = in.readString();
        gender = in.readString();
        village = in.readString();
    }

    public static final Creator<FingerPrintScanResultModel> CREATOR = new Creator<FingerPrintScanResultModel>() {
        @Override
        public FingerPrintScanResultModel createFromParcel(Parcel in) {
            return new FingerPrintScanResultModel(in);
        }

        @Override
        public FingerPrintScanResultModel[] newArray(int size) {
            return new FingerPrintScanResultModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(gender);
        dest.writeString(village);
    }
}
