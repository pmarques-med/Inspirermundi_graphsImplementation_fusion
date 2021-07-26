package com.bloomidea.inspirers.events;

import com.bloomidea.inspirers.model.TimelineItem;

import java.math.BigDecimal;

/**
 * Created by michellobato on 30/03/17.
 */

public class MedicineTakenEvent {
    private boolean onTime;
    private TimelineItem medicinetaken;
    private BigDecimal multiplierUsed;
    private int positionAdapter;
    private TimelineItem timelineItemUpdated;
    private int points;

    public MedicineTakenEvent(boolean onTime, TimelineItem medicinetaken, BigDecimal multiplierUsed, int positionAdapter, TimelineItem timelineItemUpdated, int points) {
        this.onTime = onTime;
        this.medicinetaken = medicinetaken;
        this.multiplierUsed = multiplierUsed;
        this.positionAdapter = positionAdapter;
        this.timelineItemUpdated = timelineItemUpdated;
        this.points = points;
    }

    public boolean isOnTime() {
        return onTime;
    }

    public TimelineItem getMedicinetaken() {
        return medicinetaken;
    }

    public BigDecimal getMultiplierUsed() {
        return multiplierUsed;
    }

    public int getPositionAdapter() {
        return positionAdapter;
    }

    public TimelineItem getTimelineItemUpdated() {
        return timelineItemUpdated;
    }

    public int getPoints() {
        return points;
    }
}
