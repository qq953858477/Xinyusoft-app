package com.xinyusoft.xshelllib.ui;

import java.io.File;

import org.apache.cordova.engine.SystemWebView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.xinyusoft.xshelllib.R;
import com.xinyusoft.xshelllib.application.AppConstants;
import com.xinyusoft.xshelllib.utils.SystemBarTintManager;

public class NewBrowserActivity extends XinyuHomeActivity {
	private NewBrowserInnerReceiver mReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			// getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

		}
		String url = null;
		if (getIntent().hasExtra("newBrowserUrl")) {
			String temp = getIntent().getStringExtra("newBrowserUrl");
			if ("http".regionMatches(0, temp, 0, 4)) {
				url = temp;
			} else {
				url = "file:///" + getFilesDir().getAbsolutePath() + File.separator + temp;

				// url = "file:///android_asset/www/"+temp;
			}
		}

		setcolorfulStatusBar();
		loadUrl(url);
		// loadUrl("file:///android_asset/www/testWebSocket2.html");
		appView.getView().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		NewBrowserCollector.addActivityOnlyName(this.toString());
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		Log.i("zzy","canGoBack:");
//		if (keyCode == KeyEvent.KEYCODE_BACK ) {
//			finish();
//			overridePendingTransition(R.anim.xinyusoft_activity_right_in, R.anim.xinyusoft_activity_left_out);
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

//	@Override
//	public boolean dispatchKeyEvent(KeyEvent event) {
//		Log.i("zzy","SystemWebView:"+((SystemWebView) appView.getView()).canGoBack());
//		int keyCode = event.getKeyCode();
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			
//			if (((SystemWebView) appView.getView()).canGoBack()) {
//				((SystemWebView) appView.getView()).goBack();
//				return true;
//			} else {
//				finish();
//				overridePendingTransition(R.anim.xinyusoft_activity_right_in, R.anim.xinyusoft_activity_left_out);
//				return true;
//			}
//		}
//		return super.dispatchKeyEvent(event);
//		//return super.dispatchKeyEvent(event);
//	}

	/**
	 * 设置沉浸式状态栏
	 */
	private void setcolorfulStatusBar() {
		// create our manager instance after the content view is set
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		// enable status bar tint
		tintManager.setStatusBarTintEnabled(true);
		// enable navigation bar tint
		// tintManager.setNavigationBarTintEnabled(true);
		// set a custom tint color for all system bars
		// tintManager.setTintColor(Color.parseColor("#5a3319"));
		tintManager.setTintColor(Color.parseColor("#5a3319"));
		// set a custom navigation bar resource
		// tintManager.setNavigationBarTintResource(R.drawable.my_tint);
		// set a custom status bar drawable
		// tintManager.setStatusBarTintDrawable(MyDrawable);
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
	protected void onResume() {
		super.onResume();
		mRegReceiver();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (NewBrowserCollector.getOnlyNamesFirstName().equals(this.toString())) {
			NewBrowserCollector.removeAllNames();
			System.exit(0);
		}

	}

	private void mRegReceiver() {
		mReceiver = new NewBrowserInnerReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstants.RELOAD_NEW_BROWSER_PAGE);
		registerReceiver(mReceiver, filter);
	}

	/**
	 * 广播接收器,在子类又写了一个，这个是为了通知所有的界面
	 * 
	 * @author zzy,wn
	 *
	 */
	private class NewBrowserInnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (AppConstants.RELOAD_NEW_BROWSER_PAGE.equals(action)) {
				SystemWebView v = (SystemWebView) appView.getEngine().getView();
				v.reload();
			}
		}

	}
}
