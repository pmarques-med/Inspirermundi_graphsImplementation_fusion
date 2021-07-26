package com.bloomidea.inspirers.model;

import android.graphics.Bitmap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.GregorianCalendar;

/**
 * Created by michellobato on 06/04/17.
 */

public class Review {
    private Bitmap pictureImg;
    private String nid;
    private String pictureUrl;
    private String userName;
    private String uid;
    private String reviewText;
    private BigDecimal review;
    private GregorianCalendar date;

    public Review(Bitmap pictureImg, String pictureUrl, String userName, String uid, String reviewText, BigDecimal review, GregorianCalendar date) {
        this.pictureImg = pictureImg;
        this.nid = null;
        this.pictureUrl = pictureUrl;
        this.userName = userName;
        this.uid = uid;
        this.reviewText = reviewText;
        this.review = review;
        this.date = date;
    }

    public Review(Bitmap pictureImg, String nid, String pictureUrl, String userName, String uid,String reviewText, BigDecimal review, GregorianCalendar date) {
        this.pictureImg = pictureImg;
        this.nid = nid;
        this.pictureUrl = pictureUrl;
        this.userName = userName;
        this.uid = uid;
        this.reviewText = reviewText;
        this.review = review;
        this.date = date;
    }

    public Bitmap getPictureImg() {
        return pictureImg;
    }

    public String getNid() {
        return nid;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getUid() {
        return uid;
    }

    public String getReviewText() {
        return reviewText;
    }

    public BigDecimal getReviewTreated5Stars() {
        return review.multiply(new BigDecimal(5)).divide(new BigDecimal(100),1, RoundingMode.HALF_UP);
    }

    public BigDecimal getReview() {
        return review;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }
}
