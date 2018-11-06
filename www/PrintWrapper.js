var exec = require('cordova/exec');

exports.printMethod = function (arg0, success, error) {
    exec(success, error, 'PrintWrapper', 'printMethod', [arg0]);
};

