package com.bloomidea.inspirers.model;

import java.util.GregorianCalendar;

/**
 * Created by michellobato on 27/04/17.
 */

public class NavAux {
    private Long id;
    private Long userId;
    private String description;
    private GregorianCalendar startTime;
    private GregorianCalendar endTime;
    private String type;

    public NavAux(Long id, Long userId, String description, GregorianCalendar startTime, GregorianCalendar endTime, String type) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GregorianCalendar getStartTime() {
        return startTime;
    }

    public void setStartTime(GregorianCalendar startTime) {
        this.startTime = startTime;
    }

    public GregorianCalendar getEndTime() {
        return endTime;
    }

    public void setEndTime(GregorianCalendar endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
