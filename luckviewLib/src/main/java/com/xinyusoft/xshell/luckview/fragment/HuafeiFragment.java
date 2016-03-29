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

public class HuafeiFragment extends BaseFragment {

	public HuafeiFragment(ProgressBar progress) {
		super(progress);
	}

	private View view;
	private EditText et_phone;

	private TextView valid_huafei;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(R.layout.xinyusoft_luckview_fragment_huafei, null);
		
		LinearLayout luckview_hfdui_main = (LinearLayout) view.findViewById(R.id.luckview_hfdui_main);
		LinearLayout luckview_hfdh_hint = (LinearLayout) view.findViewById(R.id.luckview_hfdh_hint);
		if(availableScore < 10) {  //小于10积分不做下面操作了
			luckview_hfdh_hint.setVisibility(View.VISIBLE);
			luckview_hfdui_main.setVisibility(View.GONE);
			return view;
		}
		
		
		et_phone = (EditText) view.findViewById(R.id.luckview_et_phone);
		et_phone.setFocusable(true);
		et_phone.setFocusableInTouchMode(true);
		et_phone.requestFocus();
		et_phone.addTextChangedListener(new MyTextWatcher());
		TextView tv_ok = (TextView) view.findViewById(R.id.luckview_huafei_ok);
		valid_huafei = (TextView) view.findViewById(R.id.valid_huafei);
		tv_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phone = et_phone.getText().toString();
				if ("".equals(phone) || null == phone || phone.length() < 11 || !phone.matches("^1\\d{10}$")) {
					Toast.makeText(getActivity(), "手机号码输入有误，请重新输入！", Toast.LENGTH_SHORT).show();
				} else {
					progress.setVisibility(View.VISIBLE);
					requestUserScore(phone, TYPE_HUAFEI);
				}
			}
		});
		updataValid(valid_huafei, TYPE_HUAFEI);

		return view;

	}

}
