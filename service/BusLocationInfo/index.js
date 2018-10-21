const util = require('util');
const request = require('request-promise');

const xml2js = require('xml2js');
const parser = new xml2js.Parser();
const parse = util.promisify(parser.parseString);

const convert = require('./Converter');

const defaultUrl = 'http://apis.its.ulsan.kr:8088/Service4.svc/BusLocationInfo.xo';
const alternativeUrl = 'http://apis.its.ulsan.kr:8088/Service4.svc/BusLocationInfo2.xo';

module.exports = function (routeId, alternative = false) {
  const query = {
    url: alternative ? alternativeUrl : defaultUrl,
    qs: {
      ctype: 'A',
      routeid: routeId,
    }
  };
  return request.get(query).then(parse).then(convert);
};

module.exports(194101332).then((data) => console.log(data[0]));