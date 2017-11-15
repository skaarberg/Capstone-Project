package com.bikefriend.jrs.bikefriend;

import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bikefriend.jrs.bikefriend.adapter.FavoriteAdapterDelete;
import com.bikefriend.jrs.bikefriend.model.Station;
import com.bikefriend.jrs.bikefriend.provider.BikeContract;
import com.bikefriend.jrs.bikefriend.provider.BikeDbHelper;
import com.bikefriend.jrs.bikefriend.widget.BikeAppWidgetProvider;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jrs on 06/11/2017.
 */

public class SettingsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        FavoriteAdapterDelete.ListItemClickListener{

    public int randomNumber = 1;
    public static int REQUEST_CODE_ADD_FAVORITE = 1;
    public static int GET_FAVORITE_IDS = 2;
    public static final String PREF_SYNC_INTERVAL = "sync_interval";

    @BindView(R.id.btn_add_favorite) Button btnAddFavorite;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.txt_edit_sync_interval) EditText txtSyncInterval;
    @BindView(R.id.txt_no_favorites) TextView txtNoFavorites;

    Parcelable listState;
    private GridLayoutManager layoutManager;
    private FavoriteAdapterDelete favoritesAdapter;
    private ArrayList<Station> favoriteList;
    private BikeDbHelper dbHelper;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ADD_FAVORITE){
            if(resultCode == RESULT_OK) {
                loadFavorites();
                updateWidget();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new BikeDbHelper(this);

        favoriteList = new ArrayList<>();

        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        if(savedInstanceState != null){
            listState = savedInstanceState.getParcelable("manager");
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
            favoriteList = (ArrayList)savedInstanceState.getSerializable("list");
        }

        favoritesAdapter = new FavoriteAdapterDelete(this, favoriteList);

        recyclerView.setAdapter(favoritesAdapter);

        btnAddFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddFavoriteActivity();
            }
        });

        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        txtSyncInterval.setText(sharedPref.getLong(PREF_SYNC_INTERVAL, 0) + "");
        txtSyncInterval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setSyncInterval();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    @Nullable
    @Override
    public ActionBar getSupportActionBar() {
        ActionBar actionBar = super.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        return actionBar;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("manager", recyclerView.getLayoutManager().onSaveInstanceState());
        outState.putSerializable("list", favoriteList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startAddFavoriteActivity(){
        Intent intent = new Intent(this, AddFavoriteActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_FAVORITE);
    }

    private void loadFavorites(){
        getLoaderManager().restartLoader(GET_FAVORITE_IDS, null, this);
    }

    private void reloadFavorites(){
        favoritesAdapter.notifyDataSetChanged();
        if(favoriteList.size() > 0){
            recyclerView.setVisibility(View.VISIBLE);
            txtNoFavorites.setVisibility(View.GONE);
        }
        else {
            recyclerView.setVisibility(View.GONE);
            txtNoFavorites.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == GET_FAVORITE_IDS) {
            CursorLoader cursorLoader = new CursorLoader(this,
                    BikeContract.StationEntry.CONTENT_URI, null, BikeDbHelper.getFavoritesQuery(), null, null);
            return cursorLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        favoriteList.clear();
        if(loader.getId() == GET_FAVORITE_IDS){
            while(cursor.moveToNext()){
                favoriteList.add(BikeContract.StationEntry.fromCursor(cursor));
            }
        }
        reloadFavorites();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Station station = favoriteList.remove(clickedItemIndex);
        dbHelper.removeFavorite(station.getId());
        favoritesAdapter.notifyDataSetChanged();
    }

    private void setSyncInterval(){
        if(TextUtils.isEmpty(txtSyncInterval.getText()))
            return;
        try{
            int interval = Integer.parseInt(txtSyncInterval.getText().toString());
            if(interval == 0) {
                interval = 1;
                txtSyncInterval.setText(interval + "");
            }
            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            sharedPref.edit().putLong(PREF_SYNC_INTERVAL, interval).apply();
        }
        catch (NumberFormatException e){

        }
    }

    private void updateWidget(){
        Intent intent = new Intent(this,BikeAppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), BikeAppWidgetProvider.class));
        intent.putExtra("random", randomNumber);
        randomNumber++;
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);

        for (int id: ids) {
            AppWidgetManager.getInstance(this).notifyAppWidgetViewDataChanged(id, R.layout.bike_widget);
        }
    }
}
