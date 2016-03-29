package com.xinyusoft.xshelllib.plugin;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.xinyusoft.xshelllib.application.AppConstants;
import com.xinyusoft.xshelllib.application.AppContext;
import com.xinyusoft.xshelllib.lock.UnlockGesturePasswordActivity;
import com.xinyusoft.xshelllib.tools.wxlogin.WeixinUtil;
import com.xinyusoft.xshelllib.ui.NewBrowserCollector;
import com.xinyusoft.xshelllib.utils.Log2FileUtil;
import com.xinyusoft.xshelllib.utils.PhoneInfo;
import com.xinyusoft.xshelllib.utils.PlaySoundsUtil;
import com.xinyusoft.xshelllib.utils.VersionUtil;

/**
 * 原生插件，比如读写文件
 * 
 * @author zzy
 *
 */
public class NativePlugin extends CordovaPlugin {
	private static final String TAG = "NativePlugin";
	private static Context context = AppContext.CONTEXT;
	private static HashMap<String, Integer> sounds = new HashMap<String, Integer>();// 音乐播放
	private static File ROOT_FILE = new File(context.getFilesDir().getAbsolutePath());
	

	
	
	/** 当打开一个页面时，需要调用回调函数名字 */
	private String newBroserCallBcak;
	
	
	//手势解锁成功的回掉函数名
	public static String FUNCTIONNAME;
	
	
	public NativePlugin(){
		Log.d("dd", "NativePlugin++++");
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.d("plugin", "NativePlugin++++stard");
		super.onStart();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.d("plugin", "NativePlugin++++onDestroy");
		super.onDestroy();
	}
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		String result = args.getString(0);
		// JSONObject jos = args.getJSONObject(0);
		Log.i(TAG, "result" + result);
		try {
			if ("getDeviceId".equals(action)) { // 获取设备id
				if (null != result && result.length() > 0) {
					JSONObject json = new JSONObject();
					json.put("version", VersionUtil.getVersionName(context));
					json.put("result", 1);
					json.put("deviceid", PhoneInfo.getInstance().getDeviceID());
					json.put("model", PhoneInfo.getInstance().getModel());
					json.put("release", "Android" + PhoneInfo.getInstance().getRelease());
					json.put("pixels", PhoneInfo.getInstance().getPixels(cordova.getActivity()));
					callbackContext.success(json.toString());
					return true;
				}
			} else if ("startToChangeOrientation".equals(action)) { // 横竖屏切换
				JSONObject jos = args.getJSONObject(0);
				final String callBackName = jos.getString("callBackName");
				String string = jos.getString("type");
				if("1".equals(jos.getString("type"))) {
					cordova.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
				} else {
					cordova.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
				}
				cordova.getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						webView.loadUrl("javascript:" + callBackName.trim() + "('{\"result\":1}')");
					}
				});
				callbackContext.success();
				return true;
			} else if ("screenPortrait".equals(action)) { // 竖屏
				if (null != result && result.length() > 0) {

					// Intent intent = new Intent(AppConstants.ACTION_PORTRAIT);
					// LocalBroadcastManager.getInstance(cordova.getActivity())
					// .sendBroadcast(intent);
					cordova.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
					JSONObject json = new JSONObject();
					json.put("result", 1);
					callbackContext.success(json);
					return true;
				}
			} else if ("share".equals(action)) { // 分享功能
				JSONObject jo = args.getJSONObject(0);
				WeixinUtil.getInstance().weixinShare(jo, cordova.getActivity());
				JSONObject json = new JSONObject();
				json.put("result", 1);
				callbackContext.success(json);
				return true;
			}/*
			 * else if ("upload".equals(action)) { JSONObject json = new
			 * JSONObject(); String filePath = json.getString("filePath");
			 * 
			 * filePath = URLDecoder.decode(filePath, "UTF-8"); String url =
			 * jos.getString("url"); String callback =
			 * json.getString("callback"); // String url = //
			 * "http://192.168.3.110:8080/jsupload/uploadAndroid"; url =
			 * URLDecoder.decode(url, "UTF-8"); Intent intent = new
			 * Intent(AppConstants.ACTION_UPLOAD); intent.putExtra("filePath",
			 * filePath); intent.putExtra("url", url);
			 * intent.putExtra("callback", callback);
			 * LocalBroadcastManager.getInstance
			 * (cordova.getActivity()).sendBroadcast(intent);
			 * 
			 * json.put("result", 1); callbackContext.success(json); return
			 * true; }
			 */else if ("sound".equals(action)) { // 播放声音
				JSONObject json = new JSONObject();
				String name = json.getString("soundfilename");
				name = URLDecoder.decode(name, "UTF-8");
				if (sounds.containsKey(name)) {
					PlaySoundsUtil.getInstance().play(sounds.get(name), 0);
				} else {
					try {
						int id = -1;
						synchronized (TAG) {
							File file = new File(ROOT_FILE, name);
							if (file.exists() && file.isFile())
								id = PlaySoundsUtil.getInstance().loadSound(file.getAbsolutePath());
						}
						if (id != -1) {
							sounds.put(name, id);
							Intent intent = new Intent(AppConstants.ACTION_WEBVIEW_PLAY_SOUND);
							intent.putExtra("soundId", id);
							LocalBroadcastManager.getInstance(cordova.getActivity()).sendBroadcast(intent);
						}
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
				json.put("result", 1);
				callbackContext.success(json);
				return true;
			} else if ("xinyuNewBrowser".endsWith(action)) { // 开启一个新的Activity
				JSONObject jos = args.getJSONObject(0);
				if (jos.has("callbackName")) {
					newBroserCallBcak = jos.getString("callbackName");

				}
				String url = jos.getString("url");
				Intent intent1 = new Intent();
				Log2FileUtil.getInstance().saveCrashInfo2File("开启了一个新的Activity");
				intent1.setAction(AppConstants.ACTION_NEW_BROSER);
				intent1.putExtra("url", url);
				LocalBroadcastManager.getInstance(cordova.getActivity()).sendBroadcast(intent1);
				return true;
			} else if ("closeNewBrowser".equals(action)) { // 关闭Activity
				Intent intent1 = new Intent();
				intent1.setAction(AppConstants.ACTION_CLOSE_BROSER);
				Log2FileUtil.getInstance().saveCrashInfo2File("关闭了一个新的Activity");
				LocalBroadcastManager.getInstance(cordova.getActivity()).sendBroadcast(intent1);
				return true;
			} else if ("clipboard".equals(action)) { //粘贴板
				JSONObject jos = args.getJSONObject(0);
				ClipboardManager clip = (ClipboardManager) AppContext.CONTEXT.getSystemService(Context.CLIPBOARD_SERVICE);
				clip.setPrimaryClip(ClipData.newPlainText(null, jos.getString("paste")));
				callbackContext.success();
				return true;
			} else if ("reminder".equals(action)) {// 提示
				// Intent intent = new Intent(AppConstants.ACTION_REMINDER);
				// LocalBroadcastManager.getInstance(cordova.getActivity()).sendBroadcast(intent);
				AudioManager audio = (AudioManager) cordova.getActivity().getSystemService(Context.AUDIO_SERVICE);
				int RingerMode = audio.getRingerMode();
				vibrator = (Vibrator) cordova.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
				// 注册音频通道
				cordova.getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
				mediaPlayer = MediaPlayer.create(cordova.getActivity(), cordova.getActivity().getResources().getIdentifier("test", "raw", cordova.getActivity().getPackageName()));
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				// 注册事件。当播放完毕一次后，重新指向流文件的开头，以准备下次播放。
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						mp.seekTo(0);
					}
				});

				switch (RingerMode) {
				case AudioManager.RINGER_MODE_NORMAL:// 铃声震动模式
					vibrator.vibrate(new long[] { 300, 500 }, -1);
					mediaPlayer.start();
					break;
				case AudioManager.RINGER_MODE_SILENT:// 静音模式
					break;
				case AudioManager.RINGER_MODE_VIBRATE:// 震动模式
					vibrator.vibrate(new long[] { 300, 500 }, -1);
					break;

				default:
					break;
				}

				callbackContext.success();
				return true;

			}else if("openLock".equals(action)){
				JSONObject jos = args.getJSONObject(0);
				String createLock = jos.getString("isCreateLock");
				FUNCTIONNAME = jos.getString("functionName");
				NewBrowserCollector.addActivityOnlyName("UnlockGesturePasswordActivity1");
					Intent intent =new Intent(cordova.getActivity(),UnlockGesturePasswordActivity.class);
					//createLock 为1则代表修改密码，0则是成功解锁
					intent.putExtra("createLock", createLock);
					//解锁成功后调用function的名字
					intent.putExtra("functionName", FUNCTIONNAME);
					cordova.getActivity().startActivity(intent);
					
					return true;
				
			} else if("setLock".equals(action)){
				AppContext.APPCONTEXT.getLockPatternUtils().clearLock();
			}else if ("showKeyboard".equals(action)) { //显示键盘
			
				Intent intent = new Intent(AppConstants.SHOW_KEYBOARD);
				intent.putExtra("callbackName", args.getString(0));
				intent.putExtra("type", args.getString(1));
				intent.putExtra("defalutIndex", args.getString(2));
				LocalBroadcastManager.getInstance(cordova.getActivity()).sendBroadcast(intent);
				return true;
			}
		} catch (Exception e) {
			callbackContext.error(e.getMessage());
			return false;
		}
		return false;
	}


	
	
	MediaPlayer mediaPlayer;
	Vibrator vibrator;

	@Override
	public void onPause(boolean multitasking) {
		super.onPause(multitasking);
		if (mediaPlayer != null) {
			mediaPlayer.release();

		}
		if (vibrator != null) {
			vibrator.cancel();
		}
	}

	@Override
	public void onResume(boolean multitasking) {
		super.onResume(multitasking);

		Log.i("zzy", "NativePlugin onresume:");
		if (newBroserCallBcak != null) {
			cordova.getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					webView.loadUrl("javascript:" + newBroserCallBcak + "()");
					newBroserCallBcak = null;
				}
			});
		}
			
	}
	
	/**
	 * 用来判断是否有我们开的第2个进程
	 * @return
	 */
	public boolean isAppOnForeground() {

		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = context.getPackageName();

		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName + ":xinyu_remote")) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					return true;
				}

			}
		}

		return false;
	}

}
