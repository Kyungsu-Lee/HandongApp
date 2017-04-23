package ghost.android.ghosthguapp.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalMethods;

public class Alarm extends Activity {

    Switch owebak, maejeom, sundayworshipmorn, sundayworship, firsttime1, firsttime, endtime, riverworship; //토글버튼 관리 변수
    LinearLayout ll_owebak, ll_maejeom, ll_sundayworshipmorn, ll_sundayworship, ll_firsttime1, ll_firsttime, ll_endtime, ll_riverworship;    //각 항목의 이름, 누르면 시간설정을 가능하게끔
    TextView owebaktext, maejeomtext, sundayworshipmorntext, sundayworshiptext, firsttime1text, firsttimetext, endtimetext, riverworshiptext, now_setting;   //각 항목에 대한 간단한 설명
    boolean owebakpref, maejeompref, sundayworshipmornpref, sundayworshippref, firsttime1pref, firsttimepref, endtimepref, riverworshippref;    //sharedpreference에 저장될 토글버튼의 값
    int owebakminpref, maejeomminpref, sundayworshipmornminpref, sundayworshipminpref, firsttime1minpref, firsttimeminpref, endtimeminpref, riverworhsipminpref;   //sharedpreference에 저장될 사용자 설정값 (ex. 20분전, 10분전...)
    Calendar now = Calendar.getInstance();    //현재시간 비교 등에 사용되는 캘린더 변수
    Calendar owebakcal, maejeomcal, sundayworshipmorncal, sundayworshipcal, firsttime1cal, firsttimecal, endtimecal, riverworshipcal;  //각 알람 항목에 대한 정보를 저장할 캘린더 변수
    Button alarm_setting;   //알람 설정 도움말 버튼
    NumberPicker np;    //알람 시간 설정에 필요한 NumberPicker

