package com.bikefriend.jrs.bikefriend.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bikefriend.jrs.bikefriend.MainActivity;
import com.bikefriend.jrs.bikefriend.MapActivity;
import com.bikefriend.jrs.bikefriend.R;
import com.bikefriend.jrs.bikefriend.adapter.FavoriteDetailedAdapter;
import com.bikefriend.jrs.bikefriend.model.Station;
import com.bikefriend.jrs.bikefriend.provider.BikeDbHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by skaar on 14.11.2017.
 */

public class MainFragment extends Fragment implements FavoriteDetailedAdapter.ListItemClickListener{

    public interface MapType {
        public void showBikes();
        public void showLocks();
    }

    public static final String MAP_INTENT_EXTRA = "mapIntentExtra";
    public static final String GET_BIKE = "getBike";
    public static final String PARK_BIKE = "parkBike";

    @BindView(R.id.btn_get_bike)
    Button btnGetBike;
    @BindView(R.id.btn_park_bike) Button btnParkBike;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.txt_no_favorites)
    TextView txtNoFavorites;

    Parcelable listState;
    private GridLayoutManager layoutManager;
    private FavoriteDetailedAdapter favoritesAdapter;
    private ArrayList<Station> favoriteList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, root);

        btnGetBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMapActivity(GET_BIKE);
            }
        });

        btnParkBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMapActivity(PARK_BIKE);
            }
        });

        favoriteList = new ArrayList<>();

        layoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        if(savedInstanceState != null){
            listState = savedInstanceState.getParcelable("manager");
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
            favoriteList = (ArrayList)savedInstanceState.getSerializable("list");
        }

        favoritesAdapter = new FavoriteDetailedAdapter(getContext(), favoriteList);

        recyclerView.setAdapter(favoritesAdapter);

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("manager", recyclerView.getLayoutManager().onSaveInstanceState());
        outState.putSerializable("list", favoriteList);
        super.onSaveInstanceState(outState);
    }

    private void startMapActivity(String type){
        MainActivity parent = (MainActivity)getActivity();
        MapFragment map = parent.getMapFragment();
        if(map == null) {
            Intent intent = new Intent(getActivity(), MapActivity.class);
            intent.putExtra(MAP_INTENT_EXTRA, type);
            startActivity(intent);
        }
        else{
            if(type.equals(GET_BIKE))
                map.showBikes();
            else
                map.showLocks();
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + favoriteList.get(clickedItemIndex).getCenter().getLatitude() + "," + favoriteList.get(clickedItemIndex).getCenter().getLongitude() + "&mode=w");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void setData(ArrayList<Station> favorites){
        favoriteList = favorites;
        favoritesAdapter = new FavoriteDetailedAdapter(getContext(), favoriteList);
        recyclerView.setAdapter(favoritesAdapter);
    }
}
