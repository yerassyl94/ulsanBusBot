const util = require('util');
const request = require('request-promise');

const xml2js = require('xml2js');
const parser = new xml2js.Parser();
const parse = util.promisify(parser.parseString);

const convert = require('./Converter');

const url = 'http://apis.its.ulsan.kr:8088/Service4.svc/RouteArrivalInfo.xo';

module.exports = function (routeId, stopId) {
  const query = {
    url: url,
    qs: {
      ctype: 'A',
      routeid: routeId,
      stopid: stopId,
    }
  };
  return request.get(query).then(parse).then(convert);
};

module.exports(194101332, 40234).then((data) => console.log(data[0]));