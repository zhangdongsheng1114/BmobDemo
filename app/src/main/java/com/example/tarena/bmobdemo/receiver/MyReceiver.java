package com.example.tarena.bmobdemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(PushConstants.ACTION_MESSAGE)) {
            try {
                // 收到了服务器推送过来的内容
                String message = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
                Log.d("TAG", "服务器推送过来的内容：" + message);
                // {"tag":"newpost","content":"xxx发布了新帖"}
                JSONObject jsonObject = new JSONObject(message);
                if (jsonObject.has("tag")) {
                    String tag = jsonObject.getString("tag");
                    if ("newpost".equals(tag)) {
                        // 有人发布了新帖子
                        // 让ShowActivity的右上角出现提示红点
                        Intent intent2 = new Intent("COM.TARENA.ACTION_NEW_POST");
                        context.sendBroadcast(intent2);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
