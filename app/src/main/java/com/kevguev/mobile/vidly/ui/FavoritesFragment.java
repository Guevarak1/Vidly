package com.kevguev.mobile.vidly.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevguev.mobile.vidly.R;
import com.kevguev.mobile.vidly.adapter.FavoritesAdapter;
import com.kevguev.mobile.vidly.adapter.realm.RealmFavoritesAdapter;
import com.kevguev.mobile.vidly.model.RealmVideo;
import com.kevguev.mobile.vidly.realm.RealmController;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Kevin Guevara on 5/24/2017.
 */

public class FavoritesFragment extends Fragment {

    private FavoritesAdapter adapter;
    private Realm realm;
    private LayoutInflater inflater;
    private RecyclerView recycler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.realm = RealmController.with(this).getRealm();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        recycler = (RecyclerView) view.findViewById(R.id.fav_rec_list);
        // Inflate the layout for this fragment
        setupRecycler();

        //if (!Prefs.with(this).getPreLoad()) {
        setRealmData();
        //}

        // refresh the realm instance
        RealmController.with(this).refresh();
        // get all persisted objects
        // create the helper adapter and notify data set changes
        // changes will be reflected automatically

        RealmResults<RealmVideo> videos = RealmController.with(this).getRealmVideos();
        setRealmAdapter(videos);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setRealmAdapter(RealmResults<RealmVideo> videos){

        RealmFavoritesAdapter realmFavoritesAdapter = new RealmFavoritesAdapter(this.getActivity().getApplicationContext(), videos, true);
        // Set the data and tell the RecyclerView to draw
        adapter.setRealmAdapter(realmFavoritesAdapter);
        adapter.notifyDataSetChanged();

    }

    private void setupRecycler() {
        // use a linear layout manager since the cards are vertically scrollable
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);

        // create an empty adapter and add it to the recycler view
        adapter = new FavoritesAdapter(getContext());
        recycler.setAdapter(adapter);
    }

    private void setRealmData() {

        ArrayList<RealmVideo> videos = new ArrayList<>();
        RealmVideo video = new RealmVideo();

        video.setId(1 + System.currentTimeMillis());
        video.setText("Random1");
        video.setImageSrc("http://api.androidhive.info/images/realm/1.png");
        videos.add(video);

        video.setId(2 + System.currentTimeMillis());
        video.setText("Random2");
        video.setImageSrc("http://api.androidhive.info/images/realm/2.png");
        videos.add(video);

        for(RealmVideo v : videos){
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(v);
            realm.commitTransaction();

        }
    }
}
