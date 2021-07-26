package com.bloomidea.inspirers.events;

import com.bloomidea.inspirers.model.Review;

/**
 * Created by michellobato on 06/04/17.
 */

public class ReviewCreatedEvent {
    private Review newReview;
    private String uid;

    public ReviewCreatedEvent(Review newReview, String uid) {
        this.newReview = newReview;
        this.uid = uid;
    }

    public Review getNewReview() {
        return newReview;
    }

    public String getUid() {
        return uid;
    }
}
