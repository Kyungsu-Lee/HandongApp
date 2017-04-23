package ghost.android.ghosthguapp.timetable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalMethods;
import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.login.Login;
import ghost.android.ghosthguapp.timetable_widget.TimeTableWidget;

public class TimeTable extends Fragment {
    private Activity thisActivity;
    private TableLayout table;
    private TimeTableList timetableList;
    private ArrayList<TextView> textViews;
    private View rootView;
    private LinearLayout.LayoutParams param;
    private Animation anim;
    private int yoil = GlobalMethods.getToday(); // 일요일(1) ~ 토요일(7)
    private boolean rmvSat;
    private boolean rmvSeven;
    private boolean rmvEight;
    private boolean rmvNine;
    private boolean rmvTen;
    private boolean rmvBar;
    private int tvSize;
    private final int MaxTextSize = 11;
    private final int MinTextSize = -6;
    private float dens;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onDestroyView() {
        super.onDestroyView();
        captureForWidget();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup fragment_container, Bundle savedInstanceState) {
        thisActivity = getActivity();
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.timetable_main, null);
        table = (TableLayout) rootView.findViewById(R.id.tt_main_table);
        param = new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textViews = new ArrayList<>();
        anim = AnimationUtils.loadAnimation(thisActivity, R.anim.timetable_optionmenu);
        dens = thisActivity.getResources().getDisplayMetrics().density;
        SharedPreferences prefs = thisActivity.getSharedPreferences("TimeTable", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        rmvSat = prefs.getBoolean("rmvSat", false);
        rmvSeven = prefs.getBoolean("rmvSeven", false);
        rmvEight = prefs.getBoolean("rmvEight", true);
        rmvNine = prefs.getBoolean("rmvNine", true);
        rmvTen = prefs.getBoolean("rmvTen", true);
        rmvBar = prefs.getBoolean("rmvBar", false);
        tvSize = prefs.getInt("tvSize", 0);

        final ImageButton opmenu = (ImageButton) rootView.findViewById(R.id.tt_option_menu);
        opmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisActivity.openOptionsMenu();
                opmenu.startAnimation(anim);
            }
        });

        ImageButton hide = (ImageButton) rootView.findViewById(R.id.tt_actionbar_hide);
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.tt_actionbar).setVisibility(View.GONE);
                rootView.findViewById(R.id.tt_actionbar_show).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.tt_actionbar_show).setClickable(true);
                rootView.findViewById(R.id.tt_actionbar_show).bringToFront();
                editor.putBoolean("rmvBar", true);
                editor.commit();
            }
        });

        ImageButton show = (ImageButton) rootView.findViewById(R.id.tt_actionbar_show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.tt_actionbar).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.tt_actionbar_show).setVisibility(View.GONE);
                rootView.findViewById(R.id.tt_actionbar_show).setClickable(false);
                editor.putBoolean("rmvBar", false);
                editor.commit();
            }
        });


        ImageButton update = (ImageButton) rootView.findViewById(R.id.tt_actionbar_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //인터넷 연결 확인
                AlertDialog.Builder netConDlgBuilder = GlobalMethods.checkInternet(thisActivity);
                //인터넷 연결 되면
                if (netConDlgBuilder == null) {
                    new AlertDialog.Builder(thisActivity)
                            .setTitle("업데이트")
                            .setMessage("히즈넷에서 시간표 정보를 불러옵니다.\n계속 하시겠습니까?\n(편집된 시간표는 지워집니다)")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            SharedPreferences prefs = thisActivity.getSharedPreferences("Login", Context.MODE_PRIVATE);
                                            String id = prefs.getString("id", "");
                                            String pw = prefs.getString("pw", "");
                                            //로그인 정보 받아와서 없으면 로그인하러 로그인 화면으로
                                            if (id.equals("") || pw.equals("")) {
                                                AlertDialog.Builder dlg = new AlertDialog.Builder(thisActivity);
                                                dlg.setTitle("");
                                                dlg.setMessage("로그인이 필요한 서비스입니다.");
                                                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        Intent i = new Intent(thisActivity, Login.class);
                                                        startActivity(i);
                                                    }
                                                }).show();
                                            } else {
                                                // 로그인 정보 없으면 통과
                                                new DoUpdateAndSave().execute();
                                            }
                                        }
                                    }
                            ).setNegativeButton("취소", null).show();
                }//인터넷 안되면
                else {
                    netConDlgBuilder.show();
                }
            }
        });

        ImageButton insert = (ImageButton) rootView.findViewById(R.id.tt_actionbar_insert);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(thisActivity);
                final View innerView = thisActivity.getLayoutInflater().inflate(R.layout.timetable_help_dialog, null);
                dlg.setView(innerView);
                dlg.setTitle("편집");
                TextView tv = (TextView) innerView.findViewById(R.id.tt_help_dialog_tv);
                tv.setText("입력 혹은 수정하고 싶은 곳을 2초간 꾹 눌러주세요.\n편집 후 확인을 누르시면 변경 사항이 저장됩니다.");
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        display();
        setDisplay();
        opmenu.startAnimation(anim);
        return rootView;
    }

    private class DoUpdateAndSave extends AsyncTask<Void, Void, String> {
        private ProgressDialog doUpdateAndSave;

        protected void onPreExecute() {
            super.onPreExecute();
            doUpdateAndSave = ProgressDialog.show(thisActivity, "", "시간표를 업데이트 하고 있습니다.", true);
            doUpdateAndSave.setCancelable(true);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            TimeTableManager timeTableManager = new TimeTableManager();
            if (timeTableManager.updateNSave(thisActivity)) {
                return "success";
            } else {
                return "network error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            doUpdateAndSave.dismiss();
            if (result.equals("success")) {
                // 화면 display
                textViews.clear();
                display();
            } else if (result.equals("network error")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                builder.setTitle("업데이트 실패");
                builder.setMessage("업데이트에 실패했습니다.\n네트워크 상태를 확인하신 후 다시 로그인 해주세요.\n(메인화면 > 설정 > 로그아웃)")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            doUpdateAndSave.dismiss();
        }
    }

    public void remarkToday() {
        if (yoil > 1) { // 일요일(1) ~ 토요일(7)
            ((TableRow) rootView.findViewById(R.id.tt_main_header_row)).getChildAt(yoil - 1).setBackgroundColor(thisActivity.getResources().getColor(R.color.default_backgroud));
            ((TextView) ((TableRow) rootView.findViewById(R.id.tt_main_header_row)).getChildAt(yoil - 1)).setTypeface(null, Typeface.NORMAL);

            for (int i = 1; i < table.getChildCount(); i++) {
                TableRow row = (TableRow) table.getChildAt(i);
                LinearLayout ll = (LinearLayout) row.getChildAt(yoil - 1);
                ll.setBackgroundColor(thisActivity.getResources().getColor(R.color.default_backgroud));
                if (ll.getChildCount() != 0) {
                    ((TextView) ll.getChildAt(0)).setTextColor(thisActivity.getResources().getColor(R.color.disabled));
                    ((TextView) ll.getChildAt(1)).setTextColor(thisActivity.getResources().getColor(R.color.disabled));
                    ((TextView) ll.getChildAt(2)).setTextColor(thisActivity.getResources().getColor(R.color.disabled));
                }
            }
        }
    }

    public void markToday() {
        if (yoil > 1) { // 일요일(1) ~ 토요일(7)
            ((TableRow) rootView.findViewById(R.id.tt_main_header_row)).getChildAt(yoil - 1).setBackgroundColor(thisActivity.getResources().getColor(R.color.white));
            ((TextView) ((TableRow) rootView.findViewById(R.id.tt_main_header_row)).getChildAt(yoil - 1)).setTextColor(thisActivity.getResources().getColor(R.color.black));
            ((TextView) ((TableRow) rootView.findViewById(R.id.tt_main_header_row)).getChildAt(yoil - 1)).setTypeface(null, Typeface.BOLD);

            for (int i = 1; i < table.getChildCount(); i++) {
                TableRow row = (TableRow) table.getChildAt(i);
                LinearLayout ll = (LinearLayout) row.getChildAt(yoil - 1);
                ll.setBackgroundColor(thisActivity.getResources().getColor(R.color.white));
                if (ll.getChildCount() != 0) {
                    ((TextView) ll.getChildAt(0)).setTextColor(thisActivity.getResources().getColor(R.color.black));
                    ((TextView) ll.getChildAt(1)).setTextColor(thisActivity.getResources().getColor(R.color.black));
                    ((TextView) ll.getChildAt(2)).setTextColor(thisActivity.getResources().getColor(R.color.black));
                }
            }
        }
    }

    public void display() {
        int day = 1;
        int period = 1;
        TimeTableManager timeTableManager = new TimeTableManager();
        timeTableManager.setting();
        timetableList = timeTableManager.getList();

        for (period = 1; period < table.getChildCount(); period++) {   // 1교시 ~ 10교시
            TableRow tableRow = (TableRow) table.getChildAt(period);
            for (day = 1; day < tableRow.getChildCount(); day++) {  // 월요일(1) ~ 토요일(6) (index 0는 시간 표시되는 cell)
                LinearLayout ll = (LinearLayout) tableRow.getChildAt(day);
                ll.removeAllViews();
                ll.setBackgroundColor(thisActivity.getResources().getColor(R.color.default_backgroud));

                TimeTableData timeTableData = timetableList.get(day, period);

                if (timeTableData != null) {
                    TextView tv_sub = new TextView(thisActivity);
                    TextView tv_prof = new TextView(thisActivity);
                    TextView tv_place = new TextView(thisActivity);

                    tv_sub.setText(timeTableData.getSubject());
                    tv_prof.setText(timeTableData.getProf());
                    tv_place.setText(timeTableData.getPlace());
                    tv_sub.setLayoutParams(param);
                    tv_sub.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9 + tvSize);
                    tv_sub.setGravity(Gravity.CENTER);
                    tv_prof.setLayoutParams(param);
                    tv_prof.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7 + tvSize);
                    tv_prof.setGravity(Gravity.CENTER);
                    tv_place.setLayoutParams(param);
                    tv_place.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7 + tvSize);
                    tv_place.setGravity(Gravity.CENTER);

                    textViews.add(tv_sub);
                    textViews.add(tv_prof);
                    textViews.add(tv_place);
                    ll.addView(tv_sub);
                    ll.addView(tv_prof);
                    ll.addView(tv_place);
                }
                longClickListener(day, period, ll);
            }
        }
        markToday();
    }

    public void longClickListener(int day, int period, LinearLayout ll) {
        final int innerDay = day;
        final int innerPeriod = period;
        final LinearLayout innerLl = ll;

        ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(thisActivity);
                final View innerView = thisActivity.getLayoutInflater().inflate(R.layout.timetable_edit_dialog, null);
                dlg.setView(innerView);
                dlg.setTitle(GlobalVariables.days[innerDay] + " " + innerPeriod + "교시");
                final EditText et_one = (EditText) innerView.findViewById(R.id.tt_edit_dialog_et1);
                final EditText et_two = (EditText) innerView.findViewById(R.id.tt_edit_dialog_et2);
                final EditText et_three = (EditText) innerView.findViewById(R.id.tt_edit_dialog_et3);

                if (innerLl.getChildCount() != 0) {
                    et_one.setText(((TextView) innerLl.getChildAt(0)).getText().toString(), TextView.BufferType.EDITABLE);
                    et_two.setText(((TextView) innerLl.getChildAt(1)).getText().toString(), TextView.BufferType.EDITABLE);
                    et_three.setText(((TextView) innerLl.getChildAt(2)).getText().toString(), TextView.BufferType.EDITABLE);
                }
                innerView.findViewById(R.id.tt_edit_dialog_btn1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        et_one.setText("");
                    }
                });
                innerView.findViewById(R.id.tt_edit_dialog_btn2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        et_two.setText("");
                    }
                });
                innerView.findViewById(R.id.tt_edit_dialog_btn3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        et_three.setText("");
                    }
                });

                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        TimeTableData timetable = new TimeTableData();
                        timetable.setDay(String.valueOf(innerDay));
                        timetable.setPeriod(String.valueOf(innerPeriod));
                        timetable.setSubject(et_one.getText().toString());
                        timetable.setProf(et_two.getText().toString());
                        timetable.setPlace(et_three.getText().toString());
                        timetableList.add(String.valueOf(innerDay), String.valueOf(innerPeriod), timetable);

                        if (innerLl.getChildCount() == 0) {
                            TextView tv_sub = new TextView(thisActivity);
                            TextView tv_prof = new TextView(thisActivity);
                            TextView tv_place = new TextView(thisActivity);

                            tv_sub.setLayoutParams(param);
                            tv_sub.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9 + tvSize);
                            tv_sub.setGravity(Gravity.CENTER);
                            tv_prof.setLayoutParams(param);
                            tv_prof.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7 + tvSize);
                            tv_prof.setGravity(Gravity.CENTER);
                            tv_place.setLayoutParams(param);
                            tv_place.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7 + tvSize);
                            tv_place.setGravity(Gravity.CENTER);

                            textViews.add(tv_sub);
                            textViews.add(tv_prof);
                            textViews.add(tv_place);

                            innerLl.addView(tv_sub);
                            innerLl.addView(tv_prof);
                            innerLl.addView(tv_place);
                        } else {
                            ((TextView) innerLl.getChildAt(0)).setText(et_one.getText().toString());
                            ((TextView) innerLl.getChildAt(1)).setText(et_two.getText().toString());
                            ((TextView) innerLl.getChildAt(2)).setText(et_three.getText().toString());
                        }
                        new DoEditAndSave().execute();
                        dialog.dismiss();
                    }
                }).setNegativeButton("취소", null).show();
                return false;
            }
        });

    }

    private class DoEditAndSave extends AsyncTask<Void, Void, String> {
        private ProgressDialog doEditAndSave;
        private TimeTableManager timeTableManager = new TimeTableManager();

        protected void onPreExecute() {
            super.onPreExecute();
            doEditAndSave = ProgressDialog.show(thisActivity, "", "시간표를 저장하고 있습니다.", true);
            doEditAndSave.setCancelable(true);
            timeTableManager.setList(timetableList);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            if (timeTableManager.savingTimeTable()) {
                return "success";
            } else {
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            doEditAndSave.dismiss();
            if (result.equals("success")) {
                // 화면 display
                display();
            } else if (result.equals("error")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                builder.setTitle("저장 오류");
                builder.setMessage("시간표를 저장할 수 없습니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            doEditAndSave.dismiss();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (rmvSat) {
            menu.findItem(R.id.time_table_menu_sat).setChecked(true);
        }else{
            menu.findItem(R.id.time_table_menu_sat).setChecked(false);
        }
        if (rmvSeven) {
            menu.findItem(R.id.time_table_menu_seven).setChecked(true);
        }else{
            menu.findItem(R.id.time_table_menu_seven).setChecked(false);
        }
        if (rmvEight) {
            menu.findItem(R.id.time_table_menu_eight).setChecked(true);
        }else{
            menu.findItem(R.id.time_table_menu_eight).setChecked(false);
        }
        if (rmvNine) {
            menu.findItem(R.id.time_table_menu_nine).setChecked(true);
        }else{
            menu.findItem(R.id.time_table_menu_nine).setChecked(false);
        }
        if (rmvTen){
            menu.findItem(R.id.time_table_menu_ten).setChecked(true);
        }else{
            menu.findItem(R.id.time_table_menu_ten).setChecked(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        thisActivity.getMenuInflater().inflate(R.menu.menu_timetable, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences prefs = thisActivity.getSharedPreferences("TimeTable", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        switch (item.getItemId()) {
            case (R.id.time_table_menu_sat):
                if (item.isChecked()) {
                    item.setChecked(false);
                    removeSat();
                    editor.putBoolean("rmvSat", false);
                } else {
                    item.setChecked(true);
                    removeSat();
                    editor.putBoolean("rmvSat", true);
                }
                rmvSat = !rmvSat;
                break;
            case (R.id.time_table_menu_seven):
                if (item.isChecked()) {
                    item.setChecked(false);
                    removePeriod(7);
                    editor.putBoolean("rmvSeven", false);
                } else {
                    item.setChecked(true);
                    removePeriod(7);
                    editor.putBoolean("rmvSeven", true);
                }
                rmvSeven = !rmvSeven;
                break;
            case (R.id.time_table_menu_eight):
                if (item.isChecked()) {
                    item.setChecked(false);
                    removePeriod(8);
                    editor.putBoolean("rmvEight", false);
                } else {
                    item.setChecked(true);
                    removePeriod(8);
                    editor.putBoolean("rmvEight", true);
                }
                rmvEight = !rmvEight;
                break;
            case (R.id.time_table_menu_nine):
                if (item.isChecked()) {
                    item.setChecked(false);
                    removePeriod(9);
                    editor.putBoolean("rmvNine", false);
                } else {
                    item.setChecked(true);
                    removePeriod(9);
                    editor.putBoolean("rmvNine", true);
                }
                rmvNine = !rmvNine;
                break;
            case (R.id.time_table_menu_ten):
                if (item.isChecked()) {
                    item.setChecked(false);
                    removePeriod(10);
                    editor.putBoolean("rmvTen", false);
                } else {
                    item.setChecked(true);
                    removePeriod(10);
                    editor.putBoolean("rmvTen", true);
                }
                rmvTen = !rmvTen;
                break;
            case (R.id.time_table_menu_inc):
                if (tvSize < MaxTextSize) {
                    increaseTextSize();
                    editor.putInt("tvSize", tvSize);
                }
                break;

            case (R.id.time_table_menu_dec):
                if (tvSize > MinTextSize) {
                    decreaseTextSize();
                    editor.putInt("tvSize", tvSize);
                }
                break;
        }
        editor.commit();
        return super.onOptionsItemSelected(item);
    }

    public void removeSat() {
        if (((TableLayout) rootView.findViewById(R.id.tt_main_header)).isColumnCollapsed(6)) {
            ((TableLayout) rootView.findViewById(R.id.tt_main_header)).setColumnCollapsed(6, false);
            ((TableLayout) rootView.findViewById(R.id.tt_main_table)).setColumnCollapsed(6, false);
        } else {
            ((TableLayout) rootView.findViewById(R.id.tt_main_header)).setColumnCollapsed(6, true);
            ((TableLayout) rootView.findViewById(R.id.tt_main_table)).setColumnCollapsed(6, true);
        }
    }

    public void removePeriod(int period) {
        switch (period) {
            case 7:
                if (rootView.findViewById(R.id.tt_main_row7).getVisibility() == View.VISIBLE) {
                    rootView.findViewById(R.id.tt_main_row7).setVisibility(View.GONE);
                } else {
                    rootView.findViewById(R.id.tt_main_row7).setVisibility(View.VISIBLE);
                }
                break;
            case 8:
                if (rootView.findViewById(R.id.tt_main_row8).getVisibility() == View.VISIBLE) {
                    rootView.findViewById(R.id.tt_main_row8).setVisibility(View.GONE);
                } else {
                    rootView.findViewById(R.id.tt_main_row8).setVisibility(View.VISIBLE);
                }
                break;
            case 9:
                if (rootView.findViewById(R.id.tt_main_row9).getVisibility() == View.VISIBLE) {
                    rootView.findViewById(R.id.tt_main_row9).setVisibility(View.GONE);
                } else {
                    rootView.findViewById(R.id.tt_main_row9).setVisibility(View.VISIBLE);
                }
                break;
            case 10:
                if (rootView.findViewById(R.id.tt_main_row10).getVisibility() == View.VISIBLE) {
                    rootView.findViewById(R.id.tt_main_row10).setVisibility(View.GONE);
                } else {
                    rootView.findViewById(R.id.tt_main_row10).setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public void setDisplay() {
        if (rmvSat) {
            removeSat();
        }
        if (rmvSeven) {
            removePeriod(7);
        }
        if (rmvEight) {
            removePeriod(8);
        }
        if (rmvNine) {
            removePeriod(9);
        }
        if (rmvTen) {
            removePeriod(10);
        }
        if (rmvBar) {
            rootView.findViewById(R.id.tt_actionbar).setVisibility(View.GONE);
            rootView.findViewById(R.id.tt_actionbar_show).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.tt_actionbar_show).setClickable(true);
            rootView.findViewById(R.id.tt_actionbar_show).bringToFront();
        }
    }

    public void captureForWidget() {
        remarkToday();
        RelativeLayout container = (RelativeLayout) rootView.findViewById(R.id.tt_headerNtable);
        container.buildDrawingCache();
        Bitmap captureView = container.getDrawingCache();
        BufferedOutputStream fos;
        try {
            fos = new BufferedOutputStream(new FileOutputStream(GlobalVariables.fWTt));
            captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        container.destroyDrawingCache();

        updateWidget();
    }

    public void updateWidget() {
        AppWidgetManager awm = AppWidgetManager.getInstance(thisActivity);
        Intent update = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        update.setClass(thisActivity, TimeTableWidget.class);
        update.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, awm.getAppWidgetIds(new ComponentName(thisActivity, TimeTableWidget.class)));
        thisActivity.sendBroadcast(update);
        markToday();
    }

    public void increaseTextSize() {
        for (TextView tv : textViews) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, ((int) tv.getTextSize() / dens) + 1.0f);
        }
        tvSize++;
    }

    public void decreaseTextSize() {
        for (TextView tv : textViews) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, ((int) tv.getTextSize() / dens) - 1.0f);
        }
        tvSize--;
    }
}
