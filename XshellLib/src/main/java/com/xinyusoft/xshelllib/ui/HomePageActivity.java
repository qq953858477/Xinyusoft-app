package com.xinyusoft.xshelllib.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.xinyusoft.xshelllib.R;
import com.xinyusoft.xshelllib.application.AppConstants;
import com.xinyusoft.xshelllib.tools.xinge.XGPushUtil;
import com.xinyusoft.xshelllib.utils.PhoneInfo;
import com.xinyusoft.xshelllib.utils.SystemBarTintManager;

import org.apache.cordova.engine.SystemWebView;

public class HomePageActivity extends XinyuHomeActivity {

	private static final int XINGE_JUMP = 1;

	private MYInnerReceiver homePageReceiver;

	private Handler tHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case XINGE_JUMP:
				String jumpurl = (String) msg.obj;
				String url = "javascript:" + "xgmsgPop" + "('" + jumpurl + "')";
				Log.e("zzy", "myjjjjjurl:" + url);
				loadUrl(url);
				break;
			default:
				break;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			// getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

		}
		initXGPush();
		String url = null;
		url = "file:///android_asset/www/testLuckView.html";
//		 url = "file:///" + getFilesDir().getAbsolutePath() + File.separator +
//		 "index.html";

		// url = "file:///" + getFilesDir().getAbsolutePath() +
		// File.separator +"C.html";
		mRegReceiver();
		loadUrl(url);
		setcolorfulStatusBar();
		appView.getView().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

	private void mRegReceiver() {
		homePageReceiver = new MYInnerReceiver();
		IntentFilter filter = new IntentFilter();
		// 刷新页面
		filter.addAction(AppConstants.RELOAD_HOME_PAGE);
		registerReceiver(homePageReceiver, filter);
	}

	/**
	 * 设置沉浸式状态栏
	 */
	private void setcolorfulStatusBar() {
		// create our manager instance after the content view is set
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setTintColor(getResources().getColor(R.color.colorful_status_bar));
	}

	/**
	 * 广播接收器，在子类又写了一个，这个为了是通知所有的界面
	 * 
	 * @author zzy,wn
	 *
	 */
	private class MYInnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (AppConstants.RELOAD_HOME_PAGE.equals(action)) {
				SystemWebView v = (SystemWebView) appView.getEngine().getView();
				v.reload();
			}
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		if (intent.hasExtra("jumpurl")) {
			Message message = Message.obtain();
			message.what = XINGE_JUMP;
			message.obj = intent.getStringExtra("jumpurl");
			tHandler.sendMessageDelayed(message, 3000);
			appView.getView().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			return;
		}

	}

	@Override
	public int getContentViewRes() {
		return R.layout.xinyusoft_main;
	}

	@Override
	public int getLinearLayoutId() {
		return R.id.linearLayout;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (homePageReceiver != null) {
			unregisterReceiver(homePageReceiver);
		}
	}

	private void initXGPush() {
		ApplicationInfo appInfo = null;
		try {
			appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
			String key = appInfo.metaData.getString("XG_V2_ACCESS_KEY");
			if (key != null && !"".equals(key)) {
				// 初始化信鸽推送
				XGPushUtil.initXGPush(getApplicationContext(), PhoneInfo.getInstance().getDeviceID());
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
