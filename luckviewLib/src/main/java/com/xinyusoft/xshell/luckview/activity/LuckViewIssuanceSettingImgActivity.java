package com.xinyusoft.xshell.luckview.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinyusoft.xshell.luckview.R;

/**
 * 实物奖品（图片）设置Activity
 */
public class LuckViewIssuanceSettingImgActivity extends LuckViewBaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xinyusoft_activity_luckview_more_settingimg);

        initView();
    }

    private void initView() {
        ImageView iv_back = (ImageView) findViewById(R.id.luckview_title_back);
        iv_back.setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.luckview_title_name);
        tv_title.setText("实物奖品设置");

        TextView tv_ok = (TextView) findViewById(R.id.luckview_issuancerule_ok);
        tv_ok.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.luckview_title_back) {
            finish();
        } else if (id == R.id.luckview_issuancerule_ok) {
            finish();
        }
    }
}
