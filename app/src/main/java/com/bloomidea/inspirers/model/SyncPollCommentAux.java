package com.bloomidea.inspirers.model;

/**
 * Created by michellobato on 08/11/2017.
 */

public class SyncPollCommentAux {
    private Long id;
    private String nid;
    private String comment;

    public SyncPollCommentAux(Long id, String nid, String comment) {
        this.id = id;
        this.nid = nid;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public String getNid() {
        return nid;
    }

    public String getComment() {
        return comment;
    }
}
