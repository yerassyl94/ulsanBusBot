package com.neighbor.ulsanbus.network;

import android.util.Log;
import com.neighbor.ulsanbus.TCardStore;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ParseTCard extends ParseXML {
    private static final String TCard = "http://apis.its.ulsan.kr:8088/Service4.svc/TCardStoreInfo.xo";
    List<Object> mCardlist;
    String mResult;
    TCardStore mTCard;
    String mTagname;
    URL mUrl;

    public void parse() throws XmlPullParserException, IOException, NullPointerException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        this.mUrl = new URL("http://apis.its.ulsan.kr:8088/Service4.svc/TCardStoreInfo.xo");
        InputStream in = this.mUrl.openStream();
        Log.e("parse: ", in.toString());
        xpp.setInput(in, "utf-8");
        for (int eventType = xpp.getEventType(); eventType != 1; eventType = xpp.next()) {
            if (eventType == 2) {
                this.mTagname = xpp.getName();
                if (this.mTagname.equals("StoreInfo")) {
                    this.mTCard = new TCardStore();
                }
            } else if (eventType == 4) {
                if (this.mTagname.equals("MsgHeader")) {
                    this.mResult = xpp.getText();
                } else if (this.mTagname.equalsIgnoreCase("NAME")) {
                    this.mTCard.setTCardStore_Name(xpp.getText());
                } else if (this.mTagname.equalsIgnoreCase("ADDRESS")) {
                    this.mTCard.setTCardStore_Address(xpp.getText());
                } else if (this.mTagname.equalsIgnoreCase("CARDX")) {
                    this.mTCard.setTCardStore_CardX(xpp.getText());
                } else if (this.mTagname.equalsIgnoreCase("CARDY")) {
                    this.mTCard.setTCardStore_CardY(xpp.getText());
                } else if (this.mTagname.equalsIgnoreCase("STORETYPE")) {
                    this.mTCard.setTCardStore_StoreType(xpp.getText());
                }
            } else if (eventType == 3) {
                this.mTagname = xpp.getName();
                if (this.mTagname.equals("MsgHeader")) {
                    if (this.mResult.equals("OK")) {
                        this.mCardlist = new ArrayList();
                    }
                } else if (this.mTagname.equals("StoreInfo")) {
                    this.mCardlist.add(this.mTCard);
                }
            }
        }
    }

    public List get() {
        return this.mCardlist;
    }

    public Object getObject() {
        return super.getObject();
    }
}
