cordova.define("cordova-plugin-xgbadge.xgbadge", function(require, exports, module) {

var exec = require('cordova/exec');

    module.exports = {
               
        setXGBadge: function(badgeCount) {
               exec(null, null, "GetXgBadgePlugin", "setXGBadge", [badgeCount]);
        }
    };
               
});
