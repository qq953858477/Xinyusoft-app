package com.xinyusoft.xshelllib.plugin;

import android.content.Intent;

import com.xinyusoft.xshell.luckview.activity.LuckViewMainActivity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

public class LuckViewPlugin extends CordovaPlugin {
	@Override
	public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
		try {
			if ("startLuckView".equals(action)) {
				String name = args.getString(0);
				Intent intent = new Intent(cordova.getActivity(), LuckViewMainActivity.class);
				intent.putExtra("userName", name);
				intent.putExtra("userId", args.getString(1));
				intent.putExtra("url", args.getString(2));

				cordova.getActivity().startActivity(intent);
				callbackContext.success();
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
