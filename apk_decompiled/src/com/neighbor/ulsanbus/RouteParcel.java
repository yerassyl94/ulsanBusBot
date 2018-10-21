package com.neighbor.ulsanbus;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;

public class RouteParcel implements Parcelable {
    public static final Creator<RouteParcel> CREATRO = new C02591();
    private String mLength;
    private ArrayList<ItemParcel> mRoutelist;
    private String mTransCnt;
    private String mTravelTime;

    /* renamed from: com.neighbor.ulsanbus.RouteParcel$1 */
    static class C02591 implements Creator<RouteParcel> {
        C02591() {
        }

        public RouteParcel createFromParcel(Parcel source) {
            return new RouteParcel(source);
        }

        public RouteParcel[] newArray(int size) {
            return new RouteParcel[size];
        }
    }

    private RouteParcel(Parcel in) {
        readFromParcel(in);
    }

    public void setTravelTime(String travelTime) {
        this.mTravelTime = travelTime;
    }

    public void setLength(String length) {
        this.mLength = length;
    }

    public void setTransCnt(String transcount) {
        this.mTransCnt = transcount;
    }

    public String getTravelTime() {
        return this.mTravelTime;
    }

    public String getLength() {
        return this.mLength;
    }

    public String getTransCnt() {
        return this.mTransCnt;
    }

    public RouteParcel() {
        this.mRoutelist = new ArrayList();
    }

    public void addTransferRoute(ItemParcel route) {
        this.mRoutelist.add(route);
    }

    public RouteParcel(ArrayList<ItemParcel> list) {
        this.mRoutelist = list;
    }

    public List<ItemParcel> getTransferRouteList() {
        return this.mRoutelist;
    }

    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in) {
        this.mTravelTime = in.readString();
        this.mLength = in.readString();
        this.mTransCnt = in.readString();
        this.mRoutelist = (ArrayList) in.readSerializable();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTravelTime);
        dest.writeString(this.mLength);
        dest.writeString(this.mTransCnt);
        dest.writeSerializable(this.mRoutelist);
    }
}
