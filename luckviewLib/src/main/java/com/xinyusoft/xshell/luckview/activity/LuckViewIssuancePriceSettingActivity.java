package com.xinyusoft.xshell.luckview.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xinyusoft.xshell.luckview.R;

/**
 * 发布奖品设置的Activity
 */
public class LuckViewIssuancePriceSettingActivity extends LuckViewBaseActivity implements View.OnClickListener {

    private ImageView stockImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xinyusoft_activity_luckview_more_pricesetting);

        initView();
    }

    private void initView() {
        ImageView iv_back = (ImageView) findViewById(R.id.luckview_title_back);
        iv_back.setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.luckview_title_name);
        tv_title.setText("抽奖设置");

        TextView tv_ok = (TextView) findViewById(R.id.luckview_issuancerule_ok);
        tv_ok.setOnClickListener(this);

        //抽股票
        RelativeLayout stockRl = (RelativeLayout) findViewById(R.id.luckview_moreprice_stock_rl);
        stockRl.setOnClickListener(this);
        stockImg = (ImageView) findViewById(R.id.luckview_moreprice_stock_img);

        //抽实物（只显示图片）
        RelativeLayout onlyImgRl = (RelativeLayout) findViewById(R.id.luckview_moreprice_onlyimg_rl);
        onlyImgRl.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.luckview_title_back) {
            finish();
        } else if (id == R.id.luckview_issuancerule_ok) {
            finish();
        } else if (id == R.id.luckview_moreprice_stock_rl) {
            stockImg.setVisibility(View.VISIBLE);
        } else if (id == R.id.luckview_moreprice_onlyimg_rl) {
            startActivity(new Intent(this,LuckViewIssuanceSettingImgActivity.class));
        }
    }
}