    MenuItem itemNotOnlyVibe;
    MenuItem itemOnlyVibe;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_layout);

        alarm_setting = (Button) findViewById(R.id.alarm_setting);
        alarm_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(Alarm.this);
                final View innerView = getLayoutInflater().inflate(R.layout.alarm_setting_help_dlg, null);
                dlg.setView(innerView);
                dlg.setTitle("알람시간 조절 방법");
                TextView tv = (TextView) innerView.findViewById(R.id.tt_help_dialog_tv);
                tv.setText("시간을 조절하고자 하는 항목을 누르시면 시간 조절이 가능합니다. \n기본설정 - 10분전 (*매점 - 20분전)");
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        final Button opmenu = (Button) findViewById(R.id.alarm_menu_button);
        opmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOptionsMenu();
            }
        });

        SharedPreferences prefs = getSharedPreferences("Alarmpref", MODE_PRIVATE);  //sharedpreference로부터 설정값 가져오기

        owebakpref = prefs.getBoolean("owebakpref", false);   //owebakpref가 NULL값을 가지고 있으면 false를 반환함.
        maejeompref = prefs.getBoolean("maejeompref", false);
        sundayworshipmornpref = prefs.getBoolean("sundayworshipmornpref", false);
        sundayworshippref = prefs.getBoolean("sundayworshippref", false);
        firsttime1pref = prefs.getBoolean("firsttime1pref", false);
        firsttimepref = prefs.getBoolean("firsttimepref", false);
        endtimepref = prefs.getBoolean("endtimepref", false);
        riverworshippref = prefs.getBoolean("riverworshippref", false);

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

        owebak = (Switch) findViewById(R.id.owebak);
        maejeom = (Switch) findViewById(R.id.maejeom);
        sundayworshipmorn = (Switch) findViewById(R.id.sundayworshipmorn);
        sundayworship = (Switch) findViewById(R.id.sundayworship);
        riverworship = (Switch) findViewById(R.id.riverworship);
        firsttime1 = (Switch) findViewById(R.id.firsttime1);
        firsttime = (Switch) findViewById(R.id.firsttime);
        endtime = (Switch) findViewById(R.id.endtime);

        ll_owebak = (LinearLayout) findViewById(R.id.ll_alarm_owebak);
        ll_maejeom = (LinearLayout) findViewById(R.id.ll_alarm_maejeom);
        ll_sundayworshipmorn = (LinearLayout) findViewById(R.id.ll_alarm_sundayworshipmorn);
        ll_sundayworship = (LinearLayout) findViewById(R.id.ll_alarm_sundayworship);
        ll_riverworship = (LinearLayout) findViewById(R.id.ll_alarm_riverworship);
        ll_firsttime1 = (LinearLayout) findViewById(R.id.ll_alarm_firsttime1);
        ll_firsttime = (LinearLayout) findViewById(R.id.ll_alarm_firsttime);
        ll_endtime = (LinearLayout) findViewById(R.id.ll_alarm_endtime);

        owebaktext = (TextView) findViewById(R.id.owebaktext);
        maejeomtext = (TextView) findViewById(R.id.maejeomtext);
        sundayworshiptext = (TextView) findViewById(R.id.sundayworshiptext);
        sundayworshipmorntext = (TextView) findViewById(R.id.sundayworshipmorntext);
        riverworshiptext = (TextView) findViewById(R.id.riverworshiptext);
        firsttime1text = (TextView) findViewById(R.id.firsttime1text);
        firsttimetext = (TextView) findViewById(R.id.firsttimetext);
        endtimetext = (TextView) findViewById(R.id.endtimetext);

        owebaktext.setText("매일 밤 " + owebakcal.get(Calendar.HOUR) + "시 " + owebakcal.get(Calendar.MINUTE) + "분");
        maejeomtext.setText("매일 밤 " + maejeomcal.get(Calendar.HOUR) + "시 " + maejeomcal.get(Calendar.MINUTE) + "분");
        sundayworshipmorntext.setText("매주 주일 오전 " + sundayworshipmorncal.get(Calendar.HOUR) + "시 " + sundayworshipmorncal.get(Calendar.MINUTE) + "분");
        sundayworshiptext.setText("매주 주일 저녁 " + sundayworshipcal.get(Calendar.HOUR) + "시 " + sundayworshipcal.get(Calendar.MINUTE) + "분");
        firsttime1text.setText("매일 오전 " + firsttime1cal.get(Calendar.HOUR) + "시 " + firsttime1cal.get(Calendar.MINUTE) + "분");
        firsttimetext.setText("매일 오전 " + firsttimecal.get(Calendar.HOUR) + "시 " + firsttimecal.get(Calendar.MINUTE) + "분 (토, 일 제외)");
        endtimetext.setText("매일 밤 " + endtimecal.get(Calendar.HOUR) + "시 " + endtimecal.get(Calendar.MINUTE) + "분 (금, 일 제외)");
        riverworshiptext.setText("매주 금요일 밤 " + riverworshipcal.get(Calendar.HOUR) + "시 " + riverworshipcal.get(Calendar.MINUTE) + "분");

        //토글버튼에 대한 preference 값이 true면 토글버튼도 true로, false면 토글버튼도 false로
        if (owebakpref == true)
            owebak.setChecked(true);
        else
            owebak.setChecked(false);
        if (maejeompref == true)
            maejeom.setChecked(true);
        else
            maejeom.setChecked(false);
        if (sundayworshipmornpref == true)
            sundayworshipmorn.setChecked(true);
        else
            sundayworshipmorn.setChecked(false);
        if (sundayworshippref == true)
            sundayworship.setChecked(true);
        else
            sundayworship.setChecked(false);
        if (firsttime1pref == true)
            firsttime1.setChecked(true);
        else
            firsttime1.setChecked(false);
        if (firsttimepref == true)
            firsttime.setChecked(true);
        else
            firsttime.setChecked(false);
        if (endtimepref == true)
            endtime.setChecked(true);
        else
            endtime.setChecked(false);
        if (riverworshippref == true)
            riverworship.setChecked(true);
        else
            riverworship.setChecked(false);

        //외박를 눌렀을 때, 시간 조절이 가능하게끔 NumberPicker를 곁들인 다이얼로그를 띄운다.
        ll_owebak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(Alarm.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View innerView = inflater.inflate(R.layout.alarm_setting_dlg, null);
                dlg.setView(innerView);
                dlg.setTitle("알람 시간 설정");
                now_setting = (TextView) innerView.findViewById(R.id.now_setting);
                now_setting.setText("현재 설정: " + owebakminpref + "분전");
                np = (NumberPicker) innerView.findViewById(R.id.numberPicker1);
                GlobalMethods.numberPickerTextColor(np, Color.BLACK);
                np.setMinValue(1);
                np.setMaxValue(30);
                np.setValue(owebakminpref);
                np.setWrapSelectorWheel(true);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {    //확인을 누름과 동시에 알람시간 재설정과 토글버튼, preference 값을 재설정 해줌.
                        owebakminpref = np.getValue();
                        owebakcal.set(Calendar.HOUR_OF_DAY, 23);
                        owebakcal.set(Calendar.MINUTE, 0);
                        owebakcal.set(Calendar.SECOND, 0);
                        owebakcal.set(Calendar.MILLISECOND, 0);
                        owebakcal.add(Calendar.MINUTE, -(owebakminpref));
                        owebak.setChecked(false);
                        owebak.setChecked(true);
                        owebakpref = false;
                        owebakpref = true;
                        owebaktext.setText("매일 밤 " + owebakcal.get(Calendar.HOUR) + "시 " + owebakcal.get(Calendar.MINUTE) + "분");
                    }
                });
                dlg.setNegativeButton("취소", null).show();
            }
        });

        ll_maejeom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(Alarm.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View innerView = inflater.inflate(R.layout.alarm_setting_dlg, null);
                dlg.setView(innerView);
                dlg.setTitle("알람 시간 설정");
                now_setting = (TextView) innerView.findViewById(R.id.now_setting);
                now_setting.setText("현재 설정: " + maejeomminpref + "분전");
                np = (NumberPicker) innerView.findViewById(R.id.numberPicker1);
                GlobalMethods.numberPickerTextColor(np, Color.BLACK);
                np.setMinValue(1);
                np.setMaxValue(30);
                np.setValue(maejeomminpref);
                np.setWrapSelectorWheel(true);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        maejeomminpref = np.getValue();
                        maejeomcal = Calendar.getInstance();
                        maejeomcal.set(Calendar.HOUR_OF_DAY, 1);
                        maejeomcal.set(Calendar.MINUTE, 0);
                        maejeomcal.set(Calendar.SECOND, 0);
                        maejeomcal.set(Calendar.MILLISECOND, 0);
                        maejeomcal.add(Calendar.MINUTE, -(maejeomminpref));
                        maejeom.setChecked(false);
                        maejeom.setChecked(true);
                        maejeompref = false;
                        maejeompref = true;
                        maejeomtext.setText("매일 밤 " + maejeomcal.get(Calendar.HOUR) + "시 " + maejeomcal.get(Calendar.MINUTE) + "분");
                    }
                });
                dlg.setNegativeButton("취소", null).show();
            }
        });

        ll_sundayworshipmorn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(Alarm.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View innerView = inflater.inflate(R.layout.alarm_setting_dlg, null);
                dlg.setView(innerView);
                dlg.setTitle("알람 시간 설정");
                now_setting = (TextView) innerView.findViewById(R.id.now_setting);
                now_setting.setText("현재 설정: " + sundayworshipmornminpref + "분전");
                np = (NumberPicker) innerView.findViewById(R.id.numberPicker1);
                GlobalMethods.numberPickerTextColor(np, Color.BLACK);
                np.setMinValue(1);
                np.setMaxValue(30);
                np.setValue(sundayworshipmornminpref);
                np.setWrapSelectorWheel(true);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sundayworshipmornminpref = np.getValue();
                        sundayworshipmorncal = Calendar.getInstance();
                        sundayworshipmorncal.set(Calendar.HOUR_OF_DAY, 11);
                        sundayworshipmorncal.set(Calendar.MINUTE, 0);
                        sundayworshipmorncal.set(Calendar.SECOND, 0);
                        sundayworshipmorncal.set(Calendar.MILLISECOND, 0);
                        sundayworshipmorncal.add(Calendar.MINUTE, -(sundayworshipmornminpref));
                        sundayworshipmorn.setChecked(false);
                        sundayworshipmorn.setChecked(true);
                        sundayworshipmornpref = false;
                        sundayworshipmornpref = true;
                        sundayworshipmorntext.setText("매주 주일 오전 " + sundayworshipmorncal.get(Calendar.HOUR) + "시 " + sundayworshipmorncal.get(Calendar.MINUTE) + "분");
                    }
                });
                dlg.setNegativeButton("취소", null).show();
            }
        });

        ll_sundayworship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(Alarm.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View innerView = inflater.inflate(R.layout.alarm_setting_dlg, null);
                dlg.setView(innerView);
                dlg.setTitle("알람 시간 설정");
                now_setting = (TextView) innerView.findViewById(R.id.now_setting);
                now_setting.setText("현재 설정: " + sundayworshipminpref + "분전");
                np = (NumberPicker) innerView.findViewById(R.id.numberPicker1);
                GlobalMethods.numberPickerTextColor(np, Color.BLACK);
                np.setMinValue(1);
                np.setMaxValue(30);
                np.setValue(sundayworshipminpref);
                np.setWrapSelectorWheel(true);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sundayworshipminpref = np.getValue();
                        sundayworshipcal = Calendar.getInstance();
                        sundayworshipcal.set(Calendar.HOUR_OF_DAY, 19);
                        sundayworshipcal.set(Calendar.MINUTE, 0);
                        sundayworshipcal.set(Calendar.SECOND, 0);
                        sundayworshipcal.set(Calendar.MILLISECOND, 0);
                        sundayworshipcal.add(Calendar.MINUTE, -(sundayworshipminpref));
                        sundayworship.setChecked(false);
                        sundayworship.setChecked(true);
                        sundayworshippref = false;
                        sundayworshippref = true;
                        sundayworshiptext.setText("매주 주일 저녁 " + sundayworshipcal.get(Calendar.HOUR) + "시 " + sundayworshipcal.get(Calendar.MINUTE) + "분");
                    }
                });
                dlg.setNegativeButton("취소", null).show();
            }
        });

        ll_firsttime1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(Alarm.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View innerView = inflater.inflate(R.layout.alarm_setting_dlg, null);
                dlg.setView(innerView);
                dlg.setTitle("알람 시간 설정");
                now_setting = (TextView) innerView.findViewById(R.id.now_setting);
                now_setting.setText("현재 설정: " + firsttime1minpref + "분전");
                np = (NumberPicker) innerView.findViewById(R.id.numberPicker1);
                GlobalMethods.numberPickerTextColor(np, Color.BLACK);
                np.setMinValue(1);
                np.setMaxValue(30);
                np.setValue(firsttime1minpref);
                np.setWrapSelectorWheel(true);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        firsttime1minpref = np.getValue();
                        firsttime1cal = Calendar.getInstance();
                        firsttime1cal.set(Calendar.HOUR_OF_DAY, 5);
                        firsttime1cal.set(Calendar.MINUTE, 30);
                        firsttime1cal.set(Calendar.SECOND, 0);
                        firsttime1cal.set(Calendar.MILLISECOND, 0);
                        firsttime1cal.add(Calendar.MINUTE, -(firsttime1minpref));
                        firsttime1.setChecked(false);
                        firsttime1.setChecked(true);
                        firsttime1pref = false;
                        firsttime1pref = true;
                        firsttime1text.setText("매일 오전 " + firsttime1cal.get(Calendar.HOUR) + "시 " + firsttime1cal.get(Calendar.MINUTE) + "분");
                    }
                });
                dlg.setNegativeButton("취소", null).show();
            }
        });

        ll_firsttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(Alarm.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View innerView = inflater.inflate(R.layout.alarm_setting_dlg, null);
                dlg.setView(innerView);
                dlg.setTitle("알람 시간 설정");
                now_setting = (TextView) innerView.findViewById(R.id.now_setting);
                now_setting.setText("현재 설정: " + firsttimeminpref + "분전");
                np = (NumberPicker) innerView.findViewById(R.id.numberPicker1);
                GlobalMethods.numberPickerTextColor(np, Color.BLACK);
                np.setMinValue(1);
                np.setMaxValue(30);
                np.setValue(firsttimeminpref);
                np.setWrapSelectorWheel(true);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        firsttimeminpref = np.getValue();
                        firsttimecal = Calendar.getInstance();
                        firsttimecal.set(Calendar.HOUR_OF_DAY, 7);
                        firsttimecal.set(Calendar.MINUTE, 0);
                        firsttimecal.set(Calendar.SECOND, 0);
                        firsttimecal.set(Calendar.MILLISECOND, 0);
                        firsttimecal.add(Calendar.MINUTE, -(firsttimeminpref));
                        firsttime.setChecked(false);
                        firsttime.setChecked(true);
                        firsttimepref = false;
                        firsttimepref = true;
                        firsttimetext.setText("매일 오전 " + firsttimecal.get(Calendar.HOUR) + "시 " + firsttimecal.get(Calendar.MINUTE) + "분 (토, 일 제외)");
                    }
                });
                dlg.setNegativeButton("취소", null).show();
            }
        });

        ll_endtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(Alarm.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View innerView = inflater.inflate(R.layout.alarm_setting_dlg, null);
                dlg.setView(innerView);
                dlg.setTitle("알람 시간 설정");
                now_setting = (TextView) innerView.findViewById(R.id.now_setting);
                now_setting.setText("현재 설정: " + endtimeminpref + "분전");
                np = (NumberPicker) innerView.findViewById(R.id.numberPicker1);
                GlobalMethods.numberPickerTextColor(np, Color.BLACK);
                np.setMinValue(1);
                np.setMaxValue(30);
                np.setValue(endtimeminpref);
                np.setWrapSelectorWheel(true);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        endtimeminpref = np.getValue();
                        endtimecal = Calendar.getInstance();
                        endtimecal.set(Calendar.HOUR_OF_DAY, 21);
                        endtimecal.set(Calendar.MINUTE, 30);
                        endtimecal.set(Calendar.SECOND, 0);
                        endtimecal.set(Calendar.MILLISECOND, 0);
                        endtimecal.add(Calendar.MINUTE, -(endtimeminpref));
                        endtime.setChecked(false);
                        endtime.setChecked(true);
                        endtimepref = false;
                        endtimepref = true;
                        endtimetext.setText("매일 밤 " + endtimecal.get(Calendar.HOUR) + "시 " + endtimecal.get(Calendar.MINUTE) + "분 (금, 일 제외)");
                    }
                });
                dlg.setNegativeButton("취소", null).show();
            }
        });

        ll_riverworship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(Alarm.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View innerView = inflater.inflate(R.layout.alarm_setting_dlg, null);
                dlg.setView(innerView);
                dlg.setTitle("알람 시간 설정");
                now_setting = (TextView) innerView.findViewById(R.id.now_setting);
                now_setting.setText("현재 설정: " + riverworhsipminpref + "분전");
                np = (NumberPicker) innerView.findViewById(R.id.numberPicker1);
                GlobalMethods.numberPickerTextColor(np, Color.BLACK);
                np.setMinValue(1);
                np.setMaxValue(30);
                np.setValue(riverworhsipminpref);
                np.setWrapSelectorWheel(true);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        riverworhsipminpref = np.getValue();
                        riverworshipcal = Calendar.getInstance();
                        riverworshipcal.set(Calendar.HOUR_OF_DAY, 21);
                        riverworshipcal.set(Calendar.MINUTE, 30);
                        riverworshipcal.set(Calendar.SECOND, 0);
                        riverworshipcal.set(Calendar.MILLISECOND, 0);
                        riverworshipcal.add(Calendar.MINUTE, -(riverworhsipminpref));
                        riverworship.setChecked(false);
                        riverworship.setChecked(true);
                        riverworshippref = false;
                        riverworshippref = true;
                        riverworshiptext.setText("매주 금요일 밤 " + riverworshipcal.get(Calendar.HOUR) + "시 " + riverworshipcal.get(Calendar.MINUTE) + "분");
                    }
                });
                dlg.setNegativeButton("취소", null).show();
            }
        });

        //토글버튼 설정 부분
        owebak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {    //토글버튼이 true일 시, preference 값을 바꾸고, 알람을 설절한다.(setAlarm 메소드)
                    owebakpref = true;
                    setAlarm(owebakcal, "owebak", 1);
                    Toast.makeText(Alarm.this, "매일 밤 " + owebakcal.get(Calendar.HOUR) + "시 " + owebakcal.get(Calendar.MINUTE) + "분에 알람이 울립니다.", Toast.LENGTH_SHORT).show();
                } else {    //토글버튼이 false일 시, preference 값을 바꾸고, 알람을 해제한다. (resetAlarm 메소드)
                    owebakpref = false;
                    resetAlarm(Alarm.this, 1);
                }
            }
        });

        maejeom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    maejeompref = true;
                    setAlarm(maejeomcal, "maejeom", 3);
                    Toast.makeText(Alarm.this, "매일 밤 " + maejeomcal.get(Calendar.HOUR) + "시 " + maejeomcal.get(Calendar.MINUTE) + "분에 알람이 울립니다.", Toast.LENGTH_SHORT).show();
                } else {
                    maejeompref = false;
                    resetAlarm(Alarm.this, 3);
                }
            }
        });

        sundayworshipmorn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sundayworshipmornpref = true;
                    setAlarm(sundayworshipmorncal, "sundayworshipmorn", 4);
                    Toast.makeText(Alarm.this, "매주 주일 오전 " + sundayworshipmorncal.get(Calendar.HOUR) + "시 " + sundayworshipmorncal.get(Calendar.MINUTE) + "분에 알람이 울립니다.", Toast.LENGTH_SHORT).show();
                } else {
                    sundayworshipmornpref = false;
                    resetAlarm(Alarm.this, 4);
                }
            }
        });

        sundayworship.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sundayworshippref = true;
                    setAlarm(sundayworshipcal, "sundayworship", 5);
                    Toast.makeText(Alarm.this, "매주 주일 저녁 " + sundayworshipcal.get(Calendar.HOUR) + "시 " + sundayworshipcal.get(Calendar.MINUTE) + "분에 알람이 울립니다.", Toast.LENGTH_SHORT).show();
                } else {
                    sundayworshippref = false;
                    resetAlarm(Alarm.this, 5);
                }
            }
        });

        firsttime1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    firsttime1pref = true;
                    setAlarm(firsttime1cal, "firsttime1", 6);
                    Toast.makeText(Alarm.this, "매일 오전 " + firsttime1cal.get(Calendar.HOUR) + "시 " + firsttime1cal.get(Calendar.MINUTE) + "분에 알람이 울립니다. ", Toast.LENGTH_SHORT).show();
                } else {
                    firsttime1pref = false;
                    resetAlarm(Alarm.this, 6);
                }
            }
        });

        firsttime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    firsttimepref = true;
                    setAlarm(firsttimecal, "firsttime", 7);
                    Toast.makeText(Alarm.this, "매일 오전 " + firsttimecal.get(Calendar.HOUR) + "시 " + firsttimecal.get(Calendar.MINUTE) + "분에 알람이 울립니다. (토, 일 제외)", Toast.LENGTH_SHORT).show();
                } else {
                    firsttimepref = false;
                    resetAlarm(Alarm.this, 7);
                }
            }
        });

        endtime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    endtimepref = true;
                    setAlarm(endtimecal, "endtime", 8);
                    Toast.makeText(Alarm.this, "매일 밤 " + endtimecal.get(Calendar.HOUR) + "시 " + endtimecal.get(Calendar.MINUTE) + "분에 알람이 울립니다. (금, 일 제외)", Toast.LENGTH_SHORT).show();
                } else {
                    endtimepref = false;
                    resetAlarm(Alarm.this, 8);
                }
            }
        });

        riverworship.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    riverworshippref = true;
                    setAlarm(riverworshipcal, "riverworship", 9);
                    Toast.makeText(Alarm.this, "매주 금요일 밤 " + riverworshipcal.get(Calendar.HOUR) + "시 " + riverworshipcal.get(Calendar.MINUTE) + "분에 알람이 울립니다.", Toast.LENGTH_SHORT).show();
                } else {
                    riverworshippref = false;
                    resetAlarm(Alarm.this, 9);
                }
            }
        });

    }

    //알람의 등록 (시간 지정을 위한 캘린더 변수와, 알람페이지에서 사용될 알람 이름, 그리고 각 알람을 구분하기 위한 ID값으로 integer 값을 받는다.(등록과 해제에는 같은 ID값이 사용되어야 함.)
    public void setAlarm(Calendar c, String alarm_name, int i) {
        if (c.before(now)) {
            c.add(Calendar.DATE, 1);
        }
        Intent intent = new Intent(Alarm.this, AlarmReceiver.class);
        intent.putExtra("alarm_name", alarm_name);
        PendingIntent sender = PendingIntent.getBroadcast(Alarm.this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);   //주기를 INTERVAL_DAY로 줌으로써 24시간(하루) 간격으로 알람을 반복하게 됨
    }

    //알람의 해제 (해제를 위해서 알람을 등록할 때 사용되었던 ID값이 필요하다(i))
    private void resetAlarm(Context context, int i) {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent Intent = new Intent(Alarm.this, AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(Alarm.this, i, Intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pIntent);
    }

    //액티비티가 종료될 때, preference 값을 저장하기 위해 호출.
    protected void onStop() {
        super.onStop();
        // 데이타를저장합니다.
        SharedPreferences prefs = getSharedPreferences("Alarmpref", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("owebakpref", owebakpref);
        editor.putBoolean("maejeompref", maejeompref);
        editor.putBoolean("sundayworshipmornpref", sundayworshipmornpref);
        editor.putBoolean("sundayworshippref", sundayworshippref);
        editor.putBoolean("firsttime1pref", firsttime1pref);
        editor.putBoolean("firsttimepref", firsttimepref);
        editor.putBoolean("endtimepref", endtimepref);
        editor.putBoolean("riverworshippref", riverworshippref);
        editor.putInt("owebakminpref", owebakminpref);
        editor.putInt("maejeomminpref", maejeomminpref);
        editor.putInt("sundayworshipmornminpref", sundayworshipmornminpref);
        editor.putInt("sundayworshipminpref", sundayworshipminpref);
        editor.putInt("firsttime1minpref", firsttime1minpref);
        editor.putInt("firsttimeminpref", firsttimeminpref);
        editor.putInt("endtimeminpref", endtimeminpref);
        editor.putInt("riverworhsipminpref", riverworhsipminpref);
        editor.commit();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        SharedPreferences prefs = getSharedPreferences("Alarmpref", MODE_PRIVATE);
        boolean ovibe = prefs.getBoolean("ovibe", true);
        if (ovibe) {
            menu.findItem(R.id.alarm_menu_only_vibe).setChecked(true);
            menu.findItem(R.id.alarm_menu_not_only_vibe).setChecked(false);
        } else {
            menu.findItem(R.id.alarm_menu_only_vibe).setChecked(false);
            menu.findItem(R.id.alarm_menu_not_only_vibe).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
        itemNotOnlyVibe = menu.findItem(R.id.alarm_menu_not_only_vibe);
        itemOnlyVibe = menu.findItem(R.id.alarm_menu_only_vibe);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences prefs = getSharedPreferences("Alarmpref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        switch (item.getItemId()) {
            case (R.id.alarm_menu_only_vibe):
                if (!item.isChecked()) {
                    item.setChecked(true);
                    itemNotOnlyVibe.setChecked(false);

                    editor.putBoolean("ovibe", true);
                }
                break;

            case (R.id.alarm_menu_not_only_vibe):
                if(!item.isChecked()) {
                    item.setChecked(true);
                    MenuItem otherItem = (MenuItem) findViewById(R.id.alarm_menu_only_vibe);
                    itemOnlyVibe.setChecked(false);

                    editor.putBoolean("ovibe", false);
                }
                break;
        }
        editor.commit();
        return super.onOptionsItemSelected(item);
    }

}