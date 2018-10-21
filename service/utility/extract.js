module.exports = function (path, object, defaultValue = undefined) {
  return path.reduce((xs, x) => (xs && xs[x]) ? xs[x] : null, object) || defaultValue;
};