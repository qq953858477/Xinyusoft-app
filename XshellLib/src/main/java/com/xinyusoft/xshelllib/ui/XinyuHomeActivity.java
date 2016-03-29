package com.xinyusoft.xshelllib.ui;

import java.io.File;
import java.util.List;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.engine.SystemWebView;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.inputmethodservice.KeyboardView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.xinyusofi.xshell.softkeyboarad.BaseKeyboard;
import com.xinyusofi.xshell.softkeyboarad.KeyboardNumeric;
import com.xinyusofi.xshell.softkeyboarad.KeyboardPrice;
import com.xinyusofi.xshell.softkeyboarad.KeyboardStock;
import com.xinyusofi.xshell.softkeyboarad.OnKeyboardClick;
import com.xinyusoft.xshelllib.R;
import com.xinyusoft.xshelllib.application.AppConfig;
import com.xinyusoft.xshelllib.application.AppConstants;
import com.xinyusoft.xshelllib.pay.OnPasswordInputFinish;
import com.xinyusoft.xshelllib.pay.PasswordView;
import com.xinyusoft.xshelllib.tools.download.Html5Download;
import com.xinyusoft.xshelllib.utils.PlaySoundsUtil;
import com.xinyusoft.xshelllib.utils.PreferenceUtil;

/**
 * 主界面
 * 
 * @author zzy
 *
 */
public abstract class XinyuHomeActivity extends CordovaActivity {

	private static final String TAG = "XinyuHomeActivity";
	private InnerReceiver receiver;
	private Context xinyuHomeContext;
	private BaseKeyboard baseKeyboard;
	private String callbackName;
	private PopupWindow pop;
	private LinearLayout ll_popup;
	ProgressDialog dialog;
	private static boolean isActive = true;

