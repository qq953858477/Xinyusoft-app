cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/org.apache.cordova.network-information/www/network.js",
        "id": "org.apache.cordova.network-information.network",
        "clobbers": [
            "navigator.connection",
            "navigator.network.connection"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.network-information/www/Connection.js",
        "id": "org.apache.cordova.network-information.Connection",
        "clobbers": [
            "Connection"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.splashscreen/www/splashscreen.js",
        "id": "org.apache.cordova.splashscreen.SplashScreen",
        "clobbers": [
            "navigator.splashscreen"
        ]
    },
    {
        "file": "plugins/cordova-plugin-inappbrowser/www/inappbrowser.js",
        "id": "cordova-plugin-inappbrowser.inappbrowser",
        "clobbers": [
            "cordova.InAppBrowser.open",
            "window.open"
        ]
    },
    {
        "file": "plugins/cordova-plugin-dialogs/www/notification.js",
        "id": "cordova-plugin-dialogs.notification",
        "merges": [
            "navigator.notification"
        ]
    },
    {
        "file": "plugins/cordova-plugin-dialogs/www/android/notification.js",
        "id": "cordova-plugin-dialogs.notification_android",
        "merges": [
            "navigator.notification"
        ]
    },
    {
        "file": "plugins/cordova-plugin-wxplugin/wxlogin.js",
        "id": "cordova-plugin-wxplugin.wxlogin",
        "clobbers": [
            "navigator.wxplugin"
        ]
    },
    {
        "file": "plugins/cordova-plugin-locationplugin/mlocation.js",
        "id": "cordova-plugin-locationplugin.mlocation",
        "clobbers": [
            "navigator.locationplugin"
        ]
    },
	{
        "file": "plugins/cordova-plugin-sqlite/www/SQLitePlugin.js",
        "id": "cordova-plugin-sqlite.SQLitePlugin",
        "clobbers": [
            "SQLitePlugin"
        ]
    },
    {
        "file": "plugins/cordova-plugin-recordplugin/record.js",
        "id": "cordova-plugin-recordplugin.record",
        "clobbers": [
            "navigator.recordplugin"
        ]
    },
    {					
    	"file": "plugins/cordova-plugin-fileplugin/FilePlugin.js",
        "id": "cordova-plugin-fileplugin.FilePlugin",
        "clobbers": [
            "navigator.FilePlugin"
        ]
    },
    {					
    	"file": "plugins/cordova-plugin-xingeplugin/xingeplugin.js",
        "id": "cordova-plugin-xingeplugin.xingeplugin",
        "clobbers": [
            "navigator.xingeplugin"
        ]
    },
    {					
    	"file": "plugins/cordova-plugin-encrypt/encrypt.js",
        "id": "cordova-plugin-encrypt.encrypt",
        "clobbers": [
            "navigator.encryptplugin"
        ]
    },
    {
        "file": "plugins/cordova-plugin-paypassword/paypassword.js",
        "id": "cordova-plugin-paypassword.paypassword",
        "clobbers": [
            "navigator.paypasswordplugin"
        ]
    },
    {
        "file": "plugins/cordova-plugin-xgbadge/xgbadge.js",
        "id": "cordova-plugin-xgbadge.xgbadge",
        "clobbers": [
            "navigator.getxgbadgeplugin"
        ]
    },
    {
        "file": "plugins/cordova-plugin-websocket/websocket.js",
        "id": "cordova-plugin-websocket.websocket",
        "clobbers": [
            "navigator.websocketplugin"
        ]
    },
    {
        "file": "plugins/cordova-plugin-qrcode/qrcode.js",
        "id": "cordova-plugin-qrcode.qrcode",
        "clobbers": [
            "navigator.qrcodeplugin"
        ]
    },
    {
        "file": "plugins/cordova-plugin-alipay/AlipayPlugin.js",
        "id": "cordova-plugin-alipay.AlipayPlugin",
        "clobbers": [
            "navigator.AlipayPlugin"
        ]
    },
    {
        "file": "plugins/cordova-plugin-sqliteuser/SqliteUserPlugin.js",
        "id": "cordova-plugin-sqliteuser.SqliteUserPlugin",
        "clobbers": [
            "navigator.SqliteUserPlugin"
        ]
    },
    {
        "file": "plugins/cordova-plugin-luckview/luckview.js",
        "id": "cordova-plugin-luckview.luckview",
        "clobbers": [
            "navigator.startLuckViewPlugin"
        ]
    }
    
    
];
module.exports.metadata = 
// TOP OF METADATA
{
    "org.apache.cordova.network-information": "0.2.15",
    "org.apache.cordova.splashscreen": "1.0.0",
    "cordova-plugin-inappbrowser": "1.0.1",
    "cordova-plugin-dialogs": "1.1.1",
    "cordova-plugin-wxplugin": "1.0.0",
    "cordova-plugin-locationplugin": "1.0.0",
	"cordova-plugin-sqlite": "0.9.0"
}
// BOTTOM OF METADATA
});