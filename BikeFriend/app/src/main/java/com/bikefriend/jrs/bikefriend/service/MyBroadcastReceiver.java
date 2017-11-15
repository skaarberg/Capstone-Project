package com.bikefriend.jrs.bikefriend.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bikefriend.jrs.bikefriend.BikeStatusActivity;

import static com.bikefriend.jrs.bikefriend.service.RequestTask.SYSTEM_STATUS;

/**
 * Created by skaar on 11.11.2017.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    private BikeStatusActivity activity;

    public interface DataSyncInterface {
        public void systemStatusSynced(boolean isSystemClosed);
        public void baseStatusSynced();
        public void bikeStatusSynced();
    }

    public MyBroadcastReceiver(BikeStatusActivity activity){
        this.activity = activity;
    }

    public MyBroadcastReceiver(){}

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getStringExtra(BikeJobService.DATA_SYNC_ACTION_TYPE)){
            case BikeJobService.SYSTEM_STATUS_SYNC_ENDED:
                activity.systemStatusSynced(intent.getBooleanExtra(SYSTEM_STATUS, false));
                break;
            case BikeJobService.BASE_DATA_SYNC_ENDED:
                activity.baseStatusSynced();
                break;
            case BikeJobService.STATUS_DATA_SYNC_ENDED:
                activity.bikeStatusSynced();
                break;
        }
    }
}
