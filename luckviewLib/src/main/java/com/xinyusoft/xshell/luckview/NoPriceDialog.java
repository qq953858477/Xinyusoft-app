package com.xinyusoft.xshell.luckview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xinyusoft.xshell.luckview.bean.PrizeStock;

public class NoPriceDialog extends Dialog {
	Context context;
	private PrizeStock price;

	public NoPriceDialog(Context context, int theme, PrizeStock price) {
		super(context, theme);
		this.context = context;
		this.price = price;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog2, null);
		setContentView(view);

		TextView show = (TextView) findViewById(R.id.luckview_user_noprice);
		show.setText("抽中" + price.getName() + "，涨跌幅为" + price.getZdf() + "%");

		Button btn_ok = (Button) findViewById(R.id.button1);
		btn_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}
