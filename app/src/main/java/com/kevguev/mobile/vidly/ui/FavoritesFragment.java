package com.kevguev.mobile.vidly.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kevguev.mobile.vidly.Constants;
import com.kevguev.mobile.vidly.adapters.CardViewAdapter;
import com.kevguev.mobile.vidly.listeners.ItemClickCallback;
import com.kevguev.mobile.vidly.R;
import com.kevguev.mobile.vidly.utils.SharedPreferenceUtil;
import com.kevguev.mobile.vidly.model.ListItem;

import java.util.ArrayList;


/**
 * Created by Kevin Guevara on 5/24/2017.
 */

public class FavoritesFragment extends Fragment
        implements ItemClickCallback{

    private CardViewAdapter adapter;
    private RecyclerView recycler;
    ArrayList<ListItem> favorites;
    SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        recycler = (RecyclerView) view.findViewById(R.id.fav_rec_list);
        setupRecycler();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupRecycler() {
        // use a linear layout manager since the cards are vertically scrollable
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);
        sharedPreferenceUtil = new SharedPreferenceUtil();
        favorites = sharedPreferenceUtil.getFavorites(getActivity());

        if (favorites == null || favorites.isEmpty()) {
            Toast.makeText(getActivity(),"No favorites saved", Toast.LENGTH_SHORT).show();
        } else {
            adapter = new CardViewAdapter(getContext(),true);
            recycler.setAdapter(adapter);
            adapter.setItemClickCallback(this);
            adapter.setListData(favorites);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onThumbnailClicked(int p) {
        ListItem item = favorites.get(p);
        Intent i = new Intent(getActivity(), DetailActivity.class);
        i.putExtra(Constants.BUNDLE_EXTRAS, item);
        startActivity(i);

    }

    @Override
    public void onLikeImageClicked(View v, int p) {
        ListItem item = favorites.get(p);
        if (item.isFavorite()) {
            item.setFavorite(false);
            sharedPreferenceUtil.removeFavorite(getActivity(),item);
            favorites.remove(p);
            adapter.notifyItemRemoved(p);
            adapter.notifyItemRangeChanged(p, favorites.size());
        }
        adapter.setListData(favorites);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onShareImageClicked(int p) {
        ListItem item = favorites.get(p);
        String videoUrlId = item.getVideoUrlId();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Sharing from Local Vids\n\n" + "https://www.youtube.com/watch?v=" + videoUrlId);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

        Toast.makeText(getActivity(),item.getTitle()+" sharing!",Toast.LENGTH_SHORT).show();

    }
}