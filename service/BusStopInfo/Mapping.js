module.exports.busMap = {
  "STOPID[0]": "id",                // [ '58083' ],
  "STOPNAME[0]": "name",            // [ '롯데백화점' ],
  "STOPLIMOUSINE[0]": "limousine",  // [ '0' ],
  "STOPX[0]": "longitude",          // [ '129.07962439437998' ],
  "STOPY[0]": "latitude",           // [ '35.212810879265206' ],
  "STOPREMARK[0]": "remark",        // [ '언양정류장 방면' ],
};

module.exports.busPath = [
  "BusStopInfoResponse",
  "BusStopInfoResult", 0,
  "BusStopInfo", 0,
  "MsgBody", 0,
  "CurrentBusStopInfo", 0,
  "BusStopInfoTable",
];