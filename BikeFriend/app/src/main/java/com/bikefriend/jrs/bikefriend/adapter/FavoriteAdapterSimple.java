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
 * Created by jrs on 07/11/2017.
 */

public class FavoriteAdapterSimple extends RecyclerView.Adapter<FavoriteAdapterSimple.FavouriteViewHolder> {

    final private FavoriteAdapterSimple.ListItemClickListener mOnClickListener;
    private ArrayList<Station> favoriteList;

    public FavoriteAdapterSimple(Context context, ArrayList<Station> favoriteList) {
        this.favoriteList = favoriteList;
        mOnClickListener = (FavoriteAdapterSimple.ListItemClickListener) context;
    }

    public void swapData(ArrayList<Station> data)
    {
        if(data == null || data.size()==0)
            return;
        if (favoriteList != null && favoriteList.size()>0)
            favoriteList.clear();
        if(favoriteList == null)
            favoriteList = new ArrayList<>();
        favoriteList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public FavouriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_favorite_simple;
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

        private TextView txtName;
        Context context;

        public FavouriteViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            txtName = (TextView) itemView.findViewById(R.id.txt_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }

        void bind(Station station) {
            txtName.setText(station.getTitle());
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
}
