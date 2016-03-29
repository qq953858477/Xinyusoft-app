package com.xinyusoft.xshell.luckview.activity;

import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.xinyusoft.xshell.luckview.R;
import com.xinyusoft.xshell.luckview.utils.SystemBarTintManager;

public abstract class LuckViewBaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			// getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			// setTranslucentStatus(true);
		}
		setcolorfulStatusBar();

	}

	/**
	 * 设置沉浸式状态栏
	 */
	private void setcolorfulStatusBar() {
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setTintColor(getResources().getColor(R.color.colorful_status_bar));
		// SystemBarConfig config = tintManager.getConfig();
		// listViewDrawer.setPadding(0, config.getPixelInsetTop(true), 0,
		// config.getPixelInsetBottom());
	}

}
