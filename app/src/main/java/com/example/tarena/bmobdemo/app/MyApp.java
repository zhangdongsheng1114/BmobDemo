package com.example.tarena.bmobdemo.app;

import android.app.Application;
import android.media.MediaPlayer;

import com.example.tarena.bmobdemo.R;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * Created by tarena on 2017/6/29.
 */

public class MyApp extends Application {

    public static MyApp CONTEXT;

    public static MediaPlayer player;

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;

        // 初始化BmobSDK
        Bmob.initialize(this, "8e6dd6b3a519f4d337ce98c265c56eda");
        //
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动接受服务器推送服务
        BmobPush.startWork(this);

        player = MediaPlayer.create(this, R.raw.notify);
    }
}

