package com.bikefriend.jrs.bikefriend.provider;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.bikefriend.jrs.bikefriend.model.Station;

/**
 * Created by skaar on 09.07.2017.
 */

public class BikeContract {

    public static final String AUTHORITY = "com.bikefriend.jrs.bikefriend";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_STATIONS= "stations";
    public static final String PATH_FAVORITES= "favorites";

    public static final class StationEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STATIONS).build();
        public static final String TABLE_NAME = "stations";
        public static final String COLUMN_ID = "stationId";
        public static final String COLUMN_IN_SERVICE = "inService";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SUBTITLE = "subtitle";
        public static final String COLUMN_NUMBER_OF_LOCKS = "numberOfLocks";
        public static final String COLUMN_AVAILABLE_BIKES = "availableBikes";
        public static final String COLUMN_AVAILABLE_LOCKS = "availableLocks";
        public static final String COLUMN_LATITUDE ="latitute";
        public static final String COLUMN_LONGITUDE ="longitude";

        public static Station fromCursor(Cursor c){
            return new Station(
                    c.getString(c.getColumnIndex(StationEntry.COLUMN_TITLE)),
                    c.getString(c.getColumnIndex(StationEntry.COLUMN_SUBTITLE)),
                    c.getInt(c.getColumnIndex(StationEntry.COLUMN_AVAILABLE_BIKES)),
                    c.getInt(c.getColumnIndex(StationEntry.COLUMN_AVAILABLE_LOCKS)),
                    c.getInt(c.getColumnIndex(StationEntry.COLUMN_ID)),
                    c.getDouble(c.getColumnIndex(StationEntry.COLUMN_LATITUDE)),
                    c.getDouble(c.getColumnIndex(StationEntry.COLUMN_LONGITUDE))
            );
        }
    }

    public static final class FavoriteEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_ID = "stationId";
    }
}
