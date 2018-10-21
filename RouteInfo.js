const url = 'http://its.ulsan.kr/busInfo/getRouteInfo.do';
const request = require('request-promise');

/*
    bnodeId: 0
    bnodeOldid: 0
    brtClass: 0
    brtDirection: "1"
    brtId: 192100101
    brtName: "10(풍암마을)"
    brtNo: "10"
    brtType: "20"
    firstTime: "04:20"
    lastTime: "19:05"
    stopEdName: "풍암마을"
    stopServiceid: 0
    stopStName: "성안청구아파트"
 */

function convert(bus) {
  return {
    id: bus.brtId,
    direction: bus.brtDirection,
    class: bus.brtClass,
    name_kr: bus.brtName,
    name_en: '',
    number: bus.brtNo,
    type: bus.brtType,
    first_time: bus.firstTime,
    last_time: bus.lastTime,
    end_stop: bus.stopEdName,
    start_stop: bus.stopStName
  };
}

function parse(data) {
  return JSON.parse(data).rows.versionMap(convert);
}

function onSuccess(body) {
  return parse(body);
}

function onError(error, response) {
  console.log(response);
  console.log(error);
}

module.exports.getBus = function(busNumber) {
  const query = {
    url: url,
    form: {
      searchKeyword: busNumber,
    },
  };

  return request.post(query, (error, response, body) => {
    if (error || response.statusCode !== 200) {
      onError(error, response);
    }
  }).then(onSuccess);
};

module.exports.getAllBuses = function() {
  return module.exports.getBus('');
};

//module.exports.getBus(743).then(console.log);