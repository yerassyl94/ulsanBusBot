const projectId = 'chrome-mediator-217203';
const {Translate} = require('@google-cloud/translate');
const googleTranslator = new Translate({
  projectId: projectId,
});

module.exports.translator = function(target) {
  return (text) =>
    googleTranslator.translate(text, target);
};

module.exports.translate = function(text, target) {
  return googleTranslator.translate(text, target);
}