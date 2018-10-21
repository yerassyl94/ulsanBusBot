const mapper = require('object-mapper');
const extract = require('../utility/extract');
const mapping = require('./Mapping');


function convert(data) {
  return mapper(data, mapping.routeMap);
}

module.exports = function (data, type) {
  return extract(mapping.routePath[type], data, []).map(convert);
};