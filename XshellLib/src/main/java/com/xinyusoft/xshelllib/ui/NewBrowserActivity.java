package com.xinyusoft.xshelllib.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.xinyusoft.xshelllib.R;
import com.xinyusoft.xshelllib.application.AppConstants;
import com.xinyusoft.xshelllib.utils.FulStatusBarUtil;

import org.apache.cordova.engine.SystemWebView;

import java.io.File;

public class NewBrowserActivity extends XinyuHomeActivity {
	private NewBrowserInnerReceiver mReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		FulStatusBarUtil.setcolorfulStatusBar(this);
		String url = null;
		if (getIntent().hasExtra("newBrowserUrl")) {
			String temp = getIntent().getStringExtra("newBrowserUrl");
			if ("http".regionMatches(0, temp, 0, 4)) {
				url = temp;
			} else {
				if(getIntent().hasExtra("projectListUrl")) {
					url = "file:///" + getFilesDir().getAbsolutePath() + File.separator +getIntent().getStringExtra("projectListUrl") + File.separator+temp;
				} else {
					url = "file:///" + getFilesDir().getAbsolutePath() + File.separator + temp;
				}


				// url = "file:///android_asset/www/"+temp;
			}
		}

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
