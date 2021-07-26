package com.bloomidea.inspirers.sync;

import com.bloomidea.inspirers.model.User;

/**
 * Created by michellobato on 24/04/17.
 */

public class InnerSyncUser {
    private User user;
    private boolean syncPic;

    public InnerSyncUser(User user, boolean syncPic) {
        this.user = user;
        this.syncPic = syncPic;
    }

    public User getUser() {
        return user;
    }

    public boolean isSyncPic() {
        return syncPic;
    }
}
