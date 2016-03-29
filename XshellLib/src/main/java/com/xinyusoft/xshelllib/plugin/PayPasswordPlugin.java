package com.xinyusoft.xshelllib.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class PayPasswordPlugin extends CordovaPlugin {
	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		// TODO Auto-generated method stub
		try {
			if ("setPayPassword".equals(action)) {
				JSONObject jos = args.getJSONObject(0);
				String callbackName = jos.getString("callbackName");

				Intent intent = new Intent();
				intent.setAction("setPayPassword");
				intent.putExtra("callbackName", callbackName);
				LocalBroadcastManager.getInstance(cordova.getActivity())
						.sendBroadcast(intent);
				return true;
			} else if("payPasswordState".equals(action)){
				JSONObject jos = args.getJSONObject(0);
				String payState = jos.getString("payState");
				Intent intent = new Intent();
				intent.setAction("payState");
				intent.putExtra("payState", payState);
				LocalBroadcastManager.getInstance(cordova.getActivity())
						.sendBroadcast(intent);
				return true;
			}

		} catch (Exception e) {
			callbackContext.error(e.getMessage());
			return false;
		}
		return false;
	}
}
