const url = 'http://its.ulsan.kr/busInfo/getRouteDetail.do';
const request = require('request-promise');

function convert(bus) {
  return {
    id: bus.stopServiceid,
    name_kr: bus.stopName,
    name_en: '',
    service_id: bus.stopServiceid,
    longitude: bus.stopX2,
    latitude: bus.stopY2,
  };
}

function bus2form(bus) {
  return {
    brtId: bus.id,
    brtNo: bus.number,
    bttDirection: bus.direction,
    brtClass: bus.class,
    brtType: bus.type,
  };
}

function parse(data) {
  return JSON.parse(data).rows.map(convert);
}

function onSuccess(body) {
  return parse(body);
}

function onError(error, response) {
  console.log(response);
  console.log(error);
}

module.exports.getBusDetails = function(bus) {
  const query = {
    url: url,
    form: bus2form(bus),
  };

  return request.post(query, (error, response, body) => {
    if (error || response.statusCode !== 200) {
      onError(error, response);
    }
  }).then(onSuccess);
};

/*require('./RouteInfo').getBus(743)
  .then((data) => data[0])
  .then(module.exports.getBusDetails)
  .then(console.log);*/