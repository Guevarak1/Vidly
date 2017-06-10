package com.kevguev.mobile.vidly.model;

/**
 * Java Representation of our data to be displayed in our recycler view
 * Created by Kevin Guevara on 5/13/2017.
 */

public class ListItem {
    private String title;
    private String subtitle;
    private boolean favorite = false;
    private int imageResId;
    private int isTurned;

    public int getIsTurned() {
        return isTurned;
    }

    public void setIsTurned(int isTurned) {
        this.isTurned = isTurned;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

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
