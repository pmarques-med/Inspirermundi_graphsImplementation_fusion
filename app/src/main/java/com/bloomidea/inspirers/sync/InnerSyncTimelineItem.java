package com.bloomidea.inspirers.sync;

import com.bloomidea.inspirers.model.TimelineItem;

/**
 * Created by michellobato on 24/04/17.
 */

public class InnerSyncTimelineItem {
    private String uid;
    private TimelineItem timelineItem;
    private boolean deleted;

    public InnerSyncTimelineItem(String uid, TimelineItem timelineItem, boolean deleted) {
        this.uid = uid;
        this.timelineItem = timelineItem;
        this.deleted = deleted;
    }

    public String getUid() {
        return uid;
    }

    public TimelineItem getTimelineItem() {
        return timelineItem;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
