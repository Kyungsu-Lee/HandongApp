package ghost.android.ghosthguapp.bus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import ghost.android.ghosthguapp.alarm.AlarmReceiver;

public class Bus_alarm_receiver extends BroadcastReceiver { //버스 알람 실행을 위한 리시버. 기본 원리는 AlarmReceiver.class와 같다.

    private static final String TAG = "AlarmWakeLock";
    private static PowerManager.WakeLock mWakeLock;
    String time, before, prefKEY;

    @Override
    public void onReceive(Context context, Intent intent) {

        time = intent.getStringExtra("time");
        before = intent.getStringExtra("before");
        prefKEY = intent.getStringExtra("prefKEY");

        alarmActivity(context);
    }

    private void alarmActivity (Context context) {
        //Screen On : AlarmWakeLock.wakeLock(Context);
        //Screen Off : AlarmWakeLock.releaseWakeLock();
        AlarmReceiver.wakeLock(context);
        Intent i = new Intent(context, Bus_alarm_page.class);
        i.putExtra("time", time);
        i.putExtra("before", before);
        i.putExtra("prefKEY", prefKEY);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        AlarmReceiver.releaseWakeLock();
    }

    public static void wakeLock(Context context) {
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
