package com.bloomidea.inspirers.sync;

import com.bloomidea.inspirers.model.UserBadge;

/**
 * Created by michellobato on 24/04/17.
 */

public class InnerSyncBadges {
    private String uid;
    private UserBadge badge;

    public InnerSyncBadges(String uid, UserBadge badge) {
        this.uid = uid;
        this.badge = badge;
    }

    public String getUid() {
        return uid;
    }

    public UserBadge getBadge() {
        return badge;
    }
}
