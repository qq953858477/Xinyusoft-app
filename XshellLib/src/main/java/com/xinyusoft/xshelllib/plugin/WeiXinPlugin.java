package com.xinyusoft.xshelllib.plugin;

import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;
import android.widget.Toast;

import com.xinyusoft.xshelllib.application.AppConfig;
import com.xinyusoft.xshelllib.tools.wxlogin.WeixinUtil;
import com.xinyusoft.xshelllib.tools.wxlogin.WeixinUtil.GetUserInfoListener;
import com.xinyusoft.xshelllib.utils.ParseConfig;

/**
 * 微信相关的插件
 * 
 * @author zzy
 *
 */
public class WeiXinPlugin extends CordovaPlugin {

	/********* 微信登录 start ******************/
	// 自己微信应用的 appId
	private String WX_APP_ID;
	// 自己微信应用的 appSecret
	private String WX_SECRET;
	/** 微信工具类 */
	private WeixinUtil weixinInstance;

	/********* 微信登录 end ******************/

	@Override
	public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
		String callBackName = args.getString(0);

		if ("login".equals(action)) {
			cordova.getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(cordova.getActivity(), "正在登录，请稍等！", Toast.LENGTH_SHORT).show();
				}
			});
			Map<String, String> configInfo = ParseConfig.getInstance(cordova.getActivity()).getConfigInfo();
			WX_APP_ID = configInfo.get("wxapp-id");
			WX_SECRET = configInfo.get("wxapp-secret");
			weixinInstance = WeixinUtil.getInstance();
			if (WX_APP_ID != null && !"".equals(WX_APP_ID)) {
				// 初始化微信登录
				if (weixinInstance.getWeixinApi() == null) {
					weixinInstance.initWeixinApi(cordova.getActivity().getApplicationContext(), WX_APP_ID);
				}
			}

			boolean isSuccess = WeixinUtil.getInstance().weixinSendReq(callBackName);
			if (isSuccess) {
				callbackContext.success();
				return true;
			} else {
				cordova.getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(cordova.getActivity(), "您可能没有安装微信，登录失败！", Toast.LENGTH_SHORT).show();
					}
				});
				return false;
			}

		}
		return false;
	}

	@Override
	public void onResume(boolean multitasking) {
		if (AppConfig.DEBUG) {
			Log.i("zzy", "xinyuplugin onresume:");
			// super.onResume(multitasking);
			Log.i("zzy", "isLogin:" + weixinInstance.isWXLogin());
			Log.i("zzy", "getWXCode:" + weixinInstance.getWXCode());
		}
		if (weixinInstance != null && weixinInstance.isWXLogin() && weixinInstance.getWXCode() != null) {
			// 设置不再再次登录微信
			weixinInstance.setIsWXLogin(false);
			weixinInstance.loadWXUserInfo(WX_SECRET, weixinInstance.getWXCode(), new GetUserInfoListener() {

				@Override
				public void onResp(final String userInfo) {
					// Message message = Message.obtain();
					// message.what = WXLOGIN;
					// message.obj = userInfo;
					// mHandler.sendMessage(message);
					weixinInstance.saveWXCode(null);
					cordova.getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							webView.loadUrl("javascript:" + WeixinUtil.getInstance().getCallBackJavascriptName() + "('" + userInfo + "')");
						}
					});

					if (AppConfig.DEBUG)
						Log.e("zzy", "myhome:" + userInfo);
				}
			});

		}

	}
}
