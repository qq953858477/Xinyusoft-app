package com.xinyusoft.xshelllib.tools.baidumap;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 百度地图地位
 *
 * @author zzy
 */
public class BaiduMapUtil implements BDLocationListener {
    /********
     * 百度地图 start
     **********/
    private LocationClient mLocationClient;
    private Vibrator mVibrator;
    private Context context;
    private JSONObject a;

    /******** 百度地图 end **********/
    /**
     * 初始化地图
     *
     * @param context
     */
    public void initBaiduMap(Context context) {
        this.context = context;
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(this);

        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        initLocation();
    }

    /**
     * 定位
     *
     * @param listener
     */
    public void queryLocation(ReceiveLocationListener listener) {
        this.listener = listener;
        mLocationClient.start();
        mLocationClient.requestLocation();
    }

    ReceiveLocationListener listener;

    /**
     * 百度地图配置
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        // option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系，
        // option.setScanSpan(span);//
        // 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);// 可选，默认false,设置是否使用gps
        option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        mLocationClient.setLocOption(option);
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        //？？？失败？， 不开定位？
        JSONObject json = new JSONObject();
        try {
            json.put("latitude", location.getLatitude());
            json.put("lontitude", location.getLongitude());
            json.put("addr", location.getCity());
            Log.i("zzy", ":location.getCity()" + location.getCity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listener.onReceiveLocationListener(json);
        mLocationClient.stop();
    }


    public interface ReceiveLocationListener {
        void onReceiveLocationListener(JSONObject json);
    }
}
