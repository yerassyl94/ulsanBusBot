module.exports.notificationMap = {
  "NOTICETITLE[0]": "title",      // [ '한글문화예술제 기간 시내(마을)버스 우회운행 알림' ],
  "NOTICECONTENT[0]": "content",  // [ '' ],
};

module.exports.notificationDate = {
  "NOTICEDATE[0]": "date",        // [ '20181001165716' ],
};

module.exports.notificationPath = [
  "NoticeResponse",
  "NoticeResult", 0,
  "NOTICE", 0,
  "MsgBody", 0,
  "NOTICEINFO", 0,
  "CurrentNoticeInfo", 0,
  "NoticeInfoTable"
];