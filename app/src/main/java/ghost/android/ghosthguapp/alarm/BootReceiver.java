package ghost.android.ghosthguapp.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    Calendar now = Calendar.getInstance();    //현재시간 비교 등에 사용되는 캘린더 변수
    Calendar owebakcal, maejeomcal, sundayworshipmorncal, sundayworshipcal, firsttime1cal, firsttimecal, endtimecal, riverworshipcal;  //각 알람 항목에 대한 정보를 저장할 캘린더 변수
    int owebakminpref, firsttime1minpref, maejeomminpref, sundayworshipmornminpref, sundayworshipminpref, firsttimeminpref, endtimeminpref, riverworhsipminpref;   //sharedpreference에 저장될 사용자 설정값 (ex. 20분전, 10분전...)
    
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs = context.getSharedPreferences("Alarmpref", Context.MODE_PRIVATE);  //sharedpreference로부터 설정값 가져오기

        owebakminpref = prefs.getInt("owebakminpref", 10);    //owebakminpref가 NULL값을 가지고 있으면 10을 반환함.(기본 설정 값 10분전을 뜻함.)
        maejeomminpref = prefs.getInt("maejeomminpref", 20);
        sundayworshipminpref = prefs.getInt("sundayworshipminpref", 10);
        sundayworshipmornminpref = prefs.getInt("sundayworshipmornminpref", 10);
        firsttime1minpref = prefs.getInt("firsttime1minpref", 10);
        firsttimeminpref = prefs.getInt("firsttimeminpref", 10);
        endtimeminpref = prefs.getInt("endtimeminpref", 10);
        riverworhsipminpref = prefs.getInt("riverworhsipminpref", 10);

        //각 캘린더 변수에 대한 시간을 설정해줌.
        owebakcal = Calendar.getInstance();
        owebakcal.set(Calendar.HOUR_OF_DAY, 23);
        owebakcal.set(Calendar.MINUTE, 0);
        owebakcal.set(Calendar.SECOND, 0);
        owebakcal.set(Calendar.MILLISECOND, 0);
        owebakcal.add(Calendar.MINUTE, -(owebakminpref));

        maejeomcal = Calendar.getInstance();
        maejeomcal.set(Calendar.HOUR_OF_DAY, 1);
        maejeomcal.set(Calendar.MINUTE, 0);
        maejeomcal.set(Calendar.SECOND, 0);
        maejeomcal.set(Calendar.MILLISECOND, 0);
        maejeomcal.add(Calendar.MINUTE, -(maejeomminpref));

        sundayworshipmorncal = Calendar.getInstance();
        sundayworshipmorncal.set(Calendar.HOUR_OF_DAY, 11);
        sundayworshipmorncal.set(Calendar.MINUTE, 0);
        sundayworshipmorncal.set(Calendar.SECOND, 0);
        sundayworshipmorncal.set(Calendar.MILLISECOND, 0);
        sundayworshipmorncal.add(Calendar.MINUTE, -(sundayworshipmornminpref));

        sundayworshipcal = Calendar.getInstance();
        sundayworshipcal.set(Calendar.HOUR_OF_DAY, 19);
        sundayworshipcal.set(Calendar.MINUTE, 0);
        sundayworshipcal.set(Calendar.SECOND, 0);
        sundayworshipcal.set(Calendar.MILLISECOND, 0);
        sundayworshipcal.add(Calendar.MINUTE, -(sundayworshipminpref));

        firsttime1cal = Calendar.getInstance();
        firsttime1cal.set(Calendar.HOUR_OF_DAY, 5);
        firsttime1cal.set(Calendar.MINUTE, 30);
        firsttime1cal.set(Calendar.SECOND, 0);
        firsttime1cal.set(Calendar.MILLISECOND, 0);
        firsttime1cal.add(Calendar.MINUTE, -(firsttime1minpref));

        firsttimecal = Calendar.getInstance();
        firsttimecal.set(Calendar.HOUR_OF_DAY, 7);
        firsttimecal.set(Calendar.MINUTE, 0);
        firsttimecal.set(Calendar.SECOND, 0);
        firsttimecal.set(Calendar.MILLISECOND, 0);
        firsttimecal.add(Calendar.MINUTE, -(firsttimeminpref));

        endtimecal = Calendar.getInstance();
        endtimecal.set(Calendar.HOUR_OF_DAY, 21);
        endtimecal.set(Calendar.MINUTE, 30);
        endtimecal.set(Calendar.SECOND, 0);
        endtimecal.set(Calendar.MILLISECOND, 0);
        endtimecal.add(Calendar.MINUTE, -(endtimeminpref));

        riverworshipcal = Calendar.getInstance();
        riverworshipcal.set(Calendar.HOUR_OF_DAY, 21);
        riverworshipcal.set(Calendar.MINUTE, 30);
        riverworshipcal.set(Calendar.SECOND, 0);
        riverworshipcal.set(Calendar.MILLISECOND, 0);
        riverworshipcal.add(Calendar.MINUTE, -(riverworhsipminpref));
        
        if (prefs.getBoolean("owebakpref", false)) {
            setAlarm(context, owebakcal, "owebak", 1);
        }
        if (prefs.getBoolean("maejeompref", false)) {
            setAlarm(context, maejeomcal, "maejeom", 3);
        }
        if (prefs.getBoolean("sundayworshipmornpref", false)) {
            setAlarm(context, sundayworshipmorncal, "sundayworshipmorn", 4);
        }
        if (prefs.getBoolean("sundayworshippref", false)) {
            setAlarm(context, sundayworshipcal, "sundayworship", 5);
        }
        if (prefs.getBoolean("firsttime1pref", false)) {
            setAlarm(context, firsttime1cal, "firsttime1", 6);
        }
        if (prefs.getBoolean("firsttimepref", false)) {
            setAlarm(context, firsttimecal, "firsttime", 7);
        }
        if (prefs.getBoolean("endtimepref", false)) {
            setAlarm(context, endtimecal, "endtime", 8);
        }
        if (prefs.getBoolean("riverworshippref", false)) {
            setAlarm(context, riverworshipcal, "riverworship", 9);
        }
    }

    //알람의 등록 (시간 지정을 위한 캘린더 변수와, 알람페이지에서 사용될 알람 이름, 그리고 각 알람을 구분하기 위한 ID값으로 integer 값을 받는다.(등록과 해제에는 같은 ID값이 사용되어야 함.)
    private void setAlarm(Context context, Calendar c, String alarm_name, int i) {
        if (c.before(now)) {
            c.add(Calendar.DATE, 1);
        }
        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarm_name", alarm_name);
        PendingIntent sender = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);   //주기를 INTERVAL_DAY로 줌으로써 24시간(하루) 간격으로 알람을 반복하게 됨
    }
}
