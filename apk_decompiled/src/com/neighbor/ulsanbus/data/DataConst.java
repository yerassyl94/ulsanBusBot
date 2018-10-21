package com.neighbor.ulsanbus.data;

import android.net.Uri;

public class DataConst {
    public static final String API_ALLBUSARRIVALINFO3 = "http://apis.its.ulsan.kr:8088/Service4.svc/AllBusArrivalInfo.xo?ctype=A&stopid=";
    public static final String API_ARRIVAL_INFO = "http://apis.its.ulsan.kr:8088/Service4.svc/RouteArrivalInfo.xo?ctype=A&routeid=";
    public static final String API_BUSLOCATIONINFO = "http://apis.its.ulsan.kr:8088/Service4.svc/BusLocationInfo.xo?ctype=A&routeid=";
    public static final String API_BUSLOCATIONINFO3 = "http://apis.its.ulsan.kr:8088/Service4.svc/BusLocationInfo2.xo?ctype=A&routeid=";
    public static final String API_BUSSTOPINFO4 = "http://apis.its.ulsan.kr:8088/Service4.svc/BusStopInfo.xo";
    public static final String API_EMERGENCYINFO1 = "http://apis.its.ulsan.kr:8088/Service4.svc/EmergencyInfo.xo";
    public static final String API_NOTICE4 = "http://apis.its.ulsan.kr:8088/Service4.svc/Notice.xo";
    public static final String API_PRIVATE_INFO = "http://apis.its.ulsan.kr:8088/Service4.svc/DeviceInfo.xo?deviceno=";
    public static final String API_ROUTEBASEINFO4 = "http://apis.its.ulsan.kr:8088/Service4.svc/RouteBaseInfoList.xo";
    public static final String API_ROUTEDETAILALLOCATIONINFO4 = "http://apis.its.ulsan.kr:8088/Service4.svc/RouteDetailAllocationInfo.xo?ctype=A&routeid=";
    public static final String API_ROUTE_STOPS_INFO = "http://apis.its.ulsan.kr:8088/Service4.svc/RouteDetailInfo3.xo?ctype=A&routeid=";
    public static final String API_ROUTE_TIMETABLE = "http://apis.its.ulsan.kr:8088/Service4.svc/RouteDetailAllocationInfo2.xo?ctype=A&routeid=";
    public static final String API_TCARDSTORE = "http://apis.its.ulsan.kr:8088/Service4.svc/TCardStoreInfo.xo";
    public static final String API_TRANFER_INFO = "http://apis.its.ulsan.kr:8088/Service4.svc/TransferInfo.xo?ctype=A&startstopid=";
    public static final String API_VERSIONINFO4 = "http://apis.its.ulsan.kr:8088/Service4.svc/VersionInfo.xo";
    public static boolean Alarm = false;
    public static final String BUS_ID_TAB1 = "bus_id_one";
    public static final String BUS_ID_TAB3 = "bus_id_three";
    public static final Uri CONTENT_ALARM_URI = Uri.parse("content://com.neighbor.ulsanbus/alarm");
    public static final Uri CONTENT_DB_URI = Uri.parse("content://com.neighbor.ulsanbus/databaseversion");
    public static final Uri CONTENT_EMER_URI = Uri.parse("content://com.neighbor.ulsanbus.emergency/emergency");
    public static final Uri CONTENT_FAVORITES_URI = Uri.parse("content://com.neighbor.ulsanbus/favorite");
    public static final Uri CONTENT_NOTICE_URI = Uri.parse("content://com.neighbor.ulsanbus.notice/notice");
    public static final Uri CONTENT_ROUTE_SEARCH_URI = Uri.parse("content://com.neighbor.ulsanbus.route/routesearchlist");
    public static final Uri CONTENT_ROUTE_URI = Uri.parse("content://com.neighbor.ulsanbus.route/busroute");
    public static final Uri CONTENT_STOPS_URI = Uri.parse("content://com.neighbor.ulsanbus.stop/bisnode");
    public static final Uri CONTENT_STOP_SEARCH_URI = Uri.parse("content://com.neighbor.ulsanbus.stop/stopsearchlist");
    public static final Uri CONTENT_TSTORE_URI = Uri.parse("content://com.neighbor.ulsanbus.store/tcardstore");
    public static final Uri CONTENT_WIDGET1_URI = Uri.parse("content://com.neighbor.ulsanbus/widget1");
    public static final Uri CONTENT_WIDGET2_URI = Uri.parse("content://com.neighbor.ulsanbus/widget2");
    public static final Uri CONTENT_WIDGET3_LIST_URI = Uri.parse("content://com.neighbor.ulsanbus/widget3_list");
    public static final Uri CONTENT_WIDGET3_URI = Uri.parse("content://com.neighbor.ulsanbus/widget3");
    public static final String DATABASE_EMERGENCY = "ulsanbusEmergency.db";
    public static int DATABASE_EMER_VER = 1;
    public static final String DATABASE_NAME = "ulsanbusnew.db";
    public static final String DATABASE_NOTICE = "ulsanbusNotice.db";
    public static int DATABASE_NOTICE_VER = 1;
    public static final String DATABASE_ROUTE = "ulsanbusRoute.db";
    public static int DATABASE_ROUTE_VER = 4;
    public static final String DATABASE_STOP = "ulsanbusStop.db";
    public static int DATABASE_STOP_VER = 1;
    public static int DATABASE_STORE_VER = 1;
    public static final String DATABASE_TSTORE = "ulsanbusStore.db";
    public static final int DATABASE_VERSION = 21;
    public static final int DEPARTUER_NAME = 5;
    public static final int DESTINATION_NAME = 6;
    public static final String FALG_BUSROUTE = "busRoutehandler";
    public static final String FLAG_ALLBUSARRIVALINFO = "allbusarrivalinfo";
    public static final String FLAG_BUSLOCATIONINFO = "buslocationinfo";
    public static final String FLAG_DATABASEVERSION = "databaseversionhandler";
    public static final String FLAG_DAY_ROUTE_SCHEDULE = "curRouteTimetablehandler";
    public static final String FLAG_EMERGENCY = "emergenchinfo";
    public static final String FLAG_LOWBUSALLOCATIONINFO = "lowbusallochandler";
    public static final String FLAG_NOTICE = "noticehandler";
    public static final String FLAG_ROUTE = "routehandler";
    public static final String FLAG_ROUTEARRIVALINFO = "routearraivalinfo";
    public static final String FLAG_ROUTEDETAIL = "routedetailhandler";
    public static final String FLAG_ROUTEDETAILALLOC = "routedetailallochandler";
    public static final String FLAG_ROUTESTOPS = "routeStopHandler";
    public static final String FLAG_ROUTETIMETABLE = "RouteTimetableHandler";
    public static final String FLAG_STOP = "stophandler";
    public static final String FLAG_TCARDSTORE = "tcardstorehandler";
    public static final String FLAG_TRANSFER = "transferinfo";
    public static String GlobalRouteid = null;
    public static final String IS_COLUMN_ADDED = "is_column_added";
    public static final String KEY_ALARM_DAY_0 = "day_0";
    public static final String KEY_ALARM_DAY_1 = "day_1";
    public static final String KEY_ALARM_DAY_2 = "day_2";
    public static final String KEY_ALARM_DAY_3 = "day_3";
    public static final String KEY_ALARM_DAY_4 = "day_4";
    public static final String KEY_ALARM_DAY_5 = "day_5";
    public static final String KEY_ALARM_DAY_6 = "day_6";
    public static final String KEY_ALARM_ID = "alarm_id";
    public static final String KEY_ALARM_ROUTE_ID = "favor_routeid";
    public static final String KEY_ALARM_STOP_ID = "favor_stopid";
    public static final String KEY_ALARM_TIME_HOUR = "alarm_time_hour";
    public static final String KEY_ALARM_TIME_MINU = "alarm_time_minu";
    public static final String KEY_BRT_APP_REMARK = "brt_app_remark";
    public static final String KEY_BRT_APP_WAYPOINT_DESC = "brt_app_waypoint_desc";
    public static final String KEY_BRT_CLASS_SEQNO = "brt_class_seqno";
    public static final String KEY_BRT_TIME_FLAG = "brt_timeflag";
    public static final String KEY_EMERGENCY_MODE = "emergency_mode";
    public static final String KEY_EMER_END_DATE = "end_date";
    public static final String KEY_EMER_ROUTE_ID = "route_id";
    public static final String KEY_EMER_START_DATE = "start_date";
    public static final String KEY_FAVORITE_ALARM = "favor_alarm_status";
    public static final String KEY_FAVORITE_CATEGORY = "category";
    public static final String KEY_FAVORITE_ROUTEID = "favor_routeid";
    public static final String KEY_FAVORITE_STOPID = "favor_stopid";
    public static String KEY_LOAD_COMPLETE = "on_save_complet_load";
    public static final String KEY_LOWFLOOR_ROUTE_DATE = "low_floor_date";
    public static final String KEY_LOWFLOOR_ROUTE_SEQ = "low_floor_seq";
    public static final String KEY_LOWFLOOR_ROUTE_TIME = "low_floor_time";
    public static final String KEY_NOTICE_CNT = "route_notice_cnt";
    public static final String KEY_NOTICE_CONTENT = "route_notice_content";
    public static final String KEY_NOTICE_DATE = "route_notice_date";
    public static final String KEY_NOTICE_EXPAND = "route_notice_expand";
    public static final String KEY_NOTICE_FILE = "route_notice_file";
    public static final String KEY_NOTICE_ID = "_id";
    public static final String KEY_NOTICE_TITLE = "route_notice_title";
    public static final String KEY_PREF_DIR_ROUTE = "pref_dir_route";
    public static final String KEY_PREF_EMER_MODE = "pref_emergence_mode";
    public static final String KEY_PREF_EMER_VER = "pref_emer_ver";
    public static final String KEY_PREF_FROM_STOP_ID = "pref_from_stop_id";
    public static final String KEY_PREF_FROM_STOP_NAME = "pref_from_stop_name";
    public static final String KEY_PREF_NOTICE_VER = "pref_notice_ver";
    public static final String KEY_PREF_RECENT_ROUTE = "pref_recent_route";
    public static final String KEY_PREF_RECENT_STOP = "pref_recent_stop";
    public static String KEY_PREF_ROUTE_NO = "pref_route_no";
    public static final String KEY_PREF_ROUTE_VER = "pref_route_ver";
    public static String KEY_PREF_ROUTE_VERSION_RE_CONFIRM = "pref_version_route_redownload";
    public static final String KEY_PREF_SEL_ROUTE = "pref_sel_stop";
    public static final String KEY_PREF_START_MAIN = "pref_start_main";
    public static final String KEY_PREF_STOP_VER = "pref_stop_ver";
    public static String KEY_PREF_STOP_VERSION_RE_CONFIRM = "pref_version_stop_redownload";
    public static final String KEY_PREF_TCARD_VER = "pref_tcard_ver";
    public static final String KEY_PREF_TO_STOP_ID = "pref_to_stop_id";
    public static final String KEY_PREF_TO_STOP_NAME = "pref_to_stop_name";
    public static final String KEY_PREF_UPDATE = "pref_notice";
    public static final String KEY_ROUTE_BUS_COMPANY = "route_bus_company";
    public static final String KEY_ROUTE_BUS_TYPE = "route_bus_type";
    public static final String KEY_ROUTE_CNT = "route_cnt";
    public static final String KEY_ROUTE_COMPANYTEL = "route_company_tel";
    public static final String KEY_ROUTE_DAY_TYPE = "route_day_type";
    public static final String KEY_ROUTE_DESTI = "route_desti";
    public static final String KEY_ROUTE_DIR = "route_dir";
    public static final String KEY_ROUTE_FSTOP_NAME = "route_fstop_name";
    public static final String KEY_ROUTE_ID = "route_id";
    public static final String KEY_ROUTE_INTERVAL = "route_interval";
    public static final String KEY_ROUTE_LENGTH = "route_length";
    public static final String KEY_ROUTE_NO = "route_no";
    public static final String KEY_ROUTE_NO_BACK = "route_no_back";
    public static final String KEY_ROUTE_NO_FRONT = "route_no_front";
    public static final String KEY_ROUTE_OPERATION_CNT = "route_operation_cnt";
    public static final String KEY_ROUTE_REMARK = "route_remark";
    public static final String KEY_ROUTE_STOPCNT = "route_stop_cnt";
    public static final String KEY_ROUTE_STOP_ID = "route_stop_id";
    public static final String KEY_ROUTE_STOP_NAME = "route_stop_name";
    public static final String KEY_ROUTE_STOP_SEQ = "route_stop_seq";
    public static final String KEY_ROUTE_STOP_X = "route_stop_x";
    public static final String KEY_ROUTE_STOP_Y = "route_stop_y";
    public static final String KEY_ROUTE_TIME = "route_time";
    public static final String KEY_ROUTE_TIME_SEQ = "route_time_seq";
    public static final String KEY_ROUTE_TRAVEL_TIME = "route_travel_time";
    public static final String KEY_ROUTE_TSTOP_NAME = "route_tstop_name";
    public static final String KEY_ROUTE_TYPE = "route_type";
    public static final String KEY_ROUTE_VERTAX_CNT = "route_vertax_cnt";
    public static final String KEY_ROUTE_VERTAX_X = "route_vertax_x";
    public static final String KEY_ROUTE_VERTAX_Y = "route_vertax_y";
    public static final String KEY_ROUTE_WD_END_TIME = "route_wd_end_time";
    public static final String KEY_ROUTE_WD_MAX_INTERVAL = "route_wd_max_interval";
    public static final String KEY_ROUTE_WD_MIN_INTERVAL = "route_wd_min_interval";
    public static final String KEY_ROUTE_WD_START_TIME = "route_wd_start_time";
    public static final String KEY_ROUTE_WD_TIME = "route_wd_time";
    public static final String KEY_ROUTE_WD_TIME_CNT = "route_wd_time_cnt";
    public static final String KEY_ROUTE_WE_END_TIME = "route_we_end_time";
    public static final String KEY_ROUTE_WE_MAX_INTERVAL = "route_we_max_interval";
    public static final String KEY_ROUTE_WE_MIN_INTERVAL = "route_we_min_interval";
    public static final String KEY_ROUTE_WE_START_TIME = "route_we_start_time";
    public static final String KEY_ROUTE_WE_TIME = "route_we_time";
    public static final String KEY_ROUTE_WE_TIME_CNT = "route_we_time_cnt";
    public static final String KEY_ROUTE_WS_END_TIME = "route_ws_end_time";
    public static final String KEY_ROUTE_WS_MAX_INTERVAL = "route_ws_max_interval";
    public static final String KEY_ROUTE_WS_MIN_INTERVAL = "route_ws_min_interval";
    public static final String KEY_ROUTE_WS_START_TIME = "route_ws_start_time";
    public static final String KEY_ROUTE_WS_TIME = "route_ws_time";
    public static final String KEY_ROUTE_WS_TIME_CNT = "route_ws_time_cnt";
    public static final String KEY_SEARCHED_MILLI = "millitime";
    public static final String KEY_SEARCH_ROUTE_ID = "route_id";
    public static final String KEY_SEARCH_STOP_ID = "search_stop_id";
    public static final String KEY_STOP_ID = "stop_id";
    public static final String KEY_STOP_LIMOUSINE = "stop_limousine";
    public static final String KEY_STOP_NAME = "stop_name";
    public static final String KEY_STOP_REMARK = "stop_remark";
    public static final String KEY_STOP_X = "stop_x";
    public static final String KEY_STOP_Y = "stop_y";
    public static final String KEY_TCARDSTORE_ADDRESS = "tcardstore_address";
    public static final String KEY_TCARDSTORE_CARDX = "tcardstore_cardX";
    public static final String KEY_TCARDSTORE_CARDY = "tcardstore_cardY";
    public static final String KEY_TCARDSTORE_NAME = "tcardstore_name";
    public static final String KEY_TCARDSTORE_PHONE = "tcardstore_phone";
    public static final String KEY_TCARDSTORE_STORETYPE = "tcardstore_storetype";
    public static final String KEY_VERSION_EMERGENCY = "version_emergency";
    public static final String KEY_VERSION_NOTICE = "version_notice";
    public static final String KEY_VERSION_NOTICE_INTER = "version_notice_interal";
    public static final String KEY_VERSION_ROUTE = "version_route";
    public static final String KEY_VERSION_STOP = "version_stop";
    public static final String KEY_VERSION_TCARDSTORE = "version_tcardstore";
    public static final String KEY_VERSION_TCARDSTORE_INTER = "version_tcardstore_internal";
    public static final String KEY_VERSION_TIME = "version_time";
    public static final String KEY_WIDGET1_REMAINTIME = "remain_time";
    public static final String KEY_WIDGET1_ROUTEBUSTYPE = "route_bus_type";
    public static final String KEY_WIDGET1_ROUTEID = "route_id";
    public static final String KEY_WIDGET1_ROUTENO = "route_no";
    public static final String KEY_WIDGET1_STOPID = "stop_id";
    public static final String KEY_WIDGET1_STOPNAME = "stop_name";
    public static final String KEY_WIDGET1_WIDGETID = "widget_id";
    public static final String KEY_WIDGET2_REMAINTIME1 = "remain_time1";
    public static final String KEY_WIDGET2_REMAINTIME2 = "remain_time2";
    public static final String KEY_WIDGET2_ROUTEBUSTYPE = "route_bus_type";
    public static final String KEY_WIDGET2_ROUTEID = "route_id";
    public static final String KEY_WIDGET2_ROUTENO = "route_no";
    public static final String KEY_WIDGET2_STOPID = "stop_id";
    public static final String KEY_WIDGET2_STOPNAME = "stop_name";
    public static final String KEY_WIDGET2_STOPREMARK = "stop_remark";
    public static final String KEY_WIDGET2_WIDGETID = "widget_id";
    public static final String KEY_WIDGET3_LIST_REMAINTIME = "remain_time";
    public static final String KEY_WIDGET3_LIST_ROUTEBUSTYPE = "route_bus_type";
    public static final String KEY_WIDGET3_LIST_ROUTEID = "route_id";
    public static final String KEY_WIDGET3_LIST_ROUTEINTERVAL = "route_interval";
    public static final String KEY_WIDGET3_LIST_STATUS = "status";
    public static final String KEY_WIDGET3_LIST_STOPID = "stop_id";
    public static final String KEY_WIDGET3_LIST_STOPNAME = "stop_name";
    public static final String KEY_WIDGET3_LIST_STOPREMARK = "stop_remark";
    public static final String KEY_WIDGET3_LIST_WIDGETID = "widget_id";
    public static final String KEY_WIDGET3_STOPID = "stop_id";
    public static final String KEY_WIDGET3_STOPNAME = "stop_name";
    public static final String KEY_WIDGET3_STOPREMARK = "stop_remark";
    public static final String KEY_WIDGET3_WIDGETID = "widget_id";
    public static boolean Noti = false;
    public static final String PHONESTATE_DENYED = "phone_state_denyed";
    public static final String PHONESTATE_PERMISSION = "phone_state";
    public static final int REQ_MAP_PERMISSION = 100;
    public static String SelectedRouteId = "";
    public static String SelectedStopId = "";
    public static String SelectedStopMapStation = "";
    public static final String TABLE_ALARM_INFO = "alarm";
    public static final String TABLE_DATABASE_VERSION_INFO = "databaseversion";
    public static final String TABLE_EMERGENCY_INFO = "emergency";
    public static final String TABLE_FAVORITE_INFO = "favorite";
    public static final String TABLE_LOWTIMETABLE_INFO = "lowtimetable";
    public static final String TABLE_NOTICE_INFO = "notice";
    public static final String TABLE_ROUTE_DETAIL_INFO = "busroutedetail";
    public static final String TABLE_ROUTE_INFO = "busroute";
    public static final String TABLE_ROUTE_LIST = "routelist";
    public static final String TABLE_ROUTE_SEARCHLIST_INFO = "routesearchlist";
    public static final String TABLE_STOP_INFO = "bisnode";
    public static final String TABLE_STOP_SEARCHLIST_INFO = "stopsearchlist";
    public static final String TABLE_TCARDSTORE_INFO = "tcardstore";
    public static final String TABLE_TIMETABLE_INFO = "timetable";
    public static final String TABLE_WIDGET1 = "widget1";
    public static final String TABLE_WIDGET2 = "widget2";
    public static final String TABLE_WIDGET3 = "widget3";
    public static final String TABLE_WIDGET3_LIST = "widget3_list";
    public static String Textname = "알람";
    public static final String autho_emergency = "com.neighbor.ulsanbus.emergency";
    public static final String autho_notice = "com.neighbor.ulsanbus.notice";
    public static final String autho_route = "com.neighbor.ulsanbus.route";
    public static final String autho_stop = "com.neighbor.ulsanbus.stop";
    public static final String autho_tstore = "com.neighbor.ulsanbus.store";
    public static final String authority = "com.neighbor.ulsanbus";
    public static boolean mMainReturnFlag = false;
    public static String position;
    public static String registration;
}