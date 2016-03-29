package com.xinyusoft.xshelllib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.xinyusoft.xshelllib.application.AppContext;

public class PreferenceUtil {
	private static final String PREF_NAME = "dhqq_pref";
	private static PreferenceUtil instance;
	private  final SharedPreferences pref;

	/** 首次安装 */
	private static final String DELAY_UPDATE = "DELAY_UPDATE";
	
	/** 信鸽登录 */
	private static final String ISWEIXIN = "install";
	
	/** 新的弹出微信登录，此方法是为了标识，来标识inAppBroeser的onresume的登录 */
	private static final String ISNEWWEIXIN = "install";
	/** 文件列表上次更新时间 */
	private static final String FILE_UPDATE_TIME = "file_update_time";
	/** 设备id */
	private static final String ANDROID_ID = "android_id";
	/** 下次打开是否应该提示安装 */
	private static final String APP_THIS_CODE = "app_this_code";
	/** 文件列表最近更新时间 */
	private static final String APP_PACKAGE_LAST_UPDATE_TIME = "app_pack_update_time";
	/** 下次打开是否应该提示安装 */
	private static final String NEXT_TO_INSTALL_APP = "next_to_install_app";
	/** 文件列表更新缓存时间（下载文件完成并解压后保存正式时间） */
	private static final String APP_CACHE_TIME = "app_cache_time";
	/** app列表最近更新时间 */
	private static final String APP_UPDATE_TIME = "app_update_time";
	/** 下载APP保存目录 */
	private static final String DOWN_APP_DIR = "down_app_dir";
	/** 下次打开时移动文件 */
	private static final String NEXT_TO_MOVE_FILE = "next_to_move_file";
	/** 正在下载File */
	private static final String DOWNLOADING_FILE = "downloading_file";

	/** 是否有后台检查更新html5 */
	private static final String BACKGROUND_UPDATE_FILE = "BACKGROUND_UPDATE_FILE";
	
	private static final String ISFIRSTRUN = "FIRST_RUN";

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
	 * 设置延缓更新
	 * 
	 * @param install
	 */
	public void setisDelayUpateHtml(boolean isDelayUpateHtml) {
		Editor appspEditor = pref.edit();
		appspEditor.putBoolean(DELAY_UPDATE, isDelayUpateHtml);
		appspEditor.commit();
	}

	/**
	 * 获取延缓更新
	 */
	public boolean isDelayUpateHtml() {
		return pref.getBoolean(DELAY_UPDATE, true);
	}


	
	/**
	 * 设置是否为信鸽推送的登录
	 * 
	 * @param install
	 */
	public void setisXingeLogin(boolean isXingeLogin) {
		Editor appspEditor = pref.edit();
		appspEditor.putBoolean(ISWEIXIN, isXingeLogin);
		appspEditor.commit();
	}

	/**
	 * 获取是否为信鸽推送的登录
	 */
	public boolean isXingeLogin() {
		return pref.getBoolean(ISWEIXIN, false);
	}
	
	
	
	/**
	 * 设置新的页面微信登录
	 * 
	 * @param install
	 */
	public void setNewXingeLogin(boolean isXingeLogin) {
		Editor appspEditor = pref.edit();
		appspEditor.putBoolean(ISNEWWEIXIN, isXingeLogin);
		appspEditor.commit();
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
		appspEditor.commit();
	}

	/**
	 * 获取App更新缓存时间（检查更新时候的时间）
	 */
	public String getAppCacheTime() {
		return pref.getString(APP_CACHE_TIME, null);
	}