	private NetworkChangeReceive networkChangeReceive;
	private boolean flag = false;
	private final static String NETACTION = "android.net.conn.CONNECTIVITY_CHANGE";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		xinyusoftInit();
		initPopupWindow();
	}

	private void xinyusoftInit() {
		xinyuHomeContext = XinyuHomeActivity.this;
	}

	private void regReceiver() {
		if (receiver == null) {
			receiver = new InnerReceiver();
			IntentFilter filter = new IntentFilter();
			// 播放
			filter.addAction(AppConstants.ACTION_WEBVIEW_PLAY_SOUND);
			// 开启一个新的Activity
			filter.addAction(AppConstants.ACTION_NEW_BROSER);
			// 关闭Activity
			filter.addAction(AppConstants.ACTION_CLOSE_BROSER);
			// 显示键盘
			filter.addAction(AppConstants.SHOW_KEYBOARD);
			filter.addAction("unlock");
			filter.addAction("forgetpassword");
			filter.addAction("setPayPassword");
			filter.addAction("payState");
			LocalBroadcastManager.getInstance(XinyuHomeActivity.this).registerReceiver(receiver, filter);
		}

		if (networkChangeReceive == null) {
			IntentFilter intentFilter = new IntentFilter();
			// addAction
			intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
			networkChangeReceive = new NetworkChangeReceive();
			registerReceiver(networkChangeReceive, intentFilter);
		}

	}

	/**
	 * 广播接收器
	 * 
	 * @author zzy,wn
	 *
	 */
	private class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context mContext, Intent intent) {
			String action = intent.getAction();
			if (AppConstants.SHOW_KEYBOARD.equals(action)) { // 自定义软键盘
				final String callBackName = intent.getStringExtra("callbackName");
				KeyboardView keyboard = (KeyboardView) findViewById(getResources().getIdentifier("keyboard_view", "id", getPackageName()));
				if (baseKeyboard != null) {
					baseKeyboard = null;
				}
				switch (Integer.valueOf(intent.getStringExtra("type"))) {
				case 1:
					baseKeyboard = new KeyboardStock(XinyuHomeActivity.this, XinyuHomeActivity.this, keyboard);
					baseKeyboard.setDefalutKeyboard(Integer.valueOf(intent.getStringExtra("defalutIndex")));
					break;
				case 2:
					baseKeyboard = new KeyboardNumeric(XinyuHomeActivity.this, XinyuHomeActivity.this, keyboard);
					break;
				case 3:
					baseKeyboard = new KeyboardPrice(XinyuHomeActivity.this, XinyuHomeActivity.this, keyboard);
					break;
				default:
					break;
				}

				baseKeyboard.setOnkeyboardClick(new OnKeyboardClick() {

					@Override
					public void onClick(String value) {
						if (BaseKeyboard.KEYCODE_CANCEL.equals(value)) {// 点击确定
							baseKeyboard.hide();
							baseKeyboard = null;
						}
						loadUrl("javascript:" + callBackName + "('" + value + "')");

					}
				});
				baseKeyboard.show();
			} else if (AppConstants.ACTION_WEBVIEW_PLAY_SOUND.equals(action)) {
				final int soundId = intent.getIntExtra("soundId", -1);
				if (soundId == -1)
					return;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						PlaySoundsUtil.getInstance().play(soundId, 0);
					}
				}, 200);
			} else if (AppConstants.ACTION_NEW_BROSER.equals(action)) { // 开启一个新的Activity

				String localUrl = intent.getStringExtra("url");
				Intent in = null;
				in = new Intent(XinyuHomeActivity.this, NewBrowserActivity.class);
				in.putExtra("newBrowserUrl", localUrl);
				startActivity(in);
				overridePendingTransition(R.anim.xinyusoft_activity_right_in, R.anim.xinyusoft_activity_left_out);
			} else if (AppConstants.ACTION_CLOSE_BROSER.equals(action)) { // 关闭那个新的broser
				finish();
				overridePendingTransition(R.anim.xinyusoft_activity_left_in, R.anim.xinyusoft_activity_right_out);
			} else if ("textJumpUrl".equals(action)) {
				String url = null;
				url = "file:///" + getFilesDir().getAbsolutePath() + File.separator + "uufpBase.html";
				loadUrl(url);
			} else if ("unlock".equals(action)) {
				String function = intent.getStringExtra("functionName");
				loadUrl("javascript:" + function + "(1)");
			} else if ("forgetpassword".equals(action)) {
				loadUrl("javascript:forgetPassword()");
			} else if ("setPayPassword".equals(action)) {
				callbackName = intent.getStringExtra("callbackName");

				ll_popup.startAnimation(AnimationUtils.loadAnimation(XinyuHomeActivity.this, getResources().getIdentifier("activity_translate_in", "anim", getPackageName())));
				pop.showAtLocation(getLayoutInflater().inflate(getContentViewRes(), null), Gravity.BOTTOM, 0, 0);
			} else if ("payState".equals(action)) {
				String payState = intent.getStringExtra("payState");
				if ("1".equals(payState)) {
					dialog.dismiss();
					pop.dismiss();
					ll_popup.clearAnimation();
				} else {
					dialog.dismiss();
					Toast.makeText(XinyuHomeActivity.this, "密码错误", Toast.LENGTH_LONG).show();
				}
			}

		}
	}

	class NetworkChangeReceive extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isAvailable()) {
				if (flag) {
					// ((WebView)
					// appView.getEngine().getView()).getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
					// ((WebView)
					// appView.getEngine().getView()).getSettings().setDomStorageEnabled(true);
					SystemWebView v = (SystemWebView) appView.getEngine().getView();
					v.reload();

					flag = false;
				}
			} else {
				Toast.makeText(XinyuHomeActivity.this, "网络不稳定", Toast.LENGTH_SHORT).show();
				// ((WebView)
				// appView.getEngine().getView()).getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
				// SystemWebView v = (SystemWebView)
				// appView.getEngine().getView();
				// v.reload();
				flag = true;
			}
		}
	}

	public void initPopupWindow() {
		pop = new PopupWindow();
		View view = getLayoutInflater().inflate(getResources().getIdentifier("xinyusoft_item_popupwindows", "layout", getPackageName()), null);

		ll_popup = (LinearLayout) view.findViewById(getResources().getIdentifier("ll_popup", "id", getPackageName()));

		final PasswordView password = (PasswordView) view.findViewById(getResources().getIdentifier("passwordView1", "id", getPackageName()));
		password.setOnFinishInput(new OnPasswordInputFinish() {

			@Override
			public void inputFinish() {
				// TODO Auto-generated method stub
				dialog = new ProgressDialog(XinyuHomeActivity.this);
				dialog.setTitle("正在验证密码");
				dialog.setMessage("请稍后...");
				dialog.show();
				loadUrl("javascript:" + callbackName + "(" + password.getStrPassword() + ")");
			}
		});

		// 关闭支付密码
		password.getCancelImageView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				pop.dismiss();
				ll_popup.clearAnimation();

			}
		});

		// 忘记支付密码
		password.getForgetTextView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadUrl("javascript:forgetPayPassword()");
			}
		});

		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (baseKeyboard != null) {
			int keyboardY = (int) baseKeyboard.getKeyboardView().getY();
			int eventY = (int) ev.getY();
			if (eventY < keyboardY) {
				baseKeyboard.hide();
			}

		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i(TAG, "onConfigurationChanged:");
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (NewBrowserCollector.onlyNames.size() == 0) {
			if (receiver != null) {
				LocalBroadcastManager.getInstance(xinyuHomeContext).unregisterReceiver(receiver);
				unregisterReceiver(networkChangeReceive);
				receiver = null;
				flag = false;
				networkChangeReceive = null;
			}
		} else {
			if (NewBrowserCollector.onlyNames.get(NewBrowserCollector.onlyNames.size() - 1).contains("NewBrowserActivity")) {
				LocalBroadcastManager.getInstance(xinyuHomeContext).unregisterReceiver(receiver);
				unregisterReceiver(networkChangeReceive);
				receiver = null;
				flag = false;
				networkChangeReceive = null;
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isActive) {
			// app 从后台唤醒，进入前台
			if (AppConfig.DEBUG)
				Log.i("zzy", "进入前台:");
			isActive = true;
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					loadUrl("javascript:appState('1')");
				}
			}, 1000);
			synchronized (TAG) {
				if (!PreferenceUtil.getInstance().getDownloadingFile()) { // 设置正在更新
					if (AppConfig.DEBUG)
						Log.e("zzy", "进入前台了更新:");
					PreferenceUtil.getInstance().setDownloadingFile(true);
					Html5Download load = new Html5Download();
					load.checkH5(this, this);
				}
			}

		}
		regReceiver();// 注册广播

		NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(101010);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!isAppOnForeground()) {
			// app 进入后台
			isActive = false;
			if (AppConfig.DEBUG)
				Log.i("zzy", "进入后台:");
			loadUrl("javascript:appState('0')");
		}
		NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(101010);
	}

	public boolean isAppOnForeground() {
		ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = getApplicationContext().getPackageName();

		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName) || appProcess.processName.equals(packageName + ":xinyu_remote") || appProcess.processName.contains("com.tencent.")) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					return true;
				}

			}
		}

		return false;

	}

}
