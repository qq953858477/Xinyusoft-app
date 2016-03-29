package com.xinyusoft.xshelllib.tools.xinge;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.service.XGPushService;
import com.xinyusoft.xshelllib.application.AppConfig;

/**
 * 信鸽注册
 * @author zzy
 *
 */
public class XGPushUtil {

	private static int temp = 0;

	public static void initXGPush(Context context, String deviceID) {

		if (temp < 1) {
			// 信鸽 推送服务
			XGPushConfig.enableDebug(context, false);

			XGPushManager.registerPush(context, deviceID, new XGIOperateCallback() {
				@Override
				public void onSuccess(Object data, int flag) {
					if(AppConfig.DEBUG)
					Log.d("zzy", "注册成功，设备token为：" + data);
				}

				@Override
				public void onFail(Object data, int errCode, String msg) {
					if(AppConfig.DEBUG)
					Log.d("zzy", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
				}
			});
			// 2.36（不包括）之前的版本需要调用以下2行代码
			Intent service = new Intent(context, XGPushService.class);
			context.startService(service);
			temp ++;
			if(AppConfig.DEBUG)
			Log.i("zzy", "推送了~！！！:");
		}
		
	}
}
