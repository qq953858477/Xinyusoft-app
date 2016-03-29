cordova.define("cordova-plugin-qrcode.qrcode", function(require, exports,
		module) {

	var exec = require('cordova/exec');

	module.exports = {

		openQRCode : function(callbcakName) {
			exec(null, null, "QRCodePlugin", "openQRCode", [ callbcakName ]);
		}
	};

});
