package com.xinyusoft.xshelllib.plugin;

import android.util.Log;

import com.tencent.android.tpush.XGPushManager;
import com.xinyusoft.xshelllib.tools.xinge.XGPushUtil;
import com.xinyusoft.xshelllib.utils.PhoneInfo;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 信鸽相关插件
 */
public class XingePlugin extends CordovaPlugin{
	private static final String TAG = "XINGEPlugin";
	@Override
	public boolean execute(String action, CordovaArgs args,
			CallbackContext callbackContext) throws JSONException {
		// TODO Auto-generated method stub
		Log.i(TAG, "action:" + action);
		
		try {
			if("xingeRegister".equals(action)){
				String jsonString = args.getString(0);
				JSONObject jos = new JSONObject(jsonString);
				JSONArray jsonArray = jos.getJSONArray("tag");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject json = jsonArray.getJSONObject(i);
					String tagName = json.getString("tagName");
					XGPushManager.setTag(cordova.getActivity(), tagName);
				}
				XGPushUtil.initXGPush(cordova.getActivity(), PhoneInfo.getInstance().getDeviceID());
				JSONObject json = new JSONObject();
				json.put("result", 1);
				callbackContext.success(json.toString());
				return true;
			} 
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
