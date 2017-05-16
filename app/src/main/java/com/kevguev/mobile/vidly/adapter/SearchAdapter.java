package com.kevguev.mobile.vidly.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kevguev.mobile.vidly.R;
import com.kevguev.mobile.vidly.model.ListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Guevara on 5/13/2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder>  {

    private List<ListItem> listData;
    private LayoutInflater inflater;

    //communication channel via activity
    private ItemClickCallback itemClickCallback;

    public interface  ItemClickCallback{
        void onItemClick(int p);
        void onSecondaryIconClick(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback){
        this.itemClickCallback = itemClickCallback;
    }

    public SearchAdapter(Context context){
        this.inflater = LayoutInflater.from(context);
        this.listData = new ArrayList<ListItem>();
    }

    public SearchAdapter(List<ListItem> listdata, Context c) {
        this.inflater = LayoutInflater.from(c);
        this.listData = listdata;
    }

    @Override
    public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);

        return new SearchHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchHolder holder, int position) {

        ListItem item = listData.get(position);
        holder.title.setText(item.getTitle());
        holder.subTitle.setText(item.getSubtitle());
        if (item.isFavorite()) {
            holder.secondaryIcon.setImageResource(R.drawable.ic_star_black_24dp);
        } else {
            holder.secondaryIcon.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
    }

    public void setListData(ArrayList<ListItem> exerciseList){
        this.listData.clear();
        this.listData.addAll(exerciseList);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    //needs a viewholder
    //assign data to appropriate place in recycler view
    class SearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private TextView subTitle;
        private ImageView thumbnail;
        private ImageView secondaryIcon;
        private View container;

        public SearchHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.lbl_item_text);
            subTitle = (TextView) itemView.findViewById(R.id.lbl_item_sub_title);
            thumbnail = (ImageView) itemView.findViewById(R.id.im_item_icon);
            secondaryIcon = (ImageView) itemView.findViewById(R.id.im_item_icon_secondary);
            secondaryIcon.setOnClickListener(this);
            container = itemView.findViewById(R.id.cont_item_root);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.cont_item_root) {
                itemClickCallback.onItemClick(getAdapterPosition());
            } else {
                itemClickCallback.onSecondaryIconClick(getAdapterPosition());

            }
        }
    }
}
