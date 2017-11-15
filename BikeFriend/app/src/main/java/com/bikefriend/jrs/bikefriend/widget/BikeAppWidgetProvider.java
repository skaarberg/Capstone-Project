package com.bikefriend.jrs.bikefriend.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.bikefriend.jrs.bikefriend.R;

/**
 * Created by skaar on 13.11.2017.
 */

public class BikeAppWidgetProvider extends AppWidgetProvider {
    Context context;
    Intent intent;
    static AppWidgetManager appWidgetManager;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManagerIn, int appWidgetId) {
        appWidgetManager = appWidgetManagerIn;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bike_widget);
        setRemoteAdapter(context, views);
        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, views.getLayoutId());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;

        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        ComponentName cn = new ComponentName(context, BikeAppWidgetProvider.class);
        mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widget_list);
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    private static void setRemoteAdapter(Context context, RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list, new Intent(context, WidgetService.class));
    }
}
