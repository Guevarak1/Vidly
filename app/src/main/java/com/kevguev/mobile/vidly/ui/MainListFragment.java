package com.kevguev.mobile.vidly.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.kevguev.mobile.vidly.Constants;
import com.kevguev.mobile.vidly.listeners.ItemClickCallback;
import com.kevguev.mobile.vidly.listeners.PostResultsListener;
import com.kevguev.mobile.vidly.R;
import com.kevguev.mobile.vidly.utils.AppUtils;
import com.kevguev.mobile.vidly.utils.SharedPreferenceUtil;
import com.kevguev.mobile.vidly.adapters.CardViewAdapter;
import com.kevguev.mobile.vidly.model.ListItem;
import com.kevguev.mobile.vidly.model.jsonpojo.videos.Item;

import java.util.ArrayList;

//our card fragment
public class MainListFragment extends Fragment
        implements ItemClickCallback, PostResultsListener {

    ProgressDialog mProgress;
    private RecyclerView recView;
    public CardViewAdapter adapter;
    public ArrayList listData;
    public ArrayList<Item> videoItems;
    SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        sharedPreferenceUtil = new SharedPreferenceUtil();
        if (bundle != null) {
            listData = bundle.getParcelableArrayList("video_items");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_list, container, false);

        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Calling YouTube Data API ...");
        recView = (RecyclerView) view.findViewById(R.id.rec_list);
        //layout manager: girdlayout manager or staggered grid layout manager
        recView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CardViewAdapter(getActivity(), false);
        adapter.setItemClickCallback(this);
        recView.setAdapter(adapter);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setPostResultsListener(this);
        if(mainActivity.videoItems != null ){
            videoItems = mainActivity.videoItems;
            listData = (ArrayList) AppUtils.getListData(videoItems);
            adapter.setListData(listData);
            adapter.notifyDataSetChanged();
        }
        return view;
    }

    @Override
    public void onThumbnailClicked(int p) {
        ListItem item = (ListItem) listData.get(p);
        Intent i = new Intent(getActivity(), DetailActivity.class);
        i.putExtra(Constants.BUNDLE_EXTRAS, item);
        startActivity(i);
    }

    @Override
    public void onLikeImageClicked(View v, int p) {
        ListItem item = (ListItem) listData.get(p);
        if (item.isFavorite()) {
            item.setFavorite(false);
            sharedPreferenceUtil.removeFavorite(getActivity(),item);
        } else {
            item.setFavorite(true);
            sharedPreferenceUtil.addFavorite(getActivity(),item);
            Toast.makeText(getActivity(),item.getTitle() + " added to favorites",Toast.LENGTH_SHORT)
                    .show();
        }
        adapter.setListData(listData);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onShareImageClicked(int p) {
        ListItem item = (ListItem) listData.get(p);
        String videoUrlId = item.getVideoUrlId();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Sharing from Local Vids\n\n" + "https://www.youtube.com/watch?v=" + videoUrlId);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

        Toast.makeText(getActivity(),item.getTitle()+" sharing!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void postResultsToFragment(ArrayList<Item> videos, GoogleAccountCredential mCredential) {
            mProgress.hide();
            //populate list
            listData = (ArrayList) AppUtils.getListData(videos);
            adapter.setListData(listData);
            adapter.notifyDataSetChanged();
    }
}
