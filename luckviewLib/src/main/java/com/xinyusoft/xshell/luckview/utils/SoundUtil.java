package com.xinyusoft.xshell.luckview.utils;

import java.io.FileNotFoundException;
import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

/**
 * 音频播放工具类
 */
public class SoundUtil {
    private HashMap<String, Integer> data;
    private HashMap<String, Integer> soundIdmaps;
    //private static SoundUtil sound;
    private Context context;
    private SoundPool soundPool;
    private boolean isLoadOver = false;

    /**不能使用单实例模式操作，当点击返回的时候，会异常崩溃 （原因？？？）**/
    public SoundUtil(Context context) {
        init(context);
    }

    /**完成类数据的初始化工作**/
    private void init(Context context) {
        this.context = context;
        this.data = new HashMap<String, Integer>();
        this.soundIdmaps = new HashMap<String, Integer>();
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
    }

    /**向存放所有音频文件的HashMap里添加数据，并加载**/
    public void put(String name, Integer file) throws IndexOutOfBoundsException, FileNotFoundException {
        isLoadOver = false;
        int m = soundPool.load(context, file, 1);
        if (null != data.get(name)) {
            return;
        }
        data.put(name, m);
        //添加加载监听
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                isLoadOver = true;
            }
        });
    }

//    /**返回一个静态Sound实例并使用传入的data来初始化数据**/
//    public static SoundUtil getInstance(Context context) {
//        if (sound == null)
//            sound = new SoundUtil(context);
//        return sound;
//    }

    /**
     * 传入在HashMap里添加的音频文件名字来启动播放
     * @param soundName  名字
     * @return 播放成功返回true饭后返回false
     */
    public boolean play(String soundName) throws IndexOutOfBoundsException, FileNotFoundException {
        if (isLoadOver) {
            //通过这个soundid来取消播放
            int soundID = soundPool.play(data.get(soundName), 1f, 1f, 0, -1, 1);
            soundIdmaps.put(soundName, soundID);
            return true;
        }
        return false;
    }

    /**销毁  包含本类静态实例，音频HashMap,音频名字的List**/
    public void destroy() {
        if (soundPool != null)
            soundPool.release();
        if (!data.isEmpty())
            data.clear();
        if (!soundIdmaps.isEmpty()) {
            soundIdmaps.clear();
        }

    }

    /**
     * 终止某一个音频
     * @param soundName 存入音频的时候的名字
     * @return
     */
    public void stop(String soundName) {
        if (soundIdmaps.get(soundName) != null) {
            int soundID = soundIdmaps.get(soundName);
            soundPool.stop(soundID);
            soundIdmaps.remove(soundName);
        }

    }


    /**
     * 暂停一个音频
     * @param soundName 存入音频的时候的名字
     */
    public void pause(String soundName) {
        if (soundIdmaps.get(soundName) != null) {
            int soundID = soundIdmaps.get(soundName);
            soundPool.pause(soundID);
        }

    }

    /**
     * 继续一个音频
     * @param soundName 存入音频的时候的名字
     */
    public void resume(String soundName) {
        if (soundIdmaps.get(soundName) != null) {
            int soundID = soundIdmaps.get(soundName);
            soundPool.resume(soundID);
        }

    }

    /**清空存放音频数据的HashMap    成功返回true**/
    public void cleanSoundMap() {
        if (!data.isEmpty()) {
            data.clear();
        }
        if (!soundIdmaps.isEmpty()) {
            soundIdmaps.clear();
        }
    }


}

