package com.xinyusoft.xshelllib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.xinyusoft.xshelllib.application.AppContext;

public class PreferenceUtil {
	private static final String PREF_NAME = "dhqq_pref";
	private static PreferenceUtil instance;
	private  final SharedPreferences pref;
	
	/** 新的弹出微信登录，此方法是为了标识，来标识inAppBroeser的onresume的登录 */
	private static final String ISNEWWEIXIN = "install";
	/** 文件列表上次更新时间 */
	private static final String FILE_UPDATE_TIME = "file_update_time";
	/** 设备id */
	private static final String ANDROID_ID = "android_id";
	/** 下次打开是否应该提示安装 */
	private static final String APP_THIS_CODE = "app_this_code";

	/** 下次打开是否应该提示安装 */
	private static final String NEXT_TO_INSTALL_APP = "next_to_install_app";
	/** app列表最近更新时间 */
	private static final String APP_UPDATE_TIME = "app_update_time";
	/** 下载APP保存目录 */
	private static final String DOWN_APP_DIR = "down_app_dir";

	/** 正在下载File */
	private static final String DOWNLOADING_FILE = "downloading_file";

	/** 是否有后台检查更新html5 */
	private static final String BACKGROUND_UPDATE_FILE = "BACKGROUND_UPDATE_FILE";
	
	private static final String ISFIRSTRUN = "FIRST_RUN";

	private static final String ISSHOWGUIDEPAGE = "ISSHOWGUIDEPAGE";

	private static final String APP_HOMEACTIVITY_PATH = "app_homeactivity_path";
	public static PreferenceUtil getInstance() {
		if (instance == null) {
			synchronized (PREF_NAME) {
				if (instance == null) {
					instance = new PreferenceUtil();
				}
			}
		}
		return instance;
	}

	private PreferenceUtil() {
		pref = AppContext.CONTEXT.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}

	/**
	 * 设置新的页面微信登录
	 *
	 */
	public void setNewXingeLogin(boolean isXingeLogin) {
		Editor appspEditor = pref.edit();
		appspEditor.putBoolean(ISNEWWEIXIN, isXingeLogin);
		appspEditor.apply();
	}

	/**
	 * 获取新的页面登录
	 */
	public boolean getNewXingeLogin() {
		return pref.getBoolean(ISNEWWEIXIN, false);
	}
	
	/**
	 * 获取程序是否为首次安装
	 */
	public boolean hadFIRSTRUN() {
		return pref.getBoolean(ISFIRSTRUN, true);
	}

	public void setFIRSTRUN(boolean install) {
		Editor appspEditor = pref.edit();
		appspEditor.putBoolean(ISFIRSTRUN, install);
		appspEditor.apply();
	}


	/**
	 * 是否需要显示引导页
	 */
	public boolean isShowGuidePage() {
		return pref.getBoolean(ISSHOWGUIDEPAGE, true);
	}

	/**
	 * 设置是否显示引导页
	 * @param show 是否显示
	 */
	public void setShowGuidePage(boolean show) {
		Editor appspEditor = pref.edit();
		appspEditor.putBoolean(ISSHOWGUIDEPAGE, show);
		appspEditor.apply();
	}

	
	
	/**
	 * 获取homeActivity的path
	 */
	public String getHomeActivityPath() {
		return pref.getString(APP_HOMEACTIVITY_PATH, null);
	}

	/**
	 * 设置homeActivity的path
	 *
	 */
	public void setHomeActivityPath(String path) {
		Editor appspEditor = pref.edit();
		appspEditor.putString(APP_HOMEACTIVITY_PATH, path);
		appspEditor.apply();
	}

	
	

	/**
	 * 设置文件更新时间（检查文件更新是会提交这个时间）
	 *
	 * @param time 时间
	 */
	public void setFileUpdateTime(String time) {
		Editor appspEditor = pref.edit();
		appspEditor.putString(FILE_UPDATE_TIME, time);
		appspEditor.apply();
	}

	/**
	 * 获取文件更新时间（检查文件时）
	 */
	public String getFileUpdateTime() {
		return pref.getString(FILE_UPDATE_TIME, null);
	}

	public void setAndroidID(String id) {
		Editor appspEditor = pref.edit();
		appspEditor.putString(ANDROID_ID, id);
		appspEditor.apply();
	}


	/**
	 * 设置App更新时间（检查文件更新是会提交这个时间）
	 * 
	 * @param time 时间
	 */
	public void setAppUpdateTime(String time) {
		Editor appspEditor = pref.edit();
		appspEditor.putString(APP_UPDATE_TIME, time);
		appspEditor.apply();
	}

	/**
	 * 获取App更新时间（检查文件时）
	 */
	public String getAppUpdateTime() {
		return pref.getString(APP_UPDATE_TIME, null);
	}

	/**
	 * 设置APP下载目录
	 * 
	 * @param dir 目录
	 */
	public void setDownAppDir(String dir) {
		Editor appspEditor = pref.edit();
		appspEditor.putString(DOWN_APP_DIR, dir);
		appspEditor.apply();
	}

	/**
	 * 获取APP下载目录
	 */
	public String getDownAppDir() {
		return pref.getString(DOWN_APP_DIR, "");
	}

	/**
	 * 设置文件更新当前下载状态（正在下载）
	 * 
	 * @param down  下载的状态
	 */
	public void setDownloadingFile(boolean down) {
		Editor appspEditor = pref.edit();
		appspEditor.putBoolean(DOWNLOADING_FILE, down);
		appspEditor.apply();
	}

	/**
	 * 获取文件更新当前下载状态（是否正在下载）
	 */
	public boolean getDownloadingFile() {
		return pref.getBoolean(DOWNLOADING_FILE, true);
	}
	
	
	/**
	 * 设置是否有后台检查更新html5
	 * 
	 * @param down  是否有后台检查更新html5
	 */
	public void setBackgroundUpdateFile(boolean down) {
		Editor appspEditor = pref.edit();
		appspEditor.putBoolean(BACKGROUND_UPDATE_FILE, down);
		appspEditor.apply();
	}



	/**************** 需要的内容？ *****************************/
	/**
	 * 设置当前版本的CODE
	 */
	public void setAppThisCode(int code) {
		Editor appspEditor = pref.edit();
		appspEditor.putInt(APP_THIS_CODE, code);
		appspEditor.apply();
	}

	/**
	 * 获取当前版本的CODE
	 */
	public int getAppThisCode() {
		return pref.getInt(APP_THIS_CODE, 0);
	}


	/**
	 * 设置应当升级
	 */
	public void setNextToInstall(boolean install) {
		Editor appspEditor = pref.edit();
		appspEditor.putBoolean(NEXT_TO_INSTALL_APP, install);
		appspEditor.apply();
	}



	/**************** 需要的内容？* end ****************************/
}
