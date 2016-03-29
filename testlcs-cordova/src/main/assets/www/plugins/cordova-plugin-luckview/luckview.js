cordova.define("cordova-plugin-luckview.luckview", function(require, exports, module) {

var exec = require('cordova/exec');

    module.exports = {
               
        startLuckView: function(userName,userId,url) {
            exec(null, null, "LuckViewPlugin", "startLuckView", [userName,userId,url]);
        }
    };
               
});
