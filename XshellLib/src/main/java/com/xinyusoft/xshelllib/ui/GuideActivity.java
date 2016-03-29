package com.xinyusoft.xshelllib.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.viewpagerindicator.CirclePageIndicator;
import com.xinyusoft.xshelllib.R;
import com.xinyusoft.xshelllib.adapter.TestFragmentAdapter;
import com.xinyusoft.xshelllib.application.AppConstants;
import com.xinyusoft.xshelllib.utils.PreferenceUtil;
import com.xinyusoft.xshelllib.utils.SystemBarTintManager;

import java.io.File;

public class GuideActivity extends BaseSampleActivity {

	private LinearLayout btn;
	private String webindex;
	private GuideBroadcastRecevier recever;
	ViewPager viewpage;
	boolean ifAll = false;
	int posi = TestFragmentAdapter.getPosi();
	int cc = TestFragmentAdapter.getC();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xinyusoft_activity_guide);
		// 程序启动后模拟录音，请求录音权限
		// RecorderUtil re = new RecorderUtil();
		// re.startRecorder(getFilesDir().getAbsolutePath()+"/test.3gp");
		// re.stopRecorder();
		viewpage = (ViewPager) this.findViewById(getResources().getIdentifier("pager", "id", getPackageName()));
		btn = (LinearLayout) this.findViewById(getResources().getIdentifier("lineee", "id", getPackageName()));
		btn.setOnClickListener(BtnClick);
		if (getIntent() != null) {// 获取loading传过来的参数
			Intent inte = getIntent();
			webindex = inte.getStringExtra("jumpurl");
		}
		mAdapter = new TestFragmentAdapter(getSupportFragmentManager(), GuideActivity.this, webindex);
		// 注册guideactivity的广播接收者
		recever = new GuideBroadcastRecevier();
		registerReceiver(recever, new IntentFilter(AppConstants.GUIDE_BROADCAST_NAME));

		mPager = (ViewPager) findViewById(getResources().getIdentifier("pager", "id", getPackageName()));
		mPager.setAdapter(mAdapter);
		
		mIndicator = (CirclePageIndicator) findViewById(getResources().getIdentifier("indicator", "id", getPackageName()));
		mIndicator.setViewPager(mPager);

		PreferenceUtil.getInstance().setFIRSTRUN(false);
		setcolorfulStatusBar();
	}

	
	/**
	 * 设置沉浸式状态栏
	 */
	private void setcolorfulStatusBar() {
		// create our manager instance after the content view is set
	    SystemBarTintManager tintManager = new SystemBarTintManager(this);
	    // enable status bar tint
	    tintManager.setStatusBarTintEnabled(true);
	    // enable navigation bar tint
	    //tintManager.setNavigationBarTintEnabled(true);
	 // set a custom tint color for all system bars
	    //tintManager.setTintColor(Color.parseColor("#5a3319"));
	    tintManager.setTintColor(Color.parseColor("#5a3319"));
	    // set a custom navigation bar resource
	    //tintManager.setNavigationBarTintResource(R.drawable.my_tint);
	    // set a custom status bar drawable
	    //tintManager.setStatusBarTintDrawable(MyDrawable);
	}
	
	class GuideBroadcastRecevier extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int type = intent.getIntExtra("type", 0);
			int posi = intent.getIntExtra("posi", 0);
			switch (type) {
			case 0:
				if (posi == getFilLength()) {
					btn.setVisibility(View.VISIBLE);
					ifAll = true;
				}
				break;
			case 1:
				if (posi == getFilLength()) {
					btn.setVisibility(View.VISIBLE);
				}
				btn.setVisibility(View.GONE);
				break;
			case 3:
				GuideActivity.this.finish();
				break;
			}
		}
	}

	OnClickListener BtnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {

			Intent intent = new Intent();
			
			try {
				intent.setClass(GuideActivity.this, Class.forName(PreferenceUtil.getInstance().getHomeActivityPath()));
				if (webindex != null) {
					intent.putExtra("jumpurl", webindex);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			GuideActivity.this.startActivity(intent);
			GuideActivity.this.finish();

		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(recever);
	}

	public int getFilLength() {
		String imageurl = this.getFilesDir().getAbsolutePath() + "/imagepage/android";
		File ff = new File(imageurl);
		String[] name = ff.list();
		return (name.length)-1;
	}
}
