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

import com.xinyusoft.xshelllib.application.AppConstants;
import com.xinyusoft.xshelllib.service.RAService;

/**
 * webSocket插件
 * 
 * @author zzy
 *
 */
public class WebSocketPlugin extends CordovaPlugin {

	private String callbackName;

	// 打开socket
	public static final int OPEN_WBESOCKET = 100;
	// 发送数据
	public static final int SEND_MESSAGE = 101;

	private SocketInnerReceiver mReceiver;

	@Override
	public void onResume(boolean multitasking) {
		super.onResume(multitasking);
		Log.i("zzy", "WebSocketPlugin:onResume!!!!!");
		mRegReceiver();
	}

	@Override
	public void onPause(boolean multitasking) {
		Log.i("zzy", "WebSocketPlugin:onPause:");
		super.onPause(multitasking);
		if (mReceiver != null) {
			cordova.getActivity().unregisterReceiver(mReceiver);
		}

	}

	@Override
	public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
		// 当新开一个浏览器的时候，是一个新的进程，所以只能在这里去注册了
		mRegReceiver();
		if ("sendMessage".equals(action)) {
			Intent intent = new Intent(cordova.getActivity(), RAService.class);
			intent.setAction(AppConstants.WSC_SEND_SERVICE);
			intent.putExtra("data", args.getString(2));
			intent.putExtra("flag", SEND_MESSAGE);
			callbackName = args.getString(0);
			cordova.getActivity().startService(intent);
			return true;
		} else if ("openSocket".equals(action)) {
			Intent intents = new Intent(cordova.getActivity(), RAService.class);
			intents.putExtra("flag", OPEN_WBESOCKET);
			intents.putExtra("url", args.getString(2));
			callbackName = args.getString(0);
			cordova.getActivity().startService(intents);
			return true;
		} else if ("closeSocket".equals(action)) {
			Intent intents = new Intent(cordova.getActivity(), RAService.class);
			cordova.getActivity().stopService(intents);
			return true;
		}

		return false;
	}

	private void mRegReceiver() {
		if (mReceiver == null) {
			mReceiver = new SocketInnerReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(AppConstants.WSC_SEND_PLUGIN);
			filter.addAction(AppConstants.WSC_SEND_PLUGIN_LOAGIN_SUCCESS);
			filter.addAction(AppConstants.WSC_SEND_PLUGIN_LOAGIN_FAILUER);
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
		public void onReceive(Context context, final Intent intent) {
			final String action = intent.getAction();

			cordova.getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (AppConstants.WSC_SEND_PLUGIN.equals(action)) {
						final String message = intent.getStringExtra("message");
						webView.loadUrl("javascript:" + callbackName + "('" + message + "')");
					} else if (AppConstants.WSC_SEND_PLUGIN_LOAGIN_SUCCESS.equals(action)) {
						webView.loadUrl("javascript:" + callbackName + "(1)");
					} else if (AppConstants.WSC_SEND_PLUGIN_LOAGIN_FAILUER.equals(action)) {
						webView.loadUrl("javascript:" + callbackName + "('" + intent.getStringExtra("failReason") + "')");
					}

				}
			});
		}

	}

}
