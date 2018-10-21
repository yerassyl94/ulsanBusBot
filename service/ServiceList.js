
// -- NOT IMPLEMENTED --------------------------------------------------------------------------------------------------

// Not used anywhere in the code, empty response
const API_PRIVATE_INFO = "http://apis.its.ulsan.kr:8088/Service4.svc/DeviceInfo.xo?deviceno=";

// Huge piece of data about possible transfers between given bus stops
const API_TRANFER_INFO = "http://apis.its.ulsan.kr:8088/Service4.svc/TransferInfo.xo?ctype=A&startstopid=&endstopid=";




// -- IMPLEMENTED ------------------------------------------------------------------------------------------------------

// Lists all bus routes
const API_ROUTEBASEINFO4 = "http://apis.its.ulsan.kr:8088/Service4.svc/RouteBaseInfoList.xo";

// Lists all bus stops and their coordinates
const API_BUSSTOPINFO4 = "http://apis.its.ulsan.kr:8088/Service4.svc/BusStopInfo.xo";

// Version of the data (yay! don't need to constantly update everything)
const API_VERSIONINFO4 = "http://apis.its.ulsan.kr:8088/Service4.svc/VersionInfo.xo";

// List of transportation card stores and their coordinates
const API_TCARDSTORE = "http://apis.its.ulsan.kr:8088/Service4.svc/TCardStoreInfo.xo";

// Notifications in Korean (probably about bus schedule changes and etc)
const API_NOTICE4 = "http://apis.its.ulsan.kr:8088/Service4.svc/Notice.xo";

// "<RouteCnt>0</RouteCnt><CurrentEmergencyInfo/>" not sure what it means
const API_EMERGENCYINFO1 = "http://apis.its.ulsan.kr:8088/Service4.svc/EmergencyInfo.xo";

// Lists next arrival time of all bus routes for a given bus stop
const API_ALLBUSARRIVALINFO3 = "http://apis.its.ulsan.kr:8088/Service4.svc/AllBusArrivalInfo.xo?ctype=A&stopid=";

// Lists all arrival times of a given bus route for a given bus stop
const API_ARRIVAL_INFO = "http://apis.its.ulsan.kr:8088/Service4.svc/RouteArrivalInfo.xo?ctype=A&routeid=&stopid=";

// Lists current location of all buses for a given bus route (bus stop, coordinates, and angle)
const API_BUSLOCATIONINFO = "http://apis.its.ulsan.kr:8088/Service4.svc/BusLocationInfo.xo?ctype=A&routeid=";

// Same as previous but provides INOUTFLAG and BUSSTOPSEQ (not sure what they mean) instead of coordinates and angle
const API_BUSLOCATIONINFO3 = "http://apis.its.ulsan.kr:8088/Service4.svc/BusLocationInfo2.xo?ctype=A&routeid=";

// Time table for a given bus route
const API_ROUTEDETAILALLOCATIONINFO4 = "http://apis.its.ulsan.kr:8088/Service4.svc/RouteDetailAllocationInfo.xo?ctype=A&routeid=";

// Couldn't find any difference from the previous one
const API_ROUTE_TIMETABLE = "http://apis.its.ulsan.kr:8088/Service4.svc/RouteDetailAllocationInfo2.xo?ctype=A&routeid=";

// Lists all bus stops for a given route (name, coordinates, speed !!! )
const API_ROUTE_STOPS_INFO = "http://apis.its.ulsan.kr:8088/Service4.svc/RouteDetailInfo3.xo?ctype=A&routeid=";