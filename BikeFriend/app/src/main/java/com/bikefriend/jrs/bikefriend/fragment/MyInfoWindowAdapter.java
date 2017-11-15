package com.bikefriend.jrs.bikefriend.fragment;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bikefriend.jrs.bikefriend.R;
import com.bikefriend.jrs.bikefriend.model.Station;
import com.bikefriend.jrs.bikefriend.provider.BikeDbHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by jrs on 31/10/2017.
 */

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

    MapFragment mapFragment;
    BikeDbHelper bikeDbHelper;
    private final View mContents;

    MyInfoWindowAdapter(MapFragment mapFragment) {
        mContents = mapFragment.getActivity().getLayoutInflater().inflate(R.layout.marker_info_content, null);
        bikeDbHelper = new BikeDbHelper(mapFragment.getActivity());
        this.mapFragment = mapFragment;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        render(mContents);
        return mContents;
    }

    private void render(View view) {
        TextView txtBikes = view.findViewById(R.id.txt_available_bikes);
        TextView txtLocks = view.findViewById(R.id.txt_available_locks);
        TextView txtTitle = view.findViewById(R.id.txt_title);
        TextView txtSubtitle = view.findViewById(R.id.txt_subtitle);

        Station station = mapFragment.getCurrentMarkerStation();
        txtTitle.setText(station.getTitle());
        txtSubtitle.setText(station.getSubtitle());
        txtBikes.setText(station.getBikes() + "");
        txtLocks.setText(station.getLocks() + "");
        if(txtSubtitle.getText().equals(""))
            txtSubtitle.setVisibility(View.GONE);
    }
}
