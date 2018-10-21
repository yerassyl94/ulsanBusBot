module.exports.versionMap = {
  "ROUTE_VER[0]": "routeVersion",           // [ '662' ],
  "STOP_VER[0]": "stopVersion",             // [ '1079' ],
  "TIME_VER[0]": "timeVersion",             // [ '1005' ],
  "NOTICE_VER[0]": "notificationVersion",   // [ '20181001165716' ],
  "EMERGENCY_VER[0]": "emergencyVersion",   // [ 'null' ],
  "EMERGENCYMODE[0]": "emergencyMode",      // [ '0' ],
  "TCARDSTORE_VER[0]": "cardStoreVersion",  // [ '20130608000000' ],
};

module.exports.versionPath = [
  "VersionInfoResponse",
  "VersionInfoResult", 0,
  "VersionInfo", 0,
  "MsgBody", 0,
  "BisVersionInfo"
];