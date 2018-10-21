const mapper = require('object-mapper');
const extract = require('../utility/extract');
const mapping = require('./Mapping');

function string2date(string) {
  return {
    year: string.substring(0, 4),
    month: string.substring(4, 6),
    day: string.substring(6, 8),
    hour: string.substring(8, 10),
    minute: string.substring(10, 12),
    second: string.substring(12, 14),
  };
}

function convert(data) {
  const dateString = mapper(data, mapping.notificationDate).date;
  const notification = mapper(data, mapping.notificationMap);
  notification.date = string2date(dateString);
  return notification;
}

module.exports = function (data) {
  return extract(mapping.notificationPath, data, []).map(convert);
};