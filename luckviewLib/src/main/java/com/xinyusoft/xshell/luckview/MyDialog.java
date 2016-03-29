package com.xinyusoft.xshell.luckview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xinyusoft.xshell.luckview.bean.PrizeStock;
import com.xinyusoft.xshell.luckview.utils.weixin.WeixinUtil;

public class MyDialog extends Dialog {

	// 奖品
	private PrizeStock price;
	Context context;
	private TextView showPrice;
	private TextView luckview_doubleprice;

	public MyDialog(Context context, PrizeStock price, int theme) {
		super(context, theme);
		// TOStringDO Auto-generated constructor stub
		this.context = context;
		this.price = price;
	}

	public MyDialog(Context context, int theme) {

		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog, null);
		setContentView(view);
		TextView jf = (TextView) findViewById(R.id.luckview_user_jf);
		TextView zdf = (TextView) findViewById(R.id.luckview_user_zdf);
		luckview_doubleprice = (TextView) findViewById(R.id.luckview_doubleprice);
		if (price.isLimitUp()) { // 涨停了
			luckview_doubleprice.setVisibility(View.VISIBLE);
			jf.setText(price.getZdf() * 2 + "");
			zdf.setText("抽中" + price.getName() + "," + price.getZdf() + "%涨停啦");
		} else {
			jf.setText(price.getZdf() + "");
			zdf.setText("抽中" + price.getName() + ",上涨了" + price.getZdf() + "%");
		}

		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				luckview_doubleprice.setVisibility(View.INVISIBLE);
				dismiss();

			}
		});
		Button share = (Button) findViewById(R.id.button2);
		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				luckview_doubleprice.setVisibility(View.INVISIBLE);
				WeixinUtil.getInstance().sendWebPage(context, "http://a.app.qq.com/o/simple.jsp?pkgname=com.xinyusoft.zhlcs", "(*^__^*) ……嘻嘻，太赞了，今天抽中" + price.getZdf() + "个积分！",
						"幸运大转盘", R.drawable.xinyusoft_luckview_sharelogo, true);
				dismiss();
			}
		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

}
