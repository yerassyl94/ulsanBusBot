module.exports.locationMap = {
  "BUSNO[0]": "busNumber",            // [ '울산71자3252' ],
  "BUSTYPE[0]": "busType",            // [ '0' ],
  "LOWTYPE[0]": "lowType",            // [ '0' ],
  "STOPID[0]": "stopId",              // [ '25004' ],
  "STOPNAME[0]": "stopName",          // [ '비치타운앞' ],
  "BUS_ANGLE[0]": "busAngle",         // [ '212' ],
  "BUSX[0]": "longitude",             // [ '129.41474291905513' ],
  "BUSY[0]": "latitude",              // [ '35.48035796217317' ],
  "EMERGENCYCD[0]": "emergency",      // [ '0' ],
  "INOUTFLAG[0]": "inOutFlag",        // [ '2' ]
  "BUSSTOPSEQ[0]": "busStopSequence", // [ '35' ]
};

module.exports.locationPath = [
  "BusLocationInfoResponse",
  "BusLocationInfoResult", 0,
  "BusLocationInfo", 0,
  "MsgBody", 0,
  "BUSINFO", 0,
  "CurrentBusLocationInfo", 0,
  "BusLocationInfoTable"
];