package com.bloomidea.inspirers.model;

import java.io.Serializable;

/**
 * Created by michellobato on 17/03/17.
 */

public class Days implements Serializable {

    public static final int SUNDAY_OPTION = 1;
    public static final int MONDAY_OPTION = 2;
    public static final int TUESDAY_OPTION = 3;
    public static final int WEDNESDAY_OPTION = 4;
    public static final int THURSDAY_OPTION = 5;
    public static final int FRIDAY_OPTION = 6;
    public static final int SATURDAY_OPTION = 7;

    private int code;
    private boolean selected;
    private String name;

    public Days(int code, boolean selected, String name) {
        this.code = code;
        this.selected = selected;
        this.name = name;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
