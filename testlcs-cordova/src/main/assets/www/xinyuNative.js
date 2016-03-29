var NativePlugin = function(){};

//获取设备id
NativePlugin.prototype.getDeviceId = function(successCallback,errorCallback){
	cordova.exec(
		successCallback,
		errorCallback,
		'NativePlugin',
		'getDeviceId',
		[{}]
	);
}

//读文件
NativePlugin.prototype.readFile = function(filename,successCallback,errorCallback){
	cordova.exec(
		successCallback,
		errorCallback,
		'NativePlugin',
		'getDeviceId',
		[{'filename':filename}]
	);
}

//写文件
NativePlugin.prototype.writeFile = function(filename,data,successCallback,errorCallback){
	cordova.exec(
		successCallback,
		errorCallback,
		'NativePlugin',
		'getDeviceId',
		[{'filename':filename,'data':data}]
	);
}

//横屏
NativePlugin.prototype.screenLandscape = function(successCallback,errorCallback){
	cordova.exec(
		successCallback,
		errorCallback,
		'NativePlugin',
		'screenLandscape',
		[{}]
	);
}

//竖屏
NativePlugin.prototype.screenPortrait = function(successCallback,errorCallback){
	cordova.exec(
		successCallback,
		errorCallback,
		'NativePlugin',
		'screenPortrait',
		[{}]
	);
}

//分享 文本 到 朋友圈
NativePlugin.prototype.shareTextToFriend = function(content,successCallback,errorCallback){
	cordova.exec(
		successCallback,
		errorCallback,
		'NativePlugin',
		'share',
		[{'flag':'1','content':content,'friend':1}]
	);
}
//分享 文本 到 会话
NativePlugin.prototype.shareTextToDialog = function(content,successCallback,errorCallback){
	cordova.exec(
		successCallback,
		errorCallback,
		'NativePlugin',
		'share',
		[{'flag':'1','content':content,'friend':2}]
	);
}

//分享 url到 朋友圈 
NativePlugin.prototype.shareUrlToFriend = function(url,title,describe,successCallback,errorCallback){
	cordova.exec(
		successCallback,
		errorCallback,
		'NativePlugin',
		'share',
		[{'flag':'2','content':url,'friend':1,'title':title,'describe':describe}]
	);
}

//分享 url到 会话
NativePlugin.prototype.shareUrlToDialog = function(url,title,describe,successCallback,errorCallback){
	cordova.exec(
		successCallback,
		errorCallback,
		'NativePlugin',
		'share',
		[{'flag':'2','content':url,'friend':2,'title':title,'describe':describe}]
	);
}
//获取地理位置
NativePlugin.prototype.locationQuery = function(callBack,successCallback,errorCallback){
	cordova.exec(
		successCallback,
		errorCallback,
		'NativePlugin',
		'location',
		[{'state':'query','callBack':callBack}]
	);
}

//开始录音
NativePlugin.prototype.startRecord = function(recordCallback,successCallback,errorCallback){
	cordova.exec(
		successCallback,
		errorCallback,
		'NativePlugin',
		'record',
		[{'flag':1,'recordCallback':recordCallback}]
	);
}

//结束录音
NativePlugin.prototype.stopRecord = function(recordCallback,successCallback,errorCallback){
	cordova.exec(
		successCallback,
		errorCallback,
		'NativePlugin',
		'record',
		[{'flag':0,'recordCallback':recordCallback}]
	);
}
//播放
NativePlugin.prototype.sound = function(filename,successCallback,errorCallback){
	cordova.exec(
		successCallback,
		errorCallback,
		'NativePlugin',
		'sound',
		[{'soundfilename':filename}]
	);
}
//开启新的一个浏览器
//callBackName  如果传值，当关闭这个新的浏览器的时候，回调调这个个页面的一个js，
//               callbackName为这个js的名字
NativePlugin.prototype.xinyuNewBrowser = function(url,callBackName){
	if(callBackName == null || callBackName == undefined){
		callBackName="";
	}
	cordova.exec(
		null,
		null,
		'NativePlugin',
		'xinyuNewBrowser',
		[{'url':url,'callbackName':callBackName}]
	);
}

//关闭新的浏览器
NativePlugin.prototype.closeNewBrowser = function(){
	
	cordova.exec(
		null,
		null,
		'NativePlugin',
		'closeNewBrowser',
		[{}]
	);
}

//显示键盘
NativePlugin.prototype.showKeyboard = function(callBackName,type,defultKeyboard){
	if(defultKeyboard == null || defultKeyboard == undefined || defultKeyboard == ""){ 
		defultKeyboard = 0;
	}
	cordova.exec(
		null,
		null,
		'NativePlugin',
		'showKeyboard',
		[callBackName,type,defultKeyboard]
	);
}
//设置粘贴板
/**
 * paste 内容
 */
NativePlugin.prototype.setClipBoard = function(paste){
	cordova.exec(
		null,
		null,
		'NativePlugin',
		'clipboard',
		[{'paste':paste}]
	);
}



if(!window.plugins){
	window.plugins = {};
}
if(!window.plugins.NativePlugin){
	window.plugins.NativePlugin = new NativePlugin();
	for(var key in window.plugins.NativePlugin){
		window[key] = window.plugins.NativePlugin[key];
	}
}

