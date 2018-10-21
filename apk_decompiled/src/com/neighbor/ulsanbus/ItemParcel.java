package com.neighbor.ulsanbus;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;

public class ItemParcel implements Parcelable {
    public static final Creator<ItemParcel> CREATOR = new C02571();
    private String mRouteId;
    private List<String> mStoplist;
    private String mVertaxCount;
    private String mWalkingLength;
    private String mWalkingTime;

    /* renamed from: com.neighbor.ulsanbus.ItemParcel$1 */
    static class C02571 implements Creator<ItemParcel> {
        C02571() {
        }

        public ItemParcel createFromParcel(Parcel source) {
            return new ItemParcel();
        }

        public ItemParcel[] newArray(int size) {
            return new ItemParcel[size];
        }
    }

    public ItemParcel() {
        this.mStoplist = new ArrayList();
    }

    public ItemParcel(Parcel in) {
        readFromParcel(in);
    }

    public void setRouteId(String routeId) {
        this.mRouteId = routeId;
    }

    public void setWalkingTime(String walkingtime) {
        this.mWalkingTime = walkingtime;
    }

    public void setWalkingLength(String walkingLength) {
        this.mWalkingLength = walkingLength;
    }

    public void setVertaxCount(String vertaxcount) {
        this.mVertaxCount = vertaxcount;
    }

    public void addStop(String stop) {
        this.mStoplist.add(stop);
    }

    public String getRouteId() {
        return this.mRouteId;
    }

    public String getWalkingTime() {
        return this.mWalkingTime;
    }

    public String getWalkingLength() {
        return this.mWalkingLength;
    }

    public String getVertaxCount() {
        return this.mVertaxCount;
    }

    public List<String> getStoplist() {
        return this.mStoplist;
    }

    public void readFromParcel(Parcel in) {
        this.mRouteId = in.readString();
        this.mWalkingTime = in.readString();
        this.mWalkingLength = in.readString();
        this.mVertaxCount = in.readString();
        in.readStringList(this.mStoplist);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel arg0, int arg1) {
        arg0.writeString(this.mRouteId);
        arg0.writeString(this.mWalkingTime);
        arg0.writeString(this.mWalkingLength);
        arg0.writeString(this.mVertaxCount);
        arg0.writeStringList(this.mStoplist);
    }
}
