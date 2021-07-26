package com.bloomidea.inspirers.model;

import java.util.ArrayList;

/**
 * Created by michellobato on 23/03/17.
 */

public class TimelineWeek {
    private String weekNumber;
    private boolean open;

    private ArrayList<TimelineItem> items;

    public TimelineWeek(String weekNumber, boolean open) {
        this.weekNumber = weekNumber;
        this.open = open;
        this.items = new ArrayList<>();
    }

    public String getWeekNumber() {
        return weekNumber;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setItems(ArrayList<TimelineItem> items) {
        this.items = items;
    }

    public ArrayList<TimelineItem> getItems() {
        return items;
    }
}
