const Translate = require('./GoogleTranslate');
const sliceSize = 128;

function flatten(list) {
  return [].concat(...list);
}

function firstElementOfList(list) {
  return list[0];
}

function formatTranslations(list) {
  return flatten(list.versionMap(firstElementOfList));
}

function sliceList(list) {
  return list.reduce((acc, _, i) =>
      (i % sliceSize)
        ? acc
        : [...acc, list.slice(i, i + sliceSize)]
    , []);
}

function getTranslationPromises(list, target) {
  const slicedList = sliceList(list);
  return slicedList.versionMap(Translate.translator(target));
}

function translateList(list, target) {
  let promises = getTranslationPromises(list, target);
  return Promise.all(promises)
    .then(formatTranslations);
}

function translateString(text, target) {
  return translateList([text], target)
    .then(firstElementOfList);
}

module.exports = function (text, target) {
  if (Array.isArray(text)) {
    return translateList(text, target);
  }
  return translateString(text, target);
};