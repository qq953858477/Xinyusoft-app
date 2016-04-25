package com.xinyusoft.xshelllib.ui;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.xinyusoft.xshelllib.R;
import com.xinyusoft.xshelllib.application.AppConfig;
import com.xinyusoft.xshelllib.application.AppConstants;
import com.xinyusoft.xshelllib.application.AppContext;
import com.xinyusoft.xshelllib.utils.Assets2DataCardUtil;
import com.xinyusoft.xshelllib.utils.FileUtil;
import com.xinyusoft.xshelllib.utils.FulStatusBarUtil;
import com.xinyusoft.xshelllib.utils.ParseConfig;
import com.xinyusoft.xshelllib.utils.PreferenceUtil;
import com.xinyusoft.xshelllib.utils.TimeUtil;
import com.xinyusoft.xshelllib.utils.VersionUtil;
import com.xinyusoft.xshelllib.utils.Write2SDCard;
import com.xinyusoft.xshelllib.utils.ZIPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 初始界面（包括下载，更新等初始化）
 *
 * @author zzy
 */
public class LoadingActivity extends Activity {
    private static final String TAG = "LoadingActivty";
    private Context context;
    private Handler mHandler;
    /**
     * 信鸽的推送过来的webIndex
     */
    private String webIndex;
    /**
     * 是否退出
     */
    private boolean isExit = false;
    /**   */
    private TextView showMessage;
    /**
     * 更新
     */
    private static final int UPDATE_MESSAGE = 222;
    private static final int SHOW_MESSAGE = 223;
    private static final int HIDE_MESSAGE = 224;

    private final static int SWITCH_TWOACTIVITY = 1000; // 主页
    private final static int SWITCH_GUIDACTIVITY = 1001; // 滑动手势
    public static String imageurl;
    private HttpUtils http;
    /**
     * 解析的配置文件集合
     */
    private Map<String, String> configInfo;
    /**
     * 主线程
     */
    private Thread mainThread;

    private ProgressDialog dialog;
    // 程序的开始时间
    private long startTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FulStatusBarUtil.setcolorfulStatusBar(this);

