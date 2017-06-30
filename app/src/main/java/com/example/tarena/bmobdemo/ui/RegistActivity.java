package com.example.tarena.bmobdemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.tarena.bmobdemo.R;
import com.example.tarena.bmobdemo.bean.MyUser;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class RegistActivity extends Activity {

    @BindView(R.id.et_regist_username)
    EditText etUsername;
    @BindView(R.id.et_regist_password)
    EditText etPassword;
    @BindView(R.id.rg_regist_gender)
    RadioGroup rgGender;
    @BindView(R.id.iv_regist_avatar)
    ImageView ivAvatar;

    // 头像图片在服务器上的位置
    String avatar;

    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_regist_regist)
    public void regist(View view) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入完整", Toast.LENGTH_SHORT).show();
            return;
        }

        MyUser user = new MyUser();
        user.setUsername(username);

        String md5 = new String(Hex.encodeHex(DigestUtils.sha(password)));

        user.setPassword(md5);

        boolean gender = true;
        if (rgGender.getCheckedRadioButtonId() == R.id.rb_regist_girl) {
            gender = false;
        }
        user.setGender(gender);
        user.setAvatar(avatar);

        //将user对象中的信息保存到Bmob服务器
        user.save(this, new SaveListener() {
            @Override
            public void onSuccess() {

                ivAvatar.setImageResource(R.mipmap.ic_launcher);
                avatar = null;
                etUsername.setText("");
                etPassword.setText("");
                rgGender.check(R.id.rb_regist_boy);

                Toast.makeText(RegistActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                switch (i) {
                    case 401:
                        Toast.makeText(RegistActivity.this, "用户名重复", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(RegistActivity.this, "注册失败,失败原因：" + i + ":" + s, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    /**
     * 利用Intent选择器实现多种途径（图库选图，相机拍摄）设定用户头像
     *
     * @param view
     */
    @OnClick(R.id.iv_regist_avatar)
    public void setAvatar(View view) {

        // 开启系统图库的Intent
        Intent intent1 = new Intent(Intent.ACTION_PICK);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        // 开启系统相机的Intent
        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpg");

        path = file.getAbsolutePath();
        Uri path = Uri.fromFile(file);
        intent2.putExtra(MediaStore.EXTRA_OUTPUT, path);

        Intent chooser = Intent.createChooser(intent1, "选择头像...");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent2});

        startActivityForResult(chooser, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 100) {
            // 处理用户选择或拍摄的图片
            final String filePath;

            if (data != null) {
                // 头像是从图库选择的图片
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                cursor.moveToNext();
                filePath = cursor.getString(0);
                cursor.close();
            } else {
                // 头像是拍摄的图片
                filePath = path;
            }
            // 上传图片文件到Bmob服务器
            final BmobFile bf = new BmobFile(new File(filePath));
            bf.uploadblock(RegistActivity.this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    avatar = bf.getFileUrl(RegistActivity.this);
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    ivAvatar.setImageBitmap(bitmap);
                }

                @Override
                public void onFailure(int i, String s) {
                    Toast.makeText(RegistActivity.this, "头像上传失败，稍后重试", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "头像上传失败，错误原因： " + i + ":" + s);
                }
            });
        }
    }
}
