package com.xinyusoft.xshell.luckview.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinyusoft.xshell.luckview.R;

/**
 * Created by zzy on 2016/2/23. 积分兑换成功界面
 */
public class LuckViewJFOKActivity extends LuckViewBaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xinyusoft_activity_luckview_jifen_ok);

		double usedScore = getIntent().getDoubleExtra("usedScore", 0);
		
		LuckViewCollector.addActivity(this);

		TextView backLuckview = (TextView) findViewById(R.id.luckview_back_luckview);
		backLuckview.setOnClickListener(this);

		ImageView luckview_rule_back = (ImageView) findViewById(R.id.luckview_title_back);
		luckview_rule_back.setOnClickListener(this);

		TextView luckview_dhok_jifen = (TextView) findViewById(R.id.luckview_dhok_jifen);
		luckview_dhok_jifen.setOnClickListener(this);
		luckview_dhok_jifen.setText("本次充值已使用"+usedScore+"积分;");
		
		TextView tv_title = (TextView) findViewById(R.id.luckview_title_name);
		tv_title.setText("积分兑换成功");
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.luckview_back_luckview) { // 返回幸运大转盘
			// finish();
			LuckViewCollector.backLuckView();
		} else if (id == R.id.luckview_title_back) {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LuckViewCollector.removeActivity(this);
	}
}
