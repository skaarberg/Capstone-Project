package com.bikefriend.jrs.bikefriend;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bikefriend.jrs.bikefriend.adapter.FavoriteAdapterSimple;
import com.bikefriend.jrs.bikefriend.model.Station;
import com.bikefriend.jrs.bikefriend.provider.BikeContract;
import com.bikefriend.jrs.bikefriend.provider.BikeDbHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jrs on 06/11/2017.
 */

public class AddFavoriteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        FavoriteAdapterSimple.ListItemClickListener{

    public final static int INITIAL_LOAD = 1;
    public final static int FILTER_LOAD = 2;
    public final static String FILTER_STRING = "filter";

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.btn_search) ImageButton btnSearch;
    @BindView(R.id.editText) EditText editText;

    Parcelable listState;
    private ArrayList<Station> favoriteList;
    private GridLayoutManager layoutManager;
    private FavoriteAdapterSimple favoritesAdapter;
    private BikeDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new BikeDbHelper(this);

        setContentView(R.layout.activity_add_favorite);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_add_favorite);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterList(editText.getText().toString());
            }
        });

        if(savedInstanceState != null){
            layoutManager = new GridLayoutManager(this, 1);
            recyclerView.setLayoutManager(layoutManager);
            if(savedInstanceState != null){
                listState = savedInstanceState.getParcelable("manager");
                recyclerView.getLayoutManager().onRestoreInstanceState(listState);
                favoriteList = (ArrayList)savedInstanceState.getSerializable("list");
            }

            favoritesAdapter = new FavoriteAdapterSimple(this, favoriteList);
        }
        else
            getLoaderManager().initLoader(INITIAL_LOAD, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("manager", recyclerView.getLayoutManager().onSaveInstanceState());
        outState.putSerializable("list", favoriteList);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public ActionBar getSupportActionBar() {
        ActionBar actionBar = super.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        return actionBar;
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

    private void setupRecyclerView(){
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        favoritesAdapter = new FavoriteAdapterSimple(this, favoriteList);
        recyclerView.setAdapter(favoritesAdapter);
    }

    private void filterList(String filter){
        Bundle bundle = new Bundle();
        bundle.putString(FILTER_STRING, filter);
        if(TextUtils.isEmpty(filter))
            getLoaderManager().restartLoader(INITIAL_LOAD, bundle, this);
        else
            getLoaderManager().restartLoader(FILTER_LOAD, bundle, this);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == INITIAL_LOAD) {
            CursorLoader cursorLoader = new CursorLoader(this,
                    BikeContract.StationEntry.CONTENT_URI, null, null, null, null);
            return cursorLoader;
        }
        else if(id == FILTER_LOAD) {
            String filter = args.getString(FILTER_STRING);
            String arg = "'%" + filter + "%'";
            CursorLoader cursorLoader = new CursorLoader(this,
                    BikeContract.StationEntry.CONTENT_URI, null, BikeContract.StationEntry.COLUMN_TITLE + " LIKE " + arg, null, null);
            return cursorLoader;
        }
        else return null;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        if(layoutManager == null)
            setupRecyclerView();
        favoriteList = new ArrayList<>();
        while (data.moveToNext()){
            favoriteList.add(BikeContract.StationEntry.fromCursor(data));
        }
        favoritesAdapter.swapData(favoriteList);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        dbHelper.insertFavorite(favoriteList.get(clickedItemIndex).getId());
        setResult(RESULT_OK);
        finish();
    }
}
