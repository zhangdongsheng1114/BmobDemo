package com.example.tarena.bmobdemo.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tarena.bmobdemo.R;
import com.example.tarena.bmobdemo.adapter.PostAdapter;
import com.example.tarena.bmobdemo.app.MyApp;
import com.example.tarena.bmobdemo.bean.MyPost;
import com.example.tarena.bmobdemo.bean.MyUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class ShowActivity extends Activity {

    MyUser user;
    @BindView(R.id.lv_show_listviews)
    ListView listView;
    List<MyPost> datas;
    PostAdapter adapter;

    @BindView(R.id.iv_header_add)
    ImageView ivAdd;
    @BindView(R.id.iv_header_refresh)
    ImageView ivRefresh;

    @BindView(R.id.iv_header_tip)
    ImageView ivTip;

    MyPostReceiver myPostReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        user = (MyUser) getIntent().getSerializableExtra("user");
        Log.d("TAG", "登录用户的用户名是: " + user.getUsername());
        ButterKnife.bind(this);
        initHeaderView();
        initListView();
    }

    private void initHeaderView() {
        ivAdd.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        ivRefresh.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    }

    private void initListView() {
        datas = new ArrayList<>();
        adapter = new PostAdapter(this, datas, user);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myPostReceiver = new MyPostReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("COM.TARENA.ACTION_NEW_POST");
        registerReceiver(myPostReceiver, filter);
        refresh();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(myPostReceiver);
        super.onPause();
    }

    private void refresh() {
        ivTip.setVisibility(View.INVISIBLE);

        BmobQuery<MyPost> query = new BmobQuery<>();

        // 查询MyUser表中保存的用户信息
        query.include("user");
        // 排序（按照发帖时间）
        query.order("-createdAt");

        query.findObjects(this, new FindListener<MyPost>() {
            @Override
            public void onSuccess(List<MyPost> list) {
                adapter.addAll(list, true);
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(ShowActivity.this, "获取帖子列表失败，稍后重试", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "获取帖子列表失败: " + i + ": " + s);
            }
        });
    }

    @OnClick(R.id.iv_header_add)
    public void post(View v) {
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("from", "add");
        startActivity(intent);
    }

    @OnClick(R.id.iv_header_refresh)
    public void refresh(View view) {

        refresh();
    }

    public class MyPostReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("COM.TARENA.ACTION_NEW_POST".equals(action)) {
                ivTip.setVisibility(View.VISIBLE);
                MyApp.player.start();
            }
        }
    }
}
