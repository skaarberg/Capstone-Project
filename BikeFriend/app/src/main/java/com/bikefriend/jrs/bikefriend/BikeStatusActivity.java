package com.bikefriend.jrs.bikefriend;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bikefriend.jrs.bikefriend.service.BikeJobService;
import com.bikefriend.jrs.bikefriend.service.MyBroadcastReceiver;
import com.bikefriend.jrs.bikefriend.widget.BikeAppWidgetProvider;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;

import org.joda.time.DateTime;

import static com.bikefriend.jrs.bikefriend.SettingsActivity.PREF_SYNC_INTERVAL;
import static com.bikefriend.jrs.bikefriend.service.BikeJobService.DATA_SYNC_ACTION;
import static com.bikefriend.jrs.bikefriend.service.BikeJobService.SYNC_BASE_DATA;
import static com.bikefriend.jrs.bikefriend.service.BikeJobService.SYNC_STATUS_DATA;
import static com.bikefriend.jrs.bikefriend.service.BikeJobService.SYNC_SYSTEM_STATUS;

/**
 * Created by skaar on 11.11.2017.
 */

public abstract class BikeStatusActivity extends AppCompatActivity implements MyBroadcastReceiver.DataSyncInterface {

    public static final String PREF_LAST_BASE_SYNC = "last_base_data_sync";
    public static final String PREF_LAST_STATUS_SYNC = "last_status_data_sync";

    MyBroadcastReceiver myBroadcastReceiver;
    SharedPreferences sharedPref;
    int randomNumber = 0;
    FirebaseJobDispatcher dispatcher;

    @Override
    public void systemStatusSynced(boolean isSystemClosed) {
        systemStatusIsSynced(isSystemClosed);
    }

    @Override
    public void baseStatusSynced() {
        baseStatusIsSynced();
    }

    @Override
    public void bikeStatusSynced() {
        bikeStatusIsSynced();
        updateWidget();
    }

    private void systemStatusIsSynced(boolean isSystemClosed){
        if(isSystemClosed)
            useDummyData();
        else
            syncBaseData();
    }

    private void baseStatusIsSynced(){
        syncStatusData();
    }

    private void bikeStatusIsSynced(){}

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

    private void useDummyData(){}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        syncSystemStatus();

        myBroadcastReceiver = new MyBroadcastReceiver(this);
        registerReceiver(myBroadcastReceiver, new IntentFilter(DATA_SYNC_ACTION));
    }

    public void syncSystemStatus(){
        Job myJob = dispatcher.newJobBuilder()
                .setService(BikeJobService.class)
                .setTag(SYNC_SYSTEM_STATUS)
                .build();

        dispatcher.mustSchedule(myJob);
    }

    public void syncBaseData(){

        long lastSync = sharedPref.getLong(PREF_LAST_BASE_SYNC, 0);
        long now = DateTime.now().getMillis();
        long millisIn24Hours = 86400000;

        if(now - lastSync > millisIn24Hours) {
            Job myJob = dispatcher.newJobBuilder()
                    .setService(BikeJobService.class)
                    .setTag(SYNC_BASE_DATA)
                    .build();

            dispatcher.mustSchedule(myJob);
        }
        else
            syncStatusData();
    }

    public void syncStatusData(){

        long lastSync = sharedPref.getLong(PREF_LAST_STATUS_SYNC, 0);
        long syncInterval = sharedPref.getLong(PREF_SYNC_INTERVAL, 1);
        long now = DateTime.now().getMillis();
        long millisInScheduledUpdate = 60000 * syncInterval;

        if(now - lastSync > millisInScheduledUpdate) {
            Job myJob = dispatcher.newJobBuilder()
                    .setService(BikeJobService.class)
                    .setTag(SYNC_STATUS_DATA)
                    .build();

            dispatcher.mustSchedule(myJob);
        }
    }
}
