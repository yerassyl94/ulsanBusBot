package com.neighbor.ulsanbus.network;

import com.neighbor.ulsanbus.Route;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ParseBus extends ParseXML {
    private static final String BUS = "http://apis.its.ulsan.kr:8088/Service4.svc/RouteBaseInfoList.xo";
    Route mBus;
    List<Object> mBuslist;
    String mResult;
    String mTagname;
    URL mUrl;

    public void parse() throws XmlPullParserException, IOException, NullPointerException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        this.mUrl = new URL("http://apis.its.ulsan.kr:8088/Service4.svc/RouteBaseInfoList.xo");
        xpp.setInput(this.mUrl.openStream(), "utf-8");
        for (int eventType = xpp.getEventType(); eventType != 1; eventType = xpp.next()) {
            if (eventType == 2) {
                this.mTagname = xpp.getName();
                if (this.mTagname.equals("RouteBaseInfoTable")) {
                    this.mBus = new Route();
                }
            } else if (eventType == 4) {
                if (this.mTagname.equals("MsgHeader")) {
                    this.mResult = xpp.getText();
                } else if (this.mTagname.equals("ROUTEID")) {
                    this.mBus.setRouteID(xpp.getText());
                } else if (this.mTagname.equals("ROUTENO")) {
                    this.mBus.setRouteNo(xpp.getText());
                } else if (this.mTagname.equals("ROUTEDIR")) {
                    this.mBus.setRouteDir(xpp.getText());
                } else if (this.mTagname.equals("ROUTETYPE")) {
                    this.mBus.setRouteType(xpp.getText());
                } else if (this.mTagname.equals("BUSTYPE")) {
                    this.mBus.setBusType(xpp.getText());
                } else if (this.mTagname.equals("BUSCOMPANY")) {
                    this.mBus.setBusCompany(xpp.getText());
                } else if (this.mTagname.equals("FSTOPNAME")) {
                    this.mBus.setFstopName(xpp.getText());
                } else if (this.mTagname.equals("TSTOPNAME")) {
                    this.mBus.setTstopName(xpp.getText());
                } else if (this.mTagname.equals("REMARK")) {
                    this.mBus.setRemark(xpp.getText());
                } else if (this.mTagname.equals("WD_START_TIME")) {
                    this.mBus.setWD_Start_Time(xpp.getText());
                } else if (this.mTagname.equals("WD_END_TIME")) {
                    this.mBus.setWD_End_Time(xpp.getText());
                } else if (this.mTagname.equals("WD_MAX_INTERVAL")) {
                    this.mBus.setWD_Max_Interval(xpp.getText());
                } else if (this.mTagname.equals("WD_MIN_INTERVAL")) {
                    this.mBus.setWD_Min_Interval(xpp.getText());
                } else if (this.mTagname.equals("WE_START_TIME")) {
                    this.mBus.setWE_Start_Time(xpp.getText());
                } else if (this.mTagname.equals("WE_END_TIME")) {
                    this.mBus.setWE_End_Time(xpp.getText());
                } else if (this.mTagname.equals("WE_MAX_INTERVAL")) {
                    this.mBus.setWE_Max_Interval(xpp.getText());
                } else if (this.mTagname.equals("WE_MIN_INTERVAL")) {
                    this.mBus.setWE_Min_Interval(xpp.getText());
                } else if (this.mTagname.equals("WS_START_TIME")) {
                    this.mBus.setWS_Start_Time(xpp.getText());
                } else if (this.mTagname.equals("WS_END_TIME")) {
                    this.mBus.setWS_End_Time(xpp.getText());
                } else if (this.mTagname.equals("WS_MAX_INTERVAL")) {
                    this.mBus.setWS_Max_Interval(xpp.getText());
                } else if (this.mTagname.equals("WS_MIN_INTERVAL")) {
                    this.mBus.setWS_Min_Interval(xpp.getText());
                } else if (this.mTagname.equals("INTERVAL")) {
                    this.mBus.setInterval(xpp.getText());
                } else if (this.mTagname.equals("TRAVELTIME")) {
                    this.mBus.setTravelTime(xpp.getText());
                } else if (this.mTagname.equals("LENGTH")) {
                    this.mBus.setLength(xpp.getText());
                } else if (this.mTagname.equals("OPERATIONCNT")) {
                    this.mBus.setOperationCnt(xpp.getText());
                } else if (this.mTagname.equals("COMPANYTEL")) {
                    this.mBus.setCompanyTel(xpp.getText());
                } else if (this.mTagname.equals("BRT_TIMEFLAG")) {
                    this.mBus.setBrtTimeFlag(xpp.getText());
                } else if (this.mTagname.equals("BRT_APP_REMARK")) {
                    this.mBus.setBrt_app_remark(xpp.getText());
                } else if (this.mTagname.equals("BRT_APP_WAYPOINT_DESC")) {
                    this.mBus.setBrt_app_waypoint_desc(xpp.getText());
                } else if (this.mTagname.equals("BRT_CLASS_SEQNO")) {
                    this.mBus.setBrt_class_seqno(xpp.getText());
                }
            } else if (eventType == 3) {
                this.mTagname = xpp.getName();
                if (this.mTagname.equals("MsgHeader")) {
                    if (this.mResult.equals("OK")) {
                        this.mBuslist = new ArrayList();
                    }
                } else if (this.mTagname.equals("RouteBaseInfoTable")) {
                    this.mBuslist.add(this.mBus);
                }
            }
        }
    }

    public List get() {
        return this.mBuslist;
    }

    public Object getObject() {
        return super.getObject();
    }
}
