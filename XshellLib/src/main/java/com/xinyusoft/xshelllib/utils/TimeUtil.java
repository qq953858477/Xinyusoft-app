package com.xinyusoft.xshelllib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

	/**
	 * 返回格式好的时间 如：201408031430 用于服务器文件更新是上次时间
	 * 
	 * @param time
	 * @return
	 */
	public static String getUpdateTime(long time) {
		return new SimpleDateFormat("yyyyMMddHHmm").format(new Date(time));
	}

	/**
	 * 
	 * @param timeStr
	 *            时间字符串 "201408171320"
	 * @return long 毫秒级
	 */
	public static long getUpdataTimeLong(String timeStr) {
		Calendar c = Calendar.getInstance();
		long time = 0l;
		try {
			c.setTime(new SimpleDateFormat("yyyyMMddHHmm").parse(timeStr));
			time = c.getTimeInMillis();
		} catch (ParseException e) {
		}
		return time;
	}
	
	
	/**获取当前时间字符串
	 * @return
	 */
	public static String now(){
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SS");
		return dateFormat.format(now);
	}
}
