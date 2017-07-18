package com.kevguev.mobile.vidly.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kevguev.mobile.vidly.R;
import com.kevguev.mobile.vidly.model.ListItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Guevara on 5/13/2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {

    private List<ListItem> listData;
    private LayoutInflater inflater;
    private Context context;

    //communication channel via activity
    private ItemClickCallback itemClickCallback;

    public interface ItemClickCallback {
        void onThumbnailClicked(int p);

        void onLikeImageClicked(View v, int p);

        void onShareImageClicked(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }

    public SearchAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listData = new ArrayList<ListItem>();
    }

    public SearchAdapter(List<ListItem> listdata, Context c) {
        this.inflater = LayoutInflater.from(c);
        this.listData = listdata;
    }

    @Override
    public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cardview_items, parent, false);

        return new SearchHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchHolder holder, int position) {

        ListItem item = listData.get(position);
        holder.title.setText(item.getTitle());
        Picasso.with(context).load(item.getImgUrl()).fit().into(holder.thumbnail);
        if (item.isFavorite()) {
            holder.likeImageView.setImageResource(R.drawable.ic_star_black_24dp);
        } else {
            holder.likeImageView.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
    }

    public void setListData(ArrayList<ListItem> exerciseList) {
        this.listData.clear();
        this.listData.addAll(exerciseList);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    //needs a viewholder
    //assign data to appropriate place in recycler view
    public class SearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private ImageView thumbnail;
        private ImageView likeImageView;
        private ImageView shareImageView;

        public SearchHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.titleTextView);
            //subTitle = (TextView) itemView.findViewById(R.id.lbl_item_sub_title);
            thumbnail = (ImageView) itemView.findViewById(R.id.coverImageView);
            thumbnail.setOnClickListener(this);
            likeImageView = (ImageView) itemView.findViewById(R.id.likeImageView);
            likeImageView.setOnClickListener(this);
            shareImageView = (ImageView) itemView.findViewById(R.id.shareImageView);
            shareImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.likeImageView) {
                itemClickCallback.onLikeImageClicked(likeImageView, getAdapterPosition());
            } else if (view.getId() == R.id.shareImageView) {
                itemClickCallback.onShareImageClicked(getAdapterPosition());
            } else if (view.getId() == R.id.coverImageView) {
                itemClickCallback.onThumbnailClicked(getAdapterPosition());
            }
        }
    }
}
