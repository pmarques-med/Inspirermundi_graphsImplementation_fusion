package com.bloomidea.inspirers.model;

import java.util.GregorianCalendar;

/**
 * Created by michellobato on 23/03/17.
 */

public class TimelineItem {
    public static final String STATE_DONE = "done";
    public static final String STATE_MISSED = "miss";
    public static final String STATE_LATE = "late";

    private Long id;
    private UserMedicine medicine;
    private UserBadge badge;
    private GregorianCalendar date;
    private GregorianCalendar startTime;
    private GregorianCalendar endTime;
    private int timePoints;
    private String weekNumber;
    private boolean isSOS;
    private String timeLineNote;
    private String state;
    private GregorianCalendar dateTaken;
    private int pointWon;
    private int dosage;

    private FaseTime faseTime;

    private Poll poll;

    private int recognitionFailedTimes;

    private String nid;
    private boolean mainTime;

    private String latitude;
    private String longitude;

    public TimelineItem(Long id, UserMedicine medicine, UserBadge badge, GregorianCalendar date, GregorianCalendar startTime, GregorianCalendar endTime, int timePoints, String weekNumber, boolean isSOS, String timeLineNote, String state, GregorianCalendar dateTaken, int pointWon, int dosage, FaseTime faseTime, Poll poll, int recognitionFailedTimes, String nid, boolean mainTime, String latitude, String longitude) {
        this.id = id;
        this.medicine = medicine;
        this.badge = badge;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timePoints = timePoints;
        this.weekNumber = weekNumber;
        this.isSOS = isSOS;
        this.timeLineNote = timeLineNote;
        this.state = state;
        this.dateTaken = dateTaken;
        this.pointWon = pointWon;
        this.dosage = dosage;
        this.faseTime = faseTime;
        this.poll = poll;
        this.recognitionFailedTimes = recognitionFailedTimes;
        this.nid = nid;
        this.mainTime = mainTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public boolean isMainTime() {
        return mainTime;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getNid() {
        return nid;
    }

    public Long getId() {
        return id;
    }

    public UserMedicine getMedicine() {
        return medicine;
    }

    public UserBadge getBadge() {
        return badge;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public GregorianCalendar getStartTime() {
        return startTime;
    }

    public GregorianCalendar getEndTime() {
        return endTime;
    }

    public void setStartTime(GregorianCalendar startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(GregorianCalendar endTime) {
        this.endTime = endTime;
    }

    public int getTimePoints() {
        return timePoints;
    }

    public String getWeekNumber() {
        return weekNumber;
    }

    public boolean isSOS() {
        return isSOS;
    }

    public String getTimeLineNote() {
        return timeLineNote;
    }

    public String getState() {
        return state;
    }

    public GregorianCalendar getDateTaken() {
        return dateTaken;
    }

    public int getPointWon() {
        return pointWon;
    }

    public int getDosage() {
        return dosage;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void markMedicineTaken(int pointsWon, String newState, GregorianCalendar dateTaken) {
        this.pointWon = pointsWon;
        this.state = newState;
        this.dateTaken = dateTaken;
    }

    public void setTimeLineNote(String timeLineNote) {
        this.timeLineNote = timeLineNote;
    }

    public FaseTime getFaseTime() {
        return faseTime;
    }

    public Poll getPoll() {
        return poll;
    }

    public boolean isMedicine(){
        return medicine != null;
    }

    public int getRecognitionFailedTimes() {
        return recognitionFailedTimes;
    }

    public void setRecognitionFailedTimes(int recognitionFailedTimes) {
        this.recognitionFailedTimes = recognitionFailedTimes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLocation(String latitude, String longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setPointWon(int pointWon) {
        this.pointWon = pointWon;
    }
}
