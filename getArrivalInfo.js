const request = require('request');
let beautifyReply = require('./beautifyReply');

module.exports = {
  getArrivalInfo: function (brtNo, brtDirection, bnodeId, brtClass) {
      let replies = [];

    let form = {
      brtNo: brtNo,
      brtDirection: brtDirection,
      bnodeId: bnodeId,
      brtClass: brtClass
    };

      request.post({
          url: 'http://its.ulsan.kr/busInfo/getArrivalInfo.do',
          form: form
      }, function (error,response,body) {
          if(!error && response.statusCode === 200){
              let data = JSON.parse(body);
              let list = data.param.list;
              list.map(function (list) {
                  replies.push(beautifyReply.beautifyReply(brtNo, list.bidNo, list.bnodeName, list.remainTime));
              });
              console.log('2');
              return replies;
          }
      });
  }
};
