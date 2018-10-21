module.exports.routeSchedule = {
  "WD_START_TIME[0]": "weekdayStartTime",       // [ '0745' ]
  "WD_END_TIME[0]": "weekdayEndTime",           // [ '2010' ]
  "WD_MAX_INTERVAL[0]": "weekdayMaxInterval",   // [ '50' ]
  "WD_MIN_INTERVAL[0]": "weekdayMinInterval",   // [ '20' ]
  "WE_START_TIME[0]": "saturdayStartTime",      // [ '0745' ]
  "WE_END_TIME[0]": "saturdayEndTime",          // [ '2010' ]
  "WE_MAX_INTERVAL[0]": "saturdayMaxInterval",  // [ '50' ]
  "WE_MIN_INTERVAL[0]": "saturdayMinInterval",  // [ '20' ]
  "WS_START_TIME[0]": "sundayStartTime",        // [ '0745' ]
  "WS_END_TIME[0]": "sundayEndTime",            // [ '2010' ]
  "WS_MAX_INTERVAL[0]": "sundayMaxInterval",    // [ '50' ]
  "WS_MIN_INTERVAL[0]": "sundayMinInterval",    // [ '20' ]
};

module.exports.route = {
  "ROUTEID[0]": "routeId",                           // [ '194101014' ],
  "ROUTENO[0]": "routeNumber",                       // [ '101' ],
  "ROUTEDIR[0]": "routeDirection",                   // [ '3' ],
  "ROUTETYPE[0]": "routeType",                       // [ '1' ],
  "BUSTYPE[0]": "busType",                           // [ '0' ],
  "BUSCOMPANY[0]": "busCompany",                     // [ '공동배차' ],
  "FSTOPNAME[0]": "startTerminusName",               // [ '꽃바위' ],
  "TSTOPNAME[0]": "endTerminusName",                 // [ '꽃바위' ],
  "INTERVAL[0]": "interval",                         // [ '55' ],
  "TRAVELTIME[0]": "travelTime",                     // [ '35' ],
  "LENGTH[0]": "length",                             // [ '12861.2' ],
  "OPERATIONCNT[0]": "operationCount",               // [ '13' ],
  "REMARK[0]": "remark",                             // [ '명덕, 현대예술관' ],
  "BRT_TIMEFLAG[0]": "timeFlag",                     // [ '0' ],
  "BRT_APP_WAYPOINT_DESC[0]": "wayPointDescription", // [ '꽃바위->명덕->꽃바위' ],
  "BRT_CLASS_SEQNO[0]": "classSequenceNumber",       // [ '4' ]
};

module.exports.routePath = [
  "RouteBaseInfoResponse",
  "RouteBaseInfoResult", 0,
  "RouteBaseInfo", 0,
  "MsgBody", 0,
  "CurrentRouteInfo", 0,
  "RouteBaseInfoTable",
];