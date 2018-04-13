package com.gracker.db;

import java.io.Serializable;

/**
 * Created by JinFeei on 7/23/2014.
 */
public class Incoming implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    int msg_id;
    int msg_rid;    // Who is the receiver
    int status;
    String phone;
    String message;

    public Incoming()
    {}

    public Incoming(int msg_rid, int msg_id, String phone, String message)
    {
        this.msg_id=msg_id;
        this.phone=phone;
        this.message=message;
        this.msg_rid = msg_rid;
    }

    public int getMsg_rid() {
        return msg_rid;
    }
    public void setMsg_rid(int msg_rid) {
        this.msg_rid = msg_rid;
    }
    public int getMsg_id() {
        return msg_id;
    }
    public void setMsg_id(int msg_id) {
        this.msg_id = msg_id;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}