	/**
	 * 设置App更新缓存时间（检查更新时候的时间）
	 * 
	 * @param time
	 */
	public void setAppCacheTime(String time) {
		Editor appspEditor = pref.edit();
		appspEditor.putString(APP_CACHE_TIME, time);
		appspEditor.commit();
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
	 * @param time
	 */
	public void setHomeActivityPath(String path) {
		Editor appspEditor = pref.edit();
		appspEditor.putString(APP_HOMEACTIVITY_PATH, path);
		appspEditor.commit();
	}

	
	

	/**
	 * 设置文件更新时间（检查文件更新是会提交这个时间）
	 * 
	 * @param time
	 */
	public void setFileUpdateTime(String time) {
		Editor appspEditor = pref.edit();
		appspEditor.putString(FILE_UPDATE_TIME, time);
		appspEditor.commit();
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
		appspEditor.commit();
	}

	public String getAndroidID() {
		return pref.getString(ANDROID_ID, "");
	}

	/**
	 * 设置App更新时间（检查文件更新是会提交这个时间）
	 * 
	 * @param time
	 */
	public void setAppUpdateTime(String time) {
		Editor appspEditor = pref.edit();
		appspEditor.putString(APP_UPDATE_TIME, time);
		appspEditor.commit();
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
	 * @param dir
	 */
	public void setDownAppDir(String dir) {
		Editor appspEditor = pref.edit();
		appspEditor.putString(DOWN_APP_DIR, dir);
		appspEditor.commit();
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
	 * @param down
	 */
	public void setDownloadingFile(boolean down) {
		Editor appspEditor = pref.edit();
		appspEditor.putBoolean(DOWNLOADING_FILE, down);
		appspEditor.commit();
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
	 * @param down
	 */
	public void setBackgroundUpdateFile(boolean down) {
		Editor appspEditor = pref.edit();
		appspEditor.putBoolean(BACKGROUND_UPDATE_FILE, down);
		appspEditor.commit();
	}

	/**
	 * 获取是否有后台检查更新html5
	 */
	public boolean getBackgroundUpdateFile() {
		return pref.getBoolean(BACKGROUND_UPDATE_FILE, false);
	}


	/**************** 需要的内容？ *****************************/
	/**
	 * 设置当前版本的CODE
	 */
	public void setAppThisCode(int code) {
		Editor appspEditor = pref.edit();
		appspEditor.putInt(APP_THIS_CODE, code);
		appspEditor.commit();
	}

	/**
	 * 获取当前版本的CODE
	 */
	public int getAppThisCode() {
		return pref.getInt(APP_THIS_CODE, 0);
	}

	public void setAppPackUpdataTime(long time) {
		Editor appspEditor = pref.edit();
		appspEditor.putLong(APP_PACKAGE_LAST_UPDATE_TIME, time);
		appspEditor.commit();
	}

	public long getAppPackUpdataTime() {
		return pref.getLong(APP_PACKAGE_LAST_UPDATE_TIME, -1);
	}

	/**
	 * 设置应当升级
	 * 
	 * @param zip
	 */
	public void setNextToInstall(boolean install) {
		Editor appspEditor = pref.edit();
		appspEditor.putBoolean(NEXT_TO_INSTALL_APP, install);
		appspEditor.commit();
	}

	/**
	 * 下载完成应当升级
	 */
	public boolean isNextToInstall() {
		return pref.getBoolean(NEXT_TO_INSTALL_APP, false);
	}

	// /**
	// * 设置APP更新信息
	// *
	// * @param down
	// */
	// public void setUpdateInfo(ChangeFile info) {
	// Editor appspEditor = pref.edit();
	// appspEditor.putString(APP_UPDATE_INFO_APP_PATH, info.getPath());
	// appspEditor.putString(APP_UPDATE_INFO_APP_TIME, info.getUpdatetime());
	// appspEditor.putString(APP_UPDATE_INFO_APP_STATUS, info.getStatus());
	// appspEditor.putString(APP_UPDATE_INFO_APP_MD5, info.getMd5());
	// appspEditor.commit();
	// }
	//
	// /**
	// * 获取APP更新信息
	// */
	// public ChangeFile getUpdateInfo() {
	// ChangeFile info = new ChangeFile();
	// info.setPath(pref.getString(APP_UPDATE_INFO_APP_PATH, ""));
	// info.setUpdatetime(pref.getString(APP_UPDATE_INFO_APP_TIME, ""));
	// info.setStatus(pref.getString(APP_UPDATE_INFO_APP_STATUS, ""));
	// info.setMd5(pref.getString(APP_UPDATE_INFO_APP_MD5, ""));
	// return info;
	// }


	/**************** 需要的内容？* end ****************************/
}
