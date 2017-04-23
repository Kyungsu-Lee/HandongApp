package ghost.android.ghosthguapp.bus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalMethods;

public class BusAdapter_for_school extends ArrayAdapter<BusData> {
    public static final int TYPE_TZONE = 0;
    public static final int TYPE_BUS = 1;
    private Context context_app, context_act;
    private ArrayList<BusData> busList;
    NumberPicker np;
    Tabs_for_bus tfb = new Tabs_for_bus();

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return busList.get(position).getType();
    }

    public BusAdapter_for_school(Context app_con, Context act_con, int id, ArrayList<BusData> list) {
        super(app_con, id, list);
        context_app = app_con;
        context_act = act_con;
        busList = list;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View v = convertView;
        final BusData bus = busList.get(position);
        int listViewItemType = bus.getType();

        if (listViewItemType == TYPE_TZONE) { //시간 경계선을 나타낼 때
            v = LayoutInflater.from(getContext()).inflate(R.layout.eachhangmok_for_bus_tzone, null);

            TextView tzone = (TextView) v.findViewById(R.id.tzone);
            tzone.setText(bus.getTzone());
            TextView timesplit = (TextView) v.findViewById(R.id.timesplit);
            timesplit.setText(bus.getTimesplit());

        } else if (listViewItemType == TYPE_BUS) {  //버스 시간을 나타낼 때
            v = LayoutInflater.from(getContext()).inflate(R.layout.eachhangmok_for_bus, null);

            final TextView six = (TextView) v.findViewById(R.id.start);
            final TextView hwanho = (TextView) v.findViewById(R.id.middle);
            final TextView school = (TextView) v.findViewById(R.id.end);

            String getSix = bus.getSix();
            String getHwan = bus.getHwan();
            String getSchool = bus.getSchool();

            if (!(getSix.substring(0, 1).equals("0") || getSix.substring(0, 1).equals("1")))
                getSix = getHwan;
            if (!(getHwan.substring(0, 1).equals("0") || getHwan.substring(0, 1).equals("1")))
                getHwan = getSchool;
            if (!(getSchool.substring(0, 1).equals("0") || getSchool.substring(0, 1).equals("1"))) {
                if (getHwan.substring(0, 1).equals("0") || getHwan.substring(0, 1).equals("1"))
                    getSchool = getHwan;
                else {
                    getHwan = getSix;
                    getSchool = getSix;
                }
            }

            if (!GlobalMethods.toBeShownRed(getSix, bus.getTimesplit(), bus.getTzone()) && GlobalMethods.toBeShownRed(getSchool, bus.getTimesplit(), bus.getTzone()))
                v.setBackgroundColor(Color.parseColor("#AAEAFF"));
            else if (!GlobalMethods.toBeShownRed(getHwan, bus.getTimesplit(), bus.getTzone()) && GlobalMethods.toBeShownRed(getSchool, bus.getTimesplit(), bus.getTzone()))
                v.setBackgroundColor(Color.parseColor("#AAEAFF"));

            six.setText(bus.getSix());
            six.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (getPreferences("sch_am_six" + bus.getSix()).equals(bus.getSix())) {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(context_act);
                        LayoutInflater inflater = (LayoutInflater) context_app.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View innerView = inflater.inflate(R.layout.bus_alarm_delete_layout, null);
                        dlg.setView(innerView);
                        dlg.setTitle("선택시간  " + six.getText());
                        TextView tv = (TextView) innerView.findViewById(R.id.tt_help_dialog_tv);
                        tv.setText("알람을 삭제하시겠습니까?");
                        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removePreferences("sch_am_six" + bus.getSix());
                                tfb.resetAlarm(context_app, "1113" + bus.getSix().substring(0, 2) + bus.getSix().substring(3, 5));
                                resetColorUL(six);
                                dialog.dismiss();
                            }
                        });
                        dlg.setNegativeButton("취소", null).show();
                    } else if (getPreferences("sch_pm_six" + bus.getSix()).equals(bus.getSix())) {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(context_act);
                        LayoutInflater inflater = (LayoutInflater) context_app.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View innerView = inflater.inflate(R.layout.bus_alarm_delete_layout, null);
                        dlg.setView(innerView);
                        dlg.setTitle("선택시간  " + six.getText());
                        TextView tv = (TextView) innerView.findViewById(R.id.tt_help_dialog_tv);
                        tv.setText("알람을 삭제하시겠습니까?");
                        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removePreferences("sch_pm_six" + bus.getSix());
                                tfb.resetAlarm(context_app, "1123" + bus.getSix().substring(0, 2) + bus.getSix().substring(3, 5));
                                resetColorUL(six);
                                dialog.dismiss();
                            }
                        });
                        dlg.setNegativeButton("취소", null).show();
                    } else {
                        if ((!six.getText().toString().substring(0, 1).equals("0") && (!six.getText().toString().substring(0, 1).equals("1")))) {
                            return;
                        }
                        AlertDialog.Builder dlg = new AlertDialog.Builder(context_act);
                        LayoutInflater inflater = (LayoutInflater) context_app.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View innerView = inflater.inflate(R.layout.bus_alarm_setting_dialog, null);
                        dlg.setView(innerView);
                        dlg.setTitle("선택시간  " + six.getText());
                        TextView tv = (TextView) innerView.findViewById(R.id.tt_help_dialog_tv);
                        tv.setText("알람이 등록되면 해당시간이 초록색으로 표시됩니다.");
                        np = (NumberPicker) innerView.findViewById(R.id.numberPicker1);
                        GlobalMethods.numberPickerTextColor(np, Color.BLACK);
                        np.setMinValue(1);
                        np.setMaxValue(30);
                        np.setWrapSelectorWheel(true);
                        dlg.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int beforeminute = np.getValue();

                                int sixhour = Integer.valueOf(bus.getSix().substring(0, 2));
                                int sixmin = Integer.valueOf(bus.getSix().substring(3, 5));

                                if (bus.getTimesplit().equals("AM") && (bus.getSix().substring(0, 1).equals("0") || bus.getSix().substring(0, 1).equals("1"))) {
                                    if (sixmin - beforeminute < 0) {
                                        if (sixhour == 0) {
                                            tfb.setAlarm(String.valueOf(sixhour - 1), String.valueOf(60 - (beforeminute - (sixmin))), String.valueOf(beforeminute), bus.getSix(), context_app, "1113" + bus.getSix().substring(0, 2) + bus.getSix().substring(3, 5), "sch_am_six" + bus.getSix());
                                            savePreferences("sch_am_six" + bus.getSix(), bus.getSix());
                                            setColorUL(six);
                                        } else {
                                            tfb.setAlarm("23", String.valueOf(60 - (beforeminute - (sixmin))), String.valueOf(beforeminute), bus.getSix(), context_app, "1113" + bus.getSix().substring(0, 2) + bus.getSix().substring(3, 5), "sch_am_six" + bus.getSix());
                                            savePreferences("sch_am_six" + bus.getSix(), bus.getSix());
                                            setColorUL(six);
                                        }
                                    } else {
                                        tfb.setAlarm(String.valueOf(sixhour), String.valueOf(sixmin - beforeminute), String.valueOf(beforeminute), bus.getSix(), context_app, "1113" + bus.getSix().substring(0, 2) + bus.getSix().substring(3, 5), "sch_am_six" + bus.getSix());
                                        savePreferences("sch_am_six" + bus.getSix(), bus.getSix());
                                        setColorUL(six);
                                    }
                                } else if (bus.getTimesplit().equals("PM") && (bus.getSix().substring(0, 1).equals("0") || bus.getSix().substring(0, 1).equals("1"))) {
                                    if (sixmin - beforeminute < 0) {
                                        if (sixhour != 12) {
                                            tfb.setAlarm(String.valueOf((sixhour + 12) - 1), String.valueOf(60 - (beforeminute - (sixmin))), String.valueOf(beforeminute), bus.getSix(), context_app, "1123" + bus.getSix().substring(0, 2) + bus.getSix().substring(3, 5), "sch_pm_six" + bus.getSix());
                                            savePreferences("sch_pm_six" + bus.getSix(), bus.getSix());
                                            setColorUL(six);
                                        } else {
                                            tfb.setAlarm(String.valueOf(sixhour - 1), String.valueOf(60 - (beforeminute - (sixmin))), String.valueOf(beforeminute), bus.getSix(), context_app, "1123" + bus.getSix().substring(0, 2) + bus.getSix().substring(3, 5), "sch_pm_six" + bus.getSix());
                                            savePreferences("sch_pm_six" + bus.getSix(), bus.getSix());
                                            setColorUL(six);
                                        }
                                    } else {
                                        tfb.setAlarm(String.valueOf(sixhour + 12), String.valueOf(sixmin - beforeminute), String.valueOf(beforeminute), bus.getSix(), context_app, "1123" + bus.getSix().substring(0, 2) + bus.getSix().substring(3, 5), "sch_pm_six" + bus.getSix());
                                        savePreferences("sch_pm_six" + bus.getSix(), bus.getSix());
                                        setColorUL(six);
                                    }
                                }
                                Toast.makeText(innerView.getContext(), String.valueOf(six.getText()).substring(0, 2) + "시 " + String.valueOf(six.getText()).substring(3, 5) + "분 차 " + String.valueOf(beforeminute) + "분전 알람이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                        dlg.setNegativeButton("취소", null).show();
                    }
                }
            });

            hwanho.setText(bus.getHwan());
            hwanho.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (getPreferences("sch_am_hwan" + bus.getHwan()).equals(bus.getHwan())) {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(context_act);
                        LayoutInflater inflater = (LayoutInflater) context_app.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View innerView = inflater.inflate(R.layout.bus_alarm_delete_layout, null);
                        dlg.setView(innerView);
                        dlg.setTitle("선택시간  " + hwanho.getText());
                        TextView tv = (TextView) innerView.findViewById(R.id.tt_help_dialog_tv);
                        tv.setText("알람을 삭제하시겠습니까?");
                        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removePreferences("sch_am_hwan" + bus.getHwan());
                                tfb.resetAlarm(context_app, "1112" + bus.getHwan().substring(0, 2) + bus.getHwan().substring(3, 5));
                                resetColorUL(hwanho);
                                dialog.dismiss();
                            }
                        });
                        dlg.setNegativeButton("취소", null).show();
                    } else if (getPreferences("sch_pm_hwan" + bus.getHwan()).equals(bus.getHwan())) {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(context_act);
                        LayoutInflater inflater = (LayoutInflater) context_app.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View innerView = inflater.inflate(R.layout.bus_alarm_delete_layout, null);
                        dlg.setView(innerView);
                        dlg.setTitle("선택시간  " + hwanho.getText());
                        TextView tv = (TextView) innerView.findViewById(R.id.tt_help_dialog_tv);
                        tv.setText("알람을 삭제하시겠습니까?");
                        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removePreferences("sch_pm_hwan" + bus.getHwan());
                                tfb.resetAlarm(context_app, "1122" + bus.getHwan().substring(0, 2) + bus.getHwan().substring(3, 5));
                                resetColorUL(hwanho);
                                dialog.dismiss();
                            }
                        });
                        dlg.setNegativeButton("취소", null).show();
                    } else {
                        if ((!hwanho.getText().toString().substring(0, 1).equals("0") && (!hwanho.getText().toString().substring(0, 1).equals("1")))) {
                            return;
                        }
                        AlertDialog.Builder dlg = new AlertDialog.Builder(context_act);
                        LayoutInflater inflater = (LayoutInflater) context_app.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View innerView = inflater.inflate(R.layout.bus_alarm_setting_dialog, null);
                        dlg.setView(innerView);
                        dlg.setTitle("선택시간  " + hwanho.getText());
                        TextView tv = (TextView) innerView.findViewById(R.id.tt_help_dialog_tv);
                        tv.setText("알람이 등록되면 해당시간이 초록색으로 표시됩니다.");
                        np = (NumberPicker) innerView.findViewById(R.id.numberPicker1);
                        GlobalMethods.numberPickerTextColor(np, Color.BLACK);
                        np.setMinValue(1);
                        np.setMaxValue(30);
                        np.setWrapSelectorWheel(true);
                        dlg.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int beforeminute = np.getValue();

                                int hwanhour = Integer.valueOf(bus.getHwan().substring(0, 2));
                                int hwanmin = Integer.valueOf(bus.getHwan().substring(3, 5));

                                if (bus.getTimesplit().equals("AM") && (bus.getHwan().substring(0, 1).equals("0") || bus.getHwan().substring(0, 1).equals("1"))) {
                                    if (hwanmin - beforeminute < 0) {
                                        if (hwanhour == 0) {
                                            tfb.setAlarm(String.valueOf(hwanhour - 1), String.valueOf(60 - (beforeminute - (hwanmin))), String.valueOf(beforeminute), bus.getHwan(), context_app, "1112" + bus.getHwan().substring(0, 2) + bus.getHwan().substring(3, 5), "sch_am_hwan" + bus.getHwan());
                                            savePreferences("sch_am_hwan" + bus.getHwan(), bus.getHwan());
                                            setColorUL(hwanho);
                                        } else {
                                            tfb.setAlarm("23", String.valueOf(60 - (beforeminute - (hwanmin))), String.valueOf(beforeminute), bus.getHwan(), context_app, "1112" + bus.getHwan().substring(0, 2) + bus.getHwan().substring(3, 5), "sch_am_hwan" + bus.getHwan());
                                            savePreferences("sch_am_hwan" + bus.getHwan(), bus.getHwan());
                                            setColorUL(hwanho);
                                        }
                                    } else {
                                        tfb.setAlarm(String.valueOf(hwanhour), String.valueOf(hwanmin - beforeminute), String.valueOf(beforeminute), bus.getHwan(), context_app, "1112" + bus.getHwan().substring(0, 2) + bus.getHwan().substring(3, 5), "sch_am_hwan" + bus.getHwan());
                                        savePreferences("sch_am_hwan" + bus.getHwan(), bus.getHwan());
                                        setColorUL(hwanho);
                                    }
                                } else if (bus.getTimesplit().equals("PM") && (bus.getHwan().substring(0, 1).equals("0") || bus.getHwan().substring(0, 1).equals("1"))) {
                                    if (hwanmin - beforeminute < 0) {
                                        if (hwanhour != 12) {
                                            tfb.setAlarm(String.valueOf((hwanhour + 12) - 1), String.valueOf(60 - (beforeminute - (hwanmin))), String.valueOf(beforeminute), bus.getHwan(), context_app, "1122" + bus.getHwan().substring(0, 2) + bus.getHwan().substring(3, 5), "sch_pm_hwan" + bus.getHwan());
                                            savePreferences("sch_pm_hwan" + bus.getHwan(), bus.getHwan());
                                            setColorUL(hwanho);
                                        } else if (bus.getHwan().equals("11") && bus.getHwan().equals("00")) {
                                            tfb.setAlarm(String.valueOf(23), String.valueOf(60 - (beforeminute - (hwanmin))), String.valueOf(beforeminute), bus.getHwan(), context_app, "1122" + bus.getHwan().substring(0, 2) + bus.getHwan().substring(3, 5), "sch_pm_hwan" + bus.getHwan());
                                            savePreferences("sch_pm_hwan" + bus.getHwan(), bus.getHwan());
                                            setColorUL(hwanho);
                                        } else {
                                            tfb.setAlarm(String.valueOf(hwanhour - 1), String.valueOf(60 - (beforeminute - (hwanmin))), String.valueOf(beforeminute), bus.getHwan(), context_app, "1122" + bus.getHwan().substring(0, 2) + bus.getHwan().substring(3, 5), "sch_pm_hwan" + bus.getHwan());
                                            savePreferences("sch_pm_hwan" + bus.getHwan(), bus.getHwan());
                                            setColorUL(hwanho);
                                        }
                                    } else {
                                        if (bus.getSix().equals("11") && bus.getHwan().equals("00")) {
                                            tfb.setAlarm(String.valueOf(0), String.valueOf(hwanmin - beforeminute), String.valueOf(beforeminute), bus.getHwan(), context_app, "1122" + bus.getHwan().substring(0, 2) + bus.getHwan().substring(3, 5), "sch_pm_hwan" + bus.getHwan());
                                            savePreferences("sch_pm_hwan" + bus.getHwan(), bus.getHwan());
                                            setColorUL(hwanho);
                                        } else {
                                            tfb.setAlarm(String.valueOf(hwanhour + 12), String.valueOf(hwanmin - beforeminute), String.valueOf(beforeminute), bus.getHwan(), context_app, "1122" + bus.getHwan().substring(0, 2) + bus.getHwan().substring(3, 5), "sch_pm_hwan" + bus.getHwan());
                                            savePreferences("sch_pm_hwan" + bus.getHwan(), bus.getHwan());
                                            setColorUL(hwanho);
                                        }
                                    }
                                }

                                Toast.makeText(innerView.getContext(), String.valueOf(hwanho.getText()).substring(0, 2) + "시 " + String.valueOf(hwanho.getText()).substring(3, 5) + "분 차 " + String.valueOf(beforeminute) + "분전 알람이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                        dlg.setNegativeButton("취소", null).show();
                    }
                }

            });

            school.setText(bus.getSchool());
            school.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (getPreferences("sch_am_sch" + bus.getSchool()).equals(bus.getSchool())) {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(context_act);
                        LayoutInflater inflater = (LayoutInflater) context_app.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View innerView = inflater.inflate(R.layout.bus_alarm_delete_layout, null);
                        dlg.setView(innerView);
                        dlg.setTitle("선택시간  " + school.getText());
                        TextView tv = (TextView) innerView.findViewById(R.id.tt_help_dialog_tv);
                        tv.setText("알람을 삭제하시겠습니까?");
                        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removePreferences("sch_am_sch" + bus.getSchool());
                                tfb.resetAlarm(context_app, "1111" + bus.getSchool().substring(0, 2) + bus.getSchool().substring(3, 5));
                                resetColorUL(school);
                                dialog.dismiss();
                            }
                        });
                        dlg.setNegativeButton("취소", null).show();
                    } else if (getPreferences("sch_pm_sch" + bus.getSchool()).equals(bus.getSchool())) {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(context_act);
                        LayoutInflater inflater = (LayoutInflater) context_app.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View innerView = inflater.inflate(R.layout.bus_alarm_delete_layout, null);
                        dlg.setView(innerView);
                        dlg.setTitle("선택시간  " + school.getText());
                        TextView tv = (TextView) innerView.findViewById(R.id.tt_help_dialog_tv);
                        tv.setText("알람을 삭제하시겠습니까?");
                        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removePreferences("sch_pm_sch" + bus.getSchool());
                                tfb.resetAlarm(context_app, "1121" + bus.getSchool().substring(0, 2) + bus.getSchool().substring(3, 5));
                                resetColorUL(school);
                                dialog.dismiss();
                            }
                        });
                        dlg.setNegativeButton("취소", null).show();
                    } else {
                        if ((!school.getText().toString().substring(0, 1).equals("0") && (!school.getText().toString().substring(0, 1).equals("1")))) {
                            return;
                        }
                        AlertDialog.Builder dlg = new AlertDialog.Builder(context_act);
                        LayoutInflater inflater = (LayoutInflater) context_app.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View innerView = inflater.inflate(R.layout.bus_alarm_setting_dialog, null);
                        dlg.setView(innerView);
                        dlg.setTitle("선택시간  " + school.getText());
                        TextView tv = (TextView) innerView.findViewById(R.id.tt_help_dialog_tv);
                        tv.setText("알람이 등록되면 해당시간이 초록색으로 표시됩니다.");
                        np = (NumberPicker) innerView.findViewById(R.id.numberPicker1);
                        GlobalMethods.numberPickerTextColor(np, Color.BLACK);
                        np.setMinValue(1);
                        np.setMaxValue(30);
                        np.setWrapSelectorWheel(true);
                        dlg.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int beforeminute = np.getValue();

                                int schoolhour = Integer.valueOf(bus.getSchool().substring(0, 2));
                                int schoolmin = Integer.valueOf(bus.getSchool().substring(3, 5));

                                if (bus.getTimesplit().equals("AM") && (bus.getSchool().substring(0, 1).equals("0") || bus.getSchool().substring(0, 1).equals("1"))) {
                                    if (schoolmin - beforeminute < 0) {
                                        if (schoolhour == 0) {
                                            tfb.setAlarm(String.valueOf(schoolhour - 1), String.valueOf(60 - (beforeminute - (schoolmin))), String.valueOf(beforeminute), bus.getSchool(), context_app, "1111" + bus.getSchool().substring(0, 2) + bus.getSchool().substring(3, 5), "sch_am_sch" + bus.getSchool());
                                            savePreferences("sch_am_sch" + bus.getSchool(), bus.getSchool());
                                            setColorUL(school);
                                        } else {
                                            tfb.setAlarm("23", String.valueOf(60 - (beforeminute - (schoolmin))), String.valueOf(beforeminute), bus.getSchool(), context_app, "1111" + bus.getSchool().substring(0, 2) + bus.getSchool().substring(3, 5), "sch_am_sch" + bus.getSchool());
                                            savePreferences("sch_am_sch" + bus.getSchool(), bus.getSchool());
                                            setColorUL(school);
                                        }
                                    } else {
                                        tfb.setAlarm(String.valueOf(schoolhour), String.valueOf(schoolmin - beforeminute), String.valueOf(beforeminute), bus.getSchool(), context_app, "1111" + bus.getSchool().substring(0, 2) + bus.getSchool().substring(3, 5), "sch_am_sch" + bus.getSchool());
                                        savePreferences("sch_am_sch" + bus.getSchool(), bus.getSchool());
                                        setColorUL(school);
                                    }
                                } else if (bus.getTimesplit().equals("PM") && (bus.getSchool().substring(0, 1).equals("0") || bus.getSchool().substring(0, 1).equals("1"))) {
                                    if (schoolmin - beforeminute < 0) {
                                        if (schoolhour != 12) {
                                            tfb.setAlarm(String.valueOf((schoolhour + 12) - 1), String.valueOf(60 - (beforeminute - (schoolmin))), String.valueOf(beforeminute), bus.getSchool(), context_app, "1121" + bus.getSchool().substring(0, 2) + bus.getSchool().substring(3, 5), "sch_pm_sch" + bus.getSchool());
                                            savePreferences("sch_pm_sch" + bus.getSchool(), bus.getSchool());
                                            setColorUL(school);
                                        } else if ((bus.getSix().equals("11") || bus.getHwan().equals("11")) && bus.getSchool().equals("00")) {
                                            tfb.setAlarm(String.valueOf(23), String.valueOf(60 - (beforeminute - (schoolmin))), String.valueOf(beforeminute), bus.getSchool(), context_app, "1121" + bus.getSchool().substring(0, 2) + bus.getSchool().substring(3, 5), "sch_pm_sch" + bus.getSchool());
                                            savePreferences("sch_pm_sch" + bus.getSchool(), bus.getSchool());
                                            setColorUL(school);
                                        } else {
                                            tfb.setAlarm(String.valueOf(schoolhour - 1), String.valueOf(60 - (beforeminute - (schoolmin))), String.valueOf(beforeminute), bus.getSchool(), context_app, "1121" + bus.getSchool().substring(0, 2) + bus.getSchool().substring(3, 5), "sch_pm_sch" + bus.getSchool());
                                            savePreferences("sch_pm_sch" + bus.getSchool(), bus.getSchool());
                                            setColorUL(school);
                                        }
                                    } else {
                                        if ((bus.getSix().equals("11") || bus.getHwan().equals("11")) && bus.getSchool().equals("00")) {
                                            tfb.setAlarm(String.valueOf(0), String.valueOf(schoolmin - beforeminute), String.valueOf(beforeminute), bus.getSchool(), context_app, "1121" + bus.getSchool().substring(0, 2) + bus.getSchool().substring(3, 5), "sch_pm_sch" + bus.getSchool());
                                            savePreferences("sch_pm_sch" + bus.getSchool(), bus.getSchool());
                                            setColorUL(school);
                                        } else {
                                            tfb.setAlarm(String.valueOf(schoolhour + 12), String.valueOf(schoolmin - beforeminute), String.valueOf(beforeminute), bus.getSchool(), context_app, "1121" + bus.getSchool().substring(0, 2) + bus.getSchool().substring(3, 5), "sch_pm_sch" + bus.getSchool());
                                            savePreferences("sch_pm_sch" + bus.getSchool(), bus.getSchool());
                                            setColorUL(school);
                                        }
                                    }
                                }

                                Toast.makeText(innerView.getContext(), String.valueOf(school.getText()).substring(0, 2) + "시 " + String.valueOf(school.getText()).substring(3, 5) + "분 차 " + String.valueOf(beforeminute) + "분전 알람이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                        dlg.setNegativeButton("취소", null).show();
                    }
                }
            });

            //지나간 시간에 대해서 빨간색으로 표시함
            if (!(GlobalMethods.toBeShownRed(getSix, bus.getTimesplit(), bus.getTzone()))) {
                six.setTextColor(Color.parseColor("#FFFF5F4F"));
            }
            if (!GlobalMethods.toBeShownRed(getHwan, bus.getTimesplit(), bus.getTzone())) {
                hwanho.setTextColor(Color.parseColor("#FFFF5F4F"));
            }
            if (!GlobalMethods.toBeShownRed(getSchool, bus.getTimesplit(), bus.getTzone())) {
                school.setTextColor(Color.parseColor("#FFFF5F4F"));
            }

            //알람이 설정되어 있으면 초록색으로 표시함
            if (getPreferences("sch_am_six" + bus.getSix()).equals(bus.getSix())) {
                setColorUL(six);
            }
            if (getPreferences("sch_pm_six" + bus.getSix()).equals(bus.getSix())) {
                setColorUL(six);
            }
            if (getPreferences("sch_am_hwan" + bus.getHwan()).equals(bus.getHwan())) {
                setColorUL(hwanho);
            }
            if (getPreferences("sch_pm_hwan" + bus.getHwan()).equals(bus.getHwan())) {
                setColorUL(hwanho);
            }
            if (getPreferences("sch_am_sch" + bus.getSchool()).equals(bus.getSchool())) {
                setColorUL(school);
            }
            if (getPreferences("sch_pm_sch" + bus.getSchool()).equals(bus.getSchool())) {
                setColorUL(school);
            }
        }
        return v;
    }

    // 값 불러오기
    public String getPreferences(String KEY) {
        SharedPreferences pref = context_act.getSharedPreferences("bus_alarm", context_act.MODE_PRIVATE);
        return pref.getString(KEY, "메롱"); //key에 해당하는 값이 없는 경우 메롱을 반환.
    }

    // 값 저장하기
    public void savePreferences(String KEY, String VALUE) {
        SharedPreferences pref = context_act.getSharedPreferences("bus_alarm", context_act.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY, VALUE);
        editor.commit();
    }

    // 값(Key Data) 삭제하기
    public void removePreferences(String KEY) {
        SharedPreferences pref = context_act.getSharedPreferences("bus_alarm", context_act.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(KEY);
        editor.commit();
    }

    // 알람 시간에 대해서 색상과 밑줄 설정
    public void setColorUL(TextView tv) {
        tv.setTextColor(Color.parseColor("#39A935"));
        tv.setTypeface(null, Typeface.BOLD);
        tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    // 알람 시간에 대해서 색상과 밑줄 해제
    public void resetColorUL(TextView tv) {
        tv.setTextColor(Color.parseColor("#3C3C3B"));
        tv.setTypeface(null, Typeface.NORMAL);
        tv.setPaintFlags(tv.getPaintFlags() & ~Paint.UNDERLINE_TEXT_FLAG);
    }
}

