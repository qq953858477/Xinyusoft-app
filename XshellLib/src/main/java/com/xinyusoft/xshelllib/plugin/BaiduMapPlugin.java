package com.xinyusoft.xshelllib.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.lidroid.xutils.util.LogUtils;
import com.xinyusoft.xshelllib.tools.baidumap.BaiduMapUtil;
import com.xinyusoft.xshelllib.tools.baidumap.BaiduMapUtil.ReceiveLocationListener;

public class BaiduMapPlugin extends CordovaPlugin {

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
		JSONObject jo = args.getJSONObject(0);
		try {
			if ("location".equals(action)) {
				if (null != jo && jo.length() > 0) {
					LogUtils.e("调用到了location");
					final String callBackName = jo.getString("callBackName");
					BaiduMapUtil util = new BaiduMapUtil();
					util.initBaiduMap(cordova.getActivity().getApplicationContext());
					util.queryLocation(new ReceiveLocationListener() {

						@Override
						public void onReceiveLocationListener(final JSONObject json) {
//							Intent intent = new Intent(AppConstants.ACTION_LOCATION_START);
//							intent.putExtra("callBackName", callBackName);
//							intent.putExtra("location", json.toString());
//							LocalBroadcastManager.getInstance(AppContext.CONTEXT).sendBroadcast(intent);
							cordova.getActivity().runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									webView.loadUrl("javascript:" + callBackName + "('" + json.toString() + "')");
									
								}
							});
							//webView.sendJavascript("javascript:" + callBackName + "('" + json.toString() + "')");
							Log.e("zzy", "location:"+json.toString());
							
						}
					});

					// Log.i("zzy", "location:"+json.toString());
					callbackContext.success();
					return true;
				}
			}
		} catch (Exception e) {
			callbackContext.error(e.getMessage());
			return false;
		}
		return false;
	}
}
