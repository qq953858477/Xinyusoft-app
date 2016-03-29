package com.xinyusoft.xshell.luckview.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinyusoft.xshell.luckview.R;

/**
 * Created by zzy on 2016/2/23.
 */
public class LuckViewRuleActivity extends LuckViewBaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.xinyusoft_luckview_dialog_rule);
		
		LuckViewCollector.addActivity(this);
		
		TextView ruleok = (TextView) findViewById(R.id.luckview_ruleok);
		ruleok.setOnClickListener(this);
		ImageView luckview_rule_back = (ImageView) findViewById(R.id.luckview_title_back);
		luckview_rule_back.setOnClickListener(this);
		TextView tv_title = (TextView) findViewById(R.id.luckview_title_name);
		tv_title.setText("规则说明");
		//TextView te = findViewById(R.id.luckview_rule_1);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.luckview_ruleok) {
			finish();
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
