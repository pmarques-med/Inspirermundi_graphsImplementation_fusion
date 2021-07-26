package com.bloomidea.inspirers.sync;

import com.bloomidea.inspirers.model.ListPoll;

/**
 * Created by michellobato on 24/04/17.
 */

public class InnerSyncPoll {
    private String uid;
    private ListPoll listPoll;

    public InnerSyncPoll(String uid, ListPoll listPoll) {
        this.uid = uid;
        this.listPoll = listPoll;
    }

    public String getUid() {
        return uid;
    }

    public ListPoll getListPoll() {
        return listPoll;
    }
}
