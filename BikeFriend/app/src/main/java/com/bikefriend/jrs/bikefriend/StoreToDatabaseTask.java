package com.bikefriend.jrs.bikefriend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.bikefriend.jrs.bikefriend.model.Availability;
import com.bikefriend.jrs.bikefriend.model.Center;
import com.bikefriend.jrs.bikefriend.model.Station;
import com.bikefriend.jrs.bikefriend.provider.BikeDbHelper;
import com.bikefriend.jrs.bikefriend.service.BikeJobService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.bikefriend.jrs.bikefriend.BikeStatusActivity.PREF_LAST_BASE_SYNC;
import static com.bikefriend.jrs.bikefriend.BikeStatusActivity.PREF_LAST_STATUS_SYNC;
import static com.bikefriend.jrs.bikefriend.service.BikeJobService.DATA_SYNC_ACTION;
import static com.bikefriend.jrs.bikefriend.service.BikeJobService.DATA_SYNC_ACTION_TYPE;
import static com.bikefriend.jrs.bikefriend.service.RequestTask.FETCH_BASE_DATA;
import static com.bikefriend.jrs.bikefriend.service.RequestTask.FETCH_BIKE_STATUS;
import static com.bikefriend.jrs.bikefriend.service.RequestTask.FETCH_SYSTEM_STATUS;

/**
 * Created by skaar on 12.11.2017.
 */

public class StoreToDatabaseTask extends AsyncTask<String, String, String> {

    Context context;
    BikeDbHelper dbHelper;
    String currentRequestType;

    public StoreToDatabaseTask(Context context){
        this.context = context;
        dbHelper = new BikeDbHelper(context);
    }

    @Override
    protected String doInBackground(String... strings) {
        currentRequestType = strings[0];

        switch (currentRequestType){
            case FETCH_BIKE_STATUS:
                bikeStatusSynced(strings[1]);
                break;
            case FETCH_BASE_DATA:
                baseDataSynced(strings[1]);
                break;
            case FETCH_SYSTEM_STATUS:
                systemStatusSynced(strings[1]);
        }

        return currentRequestType;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Intent intent = new Intent();

        switch (currentRequestType){
            case FETCH_BIKE_STATUS:
                intent.setAction(DATA_SYNC_ACTION);
                intent.putExtra(DATA_SYNC_ACTION_TYPE, BikeJobService.STATUS_DATA_SYNC_ENDED);
                context.sendBroadcast(intent);
                break;
            case FETCH_BASE_DATA:
                SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                long millis = DateTime.now().getMillis();
                sharedPref.edit().putLong(PREF_LAST_BASE_SYNC, millis);
                intent.setAction(DATA_SYNC_ACTION);
                intent.putExtra(DATA_SYNC_ACTION_TYPE, BikeJobService.BASE_DATA_SYNC_ENDED);
                context.sendBroadcast(intent);
                break;
            case FETCH_SYSTEM_STATUS:
                intent.setAction(DATA_SYNC_ACTION);
                intent.putExtra(DATA_SYNC_ACTION_TYPE, BikeJobService.SYSTEM_STATUS_SYNC_ENDED);
                context.sendBroadcast(intent);
                break;
        }
    }

    public void setSystemIsClosed(boolean systemIsClosed) {

    }

    private void systemStatusSynced(String result){
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = result;
        mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
            setSystemIsClosed(false);
        }
        JsonNode status = rootNode.get("status");
        JsonNode systemStatus = status.get("all_stations_closed");
        setSystemIsClosed(systemStatus.asBoolean());
    }

    private void baseDataSynced(String result){
        JSONObject jb = null;
        JSONArray stations = null;
        try {
            jb = new JSONObject(result);
            stations = jb.getJSONArray("stations");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(stations != null) {
            for (int i=0; i<stations.length(); i++) {
                try {
                    JSONObject object = stations.getJSONObject(i);
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        Station obj = mapper.readValue(object.toString(), Station.class);
                        Center c = mapper.readValue(object.get("center").toString(), Center.class);
                        obj.setCenter(c);
                        dbHelper.insertStation(obj);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().putLong(PREF_LAST_BASE_SYNC, DateTime.now().getMillis()).apply();
    }

    private void bikeStatusSynced(String result){
        JSONObject jb = null;
        JSONArray stations = null;
        try {
            jb = new JSONObject(result);
            stations = jb.getJSONArray("stations");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(stations != null) {
            for (int i=0; i<stations.length(); i++) {
                try {
                    JSONObject object = stations.getJSONObject(i);
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        int stationId = object.getInt("id");
                        Availability obj = mapper.readValue(object.getJSONObject("availability").toString(), Availability.class);
                        dbHelper.updateAvailability(stationId, obj);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().putLong(PREF_LAST_STATUS_SYNC, DateTime.now().getMillis()).apply();
    }
}
