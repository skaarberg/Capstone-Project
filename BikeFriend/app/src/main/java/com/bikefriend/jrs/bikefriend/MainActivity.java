package com.bikefriend.jrs.bikefriend;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bikefriend.jrs.bikefriend.adapter.FavoriteDetailedAdapter;
import com.bikefriend.jrs.bikefriend.fragment.MainFragment;
import com.bikefriend.jrs.bikefriend.fragment.MapFragment;
import com.bikefriend.jrs.bikefriend.model.Station;
import com.bikefriend.jrs.bikefriend.provider.BikeContract;
import com.bikefriend.jrs.bikefriend.provider.BikeDbHelper;

import org.joda.time.DateTime;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bikefriend.jrs.bikefriend.MapActivity.BUNDLE_MARKER_TAG;
import static com.bikefriend.jrs.bikefriend.MapActivity.GET_STATION;
import static com.bikefriend.jrs.bikefriend.MapActivity.GET_STATIONS_WITH_AVAILABLE_BIKES;
import static com.bikefriend.jrs.bikefriend.MapActivity.GET_STATIONS_WITH_AVAILABLE_LOCKS;
import static com.bikefriend.jrs.bikefriend.SettingsActivity.PREF_SYNC_INTERVAL;

public class MainActivity extends BikeStatusActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        FavoriteDetailedAdapter.ListItemClickListener{

    public static final String MAP_INTENT_EXTRA = "mapIntentExtra";
    public static final String GET_BIKE = "getBike";
    public static int GET_FAVORITES = 1;

    @BindView(R.id.container) LinearLayout container;

    private ArrayList<Station> favoriteList;
    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        long lastSync = sharedPref.getLong(PREF_LAST_STATUS_SYNC, 0);
        long syncInterval = sharedPref.getLong(PREF_SYNC_INTERVAL, 1);
        long now = DateTime.now().getMillis();
        long millisInScheduledUpdate = 60000 * syncInterval;

        if(now - lastSync < millisInScheduledUpdate) {
            bikeStatusSynced();
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        favoriteList = new ArrayList<>();

        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(GET_FAVORITES, null, this);
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
        if(id == GET_FAVORITES) {
            CursorLoader cursorLoader = new CursorLoader(this,
                    BikeContract.StationEntry.CONTENT_URI, null, BikeDbHelper.getFavoritesQuery(), null, null);
            return cursorLoader;
        }
        else if(id == GET_STATION) {
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
        if(loader.getId() == GET_FAVORITES) {
            favoriteList.clear();
            while(cursor.moveToNext()){
                favoriteList.add(BikeContract.StationEntry.fromCursor(cursor));
            }
            MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
            fragment.setData(favoriteList);
        }
        else if(loader.getId() == GET_STATIONS_WITH_AVAILABLE_BIKES || loader.getId() == GET_STATIONS_WITH_AVAILABLE_LOCKS)
            mapFragment.addStationsToMap(cursor);
        else {
            cursor.moveToFirst();
            mapFragment.setCurrentStation(BikeContract.StationEntry.fromCursor(cursor));
            mapFragment.showInfoWindow();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + favoriteList.get(clickedItemIndex).getCenter().getLatitude() + "," + favoriteList.get(clickedItemIndex).getCenter().getLongitude() + "&mode=w");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public MapFragment getMapFragment(){
        return mapFragment;
    }

    @Override
    public void baseStatusSynced(){
        getLoaderManager().restartLoader(GET_FAVORITES, null, this);
        super.baseStatusSynced();
    }

    @Override
    public void bikeStatusSynced() {
        if(mapFragment != null)
            getLoaderManager().restartLoader(GET_STATIONS_WITH_AVAILABLE_BIKES, null, this);
        container.setVisibility(View.VISIBLE);
    }

    public void reloadWithBikes(){
        favoriteList.clear();
        getLoaderManager().restartLoader(GET_STATIONS_WITH_AVAILABLE_BIKES, null, this);
    }

    public void reloadWithLocks(){
        favoriteList.clear();
        getLoaderManager().restartLoader(GET_STATIONS_WITH_AVAILABLE_LOCKS, null, this);
    }
}
