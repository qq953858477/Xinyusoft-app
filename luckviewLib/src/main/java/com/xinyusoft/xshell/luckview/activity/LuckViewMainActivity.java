package com.xinyusoft.xshell.luckview.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xinyusoft.xshell.luckview.LuckyPanView;
import com.xinyusoft.xshell.luckview.MyDialog;
import com.xinyusoft.xshell.luckview.NoPriceDialog;
import com.xinyusoft.xshell.luckview.R;
import com.xinyusoft.xshell.luckview.bean.PrizeStock;
import com.xinyusoft.xshell.luckview.utils.SoundUtil;
import com.xinyusoft.xshell.luckview.utils.VolleyUtil;
import com.xinyusoft.xshell.luckview.utils.weixin.WeixinUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * 大转盘主类
 */
public class LuckViewMainActivity extends LuckViewBaseActivity implements OnClickListener {
	/**
	 * 自定义转盘控件
	 */
	private LuckyPanView mLuckyPanView;
	/**
	 * 开始的按钮控件
	 */
	private ImageView mStartBtn;
	private SoundUtil sound;
	/**
	 * 计时的控件
	 */
	private TextView time_tv;
	/**
	 * 彩灯的背景图
	 */
	private ImageView iv_bgRotate;
	/**
	 * 判断轮流切换的图片是哪个
	 */
	private boolean bgRotateChange;
	private int den1 = R.drawable.xinyusoft_luckview_den1;
	private int den2 = R.drawable.xinyusoft_luckview_den2;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			end = System.currentTimeMillis() - start;
			time_tv.setText(toTime(end));
		}
	};
	private long start;
	private long end;
	private boolean flag = false;
	/**
	 * 是否关闭音乐
	 */
	private boolean isCloseSound = false;

	/**
	 * 按钮的状态 0-开始 1-结束 2-复位
	 */
	private int startBtnType = 0;

	/**
	 * 数据源
	 */
	private ArrayList<PrizeStock> myData = new ArrayList<PrizeStock>();

	/**
	 * 开启stocklistActivity的请求码
	 */
	public static final int STOCKLIST_REQUEST_CODE = 1;
	
	
	private Runnable bgRunnable = new Runnable() {
		@Override
		public void run() {
			if (bgRotateChange) {
				iv_bgRotate.setImageResource(den1);
			} else {
				iv_bgRotate.setImageResource(den2);
			}
			bgRotateChange = !bgRotateChange;
			handler.postDelayed(bgRunnable, 1000);
		}
	};
	private ImageView luck_music;

	private RequestQueue queue;
	private String ipUrl;
	private String userId;
	/** 抽到的奖品实体 */
	private PrizeStock mPrizeStock;
	private TextView luckview_sharemyfriend;
	private TextView iv_share;
	private ImageView luckview_changedata;
	private ImageView luckivew_back;
	private TextView luckview_score;
	/** 点击换股的动画 */
	private ObjectAnimator animator;
	private String userName;
	private TextView luckview_moreluck;
	private RelativeLayout luckview_huangu_rl;
	private RelativeLayout luckview_show_timeandbtn;
	private TextView showTomorrow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.xinyusoft_activity_luckview_main);
		LuckViewCollector.addActivity(this);

		userId = getIntent().getStringExtra("userId");
		ipUrl = getIntent().getStringExtra("url");
		userName = getIntent().getStringExtra("userName");

		queue = VolleyUtil.getRequestQueue(this);
		handler.postDelayed(bgRunnable, 1000);

		initView();
		setListener();
		initSound();
		mStartBtn.setEnabled(false);
		initDate();
		isLotteryDrawForStart();


	}

	private void setListener() {
		luckview_sharemyfriend.setOnClickListener(this);
		iv_share.setOnClickListener(this);
		luck_music.setOnClickListener(this);
		mStartBtn.setOnClickListener(this);
		luckview_changedata.setOnClickListener(this);
		luckivew_back.setOnClickListener(this);
		luckview_score.setOnClickListener(this);
		luckview_moreluck.setOnClickListener(this);
		mLuckyPanView.setOnLuckPanStopListener(new LuckyPanView.OnLuckPanStopListener() {
			@Override
			public void onLuckPanStop(PrizeStock prizeStock) {
				mPrizeStock = prizeStock;
				sound.stop("结束");
				// 上传数据
				updateUserScore(prizeStock);
				// 显示弹出框
				showMyDialog(prizeStock);
				// if(prizeStock.getZdf())
				mStartBtn.setEnabled(true);
			}
		});
		mLuckyPanView.setOnClickListener(this);
	}

	private void initView() {
		mLuckyPanView = (LuckyPanView) findViewById(R.id.id_luckypan);
		mStartBtn = (ImageView) findViewById(R.id.id_start_btn);
		iv_bgRotate = (ImageView) findViewById(R.id.id_start_bgrotate);
		luck_music = (ImageView) findViewById(R.id.luck_music);
		luckview_sharemyfriend = (TextView) findViewById(R.id.luckview_sharemyfriend);
		iv_share = (TextView) findViewById(R.id.luck_share);
		time_tv = (TextView) findViewById(R.id.show);
		luckview_changedata = (ImageView) findViewById(R.id.luckview_changedata);
		luckivew_back = (ImageView) findViewById(R.id.luckivew_back);
		luckview_score = (TextView) findViewById(R.id.luckview_score);
		luckview_moreluck = (TextView) findViewById(R.id.luckview_moreluck);
		luckview_huangu_rl = (RelativeLayout) findViewById(R.id.luckview_huangu_rl);
		luckview_show_timeandbtn = (RelativeLayout) findViewById(R.id.luckview_show_timeandbtn);
		showTomorrow = (TextView) findViewById(R.id.luckview_tomorrow);

	}

	private void updateUserScore(PrizeStock prizeStock) {
		double zdf = prizeStock.getZdf();
		if (zdf <= 0) {
			zdf = 0;
		}
		String url = ipUrl + "/ESBServlet?command=user.addluckydrawaction&" + "userid=" + userId + "&zdf=" + zdf + "&isLimitUp=" + prizeStock.isLimitUp() + "&date="
				+ prizeStock.getTime();

		StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				try {
					JSONObject object = new JSONObject(s);
					JSONObject op = object.getJSONObject("op");
					String code = op.getString("code");
					if ("Y".equals(code)) {
						// Toast.makeText(LuckViewMainActivity.this, "抽奖成功！",
						// Toast.LENGTH_SHORT).show();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				Toast.makeText(LuckViewMainActivity.this, "当前网络不可用，请稍候再试！", Toast.LENGTH_SHORT).show();
			}
		});
		queue.add(request);
	}

	/**
	 * 显示中奖奖品
	 *
	 * @param price
	 */
	private void showMyDialog(PrizeStock price) {

		if (price.getZdf() <= 0) {
			NoPriceDialog dialog = new NoPriceDialog(this, R.style.xinyusoft_dialog_fullscreen, price);
			dialog.show();
			return;
		}
		MyDialog selectDialog = new MyDialog(this, price, R.style.xinyusoft_dialog_fullscreen);// 创建Dialog并设置样式主题
		selectDialog.show();
	}

	private void initDate() {
		// 换股需要重新清理数据源
		if (myData.size() > 0) {
			myData.clear();
		}
		// 下载数据源
		String url = ipUrl + "/ESBServlet?command=hq.GetRandomStockDataAction&num=300";
		StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				try {
					JSONObject object = new JSONObject(s);
					if (object.has("datalist")) {
						String datalist = object.getString("datalist");
						JSONArray jsonArray = new JSONArray(datalist);
						PrizeStock prizeStock;
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject stock = jsonArray.getJSONObject(i);
							double zdf = stock.getDouble("zdf");
							String zdfString = String.format("%.2f", zdf);
							String name = stock.getString("name");

							// zdf = Double.parseDouble(zdfString);
							zdf = Double.valueOf(zdfString).doubleValue();

							prizeStock = new PrizeStock(name, zdf, stock.getBoolean("islimitup"), stock.getString("date"), stock.getString("symbol"));
							myData.add(prizeStock);
						}
						mLuckyPanView.setDate(myData);
						mStartBtn.setEnabled(true);
						if (animator != null) {
							animator.cancel();
							luckview_changedata.setEnabled(true);
							Toast.makeText(LuckViewMainActivity.this, "已重新随机抽取300只股票", Toast.LENGTH_SHORT).show();
						}

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				Toast.makeText(LuckViewMainActivity.this, "当前网络不稳定，请稍候再试", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		queue.add(request);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private void initSound() {
		sound = new SoundUtil(this);
		try {
			sound.put("开始", R.raw.start);
			sound.put("结束", R.raw.end);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 不再使用时调用销毁
		sound.destroy();
		LuckViewCollector.removeActivity(this);
		System.exit(0);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.id_start_btn) {
			// 设置换股不可以点击，颜色变暗
			luckview_changedata.setEnabled(false);
			luckview_changedata.setAlpha(125);

			isLotteryDraw();
		} else if (id == R.id.luck_music) { // 音乐开关

			if (!isCloseSound) { // 没有关闭变成关闭了音乐
				luck_music.setBackgroundResource(R.drawable.xinyusoft_luckview_music_close);
				if (startBtnType == 1) { // 此时是已经点击了开始的阶段，点击了之后，停止开始的音乐
					sound.stop("开始");
				} else if (startBtnType == 2) { // 此时是已经点击了结束的阶段，点击了之后，停止结束的音乐
					sound.stop("结束");
				}
			} else {
				luck_music.setBackgroundResource(R.drawable.xinyusoft_luckview_music);
				if (startBtnType == 1) { // 此时是已经点击了开始的阶段，点击了之后，开始开始的音乐
					try {
						sound.play("开始");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} else if (startBtnType == 2) { // 此时是已经点击了结束的阶段，点击了之后，开始结束的音乐
					if (flag) { // 这个用来判断是否点击了停止，而还在结束状态的情况
						try {
							sound.play("结束");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
			}

			isCloseSound = !isCloseSound;
		} else if (id == R.id.luck_share) {
			Toast.makeText(this, "请稍等~", Toast.LENGTH_SHORT).show();
			// 分享
			if (mPrizeStock == null) {
				WeixinUtil.getInstance().sendWebPage(this, "http://a.app.qq.com/o/simple.jsp?pkgname=com.xinyusoft.zhlcs", userName + "邀请你一起来玩股票幸运大转盘啦！", "幸运大转盘",
						R.drawable.xinyusoft_luckview_sharelogo, true);
			} else {
				WeixinUtil.getInstance().sendWebPage(this, "http://a.app.qq.com/o/simple.jsp?pkgname=com.xinyusoft.zhlcs",
						"幸运大转盘:" + userName + "今天抽中" + mPrizeStock.getZdf() + "个积分~快来一起玩吧！", "幸运大转盘", R.drawable.xinyusoft_luckview_sharelogo, true);
			}
		} else if (id == R.id.luckview_sharemyfriend) {

			Toast.makeText(this, "请稍等~", Toast.LENGTH_SHORT).show();
			// 分享
			if (mPrizeStock == null) {
				WeixinUtil.getInstance().sendWebPage(this, "http://a.app.qq.com/o/simple.jsp?pkgname=com.xinyusoft.zhlcs", "幸运大转盘", userName + "邀请你一起来玩股票幸运大转盘啦！",
						R.drawable.xinyusoft_luckview_sharelogo, false);
			} else {
				WeixinUtil.getInstance().sendWebPage(this, "http://a.app.qq.com/o/simple.jsp?pkgname=com.xinyusoft.zhlcs", "幸运大转盘",
						userName + "今天抽中" + mPrizeStock.getZdf() + "个积分~快来一起玩吧！", R.drawable.xinyusoft_luckview_sharelogo, false);
			}
		} else if (id == R.id.luckview_changedata) { // 换股
			luckview_changedata.setEnabled(false);
			animator = ObjectAnimator.ofFloat(luckview_changedata, "rotation", 0f, 360f);
			animator.setRepeatCount(ValueAnimator.INFINITE);
			animator.setDuration(500);
			animator.setInterpolator(new LinearInterpolator());
			animator.start();

			initDate();

		} else if (id == R.id.luckivew_back) { // 返回
			mLuckyPanView.closeLuckview();
			finish();
		} else if (id == R.id.luckview_score) { // 我的积分
			Intent intent = new Intent(this, LuckViewUserInfoActivity.class);
			intent.putExtra("userId", userId);
			intent.putExtra("ipUrl", ipUrl);
			startActivity(intent);
		} else if (id == R.id.luckview_moreluck) { // 更多抽奖
			// TODO
			//Toast.makeText(this, "敬请期待~", Toast.LENGTH_SHORT).show();
			// Intent intent = new Intent(this, ImageListActivity.class);
			// intent.putExtra("userId", userId);
			// intent.putExtra("ipUrl", ipUrl);
			// intent.putExtra(Constants.Extra.IMAGES, Constants.IMAGES);
			// startActivity(intent);
			startActivity(new Intent(this,LuckViewMorePriceActivity.class));
		} else if (id == R.id.id_luckypan) {
			// TODO
			Intent intent = new Intent(this, LuckViewStockListActivity.class);
			intent.putExtra("userId", userId);
			intent.putExtra("ipUrl", ipUrl);
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList("stocklist", myData);
			intent.putExtras(bundle);
			startActivityForResult(intent, STOCKLIST_REQUEST_CODE);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(STOCKLIST_REQUEST_CODE == requestCode && LuckViewStockListActivity.STOCKLIST_RESULTCODE == resultCode) {
			ArrayList<PrizeStock> list = data.getParcelableArrayListExtra("stockList");
			//指针重定向，原先的对象不要了
			if(list != null && list.size() >0 ) {
				myData.clear();
			}
			myData = list;
			mLuckyPanView.setDate(myData);
		}
	
	}
	

	private String toTime(long start) {
		long millisecond = start % 1000;
		millisecond /= 10;
		start /= 1000;
		long second = start % 60;
		return String.format("%02d:%02d", second, millisecond);
	}

	public class MyThread implements Runnable {
		@Override
		public void run() {
			while (flag) {
				try {
					Thread.sleep(10);// 线程暂停10秒，单位毫秒
					Message message = handler.obtainMessage();
					message.what = 1;
					handler.sendMessage(message);// 发送消息
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void pause() {
		flag = false;
	}

	public void start() {
		start = System.currentTimeMillis();
		flag = true;
		new Thread(new MyThread()).start();
	}

	/**
	 * 是否可以抽奖
	 *
	 * @return true为可以
	 */
	private void isLotteryDraw() {
		String url = ipUrl + "/ESBServlet?command=user.isluckydrawbyuseraction&userid=" + userId;
		StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				try {
					JSONObject object = new JSONObject(s);
					boolean isluckydraw = object.getBoolean("isluckydraw");
					if (isluckydraw) {
						lotterDarwStart();
					} else {
						mStartBtn.setImageResource(R.drawable.xinyusoft_luckview_button4);
						Toast.makeText(LuckViewMainActivity.this, "您当日的次数已经用完，明天再来吧", Toast.LENGTH_SHORT).show();
					}
					// lotterDarwStart();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {

			}
		});
		queue.add(request);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (flag) {
			sound.resume("开始");
			sound.resume("结束");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		sound.pause("开始");
		sound.pause("结束");
	}

	private void isLotteryDrawForStart() {
		String url = ipUrl + "/ESBServlet?command=user.isluckydrawbyuseraction&userid=" + userId;
		StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				try {
					JSONObject object = new JSONObject(s);
					String isluckydraw = object.getString("isluckydraw");
					if ("true".equals(isluckydraw)) {
						luckview_show_timeandbtn.setVisibility(View.VISIBLE);
						 luckview_huangu_rl.setVisibility(View.VISIBLE);
						 luck_music.setVisibility(View.VISIBLE);
						
					} else {
						showTomorrow.setVisibility(View.VISIBLE);
						mStartBtn.setImageResource(R.drawable.xinyusoft_luckview_button4);
						// Toast.makeText(LuckViewMainActivity.this,
						// "您当日的次数已经用完，下一个交易再来吧", Toast.LENGTH_SHORT).show();
						// TODO
//						 luckview_huangu_rl.setVisibility(View.INVISIBLE);
//						 luck_music.setVisibility(View.INVISIBLE);
//						 time_tv.setVisibility(View.INVISIBLE);
//						 mStartBtn.setVisibility(View.INVISIBLE);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {

			}
		});
		queue.add(request);
	}

	/**
	 * 点击抽奖按钮的操作
	 */
	private void lotterDarwStart() {
		if (startBtnType == 0) { // 点击开始了
			mStartBtn.setImageResource(R.drawable.xinyusoft_luckview_button1_grey);
			mStartBtn.setEnabled(false);
			mLuckyPanView.luckyStart();
			// 播放 以文件名作为播放条件
			if (!isCloseSound) {
				try {
					sound.play("开始");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			start();
			startBtnType++;
			// 1秒之后才能点击停止
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mStartBtn.setEnabled(true);
					mStartBtn.setImageResource(R.drawable.xinyusoft_luckview_button2);
				}
			}, 1000);
		} else if (startBtnType == 1) {
			if (!mLuckyPanView.isShouldEnd()) {
				pause();
				mStartBtn.setImageResource(R.drawable.xinyusoft_luckview_button4);
				mStartBtn.setEnabled(false);
				mLuckyPanView.luckyEnd();
				if (!isCloseSound) {
					try {
						sound.stop("开始");
						sound.play("结束");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				startBtnType++;
				// 1秒之后才能点击再来一次
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mStartBtn.setEnabled(true);
					}
				}, 1300);
				startBtnType = 0;
			}
		} else {
			// mStartBtn.setImageResource(R.drawable.xinyusoft_luckview_button1);
			// mLuckyPanView.restart();
			// time_tv.setText("00:00");
			// startBtnType = 0;
		}

	}

}
