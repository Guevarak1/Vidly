package com.kevguev.mobile.vidly.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Kevin Guevara on 7/7/2017.
 */

public class RealmVideo extends RealmObject{

    @PrimaryKey
    private long id;
    private String text;
    private String imageSrc;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getText() {
        return text;

    }

    public void setText(String text) {
        this.text = text;
    }

}
