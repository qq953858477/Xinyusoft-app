/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************//*
package com.xinyusoft.xshell.luckview.friendscore;

*//*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************//*

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.xinyusoft.xshell.luckview.friendscore.Constants.Extra;
import com.xinyusoft.xshell.luckview.utils.VolleyUtil;

*//**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 *//*
public class ImageListActivity extends AbsListViewBaseActivity {

	DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类

	String[] imageUrls; // 图片路径

	private RequestQueue queue; // volley实体

	private String ipUrl;
	private String userId;

	private List<Friend> friendLists;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_list);

		Bundle bundle = getIntent().getExtras();
		imageUrls = bundle.getStringArray(Extra.IMAGES);

		userId = getIntent().getStringExtra("userId");
		ipUrl = getIntent().getStringExtra("ipUrl");

		friendLists = new ArrayList<Friend>();

//		ImageView imageView = (ImageView) findViewById(R.id.title_back);
//		imageView.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});

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

		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 点击列表项转入ViewPager显示界面
				startImagePagerActivity(position);
			}
		});
	}

	*//**
	 * 获得朋友的列表img
	 *//*
	private void getFriendList() {
		String url = ipUrl + "/JSONP?rmd=1458110156567&shopid=-1&callback=xinyu&user.id=" + userId
				+ "&friendinfo=Y&randomcode=0.7995403341483325&session.id=4bca8b2f-a0f6-4b8b-a4df-904e0ad8a247" + "&command=user.selectfriendinfo";
		StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				// Log.i("zzy","ssssss:"+s);
				String substring = s.substring(6, s.length() - 3);
				Log.i("zzy", "substring" + substring);

				try {
					JSONObject object = new JSONObject(substring);
					JSONArray array = new JSONArray(object.getString("friendlist"));
					Log.i("zzy", "array----length:" + array.length());
					Friend friendBean;
					for (int i = 0; i < array.length(); i++) {
						JSONObject friend = new JSONObject(array.get(i).toString());

						String head = friend.getString("f_head");
						String name = friend.getString("f_nickname");
						//friendBean = new Friend(name, head);
						//friendLists.add(friendBean);

					}
					// 开始setadapter
					listView.setAdapter(new ItemAdapter());
					
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
	public void onBackPressed() {
		AnimateFirstDisplayListener.displayedImages.clear();
		super.onBackPressed();
	}

	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(this, ImagePagerActivity.class);
		intent.putExtra(Extra.IMAGES, imageUrls);
		intent.putExtra(Constants.Extra.IMAGE_POSITION, position);
		startActivity(intent);
	}

	*//**
	 *
	 * 自定义列表项适配器
	 *
	 *//*
	class ItemAdapter extends BaseAdapter {

		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		private class ViewHolder {
			public TextView text;
			public ImageView image;
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
				holder.text = (TextView) view.findViewById(R.id.text);
				holder.image = (ImageView) view.findViewById(R.id.image);
				view.setTag(holder); // 给View添加一个格外的数据
			} else {
				holder = (ViewHolder) view.getTag(); // 把数据取出来
			}

			holder.text.setText(friendLists.get(position).getName()); // TextView设置文本

			*//**
			 * 显示图片 参数1：图片url 参数2：显示图片的控件 参数3：显示图片的设置 参数4：监听器
			 *//*
			imageLoader.displayImage(friendLists.get(position).getUrl(), holder.image, options, animateFirstListener);

			return view;
		}
	}

	*//**
	 * 图片加载第一次显示监听器
	 * 
	 * @author Administrator
	 *
	 *//*
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
}*/