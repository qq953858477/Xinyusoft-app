package com.xinyusoft.xshelllib.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.xinyusoft.xshelllib.application.AppConfig;
import com.xinyusoft.xshelllib.application.AppConstants;
import com.xinyusoft.xshelllib.service.RAIntentService;

public class WebSocketPlugin1 extends CordovaPlugin {
	
	private String callbackName;
	
	private SocketInnerReceiver mReceiver;
	
	public WebSocketPlugin1() {
		Log.i("zzy","创建！:");
		
	}
	
	
	@Override
	public void onResume(boolean multitasking) {
		super.onResume(multitasking);
		Log.i("zzy","WebSocketPlugin:onResume!!!!!");
		mRegReceiver();
	}
	
	@Override
	public void onPause(boolean multitasking) {
		Log.i("zzy","WebSocketPlugin:onPause:");
		super.onPause(multitasking);
		if(mReceiver!= null) {
			//LocalBroadcastManager.getInstance(cordova.getActivity()).unregisterReceiver(mReceiver);
			cordova.getActivity().unregisterReceiver(mReceiver);
		}
		
	}
	
	@Override
	public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
		mRegReceiver();
		Log.i("zzy","mReceiver:"+mReceiver);
		if ("sendMessage".equals(action)) {
			Intent intent = new Intent();
			intent.setAction(AppConstants.WSC_SEND_SERVICE);
			intent.putExtra("data", args.getString(2));
			if(AppConfig.DEBUG)
			Log.i("zzy","data:"+args.getString(2));
			callbackName = args.getString(0);
			//除了广播还有更好的办法吗？？
			//LocalBroadcastManager.getInstance(cordova.getActivity()).sendBroadcast(intent);
			cordova.getActivity().sendBroadcast(intent);
			
			return true;
		} else if ("openSocket".equals(action)) {
			Intent intents = new Intent(cordova.getActivity(), RAIntentService.class);
			cordova.getActivity().startService(intents);
			return true;
		} else if ("closeSocket".equals(action)) {
			Intent intents = new Intent(cordova.getActivity(), RAIntentService.class);
			cordova.getActivity().stopService(intents);
			return true;
		}

		return false;
	}
	
	
	private void mRegReceiver() {
		if(mReceiver == null) {
			mReceiver = new SocketInnerReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(AppConstants.WSC_SEND_PLUGIN);
			//LocalBroadcastManager.getInstance(cordova.getActivity()).registerReceiver(mReceiver, filter);
			cordova.getActivity().registerReceiver(mReceiver, filter);
		}
		
	}
	
	
	/**
	 * 广播接收器
	 * 
	 * @author zzy,wn
	 *
	 */
	private class SocketInnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (AppConstants.WSC_SEND_PLUGIN.equals(action)) {
				if(AppConfig.DEBUG)
				Log.i("zzy","进入插件了！:");
				final String message = intent.getStringExtra("message");
				cordova.getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						webView.loadUrl("javascript:" + callbackName + "('"+message+"')");
						if(AppConfig.DEBUG)
						Log.i("zzy","message:"+message);
					}
				});
			}
		}

	}

}
