package com.xinyusoft.xshelllib.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.xinyusoft.xshelllib.application.AppConstants;
import com.xinyusoft.xshelllib.application.AppContext;
import com.xinyusoft.xshelllib.badger.ShortcutBadgeException;
import com.xinyusoft.xshelllib.badger.XiaomiHomeBadger;
import com.xinyusoft.xshelllib.ui.LoadingActivity;
import com.xinyusoft.xshelllib.utils.AppBackgroundCheck;
import com.xinyusoft.xshelllib.utils.MResource;

/**
 * 信鸽接收器
 * 
 * @author zzy
 *
 */
public class NotificatinReceiver extends XGPushBaseReceiver {

	@Override
	public void onDeleteTagResult(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotifactionClickedResult(Context context, XGPushClickedResult message) {
		// if (context == null || message == null) {
		// return;
		// }
		// if (message.getActionType() ==
		// XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
		// // 通知在通知栏被点击啦。。。。。
		// // APP自己处理点击的相关动作
		// // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
		// Intent intent = new Intent();
		// intent.putExtra("jumpurl", "12345");
		// message.parseIntent(intent);
		// } else if (message.getActionType() ==
		// XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
		// // 通知被清除啦。。。。
		// // APP自己处理通知被清除后的相关动作
		// }
	}

	@Override
	public void onNotifactionShowedResult(Context arg0, XGPushShowedResult arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRegisterResult(Context arg0, int arg1, XGPushRegisterResult arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetTagResult(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@SuppressLint("NewApi")
	@Override
	public void onTextMessage(Context context, XGPushTextMessage message) {
		if (AppBackgroundCheck.isApplicationBroughtToBackground(context) == 2) {
			// Intent intent = new Intent(AppConstants.ACTION_GET_MESSAGE);
			String pushtype = null;
			// String senduserid = null;
			String sendcontent = null;
			String jumpurl = null;
			// String destination = null;
			// String topic = null;
			// String nickname =null;
			// String question = null;
			String messageTitle = null;
			String messageContent = message.getContent();
			int i = 0;
			try {

				JSONObject receive = new JSONObject(messageContent);
				if (receive.has("pushtype")) {
					pushtype = receive.getString("pushtype");
				}
				if (receive.has("sendcontent")) {
					sendcontent = receive.getString("sendcontent");
				}
				if (receive.has("jumpurl")) {
					jumpurl = receive.getString("jumpurl");
					// jumpurl = /* context.getFilesDir() + File.separator +
					// */jumpurl;
				}
				NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				if (message.getTitle() != null) {
					messageTitle = message.getTitle();
				} else {
					messageTitle = context.getResources().getString(MResource.getIdByName(AppContext.CONTEXT, "string", "app_name"));
				}

				if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) { //如果是小米的话，做特殊处理
					try {
						XiaomiHomeBadger mShortcutBadger = new XiaomiHomeBadger(context,jumpurl, messageTitle, sendcontent);
						mShortcutBadger.executeBadge1(1);
					} catch (ShortcutBadgeException e) {
						e.printStackTrace();
					}
					return;
				}
				Intent notifyIntent = new Intent(context, LoadingActivity.class);
				// if ("redirecthtml".equals(pushtype)) {
				// notifyIntent.putExtra("jumpurl", jumpurl);
				// }
				notifyIntent.putExtra("jumpurl", jumpurl);
				notifyIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				// notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
				// Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent pi = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				Notification notify = new Notification.Builder(context)
						.setAutoCancel(true)
						.setTicker("【" + context.getResources().getString(MResource.getIdByName(AppContext.CONTEXT, "string", "app_name")) + "】  : " + sendcontent)
						.setSmallIcon(MResource.getIdByName(AppContext.CONTEXT, "drawable", "ic_launcher"))
						.setContentTitle(messageTitle)
						.setContentText(sendcontent)
						// .setLargeIcon(userImage)
						.setDefaults(Notification.DEFAULT_ALL).setContentIntent(pi).build();
				nm.notify(AppConstants.NOTIFICATION_ID, notify);
				AppConstants.NOTIFICATION_ID++;
				
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else {
			Log.d(this.getClass().getName(), "程序在前台，消息不展示");
		}

	}

	@Override
	public void onUnregisterResult(Context arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
