package com.kevguev.mobile.vidly.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kevguev.mobile.vidly.ItemClickCallback;
import com.kevguev.mobile.vidly.R;
import com.kevguev.mobile.vidly.SharedPreferenceUtil;
import com.kevguev.mobile.vidly.model.ListItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Guevara on 7/7/2017.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.CardViewHolder> {

    final Context context;
    private LayoutInflater inflater;
    private List<ListItem> listData;
    SharedPreferenceUtil sharedPreferenceUtil;

    //communication channel via activity
    private ItemClickCallback itemClickCallback;

    public void setItemClickCallback(final ItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }


    public FavoritesAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listData = new ArrayList<ListItem>();
        sharedPreferenceUtil = new SharedPreferenceUtil();

    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.cardview_items, parent, false);
        return new CardViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {

        final ListItem video = listData.get(position);
        holder.title.setText(video.getTitle());

        if (video.getImgUrl() != null) {
            Picasso.with(context)
                    .load(video.getImgUrl())
                    .fit()
                    .into(holder.thumbnail);
        }
        holder.likeImageView.setImageResource(R.drawable.ic_star_black_24dp);
    }

    public void setListData(ArrayList<ListItem> exerciseList) {
        this.listData.clear();
        this.listData.addAll(exerciseList);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView title;
        private ImageView thumbnail;
        private ImageView likeImageView;
        private ImageView shareImageView;

        public CardViewHolder(View itemView) {
            // standard view holder pattern with Butterknife view injection
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.titleTextView);
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