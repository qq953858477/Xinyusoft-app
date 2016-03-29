package com.xinyusoft.xshelllib.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.viewpagerindicator.IconPagerAdapter;
import com.xinyusoft.xshelllib.ui.LoadingActivity;
import com.xinyusoft.xshelllib.ui.TestFragment;

import java.io.File;

public class TestFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

	public static int c;
	public static int posi;
	Context mcontext;
	String webIndex;

	LoadingActivity s = new LoadingActivity();
	String imageurl = s.getImageurl();

	public TestFragmentAdapter(FragmentManager fm, Context context, String webindex) {
		super(fm);
		mcontext = context;
		webIndex = webindex;
	}

	@Override
	public Fragment getItem(int position) {
		posi = position;
		Log.e("position---", position + "");
		return TestFragment.newInstance(position);
	}

	public int getCc() {
		File f = new File(imageurl);
		String[] name = f.list();
		if (name != null) {
			c = name.length;

			return name.length;
		} else {
			return 0;
		}
	}

	@Override
	public int getCount() {
		return getCc();
	}

	public static int getPosi() {
		return posi;
	}

	public static void setPosi(int posi) {
		TestFragmentAdapter.posi = posi;
	}

	public static void setCc(int c) {
		TestFragmentAdapter.c = c;
	}

	public static int getC() {
		return c;
	}

	public static void setC(int c) {
		TestFragmentAdapter.c = c;

	}

	@Override
	public int getIconResId(int index) {
		// TODO Auto-generated method stub
		return index;
	}

}