package com.bikefriend.jrs.bikefriend.service;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import android.content.Intent;

import static com.bikefriend.jrs.bikefriend.service.RequestTask.FETCH_BASE_DATA;
import static com.bikefriend.jrs.bikefriend.service.RequestTask.FETCH_BIKE_STATUS;
import static com.bikefriend.jrs.bikefriend.service.RequestTask.FETCH_SYSTEM_STATUS;

/**
 * Created by skaar on 11.11.2017.
 */

public class BikeJobService extends JobService {

    public static final String DATA_SYNC_ACTION = "com.bikefriend.jrs.bikefriend.sync_action";
    public static final String DATA_SYNC_ACTION_TYPE = "com.bikefriend.jrs.bikefriend.sync_action_type";
    public static final String SYSTEM_STATUS_SYNC_STARTED = "com.bikefriend.jrs.bikefriend.system_status_sync_started";
    public static final String SYSTEM_STATUS_SYNC_ENDED = "com.bikefriend.jrs.bikefriend.system_status_sync_ended";
    public static final String BASE_DATA_SYNC_STARTED = "com.bikefriend.jrs.bikefriend.base_data_sync_started";
    public static final String BASE_DATA_SYNC_ENDED = "com.bikefriend.jrs.bikefriend.base_data_sync_ended";
    public static final String STATUS_DATA_SYNC_STARTED = "com.bikefriend.jrs.bikefriend.status_data_sync_started";
    public static final String STATUS_DATA_SYNC_ENDED = "com.bikefriend.jrs.bikefriend.status_data_sync_ended";

    public static final String BASE_DATA_URL = "https://oslobysykkel.no/api/v1/stations";
    public static final String BIKE_STATUS_URL = "https://oslobysykkel.no/api/v1/stations/availability";
    public static final String SYSTEM_STATUS_URL = "https://oslobysykkel.no/api/v1/status";

    public static final String SYNC_BASE_DATA = "sync_base_data";
    public static final String SYNC_STATUS_DATA = "sync_status_data";
    public static final String SYNC_SYSTEM_STATUS = "sync_system_status";

    @Override
    public boolean onStartJob(JobParameters params) {

        Intent intent = new Intent();

        switch (params.getTag()){
            case SYNC_BASE_DATA:
                intent.setAction(DATA_SYNC_ACTION);
                intent.putExtra(DATA_SYNC_ACTION_TYPE, BASE_DATA_SYNC_STARTED);
                new RequestTask(getApplicationContext()).execute(BASE_DATA_URL, FETCH_BASE_DATA);
                break;
            case SYNC_STATUS_DATA:
                intent.setAction(DATA_SYNC_ACTION);
                intent.putExtra(DATA_SYNC_ACTION_TYPE, STATUS_DATA_SYNC_STARTED);
                new RequestTask(getApplicationContext()).execute(BIKE_STATUS_URL, FETCH_BIKE_STATUS);
                break;
            case SYNC_SYSTEM_STATUS:
                intent.setAction(DATA_SYNC_ACTION);
                intent.putExtra(DATA_SYNC_ACTION_TYPE, SYSTEM_STATUS_SYNC_STARTED);
                new RequestTask(getApplicationContext()).execute(SYSTEM_STATUS_URL, FETCH_SYSTEM_STATUS);
                break;
        }

        getApplicationContext().sendBroadcast(intent);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return false;
    }
}
