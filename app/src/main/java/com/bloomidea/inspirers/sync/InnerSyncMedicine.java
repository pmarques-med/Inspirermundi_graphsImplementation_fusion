package com.bloomidea.inspirers.sync;

import com.bloomidea.inspirers.model.UserMedicine;

/**
 * Created by michellobato on 24/04/17.
 */

public class InnerSyncMedicine {
    private String uid;
    private UserMedicine userMedicine;
    private boolean deleted;

    public InnerSyncMedicine(String uid, UserMedicine userMedicine, boolean deleted) {
        this.uid = uid;
        this.userMedicine = userMedicine;
        this.deleted = deleted;
    }

    public String getUid() {
        return uid;
    }

    public UserMedicine getUserMedicine() {
        return userMedicine;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
