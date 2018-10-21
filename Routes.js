const RouteInfo = require('./RouteInfo');
const RouteDetail = require('./RouteDetail');
const fs = require('fs');

function getInfo(buses, i = 0) {
  if (i >= buses.length) {
    return;
  }
  return RouteDetail.getBusDetails(buses[i]).then((data) => {
    fs.appendFileSync("./parsed.txt", JSON.stringify(data));
    return setTimeout(() => getInfo(buses, i + 1), 2000);
  });
}

const buses = RouteInfo.getAllBuses().then(getInfo);

