module.exports.arrivalMap = {
  "ROUTEID[0]": "routeId",                  // [ '194101332' ],
  "BUSNO[0]": "busNumber",                  // [ '0' ],
  "BUSTYPE[0]": "busType",                  // [ '0' ],
  "LOWTYPE[0]": "lowType",                  // [ '0' ],
  "STATUS[0]": "status",                    // [ '1' ],
  "FSTOPNAME[0]": "firstStopName",          // [ '울산과학기술원' ],
  "TSTOPNAME[0]": "lastStopName",           // [ '꽃바위' ],
  "STOPID[0]": "stopId",                    // [ '0' ],
  "REMAINSTOPCNT[0]": "remainingStopCount", // [ '1' ],
  "REMAINTIME[0]": "remainingTime",         // [ '0940' ],
  "EMERGENCYCD[0]": "emergency",            // [ '0' ],
};

module.exports.arrivalPath = [
  "RouteArrivalInfoResponse",
  "AllBusArrivalInfoResult", 0,
  "AllBusArrivalInfo", 0,
  "MsgBody", 0,
  "BUSINFO", 0,
  "CurrentAllBusArrivalInfo", 0,
  "AllBusArrivalInfoTable"
];