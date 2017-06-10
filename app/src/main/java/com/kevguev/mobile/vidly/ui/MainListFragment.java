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
import android.widget.ImageView;
import android.widget.Toast;

import com.kevguev.mobile.vidly.Constants;
import com.kevguev.mobile.vidly.R;
import com.kevguev.mobile.vidly.adapter.SearchAdapter;
import com.kevguev.mobile.vidly.model.ListItem;

import java.util.ArrayList;

//our card fragment
public class MainListFragment extends Fragment implements SearchAdapter.ItemClickCallback{

    ProgressDialog mProgress;
    private RecyclerView recView;
    public SearchAdapter adapter;
    public ArrayList listData;

    public MainListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        adapter = new SearchAdapter(getActivity());
        adapter.setItemClickCallback(this);
        recView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onThumbnailClicked(int p) {
        ListItem item = (ListItem) listData.get(p);
        Intent i = new Intent(getActivity(), DetailActivity.class);
        Bundle extras = new Bundle();

        extras.putString(Constants.EXTRA_QUOTE, item.getTitle());
        extras.putString(Constants.EXTRA_ATTR, item.getSubtitle());

        i.putExtra(Constants.BUNDLE_EXTRAS, extras);
        startActivity(i);

    }

    @Override
    public void onLikeImageClicked(View v, int p) {
        ListItem item = (ListItem) listData.get(p);
        if (item.isFavorite()) {
            item.setFavorite(false);
        } else {
            item.setFavorite(true);
        }
        adapter.setListData(listData);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onShareImageClicked(int p) {
        ListItem item = (ListItem) listData.get(p);

        Toast.makeText(getActivity(),item.getTitle()+" sharing!",Toast.LENGTH_SHORT).show();

        // pass new data to adapter and update
//        adapter.setListData(listData);
//        adapter.notifyDataSetChanged();
    }
}
