const mapper = require('object-mapper');
const extract = require('../utility/extract');
const mapping = require('./Mapping');


function convert(data) {
  return mapper(data, mapping.allocationMap);
}

module.exports = function (data) {
  return extract(mapping.allocationPath, data, []).map(convert);
};