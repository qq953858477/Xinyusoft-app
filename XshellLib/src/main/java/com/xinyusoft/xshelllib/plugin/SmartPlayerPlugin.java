package com.xinyusoft.xshelllib.plugin;

import android.content.Intent;
import android.os.Bundle;

import com.bokecc.live.demo.LiveReplayActivity;
import com.bokecc.sdk.mobile.live.Exception.DWLiveException;
import com.bokecc.sdk.mobile.live.pojo.TemplateInfo;
import com.bokecc.sdk.mobile.live.replay.DWLiveReplay;
import com.bokecc.sdk.mobile.live.replay.DWLiveReplayLoginListener;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;

/**
 * Created by zzy on 2016/5/22.
 * <p>
 * 视频(cc视频)播放插件
 */
public class SmartPlayerPlugin extends CordovaPlugin {


    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) {
        if ("startSmartPlayer".equals(action)) {
            String password = null;
            String userId = "C5294E13BF60D950";
            String viewerName = "";
            String roomId = "EB5DE875BB9EA2CC9C33DC5901307461";
            String currentLiveId = "B19B3808027C095C";
            DWLiveReplay dwLiveReplay = DWLiveReplay.getInstance();
            if (password == null || "".equals(password)) {
                dwLiveReplay.setLoginParams(dwLiveReplayLoginListener, userId, roomId, currentLiveId, viewerName);
            } else {
                dwLiveReplay.setLoginParams(dwLiveReplayLoginListener, userId, roomId, currentLiveId, viewerName, password);
            }
            dwLiveReplay.startLogin();
        }
        return false;
    }


    private DWLiveReplayLoginListener dwLiveReplayLoginListener = new DWLiveReplayLoginListener() {

        @Override
        public void onLogin(TemplateInfo templateInfo) {
            Intent intent = new Intent(cordova.getActivity(), LiveReplayActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("chat", templateInfo.getChatView());
            bundle.putString("pdf", templateInfo.getPdfView());
            bundle.putString("qa", templateInfo.getQaView());
            intent.putExtras(bundle);

            cordova.getActivity().startActivity(intent);
        }

        @Override
        public void onException(DWLiveException exception) {
//            Message msg = new Message();
//            msg.what = -2;
//            msg.obj = exception.getMessage();
//            mHandler.sendMessage(msg);
        }
    };
}
