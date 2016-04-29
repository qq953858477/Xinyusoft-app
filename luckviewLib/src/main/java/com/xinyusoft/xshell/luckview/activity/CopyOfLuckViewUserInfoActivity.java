package com.xinyusoft.xshell.luckview.activity;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xinyusoft.xshell.luckview.R;
import com.xinyusoft.xshell.luckview.fragment.HuafeiFragment;
import com.xinyusoft.xshell.luckview.fragment.LiuliangFragment;
import com.xinyusoft.xshell.luckview.utils.SystemBarTintManager;
import com.xinyusoft.xshell.luckview.utils.VolleyUtil;
import com.xinyusoft.xshell.luckview.widget.PagerSlidingTabStrip;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zzy on 2016/2/23.
 */
public class CopyOfLuckViewUserInfoActivity extends FragmentActivity implements OnClickListener {

	private RequestQueue queue;
	private String ipUrl;
	private String userId;
	private TextView tv_availableScore;

	private double availableScore;

	PagerSlidingTabStrip tabs;
	ViewPager pager;
	DisplayMetrics dm;

	HuafeiFragment huafeiFragment;
	LiuliangFragment liuliangFragment;
	String[] titles = { "充话费", "充流量" };
	private LinearLayout ll_main;
	private ProgressBar progress;
	private TextView luckview_allsocre;

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
		setContentView(R.layout.xinyusoft_activity_luckview_user);

		LuckViewCollector.addActivity(this);

		initView();
		queue = VolleyUtil.getRequestQueue(this);
		userId = getIntent().getStringExtra("userId");
		ipUrl = getIntent().getStringExtra("ipUrl");
		updateUserScore();
	}

	/**
	 * 设置沉浸式状态栏
	 */
	private void setcolorfulStatusBar() {
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setTintColor(getResources().getColor(R.color.luckview_colorful_status_bar));
		// SystemBarConfig config = tintManager.getConfig();
		// listViewDrawer.setPadding(0, config.getPixelInsetTop(true), 0,
		// config.getPixelInsetBottom());
	}

	private void initView() {
		tv_availableScore = (TextView) findViewById(R.id.luckview_user_availablescore);
		ImageView iv_back = (ImageView) findViewById(R.id.luckview_title_back);
		iv_back.setOnClickListener(this);
		TextView luckview_title_rule = (TextView) findViewById(R.id.luckview_title_rule);
		luckview_title_rule.setOnClickListener(this);
		
		luckview_allsocre = (TextView) findViewById(R.id.luckview_allsocre);
		pager = (ViewPager) findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

		ll_main = (LinearLayout) findViewById(R.id.luckview_userInfo_main);
		progress = (ProgressBar) findViewById(R.id.progressBar1);
	}

	private void updateUserScore() {
		String url = ipUrl + "/ESBServlet?command=user.GetUserLuckyDrawAction&userid=" + userId;
		StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String s) {
				try {
					JSONObject object = new JSONObject(s);
					if (object.has("luckydrawscore")) {
						JSONObject luckydrawscore = object.getJSONObject("luckydrawscore");
						// String usedScore =
						// luckydrawscore.getString("f_usedscore");
						availableScore = luckydrawscore.getDouble("f_availablescore");
						String countScore = luckydrawscore.getString("f_countscore");	
						luckview_allsocre.setText("总积分："+countScore);
						tv_availableScore.setText(availableScore + "");

						ll_main.setVisibility(View.VISIBLE);
						progress.setVisibility(View.GONE);

						pager.setAdapter(new MyAdapter(getSupportFragmentManager(), titles));
						tabs.setViewPager(pager);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				ll_main.setVisibility(View.VISIBLE);
				Toast.makeText(CopyOfLuckViewUserInfoActivity.this, "当前网络不可用，请稍候再试！", Toast.LENGTH_SHORT).show();
			}
		});
		queue.add(request);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.luckview_title_back) {
			finish();
		} else if(id == R.id.luckview_title_rule) {
			startActivity(new Intent(this, LuckViewRuleActivity.class));
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
