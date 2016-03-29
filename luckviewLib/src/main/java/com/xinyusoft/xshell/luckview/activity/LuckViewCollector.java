package com.xinyusoft.xshell.luckview.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;

/**
 * 新打开的的浏览器的控制器
 * @author zzy
 *
 */
public class LuckViewCollector {
	/** NewBrowserActivity的每个实例 */
	public static List<Activity> activities = new ArrayList<Activity>();
	/** NewBrowserActivity的每个实例的唯一名字  */
	public static List<String> onlyNames = new ArrayList<String>();

	public static void addActivity(Activity activity) {
		activities.add(activity);
	}

	public static void addActivityOnlyName(String onlyName) {
		onlyNames.add(onlyName);
	}

	public static void removeActivity(Activity activity) {
		activities.remove(activity);
	}

	public static void removeAllNames() {
		onlyNames.clear();
	}
	
	/**
	 * 拿到第一个进入List的名字
	 */
	public static String getOnlyNamesFirstName() {
		return onlyNames.get(0);
	}
	
	public static void finishAll() {
		for (Activity activity : activities) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
		activities.clear();
	}
	
	/**
	 * 返回幸运大转盘
	 */
	public static void backLuckView() {
		Log.i("zzy","activities.size()  befor:"+activities.size());
		for (int i = activities.size()-1; i > 0; i--) {
			Log.i("zzy","i:"+i);
			Activity activity = activities.get(i);
			if (!activity.isFinishing()) {
				activity.finish();
			}
			activities.remove(activity);
		}
		Log.i("zzy","activities.size():"+activities.size());
		
	}
}
