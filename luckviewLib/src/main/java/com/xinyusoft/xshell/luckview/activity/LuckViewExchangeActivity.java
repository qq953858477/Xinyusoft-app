package com.xinyusoft.xshell.luckview.activity;

import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xinyusoft.xshell.luckview.R;
import com.xinyusoft.xshell.luckview.fragment.HuafeiFragment;
import com.xinyusoft.xshell.luckview.fragment.LiuliangFragment;
import com.xinyusoft.xshell.luckview.utils.SystemBarTintManager;
import com.xinyusoft.xshell.luckview.widget.PagerSlidingTabStrip;

/**
 * 积分兑换Activity
 * @author zzy
 *
 */
public class LuckViewExchangeActivity extends FragmentActivity implements OnClickListener {
	
	String[] titles = { "充话费", "充流量" };
	PagerSlidingTabStrip tabs;  //标题卡
	ViewPager pager;
	
	private String ipUrl;
	private String userId;
	private double availableScore;
	HuafeiFragment huafeiFragment;
	LiuliangFragment liuliangFragment;
	private ProgressBar progress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		userId = getIntent().getStringExtra("userId");
		ipUrl = getIntent().getStringExtra("ipUrl");
		availableScore = getIntent().getDoubleExtra("availableScore", 0);
		
		LuckViewCollector.addActivity(this);
		
		setContentView(R.layout.xinyusoft_activity_luckview_exchange);
		initViews();
		pager.setAdapter(new MyAdapter(getSupportFragmentManager(), titles));
		tabs.setViewPager(pager);
		setcolorfulStatusBar();
		
	}
	
	
	/**
	 * 设置沉浸式状态栏
	 */
	private void setcolorfulStatusBar() {
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setTintColor(getResources().getColor(R.color.colorful_status_bar));
	}

	private void initViews() {
		ImageView back = (ImageView) findViewById(R.id.luckview_title_back);
		back.setOnClickListener(this);
		
		TextView titleName = (TextView) findViewById(R.id.luckview_title_name);
		titleName.setText("积分兑换");
		
		pager = (ViewPager) findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		progress = (ProgressBar) findViewById(R.id.progressBar1);
		progress.setVisibility(View.GONE);
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.luckview_title_back) {
			finish();
		} 
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LuckViewCollector.removeActivity(this);
	}
	
	
	
	public class MyAdapter extends FragmentPagerAdapter {
		String[] _titles;

		public MyAdapter(FragmentManager fm, String[] titles) {
			super(fm);
			_titles = titles;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return _titles[position];
		}

		@Override
		public int getCount() {
			return _titles.length;
		}

		@Override
		public Fragment getItem(int position) {
			// BaseFragment fragment;
			switch (position) {
			case 0:
				if (huafeiFragment == null) {
					huafeiFragment = new HuafeiFragment(progress);
					Bundle bundle = new Bundle();
					bundle.putString("userId", userId);
					bundle.putString("ipUrl", ipUrl);
					bundle.putDouble("availableScore", availableScore);
					huafeiFragment.setArguments(bundle);
				}
				return huafeiFragment;
			case 1:
				if (liuliangFragment == null) {
					liuliangFragment = new LiuliangFragment(progress);
					Bundle bundle = new Bundle();
					bundle.putString("userId", userId);
					bundle.putString("ipUrl", ipUrl);
					bundle.putDouble("availableScore", availableScore);
					liuliangFragment.setArguments(bundle);
				}
				return liuliangFragment;
			default:
				return null;
			}
		}
	}
}
