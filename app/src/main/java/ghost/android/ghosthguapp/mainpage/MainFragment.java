package ghost.android.ghosthguapp.mainpage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ghost.android.ghosthguapp.LoadingActivity;
import ghost.android.ghosthguapp.MainActivity;
import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.alarm.Alarm;
import ghost.android.ghosthguapp.bus.Tabs_for_bus;
import ghost.android.ghosthguapp.common.GlobalMethods;
import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.convenient.Convenient;
import ghost.android.ghosthguapp.ddosuni.Ddosuni;
import ghost.android.ghosthguapp.hgushop.Hgushop;
import ghost.android.ghosthguapp.hisnetgo.Hisnetgo123;
import ghost.android.ghosthguapp.notice.NoticeMain;
import ghost.android.ghosthguapp.popup_notice.PopupNoticeManager;
import ghost.android.ghosthguapp.setting.Setting;
import ghost.android.ghosthguapp.setting.SettingManager;
import ghost.android.ghosthguapp.sickdan.Tabs_for_sickdan;
import ghost.android.ghosthguapp.yasick.YasickMain;

public class MainFragment extends Fragment {
    private View rootView;
    private Activity thisActivity;
    private ArrayList<String> sixTimeList;
    private ArrayList<String> schoolTimeList;
    private int yoil;
    private int ap;
    private int hour;
    private ViewPager vp;