        Intent i_getvalue = getIntent();
        String action = i_getvalue.getAction();
        startTime = System.currentTimeMillis();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = i_getvalue.getData();
            if (uri != null) {
                String name = uri.getQueryParameter("name");
                String age = uri.getQueryParameter("age");
                Log.i("zzy", "name:" + name);
                Log.i("zzy", "age:" + age);
            }
        }

        setContentView(R.layout.xinyusoft_activity_splash);
        final LinearLayout splash_main = (LinearLayout) findViewById(R.id.splash_main);

        // 删除前台的检测更新包
        String fileName = FileUtil.getInstance().getFilePathInSDCard(AppConstants.XINYUSOFT_CACHE, "backgroundupdatehtml5.zip");
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }

        // 这个jumpurl是信鸽推送过来的url
        if (getIntent().hasExtra("jumpurl")) {
            webIndex = getIntent().getStringExtra("jumpurl");

        }
        Log.e("webindex=", getIntent().getStringExtra("jumpurl") + "");

        init();
        System.setProperty("http.keepAlive", "false");
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);

        mainThread = new Thread() {
            @Override
            public void run() {
                // -1解析appconfig
                configInfo = ParseConfig.getInstance(LoadingActivity.this).getConfigInfo();

                final int resid = getResources().getIdentifier(configInfo.get("app-guide"), "drawable", getPackageName());

                runOnUiThread(new Runnable() {
                    public void run() {
                        splash_main.setBackgroundResource(resid);
                        // splash_main.setBackgroundResource(R.drawable.xinyusoft_gesture_guide_pic);
                    }
                });
                if (AppConfig.DEBUG) {
                    Log.i("zzy", "VersionUtil.getVersionCode(context):" + VersionUtil.getVersionCode(context));
                    Log.i("zzy", "getAppThisCode:" + PreferenceUtil.getInstance().getAppThisCode());
                }

                // 判断是否覆盖安装了，是的话，重新解压并且重新设置时间
                if (VersionUtil.getVersionCode(context) > PreferenceUtil.getInstance().getAppThisCode()) {

                    //-1 先把引导页的图片都删除完（如果有的话）
                    File mImageFile = new File(getFilesDir() + "/imagepage/android");
                    if (mImageFile.exists()) {
                        File[] files = mImageFile.listFiles();
                        if (files.length > 0) {
                            for (File mFile:files) {
                                mFile.delete();
                            }
                        }

                    }
                    // 0 首次运行拷贝assets 文件并解压

                    PreferenceUtil.getInstance().setAppThisCode(VersionUtil.getVersionCode(context));
                    // 只需要第一次设置更新时间
                    PreferenceUtil.getInstance().setAppUpdateTime(configInfo.get("app-update-time"));
                    PreferenceUtil.getInstance().setFileUpdateTime(configInfo.get("html-update-time"));
                    // 保存homeActivity的path，由此获得它的Class
                    PreferenceUtil.getInstance().setHomeActivityPath(configInfo.get("class-home"));
                    PreferenceUtil.getInstance().setFIRSTRUN(true);
                    // 将Assets中数据写入data/data中
                    File zip = null;
                    try {
                        zip = Assets2DataCardUtil.write2DataFromInput("www.zip", "www.zip", AppContext.CONTEXT);
                        ZIPUtils.unzip(zip.getAbsolutePath(), AppContext.CONTEXT.getFilesDir().getAbsolutePath());
                        // 修改所有的文件为只读权限
                        // FileUtil.getInstance().setReadOnlyFiles(getFilesDir().getAbsolutePath());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 解压之后就删除
                    if (zip != null) {
                        if (zip.exists()) {
                            zip.delete();
                        }
                    }
                    if (AppConfig.DEBUG)
                        Log.e("zzy", "第一次解压完成！!!!!!!:");
                    // 把phonegap的 cordova.js自己加入到html5中，现在是必定覆盖
                    copyCordova();

                }
                // 检查app更新
                isUpdate();
                // checkH5();
            }

            ;
        };
        /**
         * 重要（可以不打开，也能通过）：在上线百度的时候，需要打开此项，（因为百度强制要用他们的自动更新，以及sdk 可能会有安全问题），在小米平台疑似使用不了
         *
         */
        // BDAutoUpdateSDK.uiUpdateAction(this, new MyUICheckUpdateCallback());

        mainThread.start();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle result = data.getBundleExtra("result");
        resultCode++;
        if (result != null && !TextUtils.isEmpty(result.getString(AccountManager.KEY_AUTHTOKEN))) {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 把Cordova这个数据库拷贝到data/data/《包名》/files下
     */
    private void copyCordova() {
        try {
            FileUtil.getInstance().createDirInDir(getFilesDir(), "js");

            File file = new File(getFilesDir(), "js/cordova.js");
            if (file.exists() && file.length() > 0) {
                return;
            }
            InputStream is = getAssets().open("cordova.js");

            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            is.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查html5更新
     */
    private void checkH5() {
        // 没有写下载列表就直接跳过这个
        if (configInfo.get("html_url_list") == null || PreferenceUtil.getInstance().getFileUpdateTime() == null || "".equals(configInfo.get("html_url_list"))
                || "".equals(PreferenceUtil.getInstance().getFileUpdateTime())) {
            jump();
            return;
        }
        String url = configInfo.get("html_url_list") + PreferenceUtil.getInstance().getFileUpdateTime() + "&platform=Android" + "&xshllversion="
                + VersionUtil.getVersionCode(context);
        http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    if (AppConfig.DEBUG) {
                        Log.i("zzy", "开始检查html5:" + responseInfo.result);
                    }

                    toJsonFile(responseInfo.result);
                    if (AppConfig.WIRTE_SDCARD)
                        Write2SDCard.getInstance().writeMsg("start checked html5  end:" + responseInfo.result);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (AppConfig.WIRTE_SDCARD)
                        Write2SDCard.getInstance().writeMsg("start checked html5 error！");
                    if (AppConfig.WIRTE_SDCARD)
                        Write2SDCard.getInstance().writeMsg(e.toString());
                    jump();
                }

            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                Log.i(TAG, "检查html5失败:");
                Toast.makeText(LoadingActivity.this, "没有可用的网络连接", Toast.LENGTH_SHORT).show();
                jump();

            }
        });
    }

    /**
     * 下载html5
     *
     * @param res json的字符串
     * @throws JSONException
     */
    private void toJsonFile(String res) throws JSONException {
        JSONObject json = new JSONObject(res);
        JSONObject op = json.getJSONObject("op");
        String code = op.getString("code");

        final int count = json.getInt("count");
        final String changezip = json.getString("changezip");

        JSONArray array = json.getJSONArray("changelist");
        for (int i = 0; i < array.length(); i++) {
            JSONObject html = array.getJSONObject(i);
            Log.i("zzy", "pathzzzz:" + html.getString("path"));
            String path = html.getString("path");
            String status = html.getString("status");
            if ("DELETE".equals(status)) {  //找到带有删除状态的并且删除
                File file = new File(getFilesDir() + path.replaceAll("\\\\","/"));
                Log.i("zzy","filePath:"+file);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        //执行完之后，设置可以显示引导页，等待下次安装覆盖的时候来显示
        PreferenceUtil.getInstance().setShowGuidePage(true);

        if (code.equals("Y")) {// 检查更新成功
            if (count != 0) {// 有更新
                Write2SDCard.getInstance().writeMsg("有更新，前面的time:" + PreferenceUtil.getInstance().getFileUpdateTime());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d("changeFileDao.addItem" + TimeUtil.now());
                        showMessageUsehandler("请等待...");
                        zipUpdateH5(changezip);
                        if (AppConfig.WIRTE_SDCARD) {
                            Write2SDCard.getInstance().writeMsg("update html count :" + count);

                        }
                    }
                }).start();
            } else {// 没有更新
                if (AppConfig.DEBUG)
                    Log.e("zzy", "没有更新，直接跳转:" + PreferenceUtil.getInstance().getFileUpdateTime());
                if (AppConfig.WIRTE_SDCARD)
                    Write2SDCard.getInstance().writeMsg("没有更新，直接跳转:" + PreferenceUtil.getInstance().getFileUpdateTime());
                jump();
            }
        } else {
            if (AppConfig.DEBUG)
                Log.e("zzy", "数据异常！code不为Y！");
            // 就算数据异常都是跳过
            jump();
        }
    }

    /**
     * 下载app
     */
    private void downloadApp(final String zipName) {
        mHandler.sendEmptyMessage(SHOW_MESSAGE);
        PreferenceUtil.getInstance().setDownAppDir(FileUtil.getInstance().getFilePathInSDCard(AppConstants.XINYUSOFT_CACHE, AppConstants.APP_APK_NAME));
        final String file = FileUtil.getInstance().getFilePathInSDCard(AppConstants.XINYUSOFT_CACHE, AppConstants.APP_APK_NAME);
        String url = configInfo.get("app_url_download");
        http.download(url, file, false, true, new RequestCallBack<File>() {

            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {

                if (AppConfig.WIRTE_SDCARD)
                    Write2SDCard.getInstance().writeMsg("下载app");

                // 设置xversion的最新更新的时间
                String time = zipName.split("-")[1].split("\\.")[0];
                // 设置应当升级
                PreferenceUtil.getInstance().setNextToInstall(true);
                PreferenceUtil.getInstance().setAppUpdateTime(time);
                openApp();
                LoadingActivity.this.finish();

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(context, "下载失败，请重试！", Toast.LENGTH_SHORT).show();
                if (AppConfig.WIRTE_SDCARD)
                    Write2SDCard.getInstance().writeMsg("下载app失败");
                checkH5();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                float allFileSize = (float) (Math.round(((float) (total * 1.0 / 1000000)) * 10) / 10.0);
                float currentFileSize = (float) (Math.round(((float) (current * 1.0 / 1000000)) * 10) / 10.0);
                showMessageUsehandler("APP下载进度：" + currentFileSize + "M /" + allFileSize + "M");
            }
        });
    }

    private void showMessageUsehandler(String showString) {
        Message msg = Message.obtain();
        msg.what = UPDATE_MESSAGE;
        msg.obj = showString;
        mHandler.sendMessage(msg);

    }

    private void init() {
        this.context = LoadingActivity.this;
        showMessage = (TextView) findViewById(getResources().getIdentifier("showLoadingMessage", "id", getPackageName()));
        http = new HttpUtils();
        http.configTimeout(8000);
        http.configRequestRetryCount(0);
        http.configSoTimeout(8000);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case UPDATE_MESSAGE:
                        showMessage.setText((String) msg.obj);
                        break;
                    case SHOW_MESSAGE:
                        showMessage.setVisibility(View.VISIBLE);
                        break;
                    case HIDE_MESSAGE:
                        showMessage.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        };

    }

    private void openApp() {
        Intent openIntent = new Intent();
        openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openIntent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(PreferenceUtil.getInstance().getDownAppDir()));
        openIntent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(openIntent);
    }

    /**
     * 跳转
     */
    private void jump() {
        PreferenceUtil.getInstance().setDownloadingFile(false);// 文件下载中状态
        mHandler.sendEmptyMessage(HIDE_MESSAGE);
        // Intent intent = new Intent(LoadingActivity.this, MyTest.class);
        // if (webIndex != null) {
        // intent.putExtra("jumpurl", webIndex);
        // }
        // startActivity(intent);
        // LoadingActivity.this.finish();
        // 获取当前时间，然后进行2秒之后进入
        long endTime = System.currentTimeMillis();
        long dTime = endTime - startTime;
        // 2000
        if (dTime < 2000) {
            try {
                Thread.sleep(2000 - dTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        File file = new File(this.getFilesDir().getAbsolutePath() + "/imagepage/android");
        if (file.exists()) {
            checkIsFirst();
        } else {
            try {
                Intent intent = new Intent(LoadingActivity.this, Class.forName(configInfo.get("class-home")));
                if (webIndex != null) {
                    intent.putExtra("jumpurl", webIndex);
                }
                startActivity(intent);
                LoadingActivity.this.finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    // 检测是否是第一次开启app
    public void checkIsFirst() {

        imageurl = this.getFilesDir().getAbsolutePath() + "/imagepage/android";

        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        Log.e("-----bool-----", PreferenceUtil.getInstance().hadFIRSTRUN() + "");
        if (PreferenceUtil.getInstance().hadFIRSTRUN() && PreferenceUtil.getInstance().isShowGuidePage())
            mmHandler.sendEmptyMessageDelayed(SWITCH_GUIDACTIVITY, 0);
        else
            mmHandler.sendEmptyMessageDelayed(SWITCH_TWOACTIVITY, 0);
    }

    private Handler mmHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SWITCH_TWOACTIVITY:
                    Intent intent;
                    try {
                        intent = new Intent(LoadingActivity.this, Class.forName(configInfo.get("class-home")));
                        if (webIndex != null) {
                            intent.putExtra("jumpurl", webIndex);
                        }
                        startActivity(intent);

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case SWITCH_GUIDACTIVITY:
                    PreferenceUtil.getInstance().setFIRSTRUN(false);
                    PreferenceUtil.getInstance().setShowGuidePage(false);
                    Intent intents = new Intent();
                    if (webIndex != null) {
                        intents.putExtra("jumpurl", webIndex);
                    }
                    intents.setClass(LoadingActivity.this, GuideActivity.class);
                    LoadingActivity.this.startActivity(intents);
                    break;
            }
            LoadingActivity.this.finish();

        }

        ;
    };

    @Override
    public void onBackPressed() {
        if (isExit == false) {
            isExit = true;
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            File file = new File(FileUtil.getInstance().getFilePathInSDCard(AppConstants.XINYUSOFT_CACHE, AppConstants.APP_APK_NAME));
            if (file.exists()) {
                file.delete();
            }
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 显示更新dialog
     */
    protected void showappDialog(final String zip) throws Exception {
        // 这个是更新的内容提示
        String url = configInfo.get("app_url_content");
        http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {

            }

            @Override
            public void onSuccess(ResponseInfo<String> res) {

                JSONObject json;
                try {
                    json = new JSONObject(res.result);
                    Log.i("zzy", "json:" + json.toString());
                    JSONObject op = json.getJSONObject("op");
                    String code = op.getString("code");
                    String content = json.getString("content");
                    JSONObject contentJson = new JSONObject(content);
                    content = contentJson.getString("info");
                    content = content.replaceAll("///", "\n");
                    if (code.equals("Y")) {// 检查更新成功
                        Builder builder = new Builder(LoadingActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                        builder.setMessage(content);
                        builder.setTitle("更新提示");
                        builder.setCancelable(false);
                        builder.setPositiveButton("立即更新", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                downloadApp(zip);
                            }
                        });
                        builder.setNegativeButton("下次再说", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                checkH5();
                            }
                        });
                        builder.create().show();
                    } else {
                        checkH5();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    checkH5();
                }
            }

        });
    }

    /**
     * 检查app更新
     */
    private void isUpdate() {
        // 是否更新app
        String list = configInfo.get("app_url_list");
        if (list == null || PreferenceUtil.getInstance().getAppUpdateTime() == null || "".equals(PreferenceUtil.getInstance().getAppUpdateTime()) || "".equals(list)) {
            checkH5();
            return;
        }
        final String url = list + PreferenceUtil.getInstance().getAppUpdateTime();
        http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onFailure(HttpException arg0, String arg1) {
                Toast.makeText(LoadingActivity.this, "检查app失败，请下次网络好的情况下检查", Toast.LENGTH_SHORT).show();
                checkH5();
            }

            @Override
            public void onSuccess(ResponseInfo<String> res) {
                JSONObject json;
                try {
                    json = new JSONObject(res.result);
                    JSONObject op = json.getJSONObject("op");
                    String code = op.getString("code");
                    if (code.equals("Y")) {// 检查更新成功
                        final String changezip = json.getString("changezip");
                        int count = json.getInt("count");
                        if (count != 0) {// 有更新
                            Log.i("zzy", "有更新:" + url);
                            showappDialog(changezip);
                        } else {
                            checkH5();
                        }
                    } else {
                        checkH5();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    checkH5();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });

    }

    /**
     * 用zip的方式更新html5
     *
     * @param changezip zip的名字
     */
    private void zipUpdateH5(String changezip) {
        try {
            if (AppConfig.DEBUG)
                Log.i("zzy", "changezip:" + changezip);
            final String time = changezip.split("-")[1].split("\\.")[0];
            if (AppConfig.DEBUG)
                Log.i("zzy", "time:" + time);
            if (changezip != null && !"".equals(changezip)) {
                changezip = changezip.replaceAll("\\\\", "/");
                if (changezip.startsWith("/")) {
                    changezip = changezip.substring(1);
                }
            } else {
                if (AppConfig.WIRTE_SDCARD) {
                    Write2SDCard.getInstance().writeMsg("changezip error--" + changezip + "--");
                }
                jump();
                return;
            }

            String url = configInfo.get("html_url_download") + changezip;
            Log.i("zzy", "myhtml5url:" + url);
            final String file = FileUtil.getInstance().getFilePathInSDCard(AppConstants.XINYUSOFT_CACHE, "updatehtml5.zip");

            if (AppConfig.WIRTE_SDCARD) {
                Write2SDCard.getInstance().writeMsg("start download html");
                Write2SDCard.getInstance().writeMsg(url);
            }

            http.download(url, file, false, true, new RequestCallBack<File>() {

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    try {
                        ZIPUtils.unzipTest(file, AppContext.CONTEXT.getFilesDir().getAbsolutePath());

                        PreferenceUtil.getInstance().setFileUpdateTime(time);// 设置上次更新时间（设置最新版本号）
                        // 修改所有的文件为只读权限 （zip下载不能单个的设置只读）
                        // FileUtil.getInstance().setReadOnlyFiles(getFilesDir().getAbsolutePath());
                        if (AppConfig.DEBUG)
                            Log.e("zzy", "downdoading Html5 success  time=" + time);

                        jump();
                        if (AppConfig.WIRTE_SDCARD) {
                            Write2SDCard.getInstance().writeMsg("Html5 success time=" + time);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        if (AppConfig.WIRTE_SDCARD) {
                            Write2SDCard.getInstance().writeMsg("unzip Html5 error!!!");
                            Write2SDCard.getInstance().writeMsg(e.toString());
                        }

                    }
                }

                @Override
                public void onFailure(HttpException error, String msg) {
                    Toast.makeText(context, "下载失败，请重试！", Toast.LENGTH_SHORT).show();
                    jump();
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    float allFileSize = (float) (Math.round(((float) (total * 1.0 / 1000000)) * 10) / 10.0);
                    float currentFileSize = (float) (Math.round(((float) (current * 1.0 / 1000000)) * 10) / 10.0);
                    showMessageUsehandler("请稍等..." + currentFileSize + "M /" + allFileSize + "M");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (AppConfig.WIRTE_SDCARD) {
                Write2SDCard.getInstance().writeMsg("zipUpdateH5() error!!!");
                Write2SDCard.getInstance().writeMsg(e.toString());
            }
            if (AppConfig.DEBUG) {
                Log.i("zzy", "e.toString():" + e.toString());
            }
            jump();
        }

    }

    public String getImageurl() {
        return imageurl;
    }


    /**
     * 检查是否有引导页更新，
     */
    private void checkShowGuidePage() {
        {
            // 没有写下载列表就直接跳过这个
            if (configInfo.get("html_url_list") == null || PreferenceUtil.getInstance().getFileUpdateTime() == null || "".equals(configInfo.get("html_url_list"))
                    || "".equals(PreferenceUtil.getInstance().getFileUpdateTime())) {
                jump();
                return;
            }
            String url = configInfo.get("html_url_list") + PreferenceUtil.getInstance().getFileUpdateTime() + "&platform=Android" + "&xshllversion="
                    + VersionUtil.getVersionCode(context);
            http.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {

                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    try {
                        JSONObject json = new JSONObject(responseInfo.result);
                        JSONArray array = json.getJSONArray("changelist");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject html = array.getJSONObject(i);
                            Log.i("zzy", "path:" + html.getString("path"));
                            String path = html.getString("path");
                            if (path.contains("\\\\imagepage\\\\android")) {
                                String status = html.getString("status");
                                if ("DELETE".equals(status)) {  //如果状态为删除，就直接删除了。
                                    File file = new File(getFilesDir() + "/imagepage/android");
                                    //file.delete();
                                }
                                PreferenceUtil.getInstance().setShowGuidePage(true);
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (AppConfig.WIRTE_SDCARD)
                            Write2SDCard.getInstance().writeMsg("start checked html5 error！");
                        if (AppConfig.WIRTE_SDCARD)
                            Write2SDCard.getInstance().writeMsg(e.toString());
                        jump();
                    }

                }

                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    Log.i(TAG, "检查html5失败:");
                    Toast.makeText(LoadingActivity.this, "没有可用的网络连接", Toast.LENGTH_SHORT).show();
                    jump();

                }
            });
        }
    }

    // private class MyUICheckUpdateCallback implements UICheckUpdateCallback {
    //
    // @Override
    // public void onCheckComplete() {
    // dialog.dismiss();
    // if (AppConfig.DEBUG)
    // Log.e("zzy", "点击了百度！！！！！:");
    // mainThread.start();
    // }
    //
    // }

}
