module.exports = {
  beautifyReply: function (busNumber, busId, busStationName, remainTime) {
      if(busId === 0){
           createReply(busNumber + ' starts at ' + remainTime + ' from ' + busStationName);

      } else{
          createReply(busNumber + ' will arrive after ' + Math.floor(remainTime/60) + ' minutes');
      }
  }
};

function createReply(reply) {
    let replyArray = [];
    if(replyArray.includes(reply) === false){
        replyArray.push(reply);
    }
    console.log(replyArray);
}
