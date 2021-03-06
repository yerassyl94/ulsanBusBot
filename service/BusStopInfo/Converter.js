const mapper = require('object-mapper');
const extract = require('../utility/extract');
const mapping = require('./Mapping');


function convert(data) {
  return mapper(data, mapping.busMap);
}

module.exports = function (data) {
  return extract(mapping.busPath, data, []).map(convert);
};