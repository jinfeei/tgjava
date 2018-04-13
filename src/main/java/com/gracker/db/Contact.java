package com.gracker.db;

import java.io.*;

/**
 * Created by JinFeei on 7/15/2014.
 */

public class Contact implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    int contact_id;
    int user_id;
    String phone;
    String nickname;

    public Contact()
    {}

    public Contact(int contact_id, int user_id, String phone, String nickname)
    {
        this.contact_id=contact_id;
        this.user_id=user_id;
        this.phone=phone;
        this.nickname=nickname;
    }

    public int getContact_id() {
        return contact_id;
    }
    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }
    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int contact_id) {
        this.user_id = contact_id;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
