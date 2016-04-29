package com.xinyusoft.projectlist;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.xinyusoft.projectlist.greendao.AppBean;
import com.xinyusoft.projectlist.greendao.AppBeanDao;
import com.xinyusoft.projectlist.greendao.DaoMaster;
import com.xinyusoft.projectlist.greendao.DaoSession;
import com.xinyusoft.projectlist.utils.FileUtil;
import com.xinyusoft.projectlist.utils.VolleyUtil;
import com.xinyusoft.projectlist.utils.ZIPUtils;
import com.xinyusoft.xshelllib.ui.HomePageActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zzy on 2016/4/11.
 * 项目列表
 */
public class ProjectListActivity extends Activity {

    public static final String ENVIRONMENT = "http://testxv.xinyusoft.com";


    private RecyclerView mRecyclerView;

    private RequestQueue queue;
    private ArrayList<AppBean> mLists;
    private MyHomeAdapter myHomeAdapter;
    private HttpUtils http;

    private ProgressBar mProgressBar;

    //下载的旋转动画
    private ObjectAnimator animator;

    /**
     * 是否有下载，程序只允许下载一个
     */
    private static boolean isShowDowndoading = true;

    private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
    protected ImageLoader imageLoader = ImageLoader.getInstance();

//    static class MyHandler extends Handler {
//        WeakReference<ProjectListActivity> mActivity;
//
//        MyHandler(ProjectListActivity activity) {
//            mActivity = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            ProjectListActivity theActivity = mActivity.get();
//            switch (msg.what) {
//                case 0:
//                    // msg.obj = (MyHomeAdapter.MyViewHolder)myViewHolder.
//                    break;
//            }
//        }
//    }
//
//    MyHandler ttsHandler = new MyHandler(this);
//    private void test() {
//        ttsHandler.sendEmptyMessage(0);
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xinyusoft_activity_pro_list);
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        mLists = new ArrayList<>();
        queue = VolleyUtil.getRequestQueue(this);

        initImageLoader();

        initView();
        initXUtils();
        initGreenDao();
        checkProjectList();
        copyCordovaFile();
    }

    private void initImageLoader() {
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder().showStubImage(R.drawable.ic_empty) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.ic_empty) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_launcher) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
//                .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象
    }

    private void initXUtils() {
        http = new HttpUtils();
        http.configTimeout(8000);
        http.configRequestRetryCount(0);
        http.configSoTimeout(8000);
    }



    /*↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓greenDAO↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓*/

    /**
     * 初始化greenDAO，正式的话需要放在application中
     */
    private void initGreenDao() {
        // 官方推荐将获取 DaoMaster 对象的方法放到 Application 层，这样将避免多次创建生成 Session 对象
        setupDatabase();
        // 获取 AppBeanDao 对象
        getAppBeanDao();

    }

    private SQLiteDatabase db;
    private DaoSession daoSession;
    private Cursor cursor;

    private void setupDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "apps-db", null);
        db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    private AppBeanDao getAppBeanDao() {
        return daoSession.getAppBeanDao();
    }
    /*↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑greenDAO↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑*/


    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.xinyusoft_project_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
    }

    /**
     * recycleView 适配器
     */
    class MyHomeAdapter extends RecyclerView.Adapter<MyHomeAdapter.MyViewHolder> {

        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        @Override
        public MyHomeAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new MyViewHolder(LayoutInflater.from(ProjectListActivity.this).inflate(
                    R.layout.xinyusoft_item_project_list, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(final MyHomeAdapter.MyViewHolder myViewHolder, final int i) {
            final AppBean appBean = mLists.get(i);
            myViewHolder.name.setText(appBean.getAppName());
            myViewHolder.charge.setText(appBean.getAuthor());
            myViewHolder.newVersion.setText("最新版:"+appBean.getNowVersion());
            myViewHolder.localVersion.setText("本地版:"+appBean.getLocalVersion());

            /**
             * 显示图片 参数1：图片url 参数2：显示图片的控件 参数3：显示图片的设置 参数4：监听器
             */
            imageLoader.displayImage(appBean.getFirstVersion(), myViewHolder.appImage, options, animateFirstListener);

            final boolean isDownLoading = appBean.getLocalVersion().equals(appBean.getNowVersion());
            if (isDownLoading) {
                myViewHolder.download.setText("进入");
            }

            myViewHolder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击之后，然后去下载或者更新
                    if (isShowDowndoading) {
                        isShowDowndoading = false;
                        if (appBean.getLocalVersion().equals(appBean.getNowVersion())) {  //如果相等就直接进入
                            isShowDowndoading = true;
                            Intent intent = new Intent(ProjectListActivity.this, HomePageActivity.class);
                            intent.putExtra("projectListUrl", appBean.getAppName());
                            ProjectListActivity.this.startActivity(intent);

                        } else {
                            //0.显示下载进度
                            myViewHolder.download.setVisibility(View.INVISIBLE);
                            myViewHolder.downloadRl.setVisibility(View.VISIBLE);

                            animator = ObjectAnimator.ofFloat(myViewHolder.download_img, "rotation", 0f, 360f);
                            animator.setRepeatCount(ValueAnimator.INFINITE);
                            animator.setDuration(1000);
                            animator.setInterpolator(new LinearInterpolator());
                            animator.start();

                            //1.联网下载
                            checkHtml5(appBean, i, myViewHolder);
                        }

                    } else {
                        Toast.makeText(ProjectListActivity.this, "已经有其他项目在下载，请稍等！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mLists.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView charge;
            TextView download;
            RelativeLayout downloadRl;
            TextView download_tv;
            ImageView download_img;
            TextView newVersion;
            TextView localVersion;
            ImageView appImage;
            public MyViewHolder(View itemView) {
                super(itemView);
                appImage = (ImageView) itemView.findViewById(R.id.xinyusoft_project_img);
                name = (TextView) itemView.findViewById(R.id.xinyusoft_project_name);
                charge = (TextView) itemView.findViewById(R.id.xinyusoft_project_charge);
                download = (TextView) itemView.findViewById(R.id.xinyusoft_project_download);
                downloadRl = (RelativeLayout) itemView.findViewById(R.id.xinyusoft_projectlist_rl);
                download_img = (ImageView) itemView.findViewById(R.id.xinyusoft_projectlist_download_img);
                download_tv = (TextView) itemView.findViewById(R.id.xinyusoft_projectlist_download_tv);
                newVersion = (TextView) itemView.findViewById(R.id.xinyusoft_project_now_version);
                localVersion = (TextView) itemView.findViewById(R.id.xinyusoft_project_local_version);
            }
        }
    }

    /**
     * 下载指定的html的zip文件，
     */
    private void checkHtml5(final AppBean appBean, final int i, final MyHomeAdapter.MyViewHolder myViewHolder) {
        final String url = ENVIRONMENT + "/ESBServlet?command=vms.getChangedListAction&appname=" + appBean.getAppName() + "&time=" + appBean.getLocalVersion();
        Log.i("zzy", "url:" + url);
        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject json = new JSONObject(s);
                    JSONObject op = json.getJSONObject("op");
                    String code = op.getString("code");

                    if (code.equals("Y")) {
                        if (json.getInt("count") > 0) {  //代表有更新
                            String changeZip = json.getString("changezip");
                            changeZip = changeZip.replaceAll("\\\\", "/");
                            String downUrl = ENVIRONMENT + "/xvfile/" + changeZip;
                            downloadHtml5(downUrl, appBean.getAppName(), i, myViewHolder);
                        } else {   //代表无更新 直接跳转
                            isShowDowndoading = true;
                            Intent intent = new Intent(ProjectListActivity.this, HomePageActivity.class);
                            intent.putExtra("projectListUrl", appBean.getAppName());
                            ProjectListActivity.this.startActivity(intent);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            private void downloadHtml5(String url, final String appName, int i, final MyHomeAdapter.MyViewHolder myViewHolder) {
                final String file = FileUtil.getInstance().getFilePathInSDCard("Xinyusoft/" + appName, appName + ".zip");
                http.download(url, file, false, true, new RequestCallBack<File>() {

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        Log.i("zzy", "responseInfo:" + responseInfo.result);
                        try {
                            ZIPUtils.unzipTest(file, ProjectListActivity.this.getFilesDir().getAbsolutePath() + "/" + appName);

                            //2.插入数据库的本地版本
                            if (appBean.getLocalVersion().equals("0")) {  //等于0代表没有 先插入,并且设置当地版本为最新的版本
                                appBean.setLocalVersion(appBean.getNowVersion());
                                getAppBeanDao().insert(appBean);

                                //解压cordova里面的相关文件
                                ZIPUtils.unzipTest(ProjectListActivity.this.getFilesDir().getAbsolutePath()+"/cordova_android.zip",ProjectListActivity.this.getFilesDir().getAbsolutePath() + "/" + appName+"/js");
                            } else {   //不等于0代表数据有数据，更新本地版本为最新的版本
                                appBean.setLocalVersion(appBean.getNowVersion());
                                getAppBeanDao().update(appBean);
                            }
                            //3. 显示进入
                            if (animator != null)
                                animator.cancel();
                            myViewHolder.download.setText("进入");
                            myViewHolder.download.setVisibility(View.VISIBLE);
                            myViewHolder.downloadRl.setVisibility(View.INVISIBLE);
                            isShowDowndoading = true;

                            myViewHolder.localVersion.setText("本地版:"+appBean.getNowVersion());

                            Intent intent = new Intent(ProjectListActivity.this, HomePageActivity.class);
                            intent.putExtra("projectListUrl", appBean.getAppName());
                            ProjectListActivity.this.startActivity(intent);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        myViewHolder.download_tv.setText((int) ((((float)current/(float)total))*100)+"%");

                    }
                });
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ProjectListActivity.this, "当前网络不可用，请稍候再试！", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }


    /**
     * 查询所有的应用
     */
    private void checkProjectList() {
        String url = ENVIRONMENT + "/ESBServlet?command=vms.selectAppAction";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String s) {
                try {
                    JSONObject json = new JSONObject(s);
                    if (json.has("applist")) {
                        JSONArray array = json.getJSONArray("applist");
                        AppBean appBean;
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject appJson = new JSONObject(array.getString(i));
                            String author = appJson.getString("charge");
                            String nowVersion = appJson.getString("nowversion");
                            //String firstVersion = appJson.getString("firstversion");
                            String appName = appJson.getString("name");

                            //注意，这个本来是获取第一次的版本，不过没有用，现在弄成获取logo的url
                            String firstVersion = ENVIRONMENT+"/xvfile/"+appName+"/logo.png";

                            //本地版本先显示为0
                            //Long id, String appName, String author, String localVersion, String firstVersion, String nowVersion
                            appBean = new AppBean(null, appName, author, "0", firstVersion, nowVersion);
                            mLists.add(appBean);
                        }
                        //再查询数据库的数据（本地版本）
                        String appNameColumn = AppBeanDao.Properties.AppName.columnName;
                        String localVersionColumn = AppBeanDao.Properties.LocalVersion.columnName;
                        String orderBy = appNameColumn + " COLLATE LOCALIZED ASC";

                        cursor = db.query(getAppBeanDao().getTablename(), new String[]{"_id", appNameColumn, localVersionColumn}, null, null, null, null, orderBy);
                        while (cursor.moveToNext()) {

                            String appName = cursor.getString(cursor.getColumnIndex(appNameColumn));
                            String localVersion = cursor.getString(cursor.getColumnIndex(localVersionColumn));
                            Long id = cursor.getLong(cursor.getColumnIndex("_id"));
                            //Log.i("zzy", "appName :" + appName + "---localVersion:" + localVersion + "---id:" + id);
                            for (int i = 0; i < mLists.size(); i++) {
                                if (mLists.get(i).getAppName().equals(appName)) {  //如果相等代表一条数据,有下载或者更新的操作
                                    mLists.get(i).setLocalVersion(localVersion);
                                    mLists.get(i).setId(id);
                                }
                            }
                        }
                        myHomeAdapter = new MyHomeAdapter();
                        mRecyclerView.setAdapter(myHomeAdapter);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ProjectListActivity.this, "当前网络不可用，请稍候再试！", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("zzy","onDestroy:");
        imageLoader.clearMemoryCache(); // 清除内存缓存
        imageLoader.clearDiscCache();  //清除sd卡的缓存
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 把Cordova用到的文件库拷贝到data/data/《包名》/files下
     */
    private void copyCordovaFile() {
        try {
            FileUtil.getInstance().createDirInDir(getFilesDir(), "js");

            File file = new File(getFilesDir().getAbsolutePath(), "cordova_android.zip");
            Log.i("zzy","file11111:"+file);
            if (file.exists() && file.length() > 0) {
                return;
            }
            InputStream is = getAssets().open("cordova_android.zip");

            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;
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
     * 图片加载第一次显示监听器
     *
     * @author Administrator
     *
     */
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                // 是否第一次显示
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    // 图片淡入效果
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}

