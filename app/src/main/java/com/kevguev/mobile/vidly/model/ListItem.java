package com.kevguev.mobile.vidly.model;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Java Representation of our data to be displayed in our recycler view
 * Created by Kevin Guevara on 5/13/2017.
 */

public class ListItem implements Parcelable {
    private String title;
    private String imgUrl;
    private String videoUrlId;
    private String subtitle;
    private boolean favorite = false;
    private int imageResId;

    public static final Creator<ListItem> CREATOR = new Creator<ListItem>() {
        @Override
        public ListItem createFromParcel(Parcel in) {
            return new ListItem(in);
        }

        @Override
        public ListItem[] newArray(int size) {
            return new ListItem[size];
        }
    };

    public ListItem() {

    }

    public ListItem(String title, String imgUrl, String videoUrlId, String subtitle ){
        this.title = title;
        this.subtitle = subtitle;
        this.videoUrlId = videoUrlId;
        this.imgUrl = imgUrl;
    }

    public String getVideoUrlId() {
        return videoUrlId;
    }

    public void setVideoUrlId(String videoUrlId) {
        this.videoUrlId = videoUrlId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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

    // Parcelling part
    public ListItem(Parcel in) {
        String[] data = new String[4];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.title = data[0];
        this.subtitle = data[1];
        this.videoUrlId = data[2];
        this.imgUrl = data[3];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{this.title,
                this.subtitle, this.videoUrlId, this.imgUrl});
    }
}
