package com.bloomidea.inspirers.model;

import android.graphics.Bitmap;

import com.bloomidea.inspirers.utils.InhalerTypeAux;

import org.medida.inhalerdetection.InhalerDetectionActivity;

import java.io.Serializable;

/**
 * Created by michellobato on 22/03/17.
 */

public class Inhaler implements Serializable {

    private String name;
    private String imageUrl;
    private String color;
    private InhalerDetectionActivity.InhalerType type;
    private boolean selected;


    public Inhaler(String name, String imageUrl, String color, InhalerDetectionActivity.InhalerType type, boolean selected) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.color = color;
        this.type = type;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getColor() {
        return color;
    }

    public InhalerDetectionActivity.InhalerType getType() {
        return type;
    }

    public void setType(InhalerDetectionActivity.InhalerType type) {
        this.type = type;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
