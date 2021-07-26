package com.bloomidea.inspirers.model;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * Created by michellobato on 17/03/17.
 */

public class UserBadge implements Serializable{
    private Long id;
    private GregorianCalendar date;
    private String weekNumber;
    private Badge badge;

    public UserBadge(Long id, GregorianCalendar date, String weekNumber, Badge badge) {
        this.id = id;
        this.date = date;
        this.weekNumber = weekNumber;
        this.badge = badge;
    }
    public UserBadge(GregorianCalendar date, String weekNumber, Badge badge) {
        this.id = null;
        this.date = date;
        this.weekNumber = weekNumber;
        this.badge = badge;
    }

    public Long getId() {
        return id;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public String getWeekNumber() {
        return weekNumber;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