    public void onResume() {
        super.onResume();
        yoil = GlobalMethods.getToday();
        ap = GlobalMethods.getAmPm();
        hour = GlobalMethods.getCurHour();
        //인터넷 연결 확인
        ConnectivityManager manager = (ConnectivityManager) thisActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        //인터넷 연결 되면
        if (manager.getActiveNetworkInfo() != null) {
            //서버에서 받아오기
            new DownFromSever().execute();
        }
        //인터넷 안되면
        else {
            new LoadFromFile().execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Remove title bar
        thisActivity = getActivity();
        rootView = inflater.inflate(R.layout.mainpage_fragment, null);
        if(GlobalVariables.flag == true) {
            new LoadingPage().execute();
        }
        else
        {GlobalVariables.flag = true;}
        vp = ((MainActivity) thisActivity).getViewPager();

        if (Integer.valueOf(GlobalMethods.getMonth()) > 2 && Integer.valueOf(GlobalMethods.getMonth()) < 6) {
            rootView.findViewById(R.id.main_ll).setBackgroundColor(thisActivity.getResources().getColor(R.color.spring));
        } else if (Integer.valueOf(GlobalMethods.getMonth()) > 5 && Integer.valueOf(GlobalMethods.getMonth()) < 9) {
            rootView.findViewById(R.id.main_ll).setBackgroundColor(thisActivity.getResources().getColor(R.color.summer));
        } else if (Integer.valueOf(GlobalMethods.getMonth()) > 8 && Integer.valueOf(GlobalMethods.getMonth()) < 12) {
            rootView.findViewById(R.id.main_ll).setBackgroundColor(thisActivity.getResources().getColor(R.color.autumn));
        } else {
            rootView.findViewById(R.id.main_ll).setBackgroundColor(thisActivity.getResources().getColor(R.color.winter));
        }

        LinearLayout yasick = (LinearLayout) rootView.findViewById(R.id.ll_main_yasick);
        yasick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(thisActivity.getApplicationContext(), YasickMain.class));
            }
        });
        LinearLayout notice = (LinearLayout) rootView.findViewById(R.id.ll_main_notice);
        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(thisActivity.getApplicationContext(), NoticeMain.class));
            }
        });
        LinearLayout conv = (LinearLayout) rootView.findViewById(R.id.ll_main_conv);
        conv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(thisActivity.getApplicationContext(), Convenient.class));
            }
        });
        LinearLayout setting = (LinearLayout) rootView.findViewById(R.id.ll_main_setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(thisActivity.getApplicationContext(), Setting.class));
            }
        });
        LinearLayout ddosuni = (LinearLayout) rootView.findViewById(R.id.ll_main_ddosuni);
        ddosuni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.flag =  true;
                startActivity(new Intent(thisActivity.getApplicationContext(), Ddosuni.class));
            }
        });

        LinearLayout launch = (LinearLayout) rootView.findViewById(R.id.ll_main_launch);
        launch.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(thisActivity.getApplicationContext(), Tabs_for_sickdan.class));
            }
        });

        LinearLayout alarm = (LinearLayout) rootView.findViewById(R.id.ll_main_alarm);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(thisActivity.getApplicationContext(), Alarm.class));
            }
        });
        LinearLayout yookgeory = (LinearLayout) rootView.findViewById(R.id.ll_bus_to_six);
        yookgeory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(thisActivity.getApplicationContext(), Tabs_for_bus.class);
                i.putExtra("where", "yookgeory");
                startActivity(i);
            }
        });
        LinearLayout school = (LinearLayout) rootView.findViewById(R.id.ll_bus_to_school);
        school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(thisActivity.getApplicationContext(), Tabs_for_bus.class);
                i.putExtra("where", "school");
                startActivity(i);
            }
        });

        LinearLayout shop = (LinearLayout) rootView.findViewById(R.id.ll_main_hgushop);
        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(thisActivity.getApplicationContext(), Hgushop.class));
            }
        });
        LinearLayout hgutube = (LinearLayout) rootView.findViewById(R.id.ll_main_hgutube);
        hgutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                builder.setTitle("한동어플");
                builder.setMessage("서비스 준비중입니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .show();*/
                startActivity(new Intent(thisActivity.getApplicationContext(), Hisnetgo123.class));
            }
        });

        LinearLayout timetable = (LinearLayout) rootView.findViewById(R.id.ll_main_timetable);
        timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp.setCurrentItem(0);
            }
        });

        ConnectivityManager manager = (ConnectivityManager) thisActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        //인터넷 연결 되면
        if (manager.getActiveNetworkInfo() != null) {
            //서버에서 받아오기
            new CheckVersion().execute();
            new PopupNotice().execute();
        } else {
            GlobalVariables.oldVersion = false;
        }
        return rootView;
    }

    public class DownFromSever extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            MainBusManager mainBusManager = new MainBusManager();
            sixTimeList = mainBusManager.sixFromServer();
            schoolTimeList = mainBusManager.schoolFromServer();
            return null;
        }

        @Override
        protected void onPostExecute(Void arg0) {
            display();
            super.onPostExecute(arg0);
        }
    }

    public class LoadFromFile extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            MainBusManager mainBusManager = new MainBusManager();
            sixTimeList = mainBusManager.sixFromFile();
            schoolTimeList = mainBusManager.schoolFromFile();
            return null;
        }

        @Override
        protected void onPostExecute(Void arg0) {
            display();
            super.onPostExecute(arg0);
        }
    }

    public class CheckVersion extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            SettingManager settingManager = new SettingManager(thisActivity);
            settingManager.versionCheck();
            return null;
        }

        @Override
        protected void onPostExecute(Void arg0) {
            changeSettingIcon();
            super.onPostExecute(arg0);
        }
    }

    public class LoadingPage extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            startActivity(new Intent(getActivity(), LoadingActivity.class));
            return null;
        }

        @Override
        protected void onPostExecute(Void arg0) {
            super.onPostExecute(arg0);
        }
    }

    public class PopupNotice extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
            protected String doInBackground(Void... arg0) {
                SharedPreferences prefs = thisActivity.getSharedPreferences("Notice", Context.MODE_PRIVATE);
                String savedDate = prefs.getString("date", "9999");
                PopupNoticeManager popupNoticeManager = new PopupNoticeManager();
                String noticeDate = popupNoticeManager.getNoticeDate();

                // 공지의 날짜와 저장된 날짜가 같으면 보여줄 필요 없음
                if (savedDate.equals(noticeDate) || noticeDate.equals("0000")) {
                    return "";
            } else {
                return noticeDate;
            }
        }

        @Override
        protected void onPostExecute(String arg0) {
            if (arg0.isEmpty() || arg0.equals("")) {
                return;
            } else {
                popupNotice(arg0);
            }
            super.onPostExecute(arg0);
        }
    }

    public void popupNotice(String date) {
        final String noticeDate = date;
        View layout = thisActivity.getLayoutInflater().inflate(R.layout.popup_notice, null);

        //webView
        WebView webView = (WebView) layout.findViewById(R.id.webview_popup_notice);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.loadUrl(GlobalVariables.SERVER_ADDR + "Gongji/notice.html");
        AlertDialog.Builder dlg = new AlertDialog.Builder(thisActivity);
        dlg.setCancelable(false);
        dlg.setTitle("공지").setView(layout)
                .setPositiveButton("다시 보지 않기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SharedPreferences prefs = thisActivity.getSharedPreferences("Notice", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putString("date", noticeDate);
                        edit.commit();
                    }
                }).setNegativeButton("닫기", null).show();
    }

    public void changeSettingIcon() {
        ImageView settingIcon = (ImageView) rootView.findViewById(R.id.main_setting_icon);
        if (GlobalVariables.oldVersion) {
            settingIcon.setImageResource(R.drawable.setting_n);
        } else {
            settingIcon.setImageResource(R.drawable.setting);
        }
    }

    public void display() {
        LinearLayout ll;
        int cnt = 0;
        try {
            for (int i = 3; i < 6; i++) {
                ll = (LinearLayout) ((LinearLayout) thisActivity.findViewById(R.id.ll_bus_to_six)).getChildAt(i);
                for (int j = 0; j < 3; j++) {
                    ((TextView) ll.getChildAt(j)).setText(sixTimeList.get(cnt++));
                }
            }

            cnt = 0;
            for (int i = 3; i < 6; i++) {
                ll = (LinearLayout) ((LinearLayout) thisActivity.findViewById(R.id.ll_bus_to_school)).getChildAt(i);
                for (int j = 0; j < 3; j++) {
                    ((TextView) ll.getChildAt(j)).setText(schoolTimeList.get(cnt++));
                }
            }

            //평일(월요일(2)~금요일(6))
            if (yoil > 1 && yoil < 7) {
                if (yoil == 2 && ap == 0 && hour < 3) { // 일->월 오전 03시 이전은 주말
                    ((TextView) thisActivity.findViewById(R.id.six_day_or_end)).setText("(주말)");
                    ((TextView) thisActivity.findViewById(R.id.school_day_or_end)).setText("(주말)");
                    ((TextView) thisActivity.findViewById(R.id.six_day_or_end)).setTextColor((thisActivity.getResources().getColor(R.color.normal_red)));
                    ((TextView) thisActivity.findViewById(R.id.school_day_or_end)).setTextColor((thisActivity.getResources().getColor(R.color.normal_red)));
                } else {
                    ((TextView) thisActivity.findViewById(R.id.six_day_or_end)).setText("(평일)");
                    ((TextView) thisActivity.findViewById(R.id.school_day_or_end)).setText("(평일)");
                    ((TextView) thisActivity.findViewById(R.id.six_day_or_end)).setTextColor((thisActivity.getResources().getColor(R.color.white)));
                    ((TextView) thisActivity.findViewById(R.id.school_day_or_end)).setTextColor((thisActivity.getResources().getColor(R.color.white)));
                }
            }//주말(토요일(7) or 일요일(1))
            else {
                if (yoil == 7 && ap == 0 && hour < 3) { // 금->토 오전 03시 이전은 평일
                    ((TextView) thisActivity.findViewById(R.id.six_day_or_end)).setText("(평일)");
                    ((TextView) thisActivity.findViewById(R.id.school_day_or_end)).setText("(평일)");
                    ((TextView) thisActivity.findViewById(R.id.six_day_or_end)).setTextColor((thisActivity.getResources().getColor(R.color.white)));
                    ((TextView) thisActivity.findViewById(R.id.school_day_or_end)).setTextColor((thisActivity.getResources().getColor(R.color.white)));
                } else {
                    ((TextView) thisActivity.findViewById(R.id.six_day_or_end)).setText("(주말)");
                    ((TextView) thisActivity.findViewById(R.id.school_day_or_end)).setText("(주말)");
                    ((TextView) thisActivity.findViewById(R.id.six_day_or_end)).setTextColor((thisActivity.getResources().getColor(R.color.normal_red)));
                    ((TextView) thisActivity.findViewById(R.id.school_day_or_end)).setTextColor((thisActivity.getResources().getColor(R.color.normal_red)));
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


}

