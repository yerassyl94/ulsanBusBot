package com.neighbor.ulsanbus;

public class TCardStore {
    private String Address;
    private String CardX;
    private String CardY;
    private String ErrorMsg;
    private String Name;
    private String Phone;
    private String Seq;
    private String StoreType;

    public String getSeq() {
        return this.Seq;
    }

    public void setSeq(String seq) {
        this.Seq = seq;
    }

    public String getTCardStore_Name() {
        return this.Name;
    }

    public void setTCardStore_Name(String tcardstore_name) {
        this.Name = tcardstore_name;
    }

    public String getTCardStore_Phone() {
        return this.Phone;
    }

    public void setTCardStore_Phone(String tcardstore_phone) {
        this.Phone = tcardstore_phone;
    }

    public String getTCardStore_Address() {
        return this.Address;
    }

    public void setTCardStore_Address(String tcardstore_address) {
        this.Address = tcardstore_address;
    }

    public String getTCardStore_CardX() {
        return this.CardX;
    }

    public void setTCardStore_CardX(String tcardstore_cardX) {
        this.CardX = tcardstore_cardX;
    }

    public String getTCardStore_CardY() {
        return this.CardY;
    }

    public void setTCardStore_CardY(String tcardstore_cardY) {
        this.CardY = tcardstore_cardY;
    }

    public String getTCardStore_StoreType() {
        return this.StoreType;
    }

    public void setTCardStore_StoreType(String tcardstore_storetype) {
        this.StoreType = tcardstore_storetype;
    }

    public void setErrorMsg(String msg) {
        this.ErrorMsg = msg;
    }

    public String getErrorMsg() {
        return this.ErrorMsg;
    }
}
