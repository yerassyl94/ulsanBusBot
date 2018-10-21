const util = require('util');
const request = require('request-promise');

const xml2js = require('xml2js');
const parser = new xml2js.Parser();
const parse = util.promisify(parser.parseString);

const convert = require('./Converter');

const defaultUrl = 3;
const url = {
  0: 'http://apis.its.ulsan.kr:8088/Service4.svc/RouteDetailInfo.xo',
  2: 'http://apis.its.ulsan.kr:8088/Service4.svc/RouteDetailInfo2.xo',
  3: 'http://apis.its.ulsan.kr:8088/Service4.svc/RouteDetailInfo3.xo',
};

module.exports = function (routeId, type = defaultUrl) {
  if (!url[type]) {
    type = defaultUrl;
  }
  const query = {
    url: url[type],
    qs: {
      ctype: 'A',
      routeid: routeId,
    }
  };
  return request.get(query).then(parse).then((data) => convert(data, type));
};

module.exports(194101332).then((data) => console.log(data[0]));