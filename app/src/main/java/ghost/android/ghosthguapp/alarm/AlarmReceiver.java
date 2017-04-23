package ghost.android.ghosthguapp.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import java.util.Calendar;

import ghost.android.ghosthguapp.common.GlobalVariables;


public class AlarmReceiver extends BroadcastReceiver {  //setAlarm 메소드로 호출하는 리시버, 알람이 울릴 때 할 일을 설정한다. -> intent로 알람페이지를 띄움.

    private static final String TAG = "AlarmWakeLock";
    private static PowerManager.WakeLock mWakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {
        String alarm_name = intent.getStringExtra("alarm_name");

        int yoil = GlobalVariables.cal.get(Calendar.DAY_OF_WEEK); //오늘(현재)의 요일을 저장하기 위한 변수

        switch (alarm_name) {
            case "owebak":
                alarmActivity(context, "owebak");
                break;
            case "maejeom":
                alarmActivity(context, "maejeom");
                break;
            case "sundayworshipmorn":
                if (yoil == 1) {
                    alarmActivity(context, "sundayworshipmorn");
                    break;
                } else
                    break;
            case "sundayworship":
                if (yoil == 1) {
                    alarmActivity(context, "sundayworship");
                    break;
                } else
                    break;
            case "firsttime1":
                alarmActivity(context, "firsttime1");
                break;
            case "firsttime":
                if (yoil != 7 && yoil != 1) {
                    alarmActivity(context, "fisrttime");
                    break;
                } else
                    break;
            case "endtime":
                if (yoil != 6 && yoil != 1) {
                    alarmActivity(context, "endtime");
                    break;
                } else
                    break;
            case "riverworship":
                if (yoil == 6) {
                    alarmActivity(context, "riverworship");
                    break;
                } else
                    break;
        }
    }

    private void alarmActivity(Context context, String alarmname) { //알람이 울렸을 때의 동작을 설정하는 메소드, 여기서는 화면을 키고 AlarmPage로 넘어가게끔 설정됨.
        //Screen On : AlarmWakeLock.wakeLock(Context);
        //Screen Off : AlarmWakeLock.releaseWakeLock();
        AlarmReceiver.wakeLock(context);
        Intent i = new Intent(context, AlarmPage.class);
        i.putExtra("alarm_name", alarmname);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        AlarmReceiver.releaseWakeLock();    //PowerManager은 배터리 소모와 밀접한 관계가 있으므로 반드시 release해줘야함.
    }

    public static void wakeLock(Context context) {  //화면이 꺼져있을 시, 화면을 킨다.
        if (mWakeLock != null) {
            return;
        }
        PowerManager powerManager =
                (PowerManager) context.getSystemService(
                        Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(
                PowerManager.FULL_WAKE_LOCK, TAG);
        mWakeLock.acquire();
    }

    public static void releaseWakeLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
}
