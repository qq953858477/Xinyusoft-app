package com.xinyusoft.xshelllib.plugin;

import android.content.Intent;

import com.bokecc.live.demo.LoginActivity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

/**
 * Created by zzy on 2016/5/24.
 */
public class CCPlayerPlugin extends CordovaPlugin {


    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {

        if ("CCVideoPlayBack".equals(action)) {
            String roomId = args.getString(0);
            String liveId = args.getString(1);
            Intent intent = new Intent(cordova.getActivity(), LoginActivity.class);
            intent.putExtra("roomId",roomId);
            intent.putExtra("liveId", liveId);
            cordova.getActivity().startActivity(intent);
            return true;
        } else if("CCVideoLive".equals(action)) {
            String roomId = args.getString(0);
            Intent intent = new Intent(cordova.getActivity(), LoginActivity.class);
            intent.putExtra("roomId",roomId);
            cordova.getActivity().startActivity(intent);
            return true;
        }
        return false;
    }
}
