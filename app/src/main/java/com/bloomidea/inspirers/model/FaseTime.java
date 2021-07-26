package com.bloomidea.inspirers.model;

import java.io.Serializable;

import static java.lang.Integer.parseInt;

/**
 * Created by michellobato on 22/03/17.
 */

public class FaseTime implements Serializable{
    public static final int END_TIME_HOUR_ADD = 2;
    private String hourInitDesc;

    private int hourInit;
    private int minutesInit;
    private int hourEnd;
    private int minutesEnd;

    public FaseTime(int initHour, int initMinute) {
        this.hourInitDesc = String.format("%02d:%02d",initHour,initMinute);

        this.hourInit = initHour;
        this.minutesInit = initMinute;
        this.hourEnd = (initHour+END_TIME_HOUR_ADD);
        this.minutesEnd = initMinute;

        //TAG M6
        if(hourEnd>24){
            hourEnd = 1;
            minutesEnd = initMinute;
        }
    }

    public FaseTime(String hourInitDesc) {
        this.hourInitDesc = hourInitDesc;

        String[] auxTime = hourInitDesc.split(":");

        int initHour = parseInt(auxTime[0]);
        int initMinute = Integer.parseInt(auxTime[1]);

        this.hourInit = initHour;
        this.minutesInit = initMinute;
        this.hourEnd = (initHour+END_TIME_HOUR_ADD);
        this.minutesEnd = initMinute;

        //TAG M6
        if(hourEnd>24){
            hourEnd = 1;
            minutesEnd = initMinute;
        }
    }

    public String getHourInitDesc() {
        return hourInitDesc;
    }

    public String getDesc() {
        return String.format("%02d:%02d - %02d:%02d", hourInit, minutesInit, hourEnd, minutesEnd);
    }

    public String getInitTime() {
        return String.format("%02d:%02d", hourInit, minutesInit);
    }

    public int getHourInit() {
        return hourInit;
    }

    public int getMinutesInit() {
        return minutesInit;
    }

    public int getHourEnd() {
        return hourEnd;
    }

    public int getMinutesEnd() {
        return minutesEnd;
    }
}
