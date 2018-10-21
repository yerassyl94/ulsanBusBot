const mapper = require('object-mapper');
const extract = require('../utility/extract');
const mapping = require('./Mapping');


function convert(data) {
  return mapper(data, mapping.locationMap);
}

module.exports = function (data) {
  return extract(mapping.locationPath, data, []).map(convert);
};