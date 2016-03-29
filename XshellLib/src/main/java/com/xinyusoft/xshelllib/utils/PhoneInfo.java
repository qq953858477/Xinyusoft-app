package com.xinyusoft.xshelllib.utils;

import android.app.Activity;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;

import com.xinyusoft.xshelllib.application.AppContext;

/**获取设备ID ，AndroidID+packageName
 * @author ZhangSong
 * 
 */
public class PhoneInfo {
	private String android_id ;
	private static PhoneInfo id ;
	private static final String TAG = "AndroidID";
	private PhoneInfo(){
		android_id = Secure.getString(AppContext.CONTEXT.getContentResolver(),
				Secure.ANDROID_ID)+AppContext.CONTEXT.getPackageName();
	}
	
	public static PhoneInfo getInstance(){
		if(id ==null){
			synchronized (TAG) {
				if(id ==null){
					id = new PhoneInfo();
				}
			}
		}
		return id;
	}
	
	public String getDeviceID(){
		return android_id;
	}
	
	
	//获得手机型号
	public String getModel(){
		return android.os.Build.MODEL;
		
	}
	
	//获得Android版本
	public String getRelease(){
		return android.os.Build.VERSION.RELEASE;
	}
	
	public String  getPixels(Activity context){
		DisplayMetrics	dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels+"*"+dm.heightPixels; 
	}
	
	

}
