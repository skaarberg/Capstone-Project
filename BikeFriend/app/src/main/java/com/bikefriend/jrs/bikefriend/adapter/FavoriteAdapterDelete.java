package com.bikefriend.jrs.bikefriend.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bikefriend.jrs.bikefriend.R;
import com.bikefriend.jrs.bikefriend.model.Station;

import java.util.ArrayList;

/**
 * Created by skaar on 08.11.2017.
 */

public class FavoriteAdapterDelete extends RecyclerView.Adapter<FavoriteAdapterDelete.FavouriteViewHolder>{
    final private FavoriteAdapterDelete.ListItemClickListener mOnClickListener;
    private ArrayList<Station> favoriteList;

    public FavoriteAdapterDelete(Context context, ArrayList<Station> favoriteList) {
        this.favoriteList = favoriteList;
        mOnClickListener = (FavoriteAdapterDelete.ListItemClickListener) context;
    }

    @Override
    public FavouriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_favorite_delete;
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
        return favoriteList.size();
    }

    class FavouriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtName;
        private ImageButton imageButton;
        Context context;

        public FavouriteViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            txtName = (TextView) itemView.findViewById(R.id.txt_title);
            imageButton = (ImageButton) itemView.findViewById(R.id.imageButton);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnClickListener.onListItemClick(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }

        void bind(Station station) {
            txtName.setText(station.getTitle());
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
}
