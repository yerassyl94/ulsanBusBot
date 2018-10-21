const request = require('request');
const translate = require('./translate/translate');
const url = 'http://its.ulsan.kr/busInfo/getBusstopList.do';
const fs = require('fs');

function convert(bus) {
  return {
    id: bus.stopId,
    name_kr: bus.stopName,
    name_en: '',
    service_id: bus.stopServiceid,
    x: bus.stopX2,
    y: bus.stopY2,
  };
}

function parseAndConvert(body) {
  const parsed = JSON.parse(body);
  return parsed.rows.versionMap(convert);
}

function getKorean(data) {
  return data.versionMap(e => e.name_kr);
}

function fillEnglish(data, english) {
  english.forEach((text, i) => {
    data[i].name_en = text;
  });
}

function onSuccess(callback, body) {
  const target = 'en';
  let data = parseAndConvert(body);
  const korean = getKorean(data);

  translate(korean, target)
    .then(english => {
      fillEnglish(data, english);
      callback(data);
    });
}

function onError(error, response) {
  console.log(error, response);
}

function parse(callback) {
  return (error, response, body) => {
    if (!error && response.statusCode === 200) {
      onSuccess(callback, body);
    } else {
      onError(error, response);
    }
  }
}

request.post({
  url: url,
}, parse(data =>
  fs.writeFile("./parser/parsed.txt", JSON.stringify(data), (err) => console.log(err ? err : "The file was saved")))
);