package ghost.android.ghosthguapp.bus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalMethods;
import ghost.android.ghosthguapp.common.GlobalVariables;

public class Bus_to_school extends Fragment {   //학교행(평일) 버스를 나타내는 fragment
    Button go_to_yookgeory_button;
    Button go_to_heunghae;
    ImageButton alarm;
    Button bus_location;
    private ListView lv_bus;
    private BusAdapter_for_school adapter;
    private Activity thisActivity;
    private Context con;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup fragment_container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bus_layout_for_school, fragment_container, false);
        thisActivity = getActivity();
        con = thisActivity.getApplicationContext();

        TextView pyeongill = (TextView) rootView.findViewById(R.id.pyeongill_or_zoomal);
        pyeongill.setText("평일");

        go_to_yookgeory_button = (Button) rootView.findViewById(R.id.go_to_yookgeory_button);   //육거리행 버스 페이지로 넘어가는 버튼
        go_to_yookgeory_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Tabs_for_bus) thisActivity).setSy(1);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new Bus_to_yookgeory());
                fragmentTransaction.commit();
            }
        });

        go_to_heunghae = (Button) rootView.findViewById(R.id.go_to_heunghae);   //흥해 버스 페이지로 넘어간다
        go_to_heunghae.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(thisActivity, Bus_to_heunghae.class);
                startActivity(i);
            }
        });

        alarm = (ImageButton) rootView.findViewById(R.id.imageButton);  //알람 등록 안내를 위한 다이얼로그 표시
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(thisActivity);
                final View innerView = thisActivity.getLayoutInflater().inflate(R.layout.bus_alarm_help_dialog, null);
                dlg.setView(innerView);
                dlg.setTitle("알람등록");
                TextView tv = (TextView) innerView.findViewById(R.id.tt_help_dialog_tv);
                tv.setText("알람을 등록하고자 하는 시간을 찾아 누르시면 해당 시간에 대한 알람 등록이 가능합니다.");
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        bus_location = (Button) rootView.findViewById(R.id.bus_location);   //버스 위치 정보 페이지로 넘어간다
        bus_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(thisActivity, Bus_to_school_location.class);
                startActivity(i);
            }
        });

        lv_bus = (ListView) rootView.findViewById(R.id.listView2);

        //인터넷 연결 확인
        AlertDialog.Builder netConDlgBuilder = GlobalMethods.checkInternet(thisActivity);

        //인터넷 연결 되면
        if (netConDlgBuilder == null) {
            new DoCheckAndSave().execute();
        }

        //인터넷 안되면
        else {
            //파일이 없으면 : 에러 다이얼로그 띄움
            if (!GlobalVariables.fSch.exists()) {
                netConDlgBuilder.show().setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        thisActivity.finish();
                    }
                });
            }
            //파일 있으면 : 가져오기
            else {
                display();
            }

        }
        return rootView;
    }

    private class DoCheckAndSave extends AsyncTask<Void, Void, String> {
        private ProgressDialog doSaveDialog;

        protected void onPreExecute() {
            super.onPreExecute();
            doSaveDialog = ProgressDialog.show(thisActivity, "", "버스 시간표를 자동 업데이트 하고 있습니다\n잠시 기다려 주세요.", true);
            doSaveDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            BusManager_for_school busManager = new BusManager_for_school();
            if (busManager.checkNSave()) {
                return "success";
            } else {
                return "network error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            doSaveDialog.dismiss();
            if (result.equals("success")) {
                // 화면 display
                display();
            } else if (result.equals("network error")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                builder.setTitle("네트워크 오류");
                builder.setMessage("네트워크 상태를 확인 해주세요.")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            doSaveDialog.dismiss();
        }
    }

    // 화면 display
    private void display() {
        BusManager_for_school busManager = new BusManager_for_school();
        try {
            busManager.setting();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        ArrayList<String> tzone = busManager.getTzone();
        ArrayList<BusData> arrayforbusdata = new ArrayList<>();
        HashMap<String, ArrayList<BusData>> map = busManager.getHashMap();

        for (int i = 0; i < tzone.size(); i++) {
            for (int j = 0; j < map.get(tzone.get(i)).size(); j++) {
                if (j == 0)
                    (map.get(tzone.get(i)).get(j)).setType(0);
                else
                    (map.get(tzone.get(i)).get(j)).setType(1);
                arrayforbusdata.add((map.get(tzone.get(i)).get(j)));
            }
        }

        // adapter setting
        adapter = new BusAdapter_for_school(con, thisActivity, R.layout.eachhangmok_for_bus, arrayforbusdata);
        // adapter register
        lv_bus.setAdapter(adapter);
        lv_bus.setVerticalScrollBarEnabled(false);
    }
}
