package com.xinyusoft.xshell.luckview.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xinyusoft.xshell.luckview.activity.LuckViewJFOKActivity;
import com.xinyusoft.xshell.luckview.utils.VolleyUtil;

public abstract class BaseFragment extends Fragment {
	protected RequestQueue queue;

	protected String ipUrl;
	protected String userId;
	// 有效的积分
	protected double availableScore;

	// 话费
	protected static final int TYPE_HUAFEI = 1;

	// 流量
	protected static final int TYPE_LIULIANG = 2;

	protected ProgressBar progress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		queue = VolleyUtil.getRequestQueue(getActivity());
		Bundle bundle = getArguments();
		userId = bundle.getString("userId");
		ipUrl = bundle.getString("ipUrl");
		availableScore = bundle.getDouble("availableScore");
	}

	public BaseFragment(ProgressBar progress) {
		// TODO Auto-generated constructor stub
		this.progress = progress;
	}

	/**
	 * 请求可兑换的话费或者流量
	 * 
	 * @param view
	 *            修改的view
	 * @param type
	 *            话费或者流量的类型
	 */
	protected void updataValid(final TextView view, final int type) {
		String url = ipUrl + "/ESBServlet?command=user.GetExchangePrizeAction&userid=" + userId;
		StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String s) {
				try {
					JSONObject object = new JSONObject(s);
					if (object.has("op")) {
						JSONObject op = object.getJSONObject("op");
						String code = op.getString("code");
						int hfmoney = object.getInt("hfmoney");
						if ("Y".equals(code)) { // 成功 代表积分大于100
							if (TYPE_HUAFEI == type) {// 类型是话费的
								view.setText(hfmoney + "元");
							} else { // 类型是流量
								view.setText("联通:" + object.getInt("unicomc") + "M " + "移动:" + object.getInt("mobilec") + "M " + "电信:" + object.getInt("telcomc") + "M ");
							}
						} else { // 为N的话，直接说奖励少于100
							if (TYPE_HUAFEI == type) {// 类型是话费的 ,积分小于100也可以兑换话费
								view.setText(hfmoney + "元");
							} else { // 类型是流量
								view.setText("兑换流量最少需要100积分");
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				Toast.makeText(getActivity(), "当前网络不可用，请稍候再试！", Toast.LENGTH_SHORT).show();
			}
		});
		queue.add(request);
	}

	/**
	 * 请求跳转到成功页面
	 * 
	 * @param phone
	 *            电话
	 * @param type
	 *            类型
	 */
	protected void requestUserScore(String phone, final int type) {
		String url = ipUrl + "/ESBServlet?command=user.IntegralExchangeAction&userid=" + userId + "&mobile=" + phone + "&exchangetype=" + type;
		StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String s) {
				try {
					JSONObject object = new JSONObject(s);
					if (object.has("op")) {
						JSONObject op = object.getJSONObject("op");
						String code = op.getString("code");
						if ("Y".equals(code)) { // 成功
							Intent intent = new Intent(getActivity(), LuckViewJFOKActivity.class);
							if(BaseFragment.TYPE_HUAFEI == type) {
								intent.putExtra("usedScore", availableScore - availableScore%10d);
							} else {
								intent.putExtra("usedScore", availableScore - availableScore%100d);
							}
							getActivity().startActivity(intent);
							progress.setVisibility(View.GONE);
							
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				Toast.makeText(getActivity(), "当前网络不可用，请稍候再试！", Toast.LENGTH_SHORT).show();
			}
		});
		queue.add(request);
	}

	class MyTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if (s.length() >= 11) { // 隐藏软键盘
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}

	}
}
