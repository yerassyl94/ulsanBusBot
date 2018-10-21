package com.neighbor.ulsanbus.network;

import com.neighbor.ulsanbus.RouteArrival;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ParseArrivalBus extends ParseXML {
    private static final String ARRIVAL_ROUTE = "http://apis.its.ulsan.kr:8088/Service4.svc/RouteArrivalInfo.xo?ctype=A&routeid=";
    List<Object> mArrivalist = null;
    RouteArrival mBusArrival;
    String mResult;
    String mRoute;
    String mStop;
    String mTagname;
    URL mUrl;

    public void setRouteBus(String route, String stop) {
        this.mRoute = route;
        this.mStop = stop;
    }

    public void parse() throws XmlPullParserException, IOException, NullPointerException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        this.mUrl = new URL("http://apis.its.ulsan.kr:8088/Service4.svc/RouteArrivalInfo.xo?ctype=A&routeid=" + this.mRoute + "&stopid=" + this.mStop);
        xpp.setInput(this.mUrl.openStream(), "utf-8");
        for (int eventType = xpp.getEventType(); eventType != 1; eventType = xpp.next()) {
            if (eventType == 2) {
                this.mTagname = xpp.getName();
                if (this.mTagname.equals("ArrivalInfoTable")) {
                    this.mBusArrival = new RouteArrival();
                }
            } else if (eventType == 4) {
                if (this.mTagname.equals("MsgHeader")) {
                    this.mResult = xpp.getText();
                } else if (this.mTagname.equals("BUSNO")) {
                    this.mBusArrival.setBusNo(xpp.getText());
                } else if (this.mTagname.equals("BUSTYPE")) {
                    this.mBusArrival.setBusType(xpp.getText());
                } else if (this.mTagname.equals("LOWTYPE")) {
                    this.mBusArrival.setLowType(xpp.getText());
                } else if (this.mTagname.equals("STATUS")) {
                    this.mBusArrival.setStatus(xpp.getText());
                } else if (this.mTagname.equals("STOPID")) {
                    this.mBusArrival.setStopId(xpp.getText());
                } else if (this.mTagname.equals("STOPNAME")) {
                    this.mBusArrival.setStopNM(xpp.getText());
                } else if (this.mTagname.equals("REMAINSTOPCNT")) {
                    this.mBusArrival.setRemainStop(xpp.getText());
                } else if (this.mTagname.equals("REMAINTIME")) {
                    this.mBusArrival.setRemainTime(xpp.getText());
                } else if (this.mTagname.equals("EMERGENCYCD")) {
                    this.mBusArrival.setEmerCode(xpp.getText());
                }
            } else if (eventType == 3) {
                this.mTagname = xpp.getName();
                if (this.mTagname.equals("MsgHeader")) {
                    if (this.mResult.equals("OK")) {
                        this.mArrivalist = new ArrayList();
                    }
                } else if (this.mTagname.equals("ArrivalInfoTable")) {
                    this.mArrivalist.add(this.mBusArrival);
                }
            }
        }
    }

    public List get() {
        return this.mArrivalist;
    }

    public Object getObject() {
        return super.getObject();
    }
}
