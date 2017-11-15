package com.bikefriend.jrs.bikefriend.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bikefriend.jrs.bikefriend.model.Availability;
import com.bikefriend.jrs.bikefriend.model.Station;

/**
 * Created by skaar on 09.07.2017.
 */

public class BikeDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bikefriend.db";
    private static final int DATABASE_VERSION = 20;

    private Context mContext;

    public BikeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_STATION_TABLE = "CREATE TABLE " + BikeContract.StationEntry.TABLE_NAME + " (" +
                BikeContract.StationEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                BikeContract.StationEntry.COLUMN_IN_SERVICE + " BOOLEAN NOT NULL," +
                BikeContract.StationEntry.COLUMN_TITLE + " STRING NOT NULL," +
                BikeContract.StationEntry.COLUMN_SUBTITLE + " STRING," +
                BikeContract.StationEntry.COLUMN_LATITUDE + " DOUBLE," +
                BikeContract.StationEntry.COLUMN_LONGITUDE + " DOUBLE," +
                BikeContract.StationEntry.COLUMN_NUMBER_OF_LOCKS + " INTEGER," +
                BikeContract.StationEntry.COLUMN_AVAILABLE_LOCKS + " INTEGER," +
                BikeContract.StationEntry.COLUMN_AVAILABLE_BIKES + " INTEGER);";

        sqLiteDatabase.execSQL(SQL_CREATE_STATION_TABLE);

        final String SQL_CREATE_STATION_FAVORITE = "CREATE TABLE " + BikeContract.FavoriteEntry.TABLE_NAME + " (" +
                BikeContract.FavoriteEntry.COLUMN_ID + " INTEGER PRIMARY KEY);";

        sqLiteDatabase.execSQL(SQL_CREATE_STATION_FAVORITE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BikeContract.StationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BikeContract.FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertStation(Station station){
        ContentValues contentValues = new ContentValues();
        contentValues.put(BikeContract.StationEntry.COLUMN_ID, station.getId());
        contentValues.put(BikeContract.StationEntry.COLUMN_IN_SERVICE, station.getInService());
        contentValues.put(BikeContract.StationEntry.COLUMN_NUMBER_OF_LOCKS, station.getId());
        contentValues.put(BikeContract.StationEntry.COLUMN_TITLE, station.getTitle());
        contentValues.put(BikeContract.StationEntry.COLUMN_SUBTITLE, station.getSubtitle());
        contentValues.put(BikeContract.StationEntry.COLUMN_LATITUDE, station.getCenter().getLatitude());
        contentValues.put(BikeContract.StationEntry.COLUMN_LONGITUDE, station.getCenter().getLongitude());
        mContext.getContentResolver().insert(BikeContract.StationEntry.CONTENT_URI, contentValues);
    }

    public void insertFavorite(int id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(BikeContract.FavoriteEntry.COLUMN_ID, id);
        mContext.getContentResolver().insert(BikeContract.FavoriteEntry.CONTENT_URI, contentValues);
    }

    public void updateAvailability(int stationId, Availability availability){
        ContentValues cv = new ContentValues();
        cv.put(BikeContract.StationEntry.COLUMN_AVAILABLE_LOCKS, availability.getLocks());
        cv.put(BikeContract.StationEntry.COLUMN_AVAILABLE_BIKES, availability.getBikes());
        mContext.getContentResolver().update(BikeContract.StationEntry.CONTENT_URI, cv, BikeContract.StationEntry.COLUMN_ID + "=" + stationId, null);
    }

    public static String getFavoritesQuery(){
        return "EXISTS (SELECT 1 FROM " +
                BikeContract.FavoriteEntry.TABLE_NAME +
                " WHERE " + BikeContract.FavoriteEntry.TABLE_NAME + "." + BikeContract.FavoriteEntry.COLUMN_ID +
                " = " +  BikeContract.StationEntry.TABLE_NAME + "." + BikeContract.StationEntry.COLUMN_ID + ")";
    }

    public void removeFavorite(int id){
        String[] args = new String[]{id + ""};
        mContext.getContentResolver().delete(BikeContract.FavoriteEntry.CONTENT_URI, BikeContract.StationEntry.COLUMN_ID + "= ?", args);
    }
}