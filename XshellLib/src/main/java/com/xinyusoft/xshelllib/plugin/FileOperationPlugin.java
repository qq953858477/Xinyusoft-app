package com.xinyusoft.xshelllib.plugin;

import java.io.File;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.xinyusoft.xshelllib.application.AppConfig;
import com.xinyusoft.xshelllib.application.AppConstants;
import com.xinyusoft.xshelllib.utils.FileUtil;

public class FileOperationPlugin extends CordovaPlugin {
	private static final String TAG = "FileOperationPlugin";

	@Override
	public boolean execute(String action, CordovaArgs args,
			CallbackContext callbackContext) throws JSONException {
		// TODO Auto-generated method stub

		String result = args.getString(0);
		//JSONObject jos = args.getJSONObject(0);
		//Log.i("zzy", "result" + args.getString(0));
		//Log.i("zzy", "jos:" + jos);
		//Log.i("zzy", "action:" + action);
		FileUtil fileUtil = FileUtil.getInstance();
		try {
			if ("createFile".equals(action)) {// 添加文件
				String fileName = args.getString(0);
				String data = args.getString(1);
				int isAppend = args.getInt(2);
				int rt = 2;
				try {
					fileUtil.write2SDFromString(AppConstants.APP_ROOT_DIR + "/"
							+ fileName, data,isAppend);
					if(AppConfig.DEBUG)
					Log.d("dd", "write2SDFromString:到了这里");
					rt = 1;
				} catch (Exception e) {
					// TODO: handle exception
					rt = 0;
				}
				JSONObject json = new JSONObject();
				json.put("result", rt);
				callbackContext.success(json);
				return true;
			} else if ("deleteFile".equals(action)) {// 删除文件
				String fileName = args.getString(0);

				boolean tmpSign = fileUtil.delete(
						new File(fileUtil.getPathSDCard()
								+ AppConstants.APP_ROOT_DIR), fileName);
				Log.d("dd", "被删除");
				int rt = tmpSign?1:0;
				
				JSONObject json = new JSONObject();
				json.put("result", rt);
				callbackContext.success(json);
				return true;

			}else if("isfileexist".equals(action)){//文件是否存在
				String fileName = args.getString(0);
				boolean isExist = fileUtil.isFileExist(AppConstants.APP_ROOT_DIR + "/" + fileName);
				int rt = isExist?1:0;
				JSONObject json = new JSONObject();
				json.put("result", rt);
				callbackContext.success(json);
				return true;
			}else if("readFile".equals(action)){//读文件
				String fileName = args.getString(0);
				String fileContent = null;
				int rt;
				try {
					fileContent = FileUtil.getInstance().readSringFromSD(
							AppConstants.APP_ROOT_DIR + "/" + fileName);
					Log.d("dd", fileContent+"被读取出");
					rt = 1;
				} catch (Exception e) {
					rt = 0;
					e.printStackTrace();
				}
				

				JSONObject json = new JSONObject();
				json.put("result", rt);
				json.put("content", fileContent);
				callbackContext.success(json);
				return true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return false;
	}

}
