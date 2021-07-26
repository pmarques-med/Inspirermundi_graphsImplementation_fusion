package com.bloomidea.inspirers.model;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.application.AppController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by michellobato on 21/03/17.
 */

public class ScheduleAux implements Serializable{
    public static final int ONE_A_DAY = 1;
    public static final int TWO_A_DAY = 2;
    public static final int THREE_A_DAY = 3;
    public static final int FOUR_A_DAY = 4;
    public static final int FIVE_A_DAY = 5;
    public static final int SIX_A_DAY = 6;
    public static final int SEVEN_A_DAY = 7;
    public static final int EIGHT_A_DAY = 8;
    public static final int NINE_A_DAY = 9;
    public static final int TEN_A_DAY = 10;
    public static final int ELEVEN_A_DAY = 11;
    public static final int TWELVE_A_DAY = 12;

    public static final int ONE_TO_ONE = 13;
    public static final int TWO_TO_TWO = 14;
    public static final int THREE_TO_THREE = 15;
    public static final int FOUR_TO_FOUR = 16;
    public static final int SIX_TO_SIX = 17;
    public static final int EIGHT_TO_EIGHT = 18;
    public static final int TWELVE_TO_TWELVE = 19;

    private int code;
    private String desc;
    private int intervalHours;

    public ScheduleAux(int code, String desc, int intervalHours) {
        this.code = code;
        this.desc = desc;
        this.intervalHours = intervalHours;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String getDescCode(int code){
        String codeDesc = "";
        switch (code){
            case ONE_A_DAY:
                codeDesc = AppController.getmInstance().getString(R.string.one_a_day);
                break;
            case TWO_A_DAY:
                codeDesc = AppController.getmInstance().getString(R.string.two_a_day);
                break;
            case THREE_A_DAY:
                codeDesc = AppController.getmInstance().getString(R.string.three_a_day);
                break;
            case FOUR_A_DAY:
                codeDesc = AppController.getmInstance().getString(R.string.four_a_day);
                break;
            case FIVE_A_DAY:
                codeDesc = AppController.getmInstance().getString(R.string.five_a_day);
                break;
            case SIX_A_DAY:
                codeDesc = AppController.getmInstance().getString(R.string.six_a_day);
                break;
            case SEVEN_A_DAY:
                codeDesc = AppController.getmInstance().getString(R.string.seven_a_day);
                break;
            case EIGHT_A_DAY:
                codeDesc = AppController.getmInstance().getString(R.string.eight_a_day);
                break;
            case NINE_A_DAY:
                codeDesc = AppController.getmInstance().getString(R.string.nine_a_day);
                break;
            case TEN_A_DAY:
                codeDesc = AppController.getmInstance().getString(R.string.ten_a_day);
                break;
            case ELEVEN_A_DAY:
                codeDesc = AppController.getmInstance().getString(R.string.eleven_a_day);
                break;
            case TWELVE_A_DAY:
                codeDesc = AppController.getmInstance().getString(R.string.twelve_a_day);
                break;
            case ONE_TO_ONE:
                codeDesc = AppController.getmInstance().getString(R.string.one_to_one);
                break;
            case TWO_TO_TWO:
                codeDesc = AppController.getmInstance().getString(R.string.two_to_two);
                break;
            case THREE_TO_THREE:
                codeDesc = AppController.getmInstance().getString(R.string.three_to_three);
                break;
            case FOUR_TO_FOUR:
                codeDesc = AppController.getmInstance().getString(R.string.four_to_four);
                break;
            case SIX_TO_SIX:
                codeDesc = AppController.getmInstance().getString(R.string.six_to_six);
                break;
            case EIGHT_TO_EIGHT:
                codeDesc = AppController.getmInstance().getString(R.string.eight_to_eight);
                break;
            case TWELVE_TO_TWELVE:
                codeDesc = AppController.getmInstance().getString(R.string.twelve_to_twelve);
                break;
            default:
                break;

        }
        return codeDesc;
    }

    public int getIntervalHours() {
        return intervalHours;
    }

    public void setIntervalHours(int intervalHours) {
        this.intervalHours = intervalHours;
    }

    public static ArrayList<ScheduleAux> getFrequencyList(){
        ArrayList<ScheduleAux> list = new ArrayList<ScheduleAux>();

        list.add(new ScheduleAux(ONE_A_DAY, getDescCode(ONE_A_DAY), 0));
        list.add(new ScheduleAux(TWO_A_DAY, getDescCode(TWO_A_DAY), 0));
        list.add(new ScheduleAux(THREE_A_DAY, getDescCode(THREE_A_DAY), 0));
        list.add(new ScheduleAux(FOUR_A_DAY, getDescCode(FOUR_A_DAY), 0));
        list.add(new ScheduleAux(FIVE_A_DAY, getDescCode(FIVE_A_DAY), 0));
        list.add(new ScheduleAux(SIX_A_DAY, getDescCode(SIX_A_DAY), 0));
        list.add(new ScheduleAux(SEVEN_A_DAY, getDescCode(SEVEN_A_DAY), 0));
        list.add(new ScheduleAux(EIGHT_A_DAY, getDescCode(EIGHT_A_DAY), 0));
        list.add(new ScheduleAux(NINE_A_DAY, getDescCode(NINE_A_DAY), 0));
        list.add(new ScheduleAux(TEN_A_DAY, getDescCode(TEN_A_DAY), 0));
        list.add(new ScheduleAux(ELEVEN_A_DAY, getDescCode(ELEVEN_A_DAY), 0));
        list.add(new ScheduleAux(TWELVE_A_DAY, getDescCode(TWELVE_A_DAY), 0));

        return list;
    }

    public static ArrayList<ScheduleAux> getIntervalList(){
        ArrayList<ScheduleAux> list = new ArrayList<ScheduleAux>();

        list.add(new ScheduleAux(ONE_TO_ONE, getDescCode(ONE_TO_ONE), 1));
        list.add(new ScheduleAux(TWO_TO_TWO, getDescCode(TWO_TO_TWO), 2));
        list.add(new ScheduleAux(THREE_TO_THREE, getDescCode(THREE_TO_THREE), 3));
        list.add(new ScheduleAux(FOUR_TO_FOUR, getDescCode(FOUR_TO_FOUR), 4));
        list.add(new ScheduleAux(SIX_TO_SIX, getDescCode(SIX_TO_SIX), 6));
        list.add(new ScheduleAux(EIGHT_TO_EIGHT, getDescCode(EIGHT_TO_EIGHT), 8));
        list.add(new ScheduleAux(TWELVE_TO_TWELVE, getDescCode(TWELVE_TO_TWELVE), 12));

        return list;
    }

    public static int getIntervalForTime(ScheduleAux item){
        int creation = 0;

        switch (item.getCode()){
            case ONE_A_DAY:
                creation = 1;
                break;
            case TWO_A_DAY:
                creation = 12;
                break;
            case THREE_A_DAY:
                creation = 8;
                break;
            case FOUR_A_DAY:
                creation = 6;
                break;
            case FIVE_A_DAY:
                creation = 5;
                break;
            case SIX_A_DAY:
                creation = 4;
                break;
            case SEVEN_A_DAY:
                creation = 3;
                break;
            case EIGHT_A_DAY:
                creation = 3;
                break;
            case NINE_A_DAY:
                creation = 2;
                break;
            case TEN_A_DAY:
                creation = 2;
                break;
            case ELEVEN_A_DAY:
                creation = 2;
                break;
            case TWELVE_A_DAY:
                creation = 2;
                break;
            default:
                creation = 0;
                break;
        }

        return creation;
    }

    public static int creationNumberForSelection(ScheduleAux item){
        int creation = 1;

        switch (item.getCode()){
            case ONE_A_DAY:
                creation = 1;
                break;
            case TWO_A_DAY:
                creation = 2;
                break;
            case THREE_A_DAY:
                creation = 3;
                break;
            case FOUR_A_DAY:
                creation = 4;
                break;
            case FIVE_A_DAY:
                creation = 5;
                break;
            case SIX_A_DAY:
                creation = 6;
                break;
            case SEVEN_A_DAY:
                creation = 7;
                break;
            case EIGHT_A_DAY:
                creation = 8;
                break;
            case NINE_A_DAY:
                creation = 9;
                break;
            case TEN_A_DAY:
                creation = 10;
                break;
            case ELEVEN_A_DAY:
                creation = 11;
                break;
            case TWELVE_A_DAY:
                creation = 12;
                break;
            case ONE_TO_ONE:
                creation = 24;
                break;
            case TWO_TO_TWO:
                creation = 12;
                break;
            case THREE_TO_THREE:
                creation = 8;
                break;
            case FOUR_TO_FOUR:
                creation = 6;
                break;
            case SIX_TO_SIX:
                creation = 4;
                break;
            case EIGHT_TO_EIGHT:
                creation = 3;
                break;
            case TWELVE_TO_TWELVE:
                creation = 2;
                break;
            default:
                break;
        }
        return creation;
    }
}
