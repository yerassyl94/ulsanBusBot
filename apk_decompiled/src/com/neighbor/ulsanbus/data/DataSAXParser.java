package com.neighbor.ulsanbus.data;

import android.content.ContentValues;
import android.util.Log;
import com.neighbor.ulsanbus.AllBusArrival;
import com.neighbor.ulsanbus.AllBusArrival.ArrivedBusInfo;
import com.neighbor.ulsanbus.BusLocation;
import com.neighbor.ulsanbus.Emergency;
import com.neighbor.ulsanbus.LowBusAlloc;
import com.neighbor.ulsanbus.RouteArrival;
import com.neighbor.ulsanbus.RouteDetail;
import com.neighbor.ulsanbus.RouteDetailAlloc;
import com.neighbor.ulsanbus.RouteInfoDetail;
import com.neighbor.ulsanbus.Stop;
import com.neighbor.ulsanbus.Timetable;
import com.neighbor.ulsanbus.Transfer;
import com.neighbor.ulsanbus.Vertax;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class DataSAXParser {
    private String Tag = ("[[ ulsan]]" + getClass().getSimpleName());
    private AllBusArrival mAllBusArrival;
    private ArrayList<Timetable> mArrayListTimetable;
    private ArrayList<BusLocation> mBusLocationList;
    private ArrayList<ContentValues> mBusRoutelist;
    private String mDayTimeTableMatch;
    private HashMap<String, TreeMap<Integer, ArrayList<String>>> mDayTimetable;
    private ArrayList<ContentValues> mEmergency;
    private ArrayList<LowBusAlloc> mLowbusallocList;
    private RouteArrival mRouteArrival;
    private ArrayList<RouteDetailAlloc> mRouteDetailAlloclist;
    private ArrayList<RouteDetail> mRouteDetaillist;
    private RouteInfoDetail mRouteInfoDetail;
    private ArrayList<ContentValues> mStoplist;
    private ArrayList<Stop> mStoplistOfRoute;
    private HashMap<String, TreeMap<Integer, ArrayList<String>>> mTimetable;
    private Transfer mTransfer;
    private ArrayList<Vertax> mVertaxlist;

    public class AllBusArrivalHandler extends DefaultHandler {
        private ArrivedBusInfo busInfo;
        private StringBuilder sb;

        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            this.sb.append(ch, start, length);
        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            super.endElement(uri, localName, name);
            if (localName.equalsIgnoreCase("MsgHeader")) {
                DataSAXParser.this.mAllBusArrival.setErrorMsg(this.sb.toString().trim());
            }
            if (localName.equalsIgnoreCase("EMERGENCY_Ver")) {
                DataSAXParser.this.mAllBusArrival.setEmergency_ver(this.sb.toString().trim());
            }
            if (localName.equalsIgnoreCase("EMERGENCYMODE")) {
                DataSAXParser.this.mAllBusArrival.setEmergencyMode(this.sb.toString().trim());
            }
            if (localName.equalsIgnoreCase("BUSCNT")) {
                DataSAXParser.this.mAllBusArrival.setBusCnt(this.sb.toString().trim());
            }
            if (this.sb != null) {
                if (localName.equalsIgnoreCase("ROUTEID")) {
                    this.busInfo.RouteID = this.sb.toString().trim();
                } else if (localName.equals("BUSNO")) {
                    this.busInfo.mBusNo = this.sb.toString().trim();
                    Log.d(DataSAXParser.this.Tag, "routeNo" + this.busInfo.mBusNo);
                } else if (localName.equalsIgnoreCase("BUSTYPE")) {
                    this.busInfo.BusType = this.sb.toString().trim();
                } else if (localName.equalsIgnoreCase("LOWTYPE")) {
                    this.busInfo.LowType = this.sb.toString().trim();
                } else if (localName.equalsIgnoreCase("STATUS")) {
                    this.busInfo.Status = this.sb.toString().trim();
                } else if (localName.equalsIgnoreCase("STOPID")) {
                    this.busInfo.StopID = this.sb.toString().trim();
                } else if (localName.equalsIgnoreCase("REMAINSTOPCNT")) {
                    this.busInfo.RemainStopCnt = this.sb.toString().trim();
                } else if (localName.equalsIgnoreCase("REMAINTIME")) {
                    this.busInfo.RemainTime = this.sb.toString().trim();
                } else if (localName.equalsIgnoreCase("EMERGENCYCD")) {
                    this.busInfo.EmergencyCD = this.sb.toString().trim();
                } else if (localName.equalsIgnoreCase("FSTOPNAME")) {
                    this.busInfo.mFStopName = this.sb.toString().trim();
                } else if (localName.equalsIgnoreCase("TSTOPNAME")) {
                    this.busInfo.mTstopName = this.sb.toString().trim();
                } else if (localName.equalsIgnoreCase("AllBusArrivalInfoTable")) {
                    DataSAXParser.this.mAllBusArrival.addArrivalBusInfo(this.busInfo);
                }
                this.sb.setLength(0);
            }
        }

        public void startDocument() throws SAXException {
            super.startDocument();
            DataSAXParser.this.mAllBusArrival = new AllBusArrival();
            this.sb = new StringBuilder();
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, name, attributes);
            if (localName.equalsIgnoreCase("AllBusArrivalInfoTable")) {
                this.busInfo = DataSAXParser.this.mAllBusArrival.getNewInstanceBusInfo();
            }
        }
    }

    public class BusLocationHandler extends DefaultHandler {
        private BusLocation ch;
        private StringBuilder sb;

        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            this.sb.append(ch, start, length);
        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            super.endElement(uri, localName, name);
            if (localName.equalsIgnoreCase("MsgHeader")) {
                this.ch.setErrorMsg(this.sb.toString().trim());
            }
            if (localName.equalsIgnoreCase("ROUTEID") && !this.sb.toString().trim().equalsIgnoreCase(null)) {
                this.ch.setErrorMsg(this.sb.toString().trim());
            }
            if (this.ch != null) {
                if (localName.equalsIgnoreCase("BUSNO")) {
                    this.ch.setBusNo(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("BUSTYPE")) {
                    this.ch.setBusType(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("LOWTYPE")) {
                    this.ch.setLowType(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("STOPID")) {
                    this.ch.setStopID(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("BUS_ANGLE")) {
                    this.ch.setBusAngle(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("STOPNAME")) {
                    this.ch.setStopName(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("BUSX")) {
                    this.ch.setBusX(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("BUSY")) {
                    this.ch.setBusY(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("EMERGENCYCD")) {
                    this.ch.setEmergencyCD(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("INOUTFLAG")) {
                    this.ch.setInOutFlag(Integer.parseInt(this.sb.toString().trim()));
                } else if (localName.equalsIgnoreCase("BUSSTOPSEQ")) {
                    this.ch.setBusStopSeq(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("BusLocationInfoTable")) {
                    BusLocation location = new BusLocation();
                    location.setErrorMsg(this.ch.getErrorMsg());
                    location.setRouteID(this.ch.getRouteID());
                    location.setBusX(this.ch.getBusX());
                    location.setBusY(this.ch.getBusY());
                    location.setBusType(this.ch.getBusType());
                    location.setLowType(this.ch.getLowType());
                    location.setStopID(this.ch.getStopID());
                    location.setStopName(this.ch.getStopName());
                    location.setBusNo(this.ch.getBusNo());
                    location.setBusAngle(this.ch.getBusAngle());
                    location.setInOutFlag(this.ch.getInOutFlag());
                    location.setBusStopSeq(this.ch.getBusStopSeq());
                    DataSAXParser.this.mBusLocationList.add(location);
                }
                this.sb.setLength(0);
            }
        }

        public void startDocument() throws SAXException {
            super.startDocument();
            DataSAXParser.this.mBusLocationList = new ArrayList();
            this.ch = new BusLocation();
            this.sb = new StringBuilder();
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, name, attributes);
        }
    }

    public class CurRouteTimetableHandler extends DefaultHandler {
        private ArrayList<String> mMin;
        private TreeMap<Integer, ArrayList<String>> mTable;
        private String oldMin = null;
        private String oldkey = null;
        private StringBuilder sb;
        private Timetable tt;

        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            this.sb.append(ch, start, length);
        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            super.endElement(uri, localName, name);
            if (this.sb != null) {
                if (localName.equalsIgnoreCase("DAY_MATCH")) {
                    DataSAXParser.this.mDayTimeTableMatch = this.sb.toString().trim();
                    Log.d("DataSaxParser", "mDayTimeTableMatch :" + DataSAXParser.this.mDayTimeTableMatch);
                }
                ArrayList item;
                Iterator it;
                if (localName.equalsIgnoreCase("TODAYTIME")) {
                    this.tt.setTodayTime(this.sb.toString().trim());
                    String Time = this.sb.toString().trim();
                    Log.d("DataSaxParser", "Time :" + Time);
                    String strkey = Time.substring(0, 2);
                    if (this.oldkey == null) {
                        this.oldkey = strkey;
                    }
                    this.oldMin = Time.substring(2);
                    if (this.oldkey.compareTo(strkey) == 0) {
                        this.mMin.add(Time.substring(2));
                        item = new ArrayList();
                        it = this.mMin.iterator();
                        while (it.hasNext()) {
                            item.add((String) it.next());
                        }
                        this.mTable.put(Integer.valueOf(Integer.parseInt(this.oldkey)), item);
                    } else {
                        item = new ArrayList();
                        it = this.mMin.iterator();
                        while (it.hasNext()) {
                            item.add((String) it.next());
                        }
                        Log.d("DataSaxParser", "wd item size :" + item.size());
                        this.mTable.put(Integer.valueOf(Integer.parseInt(this.oldkey)), item);
                        this.mMin.clear();
                        this.mMin.add(Time.substring(2));
                        this.oldkey = strkey;
                    }
                } else if (localName.equalsIgnoreCase("REMARK")) {
                    this.tt.setRemark(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("BusTimeInfoTable")) {
                    DataSAXParser.this.mArrayListTimetable.add(this.tt);
                } else if (localName.equalsIgnoreCase("CurrentBusTimeInfo")) {
                    if (this.mMin.size() > 0) {
                        item = new ArrayList();
                        it = this.mMin.iterator();
                        while (it.hasNext()) {
                            item.add((String) it.next());
                        }
                        this.mTable.put(Integer.valueOf(Integer.parseInt(this.oldkey)), item);
                    }
                    DataSAXParser.this.mDayTimetable.put("schedule", this.mTable);
                    this.mMin.clear();
                }
                this.sb.setLength(0);
            }
        }

        public void startDocument() throws SAXException {
            super.startDocument();
            DataSAXParser.this.mDayTimetable = new HashMap();
            DataSAXParser.this.mArrayListTimetable = new ArrayList();
            this.sb = new StringBuilder();
            this.mMin = new ArrayList();
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, name, attributes);
            if (localName.equalsIgnoreCase("CurrentBusTimeInfo")) {
                this.mTable = new TreeMap();
            }
            if (localName.equalsIgnoreCase("BusTimeInfoTable")) {
                this.tt = new Timetable();
            }
        }
    }

    public class EmergencyHandler extends DefaultHandler {
        private String ErrorMsg;
        private String RouteCnt;
        private Emergency ch;
        private StringBuilder sb;

        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            this.sb.append(ch, start, length);
        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            super.endElement(uri, localName, name);
            if (localName.equalsIgnoreCase("MsgHeader")) {
                this.ch.setErrorMsg(this.sb.toString().trim());
            }
            if (localName.equalsIgnoreCase("RouteCnt")) {
                this.RouteCnt = this.sb.toString().trim();
            }
            if (this.ch != null) {
                if (localName.equalsIgnoreCase("ROUTEID")) {
                    this.ch.setRouteID(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("STARTDATE")) {
                    this.ch.setStartDate(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("ENDDATE")) {
                    this.ch.setEndDate(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("RouteInfo")) {
                    this.ch.setErrorMsg(this.ErrorMsg);
                    this.ch.setRouteCnt(this.RouteCnt);
                    ContentValues emerenlist = new ContentValues();
                    emerenlist.put("route_id", this.ch.getRouteID());
                    emerenlist.put(DataConst.KEY_EMER_START_DATE, this.ch.getStartDate());
                    emerenlist.put(DataConst.KEY_TCARDSTORE_CARDX, this.ch.getEndDate());
                    DataSAXParser.this.mEmergency.add(emerenlist);
                }
                this.sb.setLength(0);
            }
        }

        public void startDocument() throws SAXException {
            super.startDocument();
            DataSAXParser.this.mEmergency = new ArrayList();
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, name, attributes);
            if (localName.equalsIgnoreCase("RouteInfo")) {
                this.ch = new Emergency();
            }
            this.sb = new StringBuilder();
        }
    }

    public class LowBusAllocHandler extends DefaultHandler {
        private String ErrorMsg;
        private LowBusAlloc ch;
        private String mRouteid;
        private StringBuilder sb;

        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            this.sb.append(ch, start, length);
        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            super.endElement(uri, localName, name);
            if (localName.equalsIgnoreCase("ROUTEID") && !this.sb.toString().trim().equalsIgnoreCase(null)) {
                this.mRouteid = this.sb.toString().trim();
            }
            if (localName.equalsIgnoreCase("MsgHeader")) {
                this.ErrorMsg = this.sb.toString().trim();
            }
            if (this.ch != null) {
                if (localName.equalsIgnoreCase("BUSNO")) {
                    this.ch.setRouteNo(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("TIME")) {
                    this.ch.setTime(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("SEQNO")) {
                    this.ch.setSeq(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("BusInfoTable")) {
                    this.ch.setRouteID(this.mRouteid);
                    this.ch.setErrorMsg(this.ErrorMsg);
                    DataSAXParser.this.mLowbusallocList.add(this.ch);
                }
                this.sb.setLength(0);
            }
        }

        public void startDocument() throws SAXException {
            super.startDocument();
            DataSAXParser.this.mLowbusallocList = new ArrayList();
            this.sb = new StringBuilder();
            this.ch = new LowBusAlloc();
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, name, attributes);
        }
    }

    public class RouteDetailAllocHandler extends DefaultHandler {
        private String ErrorMsg;
        private RouteDetailAlloc ch;
        private StringBuilder sb;

        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            this.sb.append(ch, start, length);
        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            super.endElement(uri, localName, name);
            if (localName.equalsIgnoreCase("MsgHeader")) {
                this.ErrorMsg = this.sb.toString().trim();
            }
            if (this.ch != null) {
                if (localName.equalsIgnoreCase("ROUTEID")) {
                    this.ch.setRouteID(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("WeekType")) {
                    this.ch.setWeekType(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("Today_Time")) {
                    this.ch.setToday_TimeList(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("RouteDetailAllocationInfoTable")) {
                    this.ch.setErrorMsg(this.ErrorMsg);
                    DataSAXParser.this.mRouteDetailAlloclist.add(this.ch);
                }
                this.sb.setLength(0);
            }
        }

        public void startDocument() throws SAXException {
            super.startDocument();
            DataSAXParser.this.mRouteDetailAlloclist = new ArrayList();
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, name, attributes);
            if (localName.equalsIgnoreCase("RouteDetailAllocationInfoTable")) {
                this.ch = new RouteDetailAlloc();
            }
            this.sb = new StringBuilder();
        }
    }

    public class RouteDetailHandler extends DefaultHandler {
        private String ErrorMsg;
        private RouteDetail ch;
        private String mRouteid = null;
        private StringBuilder sb;

        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            this.sb.append(ch, start, length);
        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            super.endElement(uri, localName, name);
            if (localName.equalsIgnoreCase("MsgHeader")) {
                this.ch.setErrorMsg(this.sb.toString().trim());
            }
            if (localName.equalsIgnoreCase("ROUTEID") && !this.sb.toString().trim().equalsIgnoreCase(null)) {
                this.mRouteid = this.sb.toString().trim();
            }
            if (this.ch != null) {
                if (localName.equalsIgnoreCase("StartTime")) {
                    this.ch.setStartTime(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("EndTime")) {
                    this.ch.setEndTime(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("MinInterval")) {
                    this.ch.setMinInterval(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("MaxInterval")) {
                    this.ch.setMaxInterval(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("CompanyName")) {
                    this.ch.setCompanyName(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("CompanyPhone")) {
                    this.ch.setCompanyPhone(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("RouteInfoTable")) {
                    this.ch.setRouteID(this.mRouteid);
                    this.ch.setErrorMsg(this.ErrorMsg);
                    DataSAXParser.this.mRouteDetaillist.add(this.ch);
                }
                this.sb.setLength(0);
            }
        }

        public void startDocument() throws SAXException {
            super.startDocument();
            DataSAXParser.this.mRouteDetaillist = new ArrayList();
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, name, attributes);
            if (localName.equalsIgnoreCase("RouteInfoTable")) {
                this.ch = new RouteDetail();
            }
            this.sb = new StringBuilder();
        }
    }

    public class RouteStopsHandler extends DefaultHandler {
        private String ErrorMsg;
        private Stop ch;
        private Vertax position;
        private StringBuilder sb;

        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            this.sb.append(ch, start, length);
        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            super.endElement(uri, localName, name);
            if (localName.equalsIgnoreCase("MsgHeader")) {
                this.ch.setErrorMsg(this.sb.toString().trim());
            }
            if (this.sb != null) {
                if (localName.equalsIgnoreCase("STOPID")) {
                    this.ch.setStopID(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("STOPNAME")) {
                    this.ch.setStopName(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("STOPLIMOUSINE")) {
                    this.ch.setStopLimousine(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("STOPX")) {
                    this.ch.setStopX(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("STOPY")) {
                    this.ch.setStopY(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("ISDELETE")) {
                    this.ch.setIsDelete(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("SEQ")) {
                    this.ch.setSeq(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("StopInfoTable")) {
                    if (this.ch.getErrorMsg().equalsIgnoreCase("OK")) {
                        Stop item = new Stop();
                        item.setErrorMsg(this.ch.getErrorMsg());
                        item.setStopID(this.ch.getStopID());
                        item.setStopName(this.ch.getStopName());
                        item.setStopX(this.ch.getStopX());
                        item.setStopY(this.ch.getStopY());
                        item.setWaypoint(this.ch.getWaypoint());
                        item.setSpeed(this.ch.getSpeed());
                        item.setSeq(this.ch.getSeq());
                        DataSAXParser.this.mStoplistOfRoute.add(item);
                    }
                } else if (localName.equalsIgnoreCase("vertaxX")) {
                    this.position.setVertaxX(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("vertaxY")) {
                    this.position.setVertaxY(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("VertaxInfoTable")) {
                    Vertax routeLine = new Vertax();
                    routeLine.setVertaxX(this.position.getVertaxX());
                    routeLine.setVertaxY(this.position.getVertaxY());
                    DataSAXParser.this.mVertaxlist.add(routeLine);
                } else if (localName.equalsIgnoreCase("SPEED")) {
                    Log.d(DataSAXParser.this.Tag, this.sb.toString().trim());
                    this.ch.setSpeed(Integer.parseInt(this.sb.toString().trim()));
                } else if (localName.equalsIgnoreCase("WAYPOINT")) {
                    Log.d(DataSAXParser.this.Tag, this.sb.toString().trim());
                    this.ch.setWaypoint(this.sb.toString().trim());
                }
                this.sb.setLength(0);
            }
        }

        public void startDocument() throws SAXException {
            super.startDocument();
            DataSAXParser.this.mStoplistOfRoute = new ArrayList();
            DataSAXParser.this.mVertaxlist = new ArrayList();
            this.ch = new Stop();
            this.sb = new StringBuilder();
            this.position = new Vertax();
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, name, attributes);
            if (localName.equalsIgnoreCase("StopInfoTable")) {
                this.ch.setSeq(attributes.getValue(0));
            }
        }
    }

    public class RouteTimetableHandler extends DefaultHandler {
        private ArrayList<String> mMin;
        private TreeMap<Integer, ArrayList<String>> mWDTable;
        private TreeMap<Integer, ArrayList<String>> mWETable;
        private TreeMap<Integer, ArrayList<String>> mWSTable;
        private String oldWDkey = null;
        private String oldWEkey = null;
        private String oldWSkey = null;
        private StringBuilder sb;

        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            this.sb.append(ch, start, length);
        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            super.endElement(uri, localName, name);
            if (this.sb != null) {
                String strkey;
                ArrayList item;
                Iterator it;
                if (localName.equalsIgnoreCase("WD_TIME")) {
                    String wdTime = this.sb.toString().trim();
                    Log.d("DataSaxParser", "wdTime :" + wdTime);
                    strkey = wdTime.substring(0, 2);
                    if (this.oldWDkey == null) {
                        this.oldWDkey = strkey;
                    }
                    if (this.oldWDkey.compareTo(strkey) == 0) {
                        this.mMin.add(wdTime.substring(2));
                        item = new ArrayList();
                        it = this.mMin.iterator();
                        while (it.hasNext()) {
                            item.add((String) it.next());
                        }
                        this.mWDTable.put(Integer.valueOf(Integer.parseInt(this.oldWDkey)), item);
                    } else {
                        item = new ArrayList();
                        it = this.mMin.iterator();
                        while (it.hasNext()) {
                            item.add((String) it.next());
                        }
                        Log.d("DataSaxParser", "wd item size :" + item.size());
                        this.mWDTable.put(Integer.valueOf(Integer.parseInt(this.oldWDkey)), item);
                        this.mMin.clear();
                        this.mMin.add(wdTime.substring(2));
                        this.oldWDkey = strkey;
                    }
                } else if (localName.equalsIgnoreCase("WE_TIME")) {
                    if (this.mMin.size() > 0) {
                        item = new ArrayList();
                        it = this.mMin.iterator();
                        while (it.hasNext()) {
                            item.add((String) it.next());
                        }
                        this.mWDTable.put(Integer.valueOf(Integer.parseInt(this.oldWDkey)), item);
                    }
                    String weTime = this.sb.toString().trim();
                    Log.d("DataSaxParser", "weTime :" + weTime);
                    strkey = weTime.substring(0, 2);
                    if (this.oldWEkey == null) {
                        this.oldWEkey = strkey;
                    }
                    if (this.oldWEkey.compareTo(strkey) == 0) {
                        this.mMin.add(weTime.substring(2));
                        item = new ArrayList();
                        it = this.mMin.iterator();
                        while (it.hasNext()) {
                            item.add((String) it.next());
                        }
                        this.mWETable.put(Integer.valueOf(Integer.parseInt(this.oldWDkey)), item);
                    } else {
                        item = new ArrayList();
                        it = this.mMin.iterator();
                        while (it.hasNext()) {
                            item.add((String) it.next());
                        }
                        this.mWETable.put(Integer.valueOf(Integer.parseInt(this.oldWEkey)), item);
                        this.mMin.clear();
                        this.mMin.add(weTime.substring(2));
                        this.oldWEkey = strkey;
                    }
                } else if (localName.equalsIgnoreCase("WS_TIME")) {
                    if (this.mMin.size() > 0) {
                        item = new ArrayList();
                        it = this.mMin.iterator();
                        while (it.hasNext()) {
                            item.add((String) it.next());
                        }
                        this.mWETable.put(Integer.valueOf(Integer.parseInt(this.oldWDkey)), item);
                    }
                    String wsTime = this.sb.toString().trim();
                    Log.d("DataSaxParser", "wsTime" + wsTime);
                    strkey = wsTime.substring(0, 2);
                    if (this.oldWSkey == null) {
                        this.oldWSkey = strkey;
                    }
                    if (this.oldWSkey.compareTo(strkey) == 0) {
                        this.mMin.add(wsTime.substring(2));
                        item = new ArrayList();
                        it = this.mMin.iterator();
                        while (it.hasNext()) {
                            item.add((String) it.next());
                        }
                        this.mWSTable.put(Integer.valueOf(Integer.parseInt(this.oldWDkey)), item);
                    } else {
                        item = new ArrayList();
                        it = this.mMin.iterator();
                        while (it.hasNext()) {
                            item.add((String) it.next());
                        }
                        Log.d("DataSaxParser", "ws item size :" + item.size());
                        this.mWSTable.put(Integer.valueOf(Integer.parseInt(this.oldWSkey)), item);
                        this.mMin.clear();
                        this.mMin.add(wsTime.substring(2));
                        this.oldWSkey = strkey;
                    }
                } else if (localName.equalsIgnoreCase("CurrentWDBusTimeInfo")) {
                    DataSAXParser.this.mTimetable.put("wd_time", this.mWDTable);
                    this.mMin.clear();
                } else if (localName.equalsIgnoreCase("CurrentWEBusTimeInfo")) {
                    DataSAXParser.this.mTimetable.put("we_time", this.mWETable);
                    this.mMin.clear();
                } else if (localName.equalsIgnoreCase("CurrentWSBusTimeInfo")) {
                    DataSAXParser.this.mTimetable.put("ws_time", this.mWSTable);
                    this.mMin.clear();
                }
                this.sb.setLength(0);
            }
        }

        public void startDocument() throws SAXException {
            super.startDocument();
            DataSAXParser.this.mTimetable = new HashMap();
            this.sb = new StringBuilder();
            this.mMin = new ArrayList();
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, name, attributes);
            if (!localName.equalsIgnoreCase("RouteDetailAllocationInfo")) {
                if (localName.equalsIgnoreCase("CurrentWDBusTimeInfo")) {
                    this.mWDTable = new TreeMap();
                } else if (localName.equalsIgnoreCase("CurrentWEBusTimeInfo")) {
                    this.mWETable = new TreeMap();
                } else if (localName.equalsIgnoreCase("CurrentWSBusTimeInfo")) {
                    this.mWSTable = new TreeMap();
                }
            }
        }
    }

    public class StopHandler extends DefaultHandler {
        private String ErrorMsg;
        private Stop ch;
        private StringBuilder sb;

        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            this.sb.append(ch, start, length);
        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            super.endElement(uri, localName, name);
            if (localName.equalsIgnoreCase("MsgHeader")) {
                this.ErrorMsg = this.sb.toString().trim();
            }
            if (this.ch != null) {
                if (localName.equalsIgnoreCase("STOPID")) {
                    this.ch.setStopID(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("STOPNAME")) {
                    this.ch.setStopName(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("STOPLIMOUSINE")) {
                    this.ch.setStopLimousine(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("STOPX")) {
                    this.ch.setStopX(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("STOPY")) {
                    this.ch.setStopY(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("ISDELETE")) {
                    this.ch.setIsDelete(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("STOPREMARK")) {
                    this.ch.setStopRemark(this.sb.toString().trim());
                } else if (localName.equalsIgnoreCase("BusStopInfoTable") && this.ErrorMsg.equals("OK")) {
                    ContentValues intialItem = new ContentValues();
                    intialItem.put("stop_id", this.ch.getStopID());
                    intialItem.put("stop_name", this.ch.getStopName());
                    intialItem.put(DataConst.KEY_STOP_LIMOUSINE, this.ch.getStopLimousine());
                    intialItem.put(DataConst.KEY_STOP_X, this.ch.getStopX());
                    intialItem.put(DataConst.KEY_STOP_Y, this.ch.getStopY());
                    intialItem.put("stop_remark", this.ch.getStopRemark());
                    DataSAXParser.this.mStoplist.add(intialItem);
                }
                this.sb.setLength(0);
            }
        }

        public void startDocument() throws SAXException {
            super.startDocument();
            DataSAXParser.this.mStoplist = new ArrayList();
            this.ch = new Stop();
            this.sb = new StringBuilder();
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, name, attributes);
            if (localName.equalsIgnoreCase("BusStopInfoTable")) {
                this.ch.setSeq(attributes.getValue(0));
                this.ch.setStopRemark("");
            }
        }
    }

    public void getName(String url, String flag) throws Exception {
        Log.d(this.Tag, "url = " + url);
        try {
            System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
            XMLReader xmlR = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            Reader bufferedReader = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()));
            if (flag.equalsIgnoreCase(DataConst.FLAG_ROUTEDETAIL)) {
                xmlR.setContentHandler(new RouteDetailHandler());
                xmlR.parse(new InputSource(bufferedReader));
            }
            if (flag.equalsIgnoreCase(DataConst.FLAG_ROUTEDETAILALLOC)) {
                xmlR.setContentHandler(new RouteDetailAllocHandler());
                xmlR.parse(new InputSource(bufferedReader));
            }
            if (flag.equalsIgnoreCase(DataConst.FLAG_LOWBUSALLOCATIONINFO)) {
                xmlR.setContentHandler(new LowBusAllocHandler());
                xmlR.parse(new InputSource(bufferedReader));
            }
            if (flag.equalsIgnoreCase(DataConst.FLAG_STOP)) {
                xmlR.setContentHandler(new StopHandler());
                xmlR.parse(new InputSource(bufferedReader));
            }
            if (flag.equalsIgnoreCase(DataConst.FLAG_BUSLOCATIONINFO)) {
                xmlR.setContentHandler(new BusLocationHandler());
                xmlR.parse(new InputSource(bufferedReader));
            }
            if (flag.equalsIgnoreCase(DataConst.FLAG_ALLBUSARRIVALINFO)) {
                xmlR.setContentHandler(new AllBusArrivalHandler());
                xmlR.parse(new InputSource(bufferedReader));
            }
            if (flag.equalsIgnoreCase(DataConst.FLAG_EMERGENCY)) {
                xmlR.setContentHandler(new EmergencyHandler());
                xmlR.parse(new InputSource(bufferedReader));
            }
            if (flag.equalsIgnoreCase(DataConst.FLAG_ROUTESTOPS)) {
                xmlR.setContentHandler(new RouteStopsHandler());
                xmlR.parse(new InputSource(bufferedReader));
            }
            if (flag.equalsIgnoreCase(DataConst.FLAG_ROUTETIMETABLE)) {
                xmlR.setContentHandler(new RouteTimetableHandler());
                xmlR.parse(new InputSource(bufferedReader));
            }
            if (flag.equalsIgnoreCase(DataConst.FLAG_DAY_ROUTE_SCHEDULE)) {
                xmlR.setContentHandler(new CurRouteTimetableHandler());
                xmlR.parse(new InputSource(bufferedReader));
            }
        } catch (SAXException se) {
            Log.e(this.Tag, "SAX Error " + se.getMessage());
            throw se;
        } catch (IOException ie) {
            Log.e(this.Tag, "Input Error " + ie.getMessage());
            throw ie;
        } catch (Exception oe) {
            Log.e(this.Tag, "Unspecified Error " + oe.getMessage());
            throw oe;
        }
    }

    public RouteInfoDetail getRouteInfoDetail() {
        return this.mRouteInfoDetail;
    }

    public ArrayList<RouteDetail> getRouteDetailList() {
        return this.mRouteDetaillist;
    }

    public ArrayList<RouteDetailAlloc> getRouteDetailAllocList() {
        return this.mRouteDetailAlloclist;
    }

    public ArrayList<ContentValues> getStopList() {
        return this.mStoplist;
    }

    public ArrayList<BusLocation> getBusLocationList() {
        return this.mBusLocationList;
    }

    public RouteArrival getRouteArrivalList() {
        return this.mRouteArrival;
    }

    public AllBusArrival getAllBusArrivalList() {
        return this.mAllBusArrival;
    }

    public ArrayList<LowBusAlloc> getLowBusAllocList() {
        return this.mLowbusallocList;
    }

    public Transfer getTransferList() {
        return this.mTransfer;
    }

    public ArrayList<ContentValues> getEmergencyList() {
        return this.mEmergency;
    }

    public ArrayList<Stop> getRouteStopsList() {
        return this.mStoplistOfRoute;
    }

    public ArrayList<Vertax> getVertaxlist() {
        return this.mVertaxlist;
    }

    public HashMap<String, TreeMap<Integer, ArrayList<String>>> getRouteTimetable() {
        return this.mTimetable;
    }

    public ArrayList<ContentValues> getBusRouteArray() {
        return this.mBusRoutelist;
    }

    public HashMap<String, TreeMap<Integer, ArrayList<String>>> getDayRouteTimetable() {
        return this.mDayTimetable;
    }

    public String getDayRouteMach() {
        return this.mDayTimeTableMatch;
    }

    public ArrayList<Timetable> getmArrayListTimetable() {
        return this.mArrayListTimetable;
    }
}
