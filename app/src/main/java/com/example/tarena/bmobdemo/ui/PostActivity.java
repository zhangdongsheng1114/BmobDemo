package com.example.tarena.bmobdemo.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tarena.bmobdemo.R;
import com.example.tarena.bmobdemo.bean.MyPost;
import com.example.tarena.bmobdemo.bean.MyUser;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class PostActivity extends Activity {

    @BindView(R.id.et_post_title)
    EditText etTitle;
    @BindView(R.id.et_post_content)
    EditText etContent;

    MyUser user;

    String from;//update add

    MyPost updatePost;

    @BindView(R.id.btn_post_post)
    Button btnPost;
    @BindView(R.id.btn_post_update)
    Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        user = (MyUser) getIntent().getSerializableExtra("user");
        ButterKnife.bind(this);

        from = getIntent().getStringExtra("from");
        if ("add".equals(from)) {
            //发新帖子
            btnPost.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.INVISIBLE);

        } else {
            //更新帖子
            btnPost.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.VISIBLE);
            updatePost = (MyPost) getIntent().getSerializableExtra("post");

            etTitle.setText(updatePost.getTitle());
            etContent.setText(updatePost.getContent());
        }

    }

    @OnClick(R.id.btn_post_update)
    public void update(View view) {
        //更新帖子
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            return;
        }

        /*updatePost.setTitle(title);
        updatePost.setContent(content);

        updatePost.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(PostActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(PostActivity.this, "更新失败，稍后重试", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "帖子更新失败，失败原因："+i+": "+s);
            }
        });*/

        MyPost p = new MyPost();
        p.setTitle(title);
        p.setContent(content);
        p.update(this, updatePost.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(PostActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(PostActivity.this, "更新失败，稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.btn_post_post)
    public void post(View view) {

        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            return;
        }

        final MyPost myPost = new MyPost();
        myPost.setTitle(title);
        myPost.setContent(content);
        myPost.setUser(user);

        myPost.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(PostActivity.this, "发帖成功", Toast.LENGTH_SHORT).show();
                etTitle.setText("");
                etContent.setText("");
                //发布一个消息，xxx发了一个新帖
                push(myPost);
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(PostActivity.this, "发帖失败，稍后重试", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "发帖失败原因: " + i + ": " + s);
            }
        });

    }

    /**
     * 发布一个消息
     *
     * @param post
     */
    public void push(MyPost post) {

        try {
            BmobPushManager<BmobInstallation> manager = new BmobPushManager<>(this);
            //{"tag":"newpost","content":"xxx发布了新帖"}
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tag", "newpost");
            jsonObject.put("content", post.getUser().getUsername() + "发布了新帖");
            manager.pushMessageAll(jsonObject, new PushListener() {
                @Override
                public void onSuccess() {
                    Log.d("TAG", "发帖消息发布成功 ");
                }

                @Override
                public void onFailure(int i, String s) {
                    Log.d("TAG", "发帖消息发布失败 ");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
