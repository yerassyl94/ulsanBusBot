package com.neighbor.ulsanbus.network;

import android.util.Log;
import com.neighbor.ulsanbus.DirectTable;
import com.neighbor.ulsanbus.Position;
import com.neighbor.ulsanbus.Transfer;
import com.neighbor.ulsanbus.TransferTable;
import com.neighbor.ulsanbus.TransitionBus;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ParseTransition extends ParseXML {
    private static final int DIRECT_MODE = 1;
    private static final String TRANSITION = "http://apis.its.ulsan.kr:8088/Service4.svc/TransferInfo.xo?ctype=A&startstopid=";
    private static final int TRANS_MODE = 2;
    DirectTable mDirectTable;
    String mEndStop;
    Position mPosition;
    String mResult;
    String mStartStop;
    String mTagname;
    TransferTable mTransTable;
    int mTransferMode = 0;
    TransitionBus mTransitionBus;
    Transfer mTransiton;
    URL mUrl;

    public void setEndStop(String start, String End) {
        this.mStartStop = start;
        this.mEndStop = End;
    }

    public void parse() throws XmlPullParserException, IOException, NullPointerException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        this.mUrl = new URL("http://apis.its.ulsan.kr:8088/Service4.svc/TransferInfo.xo?ctype=A&startstopid=" + this.mStartStop + "&endstopid=" + this.mEndStop);
        Log.d("ParseTransition", "http://apis.its.ulsan.kr:8088/Service4.svc/TransferInfo.xo?ctype=A&startstopid=" + this.mStartStop + "&endstopid=" + this.mEndStop);
        xpp.setInput(this.mUrl.openStream(), "utf-8");
        for (int eventType = xpp.getEventType(); eventType != 1; eventType = xpp.next()) {
            if (eventType == 2) {
                this.mTagname = xpp.getName();
                if (this.mTagname.equals("DirectCourseInfoTable")) {
                    this.mDirectTable = new DirectTable();
                    this.mTransferMode = 1;
                } else if (this.mTagname.equals("TransferCourseInfoTable")) {
                    this.mTransTable = new TransferTable();
                    this.mTransferMode = 2;
                } else if (this.mTagname.equals("DirectVertaxInfoTable")) {
                    this.mPosition = new Position();
                } else if (this.mTagname.equals("TransferInfoTable")) {
                    this.mTransitionBus = new TransitionBus();
                } else if (this.mTagname.equals("TransferVertaxInfoTable")) {
                    this.mPosition = new Position();
                }
            } else if (eventType == 4) {
                if (this.mTagname.equals("MsgHeader")) {
                    this.mResult = xpp.getText();
                }
                if (this.mTransferMode == 1) {
                    if (this.mTagname.equals("ROUTEID")) {
                        this.mDirectTable.setRouteId(xpp.getText());
                    } else if (this.mTagname.equals("TRAVELTIME")) {
                        this.mDirectTable.setTravelTime(xpp.getText());
                    } else if (this.mTagname.equals("LENGTH")) {
                        this.mDirectTable.setLength(xpp.getText());
                    } else if (this.mTagname.equals("STOPCNT")) {
                        this.mDirectTable.setStopCNT(xpp.getText());
                    } else if (this.mTagname.equals("VERTAXCNT")) {
                        this.mDirectTable.setVertaxCNT(xpp.getText());
                    } else if (this.mTagname.equals("STOPID")) {
                        this.mDirectTable.addStop(xpp.getText());
                    } else if (this.mTagname.equals("vertaxX")) {
                        this.mPosition.posX = xpp.getText();
                    } else if (this.mTagname.equals("vertaxY")) {
                        this.mPosition.posY = xpp.getText();
                    }
                } else if (this.mTransferMode == 2) {
                    if (this.mTagname.equals("TRAVELTIME")) {
                        this.mTransTable.setTravelTime(xpp.getText());
                    } else if (this.mTagname.equals("LENGTH")) {
                        this.mTransTable.setTravelLength(xpp.getText());
                    } else if (this.mTagname.equals("TRASCNT")) {
                        this.mTransTable.setTransCount(xpp.getText());
                    } else if (this.mTagname.equals("ROUTEID")) {
                        this.mTransitionBus.setRouteId(xpp.getText());
                    } else if (this.mTagname.equals("WALKINGTIME")) {
                        this.mTransitionBus.setWalikingTime(xpp.getText());
                    } else if (this.mTagname.equals("WALKINGLENGTH")) {
                        this.mTransitionBus.setWalkingLength(xpp.getText());
                    } else if (this.mTagname.equals("STOPCNT")) {
                        this.mTransitionBus.setStopCnt(xpp.getText());
                    } else if (this.mTagname.equals("VERTAXCNT")) {
                        this.mTransitionBus.setVertaxCount(xpp.getText());
                    } else if (this.mTagname.equals("STOPID")) {
                        this.mTransitionBus.addStopId(xpp.getText());
                    } else if (this.mTagname.equals("vertaxX")) {
                        this.mPosition.posX = xpp.getText();
                    } else if (this.mTagname.equals("vertaxY")) {
                        this.mPosition.posY = xpp.getText();
                    }
                } else if (this.mTagname.equals("DIRECTCNT")) {
                    this.mTransiton.setDirectCnt(xpp.getText());
                } else if (this.mTagname.equals("TRANSCNT")) {
                    this.mTransiton.setTransCnt(xpp.getText());
                }
            } else if (eventType == 3) {
                this.mTagname = xpp.getName();
                if (this.mTagname.equals("MsgHeader") && this.mResult.equals("OK")) {
                    this.mTransiton = new Transfer();
                }
                if (this.mTagname.equals("DirectCourseInfoTable")) {
                    this.mTransiton.addDirectRoute(this.mDirectTable);
                } else if (this.mTagname.equals("TransferCourseInfoTable")) {
                    this.mTransiton.addTransRoute(this.mTransTable);
                } else if (this.mTagname.equals("DirectVertaxInfoTable")) {
                    this.mDirectTable.addPosition(this.mPosition);
                } else if (this.mTagname.equals("TransferVertaxInfoTable")) {
                    this.mTransitionBus.addPosition(this.mPosition);
                } else if (this.mTagname.equals("TransferInfoTable")) {
                    this.mTransTable.addTransitin(this.mTransitionBus);
                }
            }
        }
    }

    public List get() {
        return super.get();
    }

    public Object getObject() {
        return this.mTransiton;
    }
}
