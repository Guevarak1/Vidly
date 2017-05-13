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

import java.util.List;

/**
 * Created by Kevin Guevara on 5/13/2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {

    private List<ListItem> listData;
    private LayoutInflater inflater;

    public SearchAdapter (List<ListItem> listdata, Context c){
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
        holder.icon.setImageResource(item.getImageResId());
        holder.title.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    //needs a viewholder
    //assign data to appropriate place in recycler view
    class SearchHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private ImageView icon;
        private View container;

        public SearchHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.tv_item_text);
            icon = (ImageView) itemView.findViewById(R.id.im_item_icon);
            container = itemView.findViewById(R.id.cont_item_root);
        }
    }
}
