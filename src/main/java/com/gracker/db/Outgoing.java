package com.gracker.db;

import java.io.Serializable;

/**
 * Created by JinFeei on 7/17/2014.
 */
public class Outgoing implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    int msg_id;
    int status;
    String phone;
    String message;

    public Outgoing()
    {}

    public Outgoing(int msg_id, int status, String phone, String message)
    {
        this.msg_id=msg_id;
        this.status=status;
        this.phone=phone;
        this.message=message;
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

