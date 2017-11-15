package com.bikefriend.jrs.bikefriend;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bikefriend.jrs.bikefriend.fragment.MapFragment;
import com.bikefriend.jrs.bikefriend.provider.BikeContract;

import static com.bikefriend.jrs.bikefriend.MainActivity.GET_BIKE;
import static com.bikefriend.jrs.bikefriend.MainActivity.MAP_INTENT_EXTRA;

/**
 * Created by jrs on 25/10/2017.
 */

public class MapActivity extends BikeStatusActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public final static int GET_STATIONS_WITH_AVAILABLE_BIKES = 11;
    public final static int GET_STATIONS_WITH_AVAILABLE_LOCKS = 12;
    public final static int GET_STATION = 13;
    public final static String BUNDLE_MARKER_TAG = "MarkerTag";

    private String mapType;

    MapFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapType = getIntent().getStringExtra(MAP_INTENT_EXTRA);
        if(mapType.equals(GET_BIKE)) {
            mapType = "b";
            getSupportActionBar().setTitle(R.string.available_bikes);
            getLoaderManager().restartLoader(GET_STATIONS_WITH_AVAILABLE_BIKES, null, this);
        }
        else {
            mapType = "w";
            getSupportActionBar().setTitle(R.string.available_locks);
            getLoaderManager().restartLoader(GET_STATIONS_WITH_AVAILABLE_LOCKS, null, this);
        }

        fragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_menu){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == GET_STATION) {
            int key = args.getInt(BUNDLE_MARKER_TAG);
            CursorLoader cursorLoader = new CursorLoader(this,
                    BikeContract.StationEntry.CONTENT_URI, null, BikeContract.StationEntry.COLUMN_ID + " = ?", new String[]{key + ""}, null);
            return cursorLoader;
        }
        else if(id == GET_STATIONS_WITH_AVAILABLE_BIKES) {
            CursorLoader cursorLoader = new CursorLoader(this,
                    BikeContract.StationEntry.CONTENT_URI, null, BikeContract.StationEntry.COLUMN_AVAILABLE_BIKES + " > 0", null, null);
            return cursorLoader;
        }
        else if(id == GET_STATIONS_WITH_AVAILABLE_LOCKS) {
            CursorLoader cursorLoader = new CursorLoader(this,
                    BikeContract.StationEntry.CONTENT_URI, null, BikeContract.StationEntry.COLUMN_AVAILABLE_LOCKS + " > 0", null, null);
            return cursorLoader;
        }
        else
            return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(loader.getId() == GET_STATIONS_WITH_AVAILABLE_BIKES || loader.getId() == GET_STATIONS_WITH_AVAILABLE_LOCKS)
            fragment.addStationsToMap(cursor);
        else {
            cursor.moveToFirst();
            fragment.setCurrentStation(BikeContract.StationEntry.fromCursor(cursor));
            fragment.showInfoWindow();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public String getMapType(){
        return mapType;
    }

    public void setMapType(String mapType){
        this.mapType = mapType;
    }
}