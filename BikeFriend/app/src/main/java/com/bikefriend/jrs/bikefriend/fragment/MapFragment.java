package com.bikefriend.jrs.bikefriend.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bikefriend.jrs.bikefriend.MainActivity;
import com.bikefriend.jrs.bikefriend.MapActivity;
import com.bikefriend.jrs.bikefriend.R;
import com.bikefriend.jrs.bikefriend.model.Station;
import com.bikefriend.jrs.bikefriend.provider.BikeContract;
import com.bikefriend.jrs.bikefriend.provider.BikeDbHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;

import static com.bikefriend.jrs.bikefriend.MapActivity.BUNDLE_MARKER_TAG;
import static com.bikefriend.jrs.bikefriend.MapActivity.GET_STATION;

/**
 * Created by skaar on 14.11.2017.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener{

    private String type = "";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Marker clickedMarker;

    private GoogleMap mMap;

    private Station currentStation;

    private boolean haveTriedAddingMarkersToMap;
    private Cursor tempCursor;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, root);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return root;
    }

    public void showBikes() {
        if(getActivity().getClass().getName().equals(MapActivity.class.getName())) {
            MapActivity mapActivity = (MapActivity) getActivity();
            mapActivity.setMapType("w");
        }
        else {
            type = "w";
            mMap.clear();
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.reloadWithBikes();
        }
    }

    public void showLocks() {
        if(getActivity().getClass().getName().equals(MapActivity.class.getName())) {
            MapActivity mapActivity = (MapActivity) getActivity();
            mapActivity.setMapType("b");
        }
        else {
            type = "b";
            mMap.clear();
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.reloadWithLocks();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(59.9232619, 10.7421614), 13));
        enableMyLocation();
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(this));
        if(haveTriedAddingMarkersToMap) {
            addStationsToMap(tempCursor);
            haveTriedAddingMarkersToMap = false;
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    public void addStationsToMap(Cursor stationIdCursor){
        if(mMap != null) {
            while(stationIdCursor.moveToNext()){
                int id = stationIdCursor.getInt(stationIdCursor.getColumnIndex(BikeContract.StationEntry.COLUMN_ID));
                double lat = stationIdCursor.getDouble(stationIdCursor.getColumnIndex(BikeContract.StationEntry.COLUMN_LATITUDE));
                double lng = stationIdCursor.getDouble(stationIdCursor.getColumnIndex(BikeContract.StationEntry.COLUMN_LONGITUDE));
                addStationToMap(lat, lng, id);
            }
        }
        else {
            haveTriedAddingMarkersToMap = true;
            tempCursor = stationIdCursor;
        }
    }

    private void addStationToMap(double lat, double lng, int id) {
        try {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(lat, lng));
            markerOptions.title(id + "");
            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(id);
        }
        catch (Exception e){
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(getActivity().getClass().getName().equals(MapActivity.class.getName())) {
            MapActivity mapActivity = (MapActivity) getActivity();
            type = mapActivity.getMapType();
        }
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "&mode=" + type);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if(ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == getActivity().getPackageManager().PERMISSION_GRANTED){
            enableMyLocation();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        clickedMarker = marker;
        Bundle b = new Bundle();
        b.putInt(BUNDLE_MARKER_TAG, (int)marker.getTag());
        if(getActivity().getClass().getName().equals(MapActivity.class.getName())) {
            MapActivity mapActivity = (MapActivity) getActivity();
            mapActivity.getLoaderManager().restartLoader(GET_STATION, b, mapActivity);
        }
        else{
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.getLoaderManager().restartLoader(GET_STATION, b, mainActivity);
        }
        return true;
    }

    public Station getCurrentMarkerStation(){
        return currentStation;
    }

    public void showInfoWindow(){
        clickedMarker.showInfoWindow();
    }

    public void setCurrentStation(Station station){
        currentStation = station;
    }
}
