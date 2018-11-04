var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'PrintWrapper', 'coolMethod', [arg0]);
};

exports.myMethod = function (arg0, success, error) {
    exec(success, error, 'PrintWrapper', 'myMethod', [arg0]);
};

exports.printMethod = function (arg0, success, error) {
    exec(success, error, 'PrintWrapper', 'printMethod', [arg0]);
};

