package com.example.tarena.bmobdemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tarena.bmobdemo.R;
import com.example.tarena.bmobdemo.bean.MyUser;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;

public class MainActivity extends Activity {

    @BindView(R.id.et_main_username)
    EditText etUsername;
    @BindView(R.id.et_main_password)
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_main_regist)
    public void jump(View view) {
        Intent intent = new Intent(this, RegistActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_main_login)
    public void login(View view) {

        String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入完整", Toast.LENGTH_SHORT).show();
            return;
        }

        // 以用户输入的用户名作为查询条件查询Bmob服务器MyUser数据表中的内容
        BmobQuery<MyUser> query = new BmobQuery<>();
//        query.addWhereEqualTo(数据表的列名，用户输入的用户名)
        query.addWhereEqualTo("username", username);
        // 发起查询
        query.findObjects(this, new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {
                // 服务器给了你正常的响应
                // 根据list中是否有元素来判定查询的结果
                if (list.size() > 0) {
                    // 数据表中有一条记录其username字段值为用户输入的用户名
                    MyUser user = list.get(0);
                    String md5 = new String(Hex.encodeHex(DigestUtils.sha(password)));
                    if (user.getPassword().equals(md5)) {

                        // 将user登录成功后的消息告诉所有客户端
                        BmobPushManager<BmobInstallation> manager = new BmobPushManager<>(MainActivity.this);
                        // {"alert":"xxx登录啦"}
                        manager.pushMessageAll(user.getUsername() + "登录啦", new PushListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("TAG", "发布登录消息: ");
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Log.d("TAG", "登录消息发送失败， " + i + ":" + s);
                            }
                        });

                        // 登录成功
                        Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "该用户名或密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "该用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(MainActivity.this, "服务器繁忙，稍后重试", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
