const request = require('request');
let getArrivalInfo = require('./getArrivalInfo');

module.exports = {
  getBuses: function (stopId) {
    console.log("hi there =====>" + stopId);
    request.post({
      url: 'http://its.ulsan.kr/busInfo/dropbyBusstop.do',
      form: {bnodeId: stopId}
    }, function (error, response, body) {
      console.log('started ========> ' + stopId);
      if (!error && response.statusCode === 200) {
        let data = JSON.parse(body);
        let rows = data.rows;
        let info = rows.map(function (row) {
          getArrivalInfo.getArrivalInfo(row.brtNo, row.brtDirection, row.bnodeId, row.brtClass);
        })
      }
    })
  }
};
