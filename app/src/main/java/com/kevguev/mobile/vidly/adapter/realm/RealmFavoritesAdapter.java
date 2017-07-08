package com.kevguev.mobile.vidly.adapter.realm;

import android.content.Context;

import com.kevguev.mobile.vidly.model.RealmVideo;

import io.realm.RealmResults;

/**
 * Created by Kevin Guevara on 7/7/2017.
 */

public class RealmFavoritesAdapter extends RealmModelAdapter<RealmVideo> {

    public RealmFavoritesAdapter(Context context, RealmResults<RealmVideo> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);
    }
}
