package com.xinyusoft.xshelllib.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

import com.xinyusoft.xshelllib.aesencrypt.AES;

/**
 * 加密解密插件
 * 
 * @author zzy
 *
 */
public class EncryptPlugin extends CordovaPlugin {
	@Override
	public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
		
		String content = args.getString(0);
		AES mAes = new AES(args.getString(1));
		if("encrypt".equals(action)) {  //加密
			
			byte[] mBytes = null;
			try {
				mBytes = content.getBytes("UTF8");
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			callbackContext.success(mAes.encrypt(mBytes));
			return true;
		} else if("decrypt".equals(action)) { //解密
			callbackContext.success(mAes.decrypt(content));
			return true;
		}
		
		return false;
	}
}
