package com.xinyusoft.xshelllib.plugin;

import android.text.TextUtils;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.xinyusoft.xshelllib.application.AppConstants;
import com.xinyusoft.xshelllib.utils.FileUtil;
import com.xinyusoft.xshelllib.utils.Log2FileUtil;
import com.xinyusoft.xshelllib.utils.RecorderUtil;
import com.xinyusoft.xshelllib.utils.UploadUtil;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

/**
 * 录音插件
 */
public class RecordPlugin extends CordovaPlugin {

	private RecorderUtil mRecorderUtil = new RecorderUtil();
	private String name;
	private File path;
	private Long startTime;

	@Override
	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
		final String callBackName = args.getString(0);
		if (callBackName == null) {
			return false;
		}

		if ("startRecord".equals(action)) {
			name = System.currentTimeMillis() + ".3gp";// 生成录音保存文件名
			try {
				path = FileUtil.getInstance().createFileInSDCard(AppConstants.APP_ROOT_DIR + "/" + AppConstants.APP_AUDIO_DIR + "/" + name);// 生成录音保存路径
			} catch (IOException e) {
				e.printStackTrace();
			}
			mRecorderUtil.stopRecorder();
			mRecorderUtil.startRecorder(path.getAbsolutePath());// 开始录音
			startTime = System.currentTimeMillis();
			//webView.loadUrl("javascript:" + callBackName.trim() + "()");
			// 回调js方法告诉页面开始录音
			//callbackContext.success();
			cordova.getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					webView.loadUrl("javascript:" + callBackName.trim() + "('1')");
				}
			});
			return true;
		} else if ("endRecord".equals(action)) {
			long duration = System.currentTimeMillis() - startTime;
			LogUtils.e("startTime==" + startTime + "duration==" + duration);
			mRecorderUtil.stopRecorder();
			//Log.i("zzy", "结束录音:");
			//String uploadingUrl = args.getString(1);
			UploadUtil.upLoad(path, args.getString(1), new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					String result = arg0.result;
					try {
						LogUtils.e(result);
						if (!TextUtils.isEmpty(result)) {
							//上传之后就删除
							if(path.exists()) {
								path.delete();
							}
							webView.loadUrl("javascript:" + callBackName.trim() + "('" + result + "')");
							Log2FileUtil.getInstance().saveCrashInfo2File("录音上传成功：" + result.toString());
							
						} else {
							LogUtils.e("result为空");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					LogUtils.e("arg0==" + arg0 + "arg1==" + arg1);
					Log2FileUtil.getInstance().saveCrashInfo2File("录音上传失败：" + arg0.toString() + "&&&&" + arg1.toString());
				}

				@Override
				public void onLoading(long total, long current, boolean isUploading) {
					super.onLoading(total, current, isUploading);
				}
			});
			LogUtils.e("停止录音");
			callbackContext.success();
			return true;
		} 

		return false;
	}
}
