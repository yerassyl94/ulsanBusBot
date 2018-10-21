module.exports.arrivalMap = {
  "BUSTYPE[0]": "busType",                  // [ '0' ],
  "LOWTYPE[0]": "lowType",                  // [ '0' ],
  "STATUS[0]": "status",                    // [ '1' ],
  "STOPID[0]": "stopId",                    // [ '0' ],
  "REMAINSTOPCNT[0]": "remainingStopCount", // [ '0' ],
  "REMAINTIME[0]": "remainingTime",         // [ '0940' ],
  "EMERGENCYCD[0]": "emergency",            // [ '0' ],
};

module.exports.arrivalPath = [
  "RouteArrivalInfoResponse",
  "RouteArrivalInfoResult", 0,
  "RouteArrivalInfo", 0,
  "MsgBody", 0,
  "BUSINFO", 0,
  "CurrentArrivalInfo", 0,
  "ArrivalInfoTable"
];