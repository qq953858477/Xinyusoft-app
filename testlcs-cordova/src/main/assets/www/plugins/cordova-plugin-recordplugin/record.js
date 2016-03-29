cordova.define("cordova-plugin-recordplugin.record", function(require, exports, module) { 


var exec = require('cordova/exec');

/**
 * Provides access to notifications on the device.
 */
module.exports = {
    
	startRecord: function(callBackName) {
        exec(null, null, "RecordPlugin", "startRecord", [callBackName]);
    },
    
    endRecord: function(callBackName,uploadingUrl) {
		exec(null, null, "RecordPlugin", "endRecord", [callBackName,uploadingUrl]);
	},
               
    playRecord: function(callBackName,recordUrl) {
        exec(null, null, "RecordPlugin","playRecord", [callBackName,recordUrl]);
    }

};

});
