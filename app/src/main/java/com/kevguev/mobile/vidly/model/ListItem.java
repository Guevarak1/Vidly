package com.kevguev.mobile.vidly.model;

/**
 * Java Representation of our data to be displayed in our recycler view
 * Created by Kevin Guevara on 5/13/2017.
 */

public class ListItem {
    private String title;
    private int imageResId;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
