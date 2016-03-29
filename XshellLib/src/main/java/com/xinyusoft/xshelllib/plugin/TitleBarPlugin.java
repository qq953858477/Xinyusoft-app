package com.xinyusoft.xshelllib.plugin;

import org.apache.cordova.CordovaPlugin;

public class TitleBarPlugin extends CordovaPlugin {

//	@Override
//	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
//		JSONObject obj = args.getJSONObject(0);
//		IButtonAttrCallBack iba = (IButtonAttrCallBack) cordova.getActivity();
//		if ("setTitle".equals(action)) {
//			String titleName = obj.getString("title");
//			iba.changeTitle(titleName);
//			callbackContext.success("{'result':1}");
//			return true;
//		} else if ("changebButtonIcon".equals(action)) {
//			int position = obj.getInt("position");
//			if (position < 1 || position > 4) {
//				return false;
//			}
//			String icon = obj.getString("icon");
//			String callback = obj.getString("callback");
//			ButtonModel model = new ButtonModel(position, icon, callback);
//			iba.changeButtonAttr(model);
//			callbackContext.success("{\"result\":1}");
//			return true;
//		}
//
//		return false;
//
//	}
//
//	public interface IButtonAttrCallBack {
//		/**
//		 * 改变标题栏的按钮属性
//		 */
//		void changeButtonAttr(ButtonModel model);
//
//		/**
//		 * 改变标题
//		 */
//		void changeTitle(String titleName);
//	}
}
