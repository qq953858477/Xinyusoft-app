package com.xinyusoft.xshelllib.badger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.xinyusoft.xshelllib.application.AppConstants;
import com.xinyusoft.xshelllib.application.AppContext;
import com.xinyusoft.xshelllib.ui.LoadingActivity;
import com.xinyusoft.xshelllib.utils.MResource;

/**
 * @author leolin
 */
public class XiaomiHomeBadger extends ShortcutBadger {

	public static final String INTENT_ACTION = "android.intent.action.APPLICATION_MESSAGE_UPDATE";
	public static final String EXTRA_UPDATE_APP_COMPONENT_NAME = "android.intent.extra.update_application_component_name";
	public static final String EXTRA_UPDATE_APP_MSG_TEXT = "android.intent.extra.update_application_message_text";

	private String url = null;
	private String messageTitle = null;
	private String sendcontent = null;
	public XiaomiHomeBadger(Context context) {
		super(context);
	}

	public XiaomiHomeBadger(Context context, String url, String messageTitle, String sendcontent) {
		super(context);
		this.url = url;
		this.messageTitle = messageTitle;
		this.sendcontent = sendcontent;
		
	}
	
	
	public void executeBadge1(int badgeCount) throws ShortcutBadgeException {
		if(url == null) {
			return;
		}
		NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = null;
		boolean isMiUIV6 = true;
		NotificationCompat.Builder builder;
		try {
			builder = new NotificationCompat.Builder(mContext);
			builder.setTicker("【" + mContext.getResources().getString(MResource.getIdByName(AppContext.CONTEXT, "string", "app_name")) + "】 :"+sendcontent);
			builder.setAutoCancel(true);
			builder.setContentTitle(messageTitle);
			builder.setContentText(sendcontent);
			builder.setSmallIcon(mContext.getResources().getIdentifier("ic_launcher", "drawable", mContext.getPackageName()));
			builder.setDefaults(Notification.DEFAULT_LIGHTS);
			Intent notifyIntent = new Intent(mContext, LoadingActivity.class);
			notifyIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			notifyIntent.putExtra("jumpurl", url);
			PendingIntent pi = PendingIntent.getActivity(mContext, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(pi);
			notification = builder.build();

			//Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
			//Object miuiNotification = miuiNotificationClass.newInstance();
			// Field field =
			// miuiNotification.getClass().getDeclaredField("messageCount");
			// field.setAccessible(true);
			// field.set(miuiNotification, Integer.valueOf(badgeCount+""));

			Field field = notification.getClass().getDeclaredField("extraNotification");
			field.setAccessible(true);
			Object extraNotification = field.get(notification);
			Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
			method.invoke(extraNotification, badgeCount);
			// field = notification.getClass().getField("extraNotification");
			// field.setAccessible(true);
			// field.set(notification, miuiNotification);
			Log.i("zzy", "通知成功！！！:");
		} catch (Exception e) {
			isMiUIV6 = false;
			Intent localIntent = new Intent(INTENT_ACTION);
			localIntent.putExtra(EXTRA_UPDATE_APP_COMPONENT_NAME, getContextPackageName() + "/" + getEntryActivityName());
			localIntent.putExtra(EXTRA_UPDATE_APP_MSG_TEXT, String.valueOf(badgeCount == 0 ? "" : badgeCount));
			mContext.sendBroadcast(localIntent);
		} finally {
			if (notification != null && isMiUIV6) {
				// miui6以上版本需要使用通知发送
				nm.notify(AppConstants.NOTIFICATION_ID, notification);
				AppConstants.NOTIFICATION_ID++;
			}
		}
	}
	

	@Override
	protected void executeBadge(int badgeCount) throws ShortcutBadgeException {
		NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = null;
		boolean isMiUIV6 = true;
		NotificationCompat.Builder builder;
		try {
			builder = new NotificationCompat.Builder(mContext);
			builder.setContentTitle("您有" + badgeCount + "未读消息");
			builder.setTicker("您有" + badgeCount + "未读消息");
			builder.setAutoCancel(true);
			builder.setSmallIcon(mContext.getResources().getIdentifier("ic_launcher", "drawable", mContext.getPackageName()));
			builder.setDefaults(Notification.DEFAULT_LIGHTS);
			Intent notifyIntent = new Intent(mContext, LoadingActivity.class);
			notifyIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			PendingIntent pi = PendingIntent.getActivity(mContext, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(pi);
			notification = builder.build();
			//Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
			//Object miuiNotification = miuiNotificationClass.newInstance();
			// Field field =
			// miuiNotification.getClass().getDeclaredField("messageCount");
			// field.setAccessible(true);
			// field.set(miuiNotification, Integer.valueOf(badgeCount+""));

			Field field = notification.getClass().getDeclaredField("extraNotification");
			field.setAccessible(true);
			Object extraNotification = field.get(notification);
			Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
			method.invoke(extraNotification, badgeCount);
			// field = notification.getClass().getField("extraNotification");
			// field.setAccessible(true);
			// field.set(notification, miuiNotification);
			Log.i("zzy", "通知成功！！！:");
		} catch (Exception e) {
			isMiUIV6 = false;
			Intent localIntent = new Intent(INTENT_ACTION);
			localIntent.putExtra(EXTRA_UPDATE_APP_COMPONENT_NAME, getContextPackageName() + "/" + getEntryActivityName());
			localIntent.putExtra(EXTRA_UPDATE_APP_MSG_TEXT, String.valueOf(badgeCount == 0 ? "" : badgeCount));
			mContext.sendBroadcast(localIntent);
		} finally {
			if (notification != null && isMiUIV6) {
				// miui6以上版本需要使用通知发送
				nm.notify(101010, notification);
			}
		}
	}

	@Override
	public List<String> getSupportLaunchers() {
		return Arrays.asList("com.miui.miuilite", "com.miui.home", "com.miui.miuihome", "com.miui.miuihome2", "com.miui.mihome", "com.miui.mihome2");
	}
}
