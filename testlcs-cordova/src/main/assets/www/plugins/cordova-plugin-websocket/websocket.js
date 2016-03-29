cordova.define("cordova-plugin-websocket.websocket", function(require, exports,module) {

	var exec = require('cordova/exec');

	module.exports = {

		openSocket : function(successCallbackName, failureCallbackName,url) {
			exec(null, null, "WebSocketPlugin", "openSocket", [successCallbackName,failureCallbackName,url]);
		},
		closeSocket : function() {
			exec(null, null, "WebSocketPlugin", "closeSocket", []);
		},
		sendMessage : function(successCallbackName, failureCallbackName,content) {
			exec(null, null, "WebSocketPlugin", "sendMessage", [successCallbackName,failureCallbackName,content]);
		}
	};

});
