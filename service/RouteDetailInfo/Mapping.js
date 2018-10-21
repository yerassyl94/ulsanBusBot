module.exports.routeMap = {
  "STOPID[0]": "stopId",        // [ '40234' ],
  "STOPNAME[0]": "stopName",    // [ '울산과학기술원' ],
  "STOPX[0]": "longitude",      // [ '129.19180270488982' ],
  "STOPY[0]": "latitude",       // [ '35.57370787068543' ],
  "WAYPOINT[0]": "wayPoint",    // [ '꽃바위' ],
  "STNODEID[0]": "startNodeId", // [ '196040234' ],
  "EDNODEID[0]": "endNodeId",   // [ '0' ],
  "SPEED[0]": "speed",          // [ '0' ],
};

module.exports.routePath = {
  0: [
    "RouteDetailInfoResponse",
    "RouteDetailInfoResult", 0,
    "RouteDetailInfo", 0,
    "MsgBody", 0,
    "StopInfo", 0,
    "CurrentStopInfo", 0,
    "StopInfoTable",
  ],
  2: [
    "RouteDetailInfoResponse",
    "RouteDetailInfoResult", 0,
    "RouteDetailInfo", 0,
    "MsgBody", 0,
    "StopInfo", 0,
    "CurrentStopInfo", 0,
    "StopInfoTable",
  ],
  3: [
    "RouteDetailInfoResponse3",
    "RouteDetailInfoResult", 0,
    "RouteDetailInfo", 0,
    "MsgBody", 0,
    "StopInfo", 0,
    "CurrentStopInfo", 0,
    "StopInfoTable",
  ],
};