package com.bloomidea.inspirers.model;

import java.util.GregorianCalendar;

/**
 * Created by michellobato on 11/04/17.
 */

public class Message {
    private boolean myMsg;
    private String msg;
    private GregorianCalendar sendDate;

    public Message(boolean myMsg, String msg, GregorianCalendar sendDate) {
        this.myMsg = myMsg;
        this.msg = msg;
        this.sendDate = sendDate;
    }

    public boolean isMyMsg() {
        return myMsg;
    }

    public String getMsg() {
        return msg;
    }

    public GregorianCalendar getSendDate() {
        return sendDate;
    }
}
