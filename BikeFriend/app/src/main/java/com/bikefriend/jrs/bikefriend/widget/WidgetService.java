package com.bikefriend.jrs.bikefriend.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by jrs on 18/07/2017.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(this);
    }
}
