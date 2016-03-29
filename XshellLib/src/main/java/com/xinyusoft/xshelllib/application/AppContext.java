package com.xinyusoft.xshelllib.application;

import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.xinyusoft.xshelllib.utils.PhoneInfo;
import com.xinyusoft.xshelllib.utils.PreferenceUtil;
import com.xinyusoft.xshelllib.utils.Write2SDCard;
import com.xinyusoft.xshelllib.view.LockPatternUtils;

public class AppContext extends Application {
	@SuppressWarnings("unused")
	private static final String TAG = "LCApplication";
	public static Context CONTEXT;
	public static AppContext APPCONTEXT;
	private String cookies = null;
	private HashMap<String, String> CookieContiner = null;
	private String sid;
	private String custid;
	private String curfundid;
	private String cursecuid;
	private LockPatternUtils mLockPatternUtils;

	
	public HashMap<String, String> getCookieContiner() {
		return CookieContiner;
	}

	public void setCookieContiner(HashMap<String, String> cookieContiner) {
		CookieContiner = cookieContiner;
	}

	public String getCookies() {
		return cookies;
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		CONTEXT = this;
		APPCONTEXT = this;
		mLockPatternUtils = new LockPatternUtils(this);
		PreferenceUtil.getInstance();
		init();

	}

	public LockPatternUtils getLockPatternUtils() {
		return mLockPatternUtils;
	}
	
	private void init() {
		// ???? initVersion();// 是否进行了覆盖安装升级
		initErrorLog();// 初始化错误收集
		PreferenceUtil.getInstance().setAndroidID(PhoneInfo.getInstance().getDeviceID());
	}

//	private void initVersion() {// 根据code值判断是否是覆盖安装
//		int codeOld = PreferenceUtil.getInstance().getAppThisCode();
//		int codeNew = VersionUtil.getVersionCode(CONTEXT);
//		if (codeNew != codeOld) { // code值不一样 代表刚刚进行覆盖安装
//			installNewAPK(codeNew);
//		} else if (VersionUtil.getPackLastUpdataTime(CONTEXT) != PreferenceUtil.getInstance().getAppPackUpdataTime()) {// 同版本覆盖安装
//			installNewAPK(codeNew);
//		}
//	}
//
//	private void installNewAPK(int codeNew) {
//		if (PreferenceUtil.getInstance().isNextToInstall()) {// 下载完成 需要安装
//			// 把时间修改为刚更新的时间
//			long cacheTime = TimeUtil.getUpdataTimeLong(PreferenceUtil.getInstance().getAppCacheTime());
//			long appTime = TimeUtil.getUpdataTimeLong(AppConstants.APP_UPDATE_START_TIME);
//			if (cacheTime > appTime) {
//				PreferenceUtil.getInstance().setAppUpdateTime(PreferenceUtil.getInstance().getAppCacheTime());
//			} else {
//				PreferenceUtil.getInstance().setAppUpdateTime(AppConstants.APP_UPDATE_START_TIME);
//			}
//		}
//
//		PreferenceUtil.getInstance().setAppThisCode(codeNew);
//		PreferenceUtil.getInstance().setNextToInstall(false);
//
//		if (AppConfig.NEW_DATA) {// 内置文件 强制安装到手机中
//			PreferenceUtil.getInstance().setInstall(false);
//			PreferenceUtil.getInstance().setFileUpdateTime(AppConstants.FILE_UPDATE_START_TIME);
//		}
//		// apk pack更新时间修改一下
//		PreferenceUtil.getInstance().setAppPackUpdataTime(VersionUtil.getPackLastUpdataTime(CONTEXT));
//	}

	private void initErrorLog() {// 初始化错误手机日志
		AppException appEcxeption = AppException.getInstance();
		appEcxeption.init(CONTEXT);
		if (AppConfig.WIRTE_SDCARD)
			Write2SDCard.getInstance().writeMsg("开始写入");
	}

	// 打印log
	public static void log(String tag, String msg) {
		Log.d(tag, msg);
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getCustid() {
		return custid;
	}

	public void setCustid(String custid) {
		this.custid = custid;
	}

	public String getCurfundid() {
		return curfundid;
	}

	public void setCurfundid(String curfundid) {
		this.curfundid = curfundid;
	}

	public String getCursecuid() {
		return cursecuid;
	}

	public void setCursecuid(String cursecuid) {
		this.cursecuid = cursecuid;
	}
	

	

}
