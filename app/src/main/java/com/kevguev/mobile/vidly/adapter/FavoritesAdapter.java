package com.kevguev.mobile.vidly.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kevguev.mobile.vidly.R;
import com.kevguev.mobile.vidly.adapter.realm.RealmRecyclerViewAdapter;
import com.kevguev.mobile.vidly.model.RealmVideo;
import com.kevguev.mobile.vidly.realm.RealmController;
import com.squareup.picasso.Picasso;

import io.realm.Realm;

/**
 * Created by Kevin Guevara on 7/7/2017.
 */

public class FavoritesAdapter extends RealmRecyclerViewAdapter<RealmVideo> {

    final Context context;
    private Realm realm;
    private LayoutInflater inflater;

    public FavoritesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_items, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        realm = RealmController.getInstance().getRealm();

        final RealmVideo video = getItem(position);
        //cast the generic view holder to our specific one
        final CardViewHolder holder = (CardViewHolder) viewHolder;

        holder.title.setText(video.getText());

        if (video.getImageSrc() != null) {
            Picasso.with(context)
                    .load(video.getImageSrc())
                    .fit()
                    .into(holder.thumbnail);
        }

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "thumbnail clicked", Toast.LENGTH_SHORT).show();
            }
        });

        holder.likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "like clicked", Toast.LENGTH_SHORT).show();
            }
        });

        holder.shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "share clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView thumbnail;
        private ImageView likeImageView;
        private ImageView shareImageView;

        public CardViewHolder(View itemView) {
            // standard view holder pattern with Butterknife view injection
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.titleTextView);
            thumbnail = (ImageView) itemView.findViewById(R.id.coverImageView);
            likeImageView = (ImageView) itemView.findViewById(R.id.likeImageView);
            shareImageView = (ImageView) itemView.findViewById(R.id.shareImageView);
        }
    }
}