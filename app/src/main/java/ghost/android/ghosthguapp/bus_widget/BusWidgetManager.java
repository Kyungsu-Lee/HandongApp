package ghost.android.ghosthguapp.bus_widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import java.util.ArrayList;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalMethods;
import ghost.android.ghosthguapp.mainpage.MainBusManager;

public class BusWidgetManager {
    private Class<?> mclass;
    private Context con;
    private ArrayList<String> sixTimeList;
    private ArrayList<String> schoolTimeList;
    private RemoteViews views;
    private int yoil = GlobalMethods.getToday();
    private int ap = GlobalMethods.getAmPm();
    private int hour = GlobalMethods.getCurHour();

    public BusWidgetManager(Context context, RemoteViews remoteViews, Class which) {
        this.con = context;
        this.views = remoteViews;
        this.mclass = which;
    }

    public void LoadBusData(String dst) {
        MainBusManager mainBusManager = new MainBusManager();
        if (dst.equals("six")) {
            sixTimeList = mainBusManager.sixFromFile();
        } else {
            schoolTimeList = mainBusManager.schoolFromFile();
        }
        display(dst);
    }

    public void display(String dst) {
        views.removeAllViews(R.layout.bus_widget_layout);
        // 육거리행 or 학교행

        //평일(월요일(2)~금요일(6))
        if (yoil > 1 && yoil < 7) {
            if (yoil == 2 && ap == 0 && hour < 3) { // 일->월 오전 03시 이전은 주말
                views.setTextViewText(R.id.bus_widget_title_day, "주말");
                views.setTextColor(R.id.bus_widget_title_day, con.getResources().getColor(R.color.normal_red));
            } else {
                views.setTextViewText(R.id.bus_widget_title_day, "평일");
                views.setTextColor(R.id.bus_widget_title_day, con.getResources().getColor(R.color.white));
            }
        }//주말(토요일(7) or 일요일(1))
        else {
            if (yoil == 7 && ap == 0 && hour < 3) { // 금->토 오전 03시 이전은 평일
                views.setTextViewText(R.id.bus_widget_title_day, "평일");
                views.setTextColor(R.id.bus_widget_title_day, con.getResources().getColor(R.color.white));
            } else {
                views.setTextViewText(R.id.bus_widget_title_day, "주말");
                views.setTextColor(R.id.bus_widget_title_day, con.getResources().getColor(R.color.normal_red));
            }
        }
        int i = 0;
        if (dst.equals("six")) {
            views.setTextViewText(R.id.bus_widget_title_dst, "육거리행");
            //학교-환호-육거리 or 육거리-환호-확교
            views.setTextViewText(R.id.bus_widget_station1, "학교");
            views.setTextViewText(R.id.bus_widget_station2, "환호동");
            views.setTextViewText(R.id.bus_widget_station3, "육거리");
            //시간대별 버스 시간
            views.setTextViewText(R.id.bus_widget_time1_tv1, sixTimeList.get(i++));
            views.setTextViewText(R.id.bus_widget_time1_tv2, sixTimeList.get(i++));
            views.setTextViewText(R.id.bus_widget_time1_tv3, sixTimeList.get(i++));
            views.setTextViewText(R.id.bus_widget_time2_tv1, sixTimeList.get(i++));
            views.setTextViewText(R.id.bus_widget_time2_tv2, sixTimeList.get(i++));
            views.setTextViewText(R.id.bus_widget_time2_tv3, sixTimeList.get(i++));
            if(sixTimeList.get(i).equals("인터넷")){
                views.setTextViewText(R.id.bus_widget_time3_tv1, "한동어플을");
                views.setTextViewText(R.id.bus_widget_time3_tv2, "실행해");
                views.setTextViewText(R.id.bus_widget_time3_tv3, "주세요");
            }else{
                views.setTextViewText(R.id.bus_widget_time3_tv1, sixTimeList.get(i++));
                views.setTextViewText(R.id.bus_widget_time3_tv2, sixTimeList.get(i++));
                views.setTextViewText(R.id.bus_widget_time3_tv3, sixTimeList.get(i++));
            }
        } else {
            views.setTextViewText(R.id.bus_widget_title_dst, "학교행");
            //학교-환호-육거리 or 육거리-환호-확교
            views.setTextViewText(R.id.bus_widget_station1, "육거리");
            views.setTextViewText(R.id.bus_widget_station2, "환호동");
            views.setTextViewText(R.id.bus_widget_station3, "학교");
            //시간대별 버스 시간
            views.setTextViewText(R.id.bus_widget_time1_tv1, schoolTimeList.get(i++));
            views.setTextViewText(R.id.bus_widget_time1_tv2, schoolTimeList.get(i++));
            views.setTextViewText(R.id.bus_widget_time1_tv3, schoolTimeList.get(i++));
            views.setTextViewText(R.id.bus_widget_time2_tv1, schoolTimeList.get(i++));
            views.setTextViewText(R.id.bus_widget_time2_tv2, schoolTimeList.get(i++));
            views.setTextViewText(R.id.bus_widget_time2_tv3, schoolTimeList.get(i++));
            if(schoolTimeList.get(i).equals("인터넷")){
                views.setTextViewText(R.id.bus_widget_time3_tv1, "한동어플을");
                views.setTextViewText(R.id.bus_widget_time3_tv2, "실행해");
                views.setTextViewText(R.id.bus_widget_time3_tv3, "주세요");
            }else {
                views.setTextViewText(R.id.bus_widget_time3_tv1, schoolTimeList.get(i++));
                views.setTextViewText(R.id.bus_widget_time3_tv2, schoolTimeList.get(i++));
                views.setTextViewText(R.id.bus_widget_time3_tv3, schoolTimeList.get(i++));
            }
        }
    }

    public void attachListener(String dst) {
        String where = "";
        String action = "";
        if (dst.equals("six")) {
            action = "goSix";
            where = "yookgeory";
        } else {
            action = "goSchool";
            where = "school";
        }

        Intent update = new Intent(con, mclass);
        update.setAction("update");
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(con, 0, update, 0);
        views.setOnClickPendingIntent(R.id.bus_widget_update, actionPendingIntent);

        Intent go = new Intent(con, mclass);
        go.setAction(action);
        go.putExtra("where", where);
        PendingIntent goPendingIntent = PendingIntent.getBroadcast(con, 0, go, 0);
        views.setOnClickPendingIntent(R.id.ll_bus_widget, goPendingIntent);
    }
}
