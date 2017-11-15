package com.bikefriend.jrs.bikefriend.widget;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bikefriend.jrs.bikefriend.R;
import com.bikefriend.jrs.bikefriend.model.Station;
import com.bikefriend.jrs.bikefriend.provider.BikeContract;
import com.bikefriend.jrs.bikefriend.provider.BikeDbHelper;

import java.util.ArrayList;
import java.util.List;

import static com.bikefriend.jrs.bikefriend.provider.BikeContract.BASE_CONTENT_URI;
import static com.bikefriend.jrs.bikefriend.provider.BikeContract.PATH_STATIONS;

/**
 * Created by jrs on 18/07/2017.
 */

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    List<Station> mCollection = new ArrayList<>();
    Context mContext = null;
    Cursor cursor;

    public WidgetDataProvider(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mCollection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.list_item_favorite_widget);
        view.setTextViewText(R.id.txt_title, mCollection.get(position).getTitle());
        view.setTextViewText(R.id.txt_subtitle, mCollection.get(position).getSubtitle());
        view.setTextViewText(R.id.txt_bikes, mCollection.get(position).getBikes() + "");
        view.setTextViewText(R.id.txt_locks, mCollection.get(position).getLocks() + "");
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        mCollection.clear();
        mCollection = new ArrayList<>();

        final long identityToken = Binder.clearCallingIdentity();
        Uri BASE_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STATIONS).build();
        if (cursor != null) cursor.close();

        cursor = mContext.getContentResolver().query(
                BASE_URI,
                null,
                BikeDbHelper.getFavoritesQuery(),
                null,
                null
        );

        try {
            while (cursor.moveToNext()) {
                mCollection.add(BikeContract.StationEntry.fromCursor(cursor));
            }
        } finally {
            cursor.close();
        }
        Binder.restoreCallingIdentity(identityToken);
    }
}