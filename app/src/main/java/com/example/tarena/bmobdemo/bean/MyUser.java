package com.example.tarena.bmobdemo.bean;

import cn.bmob.v3.BmobObject;

/**
 * 1.必须继承自BmobObject
 * 2.属性只能是String，String[],List<String>,以及八大基本数据类型的
 * 包装类型，还有其它BmobSDK中包含的类型
 * Created by tarena on 2017/6/29.
 */

public class MyUser extends BmobObject {

    String username;
    String password;
    Boolean gender; //true 男生 false 女生
    String avatar;//头像

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
