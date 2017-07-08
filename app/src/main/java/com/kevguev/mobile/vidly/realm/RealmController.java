package com.kevguev.mobile.vidly.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.kevguev.mobile.vidly.model.RealmVideo;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Kevin Guevara on 7/7/2017.
 */

public class RealmController {


    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.refresh();
    }

    //clear all objects from RealmVideo.class
    public void clearAll() {

        realm.beginTransaction();
        realm.clear(RealmVideo.class);
        realm.commitTransaction();
    }

    //find all objects in the RealmVideo.class
    public RealmResults<RealmVideo> getRealmVideos() {

        return realm.where(RealmVideo.class).findAll();
    }

    //query a single item with the given id
    public RealmVideo getRealmVideo(String id) {

        return realm.where(RealmVideo.class).equalTo("id", id).findFirst();
    }

    //check if RealmVideo.class is empty
    public boolean hasRealmVideos() {

        return !realm.allObjects(RealmVideo.class).isEmpty();
    }

    //query example
    public RealmResults<RealmVideo> queryedRealmVideos() {

        return realm.where(RealmVideo.class)
                .contains("favorites", "true")
                .or()
                .contains("type", "video")
                .findAll();

    }
}
