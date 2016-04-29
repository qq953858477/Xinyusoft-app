package com.xinyusoft.xshell.luckview.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.xinyusoft.xshell.luckview.utils.FulStatusBarUtil;

public abstract class LuckViewBaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		FulStatusBarUtil.setcolorfulStatusBar(this);

	}

}
