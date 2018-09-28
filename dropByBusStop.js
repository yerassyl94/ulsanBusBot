const request = require('request');
let getArrivalInfo = require('./getArrivalInfo');

module.exports = {
    getBuses: function (stopId) {
        let replies = [];

        request.post({
            url: 'http://its.ulsan.kr/busInfo/dropbyBusstop.do',
            form: {bnodeId: stopId}
        }, function (error,response,body) {
            if(!error && response.statusCode === 200){
                let data = JSON.parse(body);
                let rows = data.rows;
                console.log('1');
                rows.map(function (row) {
                    return new Promise(function (resolve) {
                        let getReplies = getArrivalInfo.getArrivalInfo(row.brtNo, row.brtDirection, row.bnodeId, row.brtClass);
                        resolve(getReplies);
                    }).then(function (resp) {
                        console.log(resp);
                        console.log('3');
                    })
                });
                console.log('4');
            }
        });
    }
};
