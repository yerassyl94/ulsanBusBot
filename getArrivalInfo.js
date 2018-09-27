const request = require('request');

module.exports = {
  getArrivalInfo: function (brtNo, brtDirection, bnodeId, brtClass) {
    let form = {
      brtNo: brtNo,
      brtDirection: brtDirection,
      bnodeId: bnodeId,
      brtClass: brtClass
    };

    request.post({
      url: 'http://its.ulsan.kr/busInfo/getArrivalInfo.do',
      form: form
    }, function (error, response, body) {
      if (!error && response.statusCode === 200) {
        let data = JSON.parse(body);
        console.log(data.param.list);
      }
    });
  }
};
