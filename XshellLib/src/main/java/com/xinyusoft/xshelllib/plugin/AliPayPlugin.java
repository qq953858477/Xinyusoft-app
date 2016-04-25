package com.xinyusoft.xshelllib.plugin;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.xinyusoft.xshelllib.aliPay.PayResult;
import com.xinyusoft.xshelllib.aliPay.SignUtils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AliPayPlugin extends CordovaPlugin {

	// 商户PID
	public static final String PARTNER = "2088512726019953";
	// 商户收款账号
	public static final String SELLER = "xuyue@xinyusoft.com";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALu9rVOVkN+c2VqB"
			+ "zlm907jN41D1lJxTTsVnzWfrkxCHxTsMhW5yMUSONtfjKAPDfEdVcbspRNIL9kT4"
			+ "0ug+eIzpO6/w3ASL7NOBXb+zOdnLZN8ibesmrgcZiBW3v6bTwT1y79mn1u5AJ6do"
			+ "dls5RD4vXmPUKZFWW6wXGJr7WS/XAgMBAAECgYBIK1T6wY8MdrYTFb0mQFOoQfU1"
			+ "RQJYyamJNyr93NAO1bJazahpWZpY2TZ57QeP0tDudwkeQnIDMLf9sY0Z3BHopygY"
			+ "GQWOtm5WkGpooqMcihrOKuAZrp/toB9ic9OGJ/E0RWfUOEJOU+/eQYAp5owqqm1h"
			+ "pIwb3tkxWqSLEmea8QJBAPoYs0bEA3190LJbSnS5JtL5IjFu0wG3X3u5hqm6xBQK"
			+ "mX9bdBJLd8HlYfRsw4PHuVOnHtFqdovkYyaKI3iQZp8CQQDALCuhRFJEg027dYRI"
			+ "ZHqKhSjUcJVBl7FHkM+16JNps9A4G7EIZT1CyAQE79wHKg5JiASEHaIiDtJ1J9kk"
			+ "w0PJAkEAsWZJ7H1nmGFhYtJcqxUWk/oCJhxdck83XJTKD6UaJrkqDmu5lqyPgysJ"
			+ "Y46u1NAcBll6A8PH2Q0kw5ai+Ic9MwJAbRAkas/liLCkSwF048jyqu7Lr1V9v84h"
			+ "dKZA29J05wZ/43gDzun2DJKLhWnDi+VYWm7xcTGINKAI3SiSWgTf0QJBAJ92aPdx"
			+ "DO8vV5f0/sCbd2ydYh6XFW40P+k5N8aXrvl5xUBRz4kHbYQma6hgeo656Gpd/FM9"
			+ "INJBftSEnz2p5Uw=";

	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;
	
	private String callbackName = "";

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(cordova.getActivity(), "支付成功",
							Toast.LENGTH_SHORT).show();
					  		webView.loadUrl("javascript:"+callbackName+"("+1+")");
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(cordova.getActivity(), "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(cordova.getActivity(), "支付失败",
								Toast.LENGTH_SHORT).show();
						webView.loadUrl("javascript:"+callbackName+"("+0+")");

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(cordova.getActivity(), "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};
	String payInfo;
	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		// TODO Auto-generated method stub

		try {

			if ("aliPay".equals(action)) {
//				JSONObject jso = args.getJSONObject(0);
//s				String orderInfo = getOrderInfo(jso);
//				String sign = sign(orderInfo);
//				try {
//					// 仅需对sign 做URL编码
//					sign = URLEncoder.encode(sign, "UTF-8");
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}

				// 完整的符合支付宝参数规范的订单信息
//				final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
//						+ getSignType();
				
				//content + "&sign=\"" +sign+"\"&sign_type=\"RSA\""
				String data = args.getString(0);
				JSONObject json = new JSONObject(data);
				String orderinfo=getOrderInfo(json);
				callbackName = args.getString(1);
				Runnable payRunnable = new Runnable() {

					@Override
					public void run() {
						// 构造PayTask 对象
						PayTask alipay = new PayTask(cordova.getActivity());
						// 调用支付接口，获取支付结果
						String result = alipay.pay(payInfo,true);

						Message msg = new Message();
						msg.what = SDK_PAY_FLAG;
						msg.obj = result;
						mHandler.sendMessage(msg);
					}
				};

				// 必须异步调用
				Thread payThread = new Thread(payRunnable);
				payThread.start();
			}

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

		return false;

	}

	public String getOrderInfo(JSONObject json) {
		String orderInfo = "";
		
		try {
		// 签约合作者身份ID
		 orderInfo = "partner=" + "\"" + json.getString("partner") + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + json.getString("seller_id") + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + json.getString("orderid") + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + json.getString("subject") + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + json.getString("body") + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + json.getString("orderamount") + "\"";

		// 服务器异步通知页面路径
		String notify = json.getString("notify_url").replaceAll("\\\\/\\\\/", "//");
		orderInfo += "&notify_url=" + "\"" + notify
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\""+json.getString("service")+"\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\""+json.getString("payment_type")+"\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\""+json.getString("_input_charset")+"\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\""+json.getString("it_b_pay")+"\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		//orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";
		}catch (Exception e) {
			// TODO: handle exception
		}

		return orderInfo;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
