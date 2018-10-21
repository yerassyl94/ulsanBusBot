const mapper = require('object-mapper');
const extract = require('../utility/extract');
const mapping = require('./Mapping');


function convert(data) {
  return mapper(data, mapping.versionMap);
}

module.exports = function (data) {
  return extract(mapping.versionPath, data, []).map(convert);
};