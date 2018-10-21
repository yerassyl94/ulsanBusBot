const mapper = require('object-mapper');
const extract = require('../utility/extract');
const mapping = require('./Mapping');


function convert(data) {
  return mapper(data, mapping.arrivalMap);
}

module.exports = function (data) {
  return extract(mapping.arrivalPath, data, []).map(convert);
};