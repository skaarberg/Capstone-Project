package com.bikefriend.jrs.bikefriend.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bikefriend.jrs.bikefriend.R;
import com.bikefriend.jrs.bikefriend.model.Station;

import java.util.ArrayList;

/**
 * Created by skaar on 09.11.2017.
 */

public class FavoriteDetailedAdapter extends RecyclerView.Adapter<FavoriteDetailedAdapter.FavouriteViewHolder> {

    final private FavoriteDetailedAdapter.ListItemClickListener mOnClickListener;
    private ArrayList<Station> favoriteList;

    public FavoriteDetailedAdapter(Context context, ArrayList<Station> favoriteList) {
        this.favoriteList = favoriteList;
        mOnClickListener = (FavoriteDetailedAdapter.ListItemClickListener) context;
    }

    @Override
    public FavouriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_favorite_detailed;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        FavouriteViewHolder viewHolder = new FavouriteViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FavouriteViewHolder holder, int position) {
        holder.bind(favoriteList.get(position));
    }

    @Override
    public int getItemCount() {
        if(favoriteList != null)
            return favoriteList.size();
        else
            return 0;
    }

    class FavouriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtTitle;
        private TextView txtSubtitle;
        private TextView txtBikes;
        private TextView txtLocks;
        Context context;

        public FavouriteViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
            txtSubtitle = (TextView) itemView.findViewById(R.id.txt_subtitle);
            txtBikes = (TextView) itemView.findViewById(R.id.txt_bikes);
            txtLocks = (TextView) itemView.findViewById(R.id.txt_locks);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }

        void bind(Station station) {
            txtTitle.setText(station.getTitle());
            txtSubtitle.setText(station.getSubtitle());
            txtBikes.setText(station.getBikes() + "");
            txtLocks.setText(station.getLocks() + "");
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
}
