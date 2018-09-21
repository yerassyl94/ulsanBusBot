const TelegramBot = require('node-telegram-bot-api');
const request = require('request');
let getBuses = require('./dropByBusStop');
const token = '649988529:AAFX920W0QfS6g3lBgVrdse5qCqqSBzKRqg';
const bot = new TelegramBot(token, {polling: true});

bot.onText(/^[0-9]+$/,function (msg,match) {
   const chatId = msg.chat.id;
   let stopId;

   let formData = {
       searchCondition: 'stopId',
       searchKeyword: match[0]
   };

   request.post({
       url: 'http://its.ulsan.kr/busInfo/getBusstopList.do',
       form: formData
   }, function (error, response, body) {
       if(!error && response.statusCode === 200){
           let data = JSON.parse(body);
           stopId = data.rows[0].stopId;
           getBuses.getBuses(stopId);
       }
   });


});
