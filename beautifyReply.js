module.exports = {
  beautifyReply: function (busNumber, busId, busStationName, remainTime) {
      if(busId === 0){
           return busNumber + ' starts at ' + remainTime + ' from ' + busStationName;

      } else{
          return busNumber + ' will arrive after ' + Math.floor(remainTime/60) + ' minutes';
      }
  }
};
