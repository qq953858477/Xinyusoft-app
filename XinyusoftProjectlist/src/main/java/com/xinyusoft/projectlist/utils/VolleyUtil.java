package com.xinyusoft.projectlist.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by zzy on 2016/2/21.
 * volley工具类
 */
public class VolleyUtil {
    public static RequestQueue queue;

    public static RequestQueue getRequestQueue(Context context){
        if(queue == null){
            queue = Volley.newRequestQueue(context);
        }
        return queue;
    }

}
