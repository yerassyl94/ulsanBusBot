package com.neighbor.ulsanbus.network;

import com.neighbor.ulsanbus.Stop;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ParseStop extends ParseXML {
    private static final String STOP = "http://apis.its.ulsan.kr:8088/Service4.svc/BusStopInfo.xo";
    String mResult;
    Stop mStop;
    List<Object> mStoplist;
    String mTagname;
    URL mUrl;

    public void parse() throws XmlPullParserException, IOException, NullPointerException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        this.mUrl = new URL("http://apis.its.ulsan.kr:8088/Service4.svc/BusStopInfo.xo");
        xpp.setInput(this.mUrl.openStream(), "utf-8");
        for (int eventType = xpp.getEventType(); eventType != 1; eventType = xpp.next()) {
            if (eventType == 2) {
                this.mTagname = xpp.getName();
                if (this.mTagname.equals("BusStopInfoTable")) {
                    this.mStop = new Stop();
                }
            } else if (eventType == 4) {
                if (this.mTagname.equals("MsgHeader")) {
                    this.mResult = xpp.getText();
                } else if (this.mTagname.equals("STOPID")) {
                    this.mStop.setStopID(xpp.getText().trim());
                } else if (this.mTagname.equals("STOPNAME")) {
                    this.mStop.setStopName(xpp.getText().trim());
                } else if (this.mTagname.equals("STOPLIMOUSINE")) {
                    this.mStop.setStopLimousine(xpp.getText().trim());
                } else if (this.mTagname.equals("STOPX")) {
                    this.mStop.setStopX(xpp.getText().trim());
                } else if (this.mTagname.equals("STOPY")) {
                    this.mStop.setStopY(xpp.getText().trim());
                } else if (this.mTagname.equals("STOPREMARK")) {
                    this.mStop.setStopRemark(xpp.getText().trim());
                }
            } else if (eventType == 3) {
                this.mTagname = xpp.getName();
                if (this.mTagname.equals("MsgHeader")) {
                    if (this.mResult.equals("OK")) {
                        this.mStoplist = new ArrayList();
                    }
                } else if (this.mTagname.equals("BusStopInfoTable")) {
                    this.mStoplist.add(this.mStop);
                }
            }
        }
    }

    public List get() {
        return this.mStoplist;
    }

    public Object getObject() {
        return super.getObject();
    }
}
