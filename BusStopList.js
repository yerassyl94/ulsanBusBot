const url = 'http://its.ulsan.kr/busInfo/getBusstopList.do';
const request = require('request-promise');
const geolib = require('geolib');

function convert(bus) {
  return {
    id: bus.stopId,
    name_kr: bus.stopName,
    name_en: '',
    service_id: bus.stopServiceid,
    longitude: bus.stopX2,
    latitude: bus.stopY2,
  };
}

function parse(data) {
  return JSON.parse(data).rows.map(convert);
}

function onSuccess(body, filter) {
  return filter(parse(body));
}

function onError(error, response) {
  console.log(response);
  console.log(error);
}

function find(form, filter) {
  const query = {
    url: url,
    form: form,
  };

  return request.post(query, (error, response) => {
    if (error || response.statusCode !== 200) {
      onError(error, response);
    }
  }).then((data) => onSuccess(data, filter));
}

function findByDistance(location, filter) {
  return find({}, (data) => {
    data.forEach((busStop) => {
      busStop.distance = geolib.getDistance(location, busStop);
    });
    data.sort((a, b) => a.distance - b.distance);
    return filter(data);
  });
}


module.exports.findById = function (stopId) {
  const form = {
    searchCondition: 'stopId',
    searchKeyword: stopId,
  };

  return find(form, (data) => data);
};

module.exports.findByName = function (stopName) {
  const form = {
    searchCondition: 'stopName',
    searchKeyword: stopName,
  };

  return find(form, (data) => data);
};

module.exports.findClosestN = function (location, N) {
  return findByDistance(location, (data) =>
    data.slice(0, N));
};

module.exports.findCloserThan = function (location, distance) {
  return findByDistance(location, (data) =>
    data.filter((busStop) =>
      busStop.distance <= distance));
};

//module.exports.findByName('LG').then(console.log);

/*module.exports.findClosestN({
  latitude: 35.5715542,
  longitude: 129.1893059,
}, 5).then(console.log);*/