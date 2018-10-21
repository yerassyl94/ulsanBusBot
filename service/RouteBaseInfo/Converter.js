const mapper = require('object-mapper');
const extract = require('../utility/extract');
const mapping = require('./Mapping');

function createWeek(sunday, weekday, saturday) {
  const week = new Array(7).fill(weekday, 1, 6);
  week[0] = saturday;
  week[6] = sunday;
  return week;
}

function convert(data) {
  const schedule = mapper(data, mapping.routeSchedule);
  const route = mapper(data, mapping.route);

  route.startTime = createWeek(schedule.sundayStartTime, schedule.weekdayStartTime, schedule.saturdayStartTime);
  route.endTime = createWeek(schedule.sundayEndTime, schedule.weekdayEndTime, schedule.saturdayEndTime);
  route.maxInterval = createWeek(schedule.sundayMaxInterval, schedule.weekdayMaxInterval, schedule.saturdayMaxInterval);
  route.minInterval = createWeek(schedule.sundayMinInterval, schedule.weekdayMinInterval, schedule.saturdayMinInterval);

  return route;
}

module.exports = function (data) {
  return extract(mapping.routePath, data, []).map(convert);
};