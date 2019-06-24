var exec = require('cordova/exec');

exports.printMethod = function (arg0, success, error) {
    exec(success, error, 'PrintWrapper', 'printMethod', [arg0]);
};

exports.connect = function (arg0, success, error) {
    exec(success, error, 'PrintWrapper', 'connectMethod', [arg0]);
};

exports.check = function (arg0, success, error) {
    exec(success, error, 'PrintWrapper', 'checkConnectMethod', [arg0]);
};

exports.printIntlMethod = function (arg0, success, error) {
    exec(success, error, 'PrintWrapper', 'printIntlMethod', [arg0]);
};

exports.printBillpaymentMethod = function (arg0, success, error) {
    exec(success, error, 'PrintWrapper', 'billpaymentMethod', [arg0]);
};
