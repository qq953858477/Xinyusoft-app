package com.xinyusoft.xshell.luckview.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;

/**
 * appconfig.xml解析器
 * @author zzy
 *
 */
public class ParseConfig {
	private static ParseConfig instance;
	private static Map<String, String> configInfo = new HashMap<String, String>();

	public static ParseConfig getInstance(Context context) {
		if(instance == null) {
			instance = new ParseConfig();
			parse(context);
		}
		return instance;
	}

	private ParseConfig() {

	}

	public Map<String, String> getConfigInfo() {
		return configInfo;
	}

	public static void parse(Context action) {
		int id = action.getResources().getIdentifier("appconfig", "xml", action.getPackageName());
		parse(action.getResources().getXml(id));

	}

	private static void parse(XmlPullParser xml) {
		int eventType = -1;

		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				handleStartTag(xml);
			} else if (eventType == XmlPullParser.END_TAG) {
				// handleEndTag(xml);
			}
			try {
				eventType = xml.next();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void handleStartTag(XmlPullParser xml) {
		String strNode = xml.getName();
		if (strNode.equals("address")) {
			String addrType = xml.getAttributeValue(null, "name");
			if (addrType.equals("html_url_list"))
				configInfo.put("html_url_list", xml.getAttributeValue(null, "value"));
			else if (addrType.equals("app_url_download"))
				configInfo.put("app_url_download", xml.getAttributeValue(null, "value"));
			else if (addrType.equals("html_url_download"))
				configInfo.put("html_url_download", xml.getAttributeValue(null, "value"));
			else if (addrType.equals("app_url_list"))
				configInfo.put("app_url_list", xml.getAttributeValue(null, "value"));
			else if (addrType.equals("app_url_content"))
				configInfo.put("app_url_content", xml.getAttributeValue(null, "value"));
		} else if (strNode.equals("wxapp-id"))
			configInfo.put("wxapp-id", xml.getAttributeValue(null, "id"));
		else if (strNode.equals("wxapp-secret"))
			configInfo.put("wxapp-secret", xml.getAttributeValue(null, "secret"));
		else if (strNode.equals("app-update-time"))
			configInfo.put("app-update-time", xml.getAttributeValue(null, "time"));
		else if (strNode.equals("html-update-time"))
			configInfo.put("html-update-time", xml.getAttributeValue(null, "time"));
		else if (strNode.equals("app-guide"))
			configInfo.put("app-guide", xml.getAttributeValue(null, "name"));
		else if (strNode.equals("class-home"))
			configInfo.put("class-home", xml.getAttributeValue(null, "path"));
		
		
	}
}
