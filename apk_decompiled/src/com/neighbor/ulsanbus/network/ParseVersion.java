package com.neighbor.ulsanbus.network;

import com.neighbor.ulsanbus.Database;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ParseVersion extends ParseXML {
    private static final String VERSION = "http://apis.its.ulsan.kr:8088/Service4.svc/VersionInfo.xo";
    String mResult;
    String mTagname;
    URL mUrl;
    Database mVersion;

    public void parse() throws XmlPullParserException, IOException, NullPointerException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        this.mUrl = new URL("http://apis.its.ulsan.kr:8088/Service4.svc/VersionInfo.xo");
        xpp.setInput(this.mUrl.openStream(), "utf-8");
        for (int eventType = xpp.getEventType(); eventType != 1; eventType = xpp.next()) {
            if (eventType == 2) {
                this.mTagname = xpp.getName();
            } else if (eventType == 4) {
                if (this.mTagname.equals("MsgHeader")) {
                    this.mResult = xpp.getText();
                } else if (this.mTagname.equals("ROUTE_VER")) {
                    this.mVersion.setRoute_Ver(xpp.getText());
                } else if (this.mTagname.equals("STOP_VER")) {
                    this.mVersion.setStop_Ver(xpp.getText());
                } else if (this.mTagname.equals("TIME_VER")) {
                    this.mVersion.setTime_Ver(xpp.getText());
                } else if (this.mTagname.equals("NOTICE_VER")) {
                    this.mVersion.setNotice_Ver(xpp.getText());
                } else if (this.mTagname.equals("EMERGENCY_VER")) {
                    this.mVersion.setEmergency_Ver(xpp.getText());
                } else if (this.mTagname.equals("EMERGENCYMODE")) {
                    this.mVersion.setEmergency_Mode(xpp.getText());
                } else if (this.mTagname.equals("TCARDSTORE_VER")) {
                    this.mVersion.setTCardStore(xpp.getText());
                }
            } else if (eventType == 3) {
                this.mTagname = xpp.getName();
                if (this.mTagname.equals("MsgHeader") && this.mResult.equals("OK")) {
                    this.mVersion = new Database();
                }
            }
        }
    }

    public List get() {
        return super.get();
    }

    public Object getObject() {
        return this.mVersion;
    }
}
