package com.xinyusoft.xshell.luckview.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xinyusoft.xshell.luckview.R;
import com.xinyusoft.xshell.luckview.bean.Friend;
import com.xinyusoft.xshell.luckview.friendscore.AbsListViewBaseActivity;
import com.xinyusoft.xshell.luckview.friendscore.Constants;
import com.xinyusoft.xshell.luckview.friendscore.Constants.Extra;
import com.xinyusoft.xshell.luckview.friendscore.ImagePagerActivity;
import com.xinyusoft.xshell.luckview.utils.FulStatusBarUtil;
import com.xinyusoft.xshell.luckview.utils.RelativeDateFormat;
import com.xinyusoft.xshell.luckview.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zzy on 2016/2/23.
 */
public class LuckViewUserInfoActivity extends AbsListViewBaseActivity implements OnClickListener {
	
	DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	
	String[] imageUrls; // 图片路径
	
	private RequestQueue queue;
	private String ipUrl;
	private String userId;
	private TextView tv_availableScore;

	private double availableScore;

	private LinearLayout ll_main;
	private ProgressBar progress;
	private TextView luckview_allsocre;

	private List<Friend> friendLists;

	private TextView luckview_allcount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		FulStatusBarUtil.setcolorfulStatusBar(this);

		setContentView(R.layout.xinyusoft_activity_luckview_user);

		LuckViewCollector.addActivity(this);

		initView();
		queue = VolleyUtil.getRequestQueue(this);
		userId = getIntent().getStringExtra("userId");
		ipUrl = getIntent().getStringExtra("ipUrl");

		updateUserScore();

		initListView();
	}
	
	
	
	private void initListView() {
		Bundle bundle = getIntent().getExtras();
		imageUrls = bundle.getStringArray(Extra.IMAGES);

		userId = getIntent().getStringExtra("userId");
		ipUrl = getIntent().getStringExtra("ipUrl");

		friendLists = new ArrayList<Friend>();

		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.ic_stub) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.ic_empty) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.ic_error) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				.displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象

		listView = (ListView) findViewById(R.id.list);
		queue = VolleyUtil.getRequestQueue(this);
		getFriendList();
//		listView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				// 点击列表项转入ViewPager显示界面
//				//startImagePagerActivity(position);
//			}
//		});

	}

	private void initView() {
		tv_availableScore = (TextView) findViewById(R.id.luckview_user_availablescore);
		ImageView iv_back = (ImageView) findViewById(R.id.luckview_title_back);
		iv_back.setOnClickListener(this);
		TextView luckview_title_rule = (TextView) findViewById(R.id.luckview_title_rule);
		luckview_title_rule.setOnClickListener(this);

		luckview_allsocre = (TextView) findViewById(R.id.luckview_allsocre);

		ll_main = (LinearLayout) findViewById(R.id.luckview_userInfo_main);
		progress = (ProgressBar) findViewById(R.id.progressBar1);

		TextView luckview_user_wydh = (TextView) findViewById(R.id.luckview_user_wydh);
		luckview_user_wydh.setOnClickListener(this);
		luckview_allcount = (TextView) findViewById(R.id.luckview_allcount);
		
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
						luckview_allsocre.setText("总积分:" + countScore);
						tv_availableScore.setText(availableScore + "");
						luckview_allcount.setText(" "+luckydrawscore.getString("f_luckydraw_count")+"次");
						ll_main.setVisibility(View.VISIBLE);
						progress.setVisibility(View.GONE);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				ll_main.setVisibility(View.VISIBLE);
				Toast.makeText(LuckViewUserInfoActivity.this, "当前网络不可用，请稍候再试！", Toast.LENGTH_SHORT).show();
			}
		});
		queue.add(request);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.luckview_title_back) {
			finish();
		} else if (id == R.id.luckview_title_rule) {
			startActivity(new Intent(this, LuckViewRuleActivity.class));
		} else if (id == R.id.luckview_user_wydh) {
			Intent intent = new Intent(this, LuckViewExchangeActivity.class);
			intent.putExtra("userId", userId);
			intent.putExtra("ipUrl", ipUrl);
			intent.putExtra("availableScore", availableScore);
			startActivity(intent);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LuckViewCollector.removeActivity(this);
	}
	
	
	/**
	 * 获得朋友的列表img
	 */
	private void getFriendList() {
		String url = ipUrl + "/ESBServlet?command=user.GetLuckyFriendAction&userid="+userId;
		StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				try {
					JSONObject object = new JSONObject(s);
					JSONArray array = new JSONArray(object.getString("data"));
					Friend friendBean;
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:m:s");

					for (int i = 0; i < array.length(); i++) {
						JSONObject friend = new JSONObject(array.get(i).toString());

						String head = friend.getString("f_head");
						String name = friend.getString("f_nickname");
						int luckydrawCount = friend.getInt("f_luckydraw_count");
						double f_countscore = friend.getDouble("f_countscore");
						Log.i("zzy","lasttime:"+friend.getString("f_lastluckydraw_time"));
						Date date = format.parse(friend.getString("f_lastluckydraw_time"));
						String lastTime = RelativeDateFormat.format(date);
						friendBean = new Friend(name, head, luckydrawCount, lastTime+"玩了一次大转盘", f_countscore);
						
						
						friendLists.add(friendBean);

					}
					// 开始setadapter
					listView.setAdapter(new ItemAdapter());
					
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
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
	
	
	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		intent.putExtra(Extra.IMAGES, imageUrls);
		intent.putExtra(Constants.Extra.IMAGE_POSITION, position);
		startActivity(intent);
	}
	
	
	
	/**
	 *
	 * 自定义列表项适配器
	 *
	 */
	class ItemAdapter extends BaseAdapter {

		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		private class ViewHolder {
			public TextView name;
			public ImageView image;
			public TextView playcount;
			public TextView allscore;
			public TextView lastplaytime;
		}

		@Override
		public int getCount() {
			return friendLists.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.item_list_image, parent, false);
				holder = new ViewHolder();
				holder.name = (TextView) view.findViewById(R.id.luckview_friend_name);
				holder.image = (ImageView) view.findViewById(R.id.luckview_friend_image);
				
				holder.playcount = (TextView) view.findViewById(R.id.luckview_friend_playcount);
				holder.allscore = (TextView) view.findViewById(R.id.luckview_friend_allscore);
				holder.lastplaytime = (TextView) view.findViewById(R.id.luckview_friend_lastplaytime);
				view.setTag(holder); // 给View添加一个格外的数据
			} else {
				holder = (ViewHolder) view.getTag(); // 把数据取出来
			}

			holder.name.setText(friendLists.get(position).getName()); // TextView设置文本
			holder.playcount.setText(friendLists.get(position).getDrawCount()+"次"); // TextView设置文本
			holder.allscore.setText(Html.fromHtml("<font color=\"red\">"+friendLists.get(position).getAllScore()+"</font> 积分")); // TextView设置文本
			holder.lastplaytime.setText(friendLists.get(position).getLastTime()); // TextView设置文本

			/**
			 * 显示图片 参数1：图片url 参数2：显示图片的控件 参数3：显示图片的设置 参数4：监听器
			 */
			imageLoader.displayImage(friendLists.get(position).getUrl(), holder.image, options, animateFirstListener);

			return view;
		}
	}

	/**
	 * 图片加载第一次显示监听器
	 * 
	 * @author Administrator
	 *
	 */
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				// 是否第一次显示
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					// 图片淡入效果
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

}
