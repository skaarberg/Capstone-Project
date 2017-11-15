package com.bikefriend.jrs.bikefriend.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

/**
 * Created by skaar on 09.07.2017.
 */

public class BikeContentProvider extends ContentProvider {

    public static final int STATIONS = 100;
    public static final int STATIONS_WITH_ID = 101;
    public static final int STATION_AVAILABILITIES = 200;
    public static final int STATION_AVAILABILITIES_WITH_ID = 201;
    public static final int FAVORITES = 600;
    public static final int FAVORITES_WITH_ID = 601;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String TAG =BikeContentProvider.class.getName();

    private BikeDbHelper bikeDbHelper;
    private Context mContext;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BikeContract.AUTHORITY, BikeContract.PATH_STATIONS, STATIONS);
        uriMatcher.addURI(BikeContract.AUTHORITY, BikeContract.PATH_STATIONS + "/#", STATIONS_WITH_ID);
        uriMatcher.addURI(BikeContract.AUTHORITY, BikeContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(BikeContract.AUTHORITY, BikeContract.PATH_FAVORITES + "/#", FAVORITES_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        bikeDbHelper = new BikeDbHelper(mContext);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = bikeDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case STATIONS:
                retCursor = db.query(BikeContract.StationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case STATIONS_WITH_ID:
                String stationId = uri.getPathSegments().get(1);
                retCursor = db.query(BikeContract.StationEntry.TABLE_NAME,
                        projection,
                        "_id=?",
                        new String[]{stationId},
                        null,
                        null,
                        sortOrder);
                break;
            case STATION_AVAILABILITIES:
                retCursor = db.query(BikeContract.StationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case STATION_AVAILABILITIES_WITH_ID:
                String stationAvailabilityId = uri.getPathSegments().get(1);
                retCursor = db.query(BikeContract.StationEntry.TABLE_NAME,
                        projection,
                        "_id=?",
                        new String[]{stationAvailabilityId},
                        null,
                        null,
                        sortOrder);
                break;

            case FAVORITES:
                retCursor = db.query(BikeContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = bikeDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri = null;
        long id = 0;
        switch (match) {
            case STATIONS:
                id = db.insertWithOnConflict(BikeContract.StationEntry.TABLE_NAME, null,
                        values, CONFLICT_REPLACE);
                break;
            case FAVORITES:
                id = db.insertWithOnConflict(BikeContract.FavoriteEntry.TABLE_NAME, null,
                        values, CONFLICT_REPLACE);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs){
        return bikeDbHelper.getWritableDatabase().delete(BikeContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = bikeDbHelper.getReadableDatabase();

        db.update(BikeContract.StationEntry.TABLE_NAME, values, selection, null);

        return 0;
    }
}
