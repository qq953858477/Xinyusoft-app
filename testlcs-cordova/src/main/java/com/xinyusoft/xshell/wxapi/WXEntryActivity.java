package com.xinyusoft.xshell.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.xinyusoft.xshelllib.tools.wxlogin.WeixinUtil;
import com.xinyusoft.xshelllib.utils.ParseConfig;

import java.util.Map;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("zzy", "zzy!!!!!!!:" + WeixinUtil.getInstance().getWeixinApi());
		try {
			if(WeixinUtil.getInstance().getWeixinApi() == null) {
				Map<String, String> configInfo = ParseConfig.getInstance(this).getConfigInfo();
				WeixinUtil.getInstance().initWeixinApi(this, configInfo.get("wxapp-id"));
			}
			WeixinUtil.getInstance().getWeixinApi().handleIntent(getIntent(), this);
		} catch(Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "app的登录码和微信登录不是同一个账号！", Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	public void onReq(BaseReq req) {
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.i("zzy", "进入onResp:"+resp.errCode);
		switch(resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				if(WeixinUtil.getInstance().isWXLogin()){
					SendAuth.Resp sendResp = (SendAuth.Resp) resp;
					WeixinUtil.getInstance().saveWXCode(sendResp.code);
					Toast.makeText(this, "请稍等~", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(this, "分享成功!!", Toast.LENGTH_LONG).show();
				}
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				WeixinUtil.getInstance().setIsWXLogin(false);
				Toast.makeText(this, "取消!", Toast.LENGTH_LONG).show();
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				Toast.makeText(this, "被拒绝", Toast.LENGTH_LONG).show();
				break;
			default:
				Toast.makeText(this, "失败!!", Toast.LENGTH_LONG).show();
				break;
		}
		//AppsActivity.isWXLogin=false;
		finish();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		WeixinUtil.getInstance().getWeixinApi().handleIntent(intent, this);
		finish();
	}
	
}
