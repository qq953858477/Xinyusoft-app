package com.xinyusoft.xshell.luckview.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xinyusoft.xshell.luckview.R;

/**
 * 流量兑换fragment
 * @author zzy
 *
 */
public class LiuliangFragment extends BaseFragment {

	public LiuliangFragment(ProgressBar progress) {
		super(progress);
	}
	
	private View view;
	private EditText et_phone;

	private TextView valid_data;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(R.layout.xinyusoft_luckview_fragment_liuliang, null);

		LinearLayout luckview_lldh_main = (LinearLayout) view.findViewById(R.id.luckview_lldh_main);
		LinearLayout luckview_lldh_hint = (LinearLayout) view.findViewById(R.id.luckview_lldh_hint);
		if(availableScore < 100) {  //小于100积分不做下面操作了
			luckview_lldh_hint.setVisibility(View.VISIBLE);
			luckview_lldh_main.setVisibility(View.GONE);
			return view;
		}
		
		
		et_phone = (EditText) view.findViewById(R.id.luckview_et_phone);
		et_phone.setFocusable(true);
		et_phone.setFocusableInTouchMode(true);
		et_phone.requestFocus();
		et_phone.addTextChangedListener(new MyTextWatcher());
		TextView tv_ok = (TextView) view.findViewById(R.id.luckview_liuliang_ok);
		valid_data = (TextView) view.findViewById(R.id.valid_data);
		tv_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phone = et_phone.getText().toString();
				if ("".equals(phone) || null == phone || phone.length() < 11 || !phone.matches("^1\\d{10}$")) {
					Toast.makeText(getActivity(), "手机号码输入有误，请重新输入！", Toast.LENGTH_SHORT).show();
				} else if (availableScore < 100) {
					Toast.makeText(getActivity(), "兑换流量必须大于等于100积分!", Toast.LENGTH_SHORT).show();
				} else {
					progress.setVisibility(View.VISIBLE);
					requestUserScore(phone, TYPE_LIULIANG);
				}
			}
		});
		updataValid(valid_data, TYPE_LIULIANG);

		return view;

	}

}
