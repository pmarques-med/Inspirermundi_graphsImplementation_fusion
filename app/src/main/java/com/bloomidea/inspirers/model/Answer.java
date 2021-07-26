package com.bloomidea.inspirers.model;

import java.io.Serializable;

/**
 * Created by michellobato on 26/04/17.
 */

public class Answer implements Serializable {
    private Long id;
    private String nid;
    private String text;
    private boolean selected;

    public Answer(Long id, String nid, String text, boolean selected) {
        this.id = id;
        this.nid = nid;
        this.text = text;
        this.selected = selected;
    }

    public Answer(Answer a){
        this.id = a.getId();
        this.nid = a.getNid();
        this.text = a.getText();
        this.selected = a.isSelected();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNid() {
        return nid;
    }

    public String getText() {
        return text;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
