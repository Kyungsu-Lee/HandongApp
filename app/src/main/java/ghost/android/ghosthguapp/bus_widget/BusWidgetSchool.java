package ghost.android.ghosthguapp.bus_widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.bus.Tabs_for_bus;

public class BusWidgetSchool extends AppWidgetProvider {
    private BusWidgetManager busWidgetManager;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bus_widget_layout);
        busWidgetManager = new BusWidgetManager(context, views, BusWidgetSchool.class);
        busWidgetManager.LoadBusData("school");
        busWidgetManager.attachListener("school");
        appWidgetManager.updateAppWidget(appWidgetIds, views);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public void onReceive(Context context, Intent intent) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bus_widget_layout);
        ComponentName componentName = new ComponentName(context, BusWidgetSchool.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        busWidgetManager = new BusWidgetManager(context, views, BusWidgetSchool.class);

        if (intent.getAction().equals("update")) {
            busWidgetManager.LoadBusData("school");
        } else if (intent.getAction().equals("goSchool")) {
            Intent i = new Intent(context, Tabs_for_bus.class);
            i.putExtra("where", intent.getStringExtra("where"));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        appWidgetManager.updateAppWidget(componentName, views);
        super.onReceive(context, intent);
    }
}
