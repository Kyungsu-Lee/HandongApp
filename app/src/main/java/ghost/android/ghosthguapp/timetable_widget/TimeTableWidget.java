package ghost.android.ghosthguapp.timetable_widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.RemoteViews;

import java.io.File;

import ghost.android.ghosthguapp.MainActivity;
import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalVariables;

public class TimeTableWidget extends AppWidgetProvider {
    private File file = GlobalVariables.fWTt;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.timetable_widget_layout);
        if (file.exists()) {
            views.setViewVisibility(R.id.tt_widget_iv, View.VISIBLE);
            views.setViewVisibility(R.id.tt_widget_tv, View.GONE);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            views.setImageViewBitmap(R.id.tt_widget_iv, bitmap);
        } else {
            views.setViewVisibility(R.id.tt_widget_tv, View.VISIBLE);
            views.setViewVisibility(R.id.tt_widget_iv, View.GONE);
        }

        Intent go = new Intent(context, MainActivity.class);
        go.setAction("go");
        PendingIntent goPendingIntent = PendingIntent.getActivity(context, 0, go, 0);
        views.setOnClickPendingIntent(R.layout.timetable_widget_layout, goPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, views);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}
