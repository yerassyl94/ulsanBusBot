module.exports.storeMap = {
  "NAME[0]": "name",        // [ '버스표충전소' ],
  "ADDRESS[0]": "address",  // [ '울산 중구 태화동 119-11번지' ],
  "CARDX[0]": "longitude",  // [ '129.3009194' ],
  "CARDY[0]": "latitude",   // [ '35.55236944' ],
  "STORETYPE[0]": "type",   // [ '일반' ],
};

module.exports.storePath = [
  "TransPortationCardStoreResponse",
  "TransPortationCardStoreResult", 0,
  "TransPortationCardStoreInfo", 0,
  "MsgBody", 0,
  "TransPortationCardStore", 0,
  "CurrentTransPortationCardStore", 0,
  "StoreInfo"
];