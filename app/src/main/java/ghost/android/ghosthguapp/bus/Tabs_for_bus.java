package ghost.android.ghosthguapp.bus;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

import ghost.android.ghosthguapp.R;

public class Tabs_for_bus extends Activity implements View.OnClickListener {
    Button pyeongill, zoomal;
    Intent Intent;
    int sy; //0:학교, 1:육거리
    FragmentManager fragmentManager = getFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs_layout_for_bus);

        pyeongill = (Button) findViewById(R.id.pyeongill);
        zoomal = (Button) findViewById(R.id.zoomal);

        Intent intent = getIntent();
        String where = intent.getStringExtra("where");

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int yoil = cal.get(Calendar.DAY_OF_WEEK);

        switch (where) {
            case "yookgeory":
                if (yoil == 1 || (yoil == 7 && hour > 3)) {
                    fragmentTransaction.add(R.id.fragment_container, new Bus_to_yookgeory_mal());
                    zoomal.setSelected(true);
                    sy = 1;
                } else {
                    fragmentTransaction.add(R.id.fragment_container, new Bus_to_yookgeory());
                    pyeongill.setSelected(true);
                    sy = 1;
                }
                fragmentTransaction.commit();
                break;
            case "school":
                if (yoil == 1 || (yoil == 7 && hour > 3)) {
                    fragmentTransaction.add(R.id.fragment_container, new Bus_to_school_mal());
                    zoomal.setSelected(true);
                    sy = 0;
                } else {
                    fragmentTransaction.add(R.id.fragment_container, new Bus_to_school());
                    pyeongill.setSelected(true);
                    sy = 0;
                }
                fragmentTransaction.commit();
                break;
        }

        pyeongill.setOnClickListener(this);
        zoomal.setOnClickListener(this);
    }

    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.pyeongill:
                if (sy == 0) {
                    fragmentTransaction.replace(R.id.fragment_container, new Bus_to_school());
                    sy = 0;
                } else if (sy == 1) {
                    fragmentTransaction.replace(R.id.fragment_container, new Bus_to_yookgeory());
                    sy = 1;
                }
                fragmentTransaction.commit();
                pyeongill.setSelected(true);
                zoomal.setSelected(false);
                break;
            case R.id.zoomal:
                if (sy == 0) {
                    fragmentTransaction.replace(R.id.fragment_container, new Bus_to_school_mal());
                    sy = 0;
                } else if (sy == 1) {
                    fragmentTransaction.replace(R.id.fragment_container, new Bus_to_yookgeory_mal());
                    sy = 1;
                }
                fragmentTransaction.commit();
                zoomal.setSelected(true);
                pyeongill.setSelected(false);
                break;
        }
    }

    public void setSy(int nsy) {
        sy = nsy;
    }

    //알람의 설정
    public void setAlarm(String h, String min, String beforemin, String time, Context con, String ID, String KEY) {
        int hour = Integer.valueOf(h);
        int minute = Integer.valueOf(min);
        Calendar timer = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        timer.setTimeInMillis(System.currentTimeMillis());
        timer.set(Calendar.HOUR_OF_DAY, hour);
        timer.set(Calendar.MINUTE, minute);
        timer.set(Calendar.SECOND, 0);
        timer.set(Calendar.MILLISECOND, 0);
        if (hour < now.get(Calendar.HOUR_OF_DAY)) {
            timer.add(Calendar.DATE, 1);
        } else if (hour == now.get(Calendar.HOUR_OF_DAY)) {
            if (minute < now.get(Calendar.MINUTE))
                timer.add(Calendar.DATE, 1);
        }
        Intent = new Intent(con, Bus_alarm_receiver.class);
        Intent.putExtra("time", time);
        Intent.putExtra("before", beforemin);
        Intent.putExtra("prefKEY", KEY);
        int id = Integer.valueOf(ID);
        PendingIntent pIntent = PendingIntent.getBroadcast(con, id, Intent, 0);
        AlarmManager am = (AlarmManager) con.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, timer.getTimeInMillis(), pIntent);
    }

    public void resetAlarm(Context con, String ID) {
        AlarmManager alarmManager = (AlarmManager) con.getSystemService(Context.ALARM_SERVICE);
        Intent = new Intent(con, Bus_alarm_receiver.class);
        int id = Integer.valueOf(ID);
        PendingIntent pIntent = PendingIntent.getBroadcast(con, id, Intent, 0);
        alarmManager.cancel(pIntent);
    }
}
