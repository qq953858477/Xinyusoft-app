package com.xinyusoft.xshell.luckview.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xinyusoft.xshell.luckview.R;
import com.xinyusoft.xshell.luckview.bean.PrizeStock;
import com.xinyusoft.xshell.luckview.utils.VolleyUtil;

/**
 * 300只股票列表
 * 
 * @author zzy
 *
 */
public class LuckViewStockListActivity extends LuckViewBaseActivity implements OnClickListener {

	//返回到主页面的返回码
	public static final int STOCKLIST_RESULTCODE = 4;
	
	private ListView mListView;
	private ArrayList<PrizeStock> list;
	// 用来保存开始的list
	private ArrayList<PrizeStock> nomalList;
	private MyAdapter adapter;
	// 排序的标识
	private int zdfFlag = 0;
	/** 点击换股的动画 */
	private ObjectAnimator animator;

	private RequestQueue queue;
	/**
	 * 升序
	 */
	private Comparator<PrizeStock> upComparator = new Comparator<PrizeStock>() {

		/*
		 * int compare(Student o1, Student o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2， 返回0
		 * 表示：o1和o2相等， 返回正数表示：o1大于o2。
		 */
		public int compare(PrizeStock o1, PrizeStock o2) {

			// 按照学生的年龄进行升序排列
			if (o1.getZdf() < o2.getZdf()) {
				return 1;
			}
			if (o1.getZdf() == o2.getZdf()) {
				return 0;
			}
			return -1;
		}
	};

	/**
	 * 降序
	 */
	private Comparator<PrizeStock> downComparator = new Comparator<PrizeStock>() {

		public int compare(PrizeStock o1, PrizeStock o2) {
			if (o1.getZdf() > o2.getZdf()) {
				return 1;
			}
			if (o1.getZdf() == o2.getZdf()) {
				return 0;
			}
			return -1;
		}
	};

	private String ipUrl;
	/**
	 * 换股的那个圈圈
	 */
	private ImageView luckview_changedata;
	/**
	 * 涨跌幅的中包裹的RelativeLayout
	 */
	private RelativeLayout luckivew_stocklist_rl;
	private ImageView luckview_stocklist_zdf_img;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xinyusoft_activity_luckview_stocklist);
		list = new ArrayList<PrizeStock>();
		Bundle bundle = getIntent().getExtras();
		list = bundle.getParcelableArrayList("stocklist");
		nomalList = (ArrayList<PrizeStock>) list.clone();

		ipUrl = getIntent().getStringExtra("ipUrl");
		queue = VolleyUtil.getRequestQueue(this);
		initView();

	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.luckview_stock_list);
		adapter = new MyAdapter(this, R.layout.xinyusoft_item_stocklist, list);
		mListView.setAdapter(adapter);

		ImageView title_back = (ImageView) findViewById(R.id.luckview_title_back);
		title_back.setOnClickListener(this);

		luckivew_stocklist_rl = (RelativeLayout) findViewById(R.id.luckivew_stocklist_rl);
		luckivew_stocklist_rl.setOnClickListener(this);

		RelativeLayout huangu_rl = (RelativeLayout) findViewById(R.id.luckview_huangu_rl);
		huangu_rl.setOnClickListener(this);

		luckview_changedata = (ImageView) findViewById(R.id.luckview_changedata);
		luckview_stocklist_zdf_img = (ImageView) findViewById(R.id.luckview_stocklist_zdf_img);
		
	}

	class MyAdapter extends ArrayAdapter<PrizeStock> {
		Context context;
		int resource;
		List<PrizeStock> list;

		public MyAdapter(Context context, int resource, List<PrizeStock> objects) {
			super(context, resource, objects);
			this.context = context;
			this.resource = resource;
			this.list = objects;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder = null;
			PrizeStock prizeStock = list.get(position);
			double zdf = prizeStock.getZdf();
			if (convertView == null) {
				view = LayoutInflater.from(context).inflate(resource, null);
				holder = new ViewHolder();
				holder.id = (TextView) view.findViewById(R.id.luckview_stocklist_id);
				holder.name = (TextView) view.findViewById(R.id.luckview_stocklist_stock);
				holder.code = (TextView) view.findViewById(R.id.luckview_stocklist_code);
				holder.zdf = (TextView) view.findViewById(R.id.luckview_stocklist_zdf);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			holder.id.setText((position + 1) + "");
			holder.name.setText(prizeStock.getName());
			holder.code.setText(prizeStock.getSymbol());
			holder.zdf.setText(zdf + "%");

			if (zdf > 0) {
				holder.zdf.setTextColor(getResources().getColor(R.color.luckview_stocklist_up));
			} else if (zdf < 0) {
				holder.zdf.setTextColor(getResources().getColor(R.color.luckview_stocklist_down));
			} else {
				holder.zdf.setTextColor(getResources().getColor(R.color.luckview_stocklist_normal));
			}

			return view;
		}

		class ViewHolder {
			TextView id;
			TextView code;
			TextView name;
			TextView zdf;
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.luckivew_stocklist_rl) { // 点排序
			if (zdfFlag == 0) {
				Collections.sort(list, upComparator);
				luckview_stocklist_zdf_img.setImageResource(R.drawable.xinyusoft_luckview_stocklist_down);
				zdfFlag++;
			} else if (zdfFlag == 1) {
				Collections.sort(list, downComparator);
				luckview_stocklist_zdf_img.setImageResource(R.drawable.xinyusoft_luckview_stocklist_up);
				zdfFlag++;
			} else {
				if (list != null && list.size() > 0) {
					list.clear();
				}
				for (int i = 0; i < nomalList.size(); i++) {
					list.add(nomalList.get(i));
				}
				luckview_stocklist_zdf_img.setImageResource(R.drawable.xinyusoft_luckview_stocklist_nomal);
				zdfFlag = 0;
			}
			adapter.notifyDataSetInvalidated();
		} else if (id == R.id.luckview_title_back) {
			Intent resultIntent = new Intent();
			resultIntent.putParcelableArrayListExtra("stockList", list);
			setResult(STOCKLIST_RESULTCODE, resultIntent);
			
			finish();
		} else if (id == R.id.luckview_huangu_rl) { // 换股
			animator = ObjectAnimator.ofFloat(luckview_changedata, "rotation", 0f, 360f);
			animator.setRepeatCount(ValueAnimator.INFINITE);
			animator.setDuration(500);
			animator.setInterpolator(new LinearInterpolator());
			animator.start();
			getStockDate();
		}
	}

	private void getStockDate() {
		// 换股需要重新清理数据源
		if (list.size() > 0) {
			list.clear();
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
							list.add(prizeStock);
							
						}
						nomalList = (ArrayList<PrizeStock>) list.clone();
						adapter.notifyDataSetInvalidated();
						if (animator != null) {
							animator.cancel();
							Toast.makeText(LuckViewStockListActivity.this, "已重新随机抽取300只股票", 0).show();
						}
						//重置下涨跌的排序显示
						luckview_stocklist_zdf_img.setImageResource(R.drawable.xinyusoft_luckview_stocklist_nomal);
						zdfFlag = 0;

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				Toast.makeText(LuckViewStockListActivity.this, "当前网络不稳定，请稍候再试", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		queue.add(request);
	}
}
