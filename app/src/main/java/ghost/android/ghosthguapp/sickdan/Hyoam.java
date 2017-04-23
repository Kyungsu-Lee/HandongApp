package ghost.android.ghosthguapp.sickdan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalMethods;
import ghost.android.ghosthguapp.common.GlobalVariables;

public class Hyoam extends Fragment {

    private ListView lv_hyoam;
    private ArrayList<HyoamData> al_hyoam;
    private HyoamAdapter adapter;
    TextView hyoam_date;
    ImageButton prev_date, next_date;
    int i = 0, menu_length = 7;
    int day = GlobalVariables.cal.get(Calendar.DAY_OF_MONTH);
    String searchstr;
    String daystr = String.valueOf(day);
    private Activity thisActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup fragment_container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hyoam_layout, fragment_container, false);
        thisActivity = getActivity();
        al_hyoam = new ArrayList<>();

        String SDPath = "" + Environment.getExternalStorageDirectory();
        File file = new File(SDPath + "/HGUapp/moms.xml");

        lv_hyoam = (ListView) rootView.findViewById(R.id.listView);
        hyoam_date = (TextView) rootView.findViewById(R.id.hyoam_date);

        prev_date = (ImageButton) rootView.findViewById(R.id.prev_date);
        next_date = (ImageButton) rootView.findViewById(R.id.next_date);

        next_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i < menu_length - 1) {
                    i = i + 1;
                }
                updateDisplay();
            }
        });
        prev_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i > 0) {
                    i = i - 1;
                }
                updateDisplay();
            }
        });
        //인터넷 연결 확인
        AlertDialog.Builder netConDlgBuilder = GlobalMethods.checkInternet(thisActivity);

        //인터넷 연결 되면
        if (netConDlgBuilder == null) {
            //다운로드
            new DoSaveAtSDcard().execute();
        }

        //인터넷 안되면
        else {
            //파일이 없으면 : 에러 다이얼로그 띄움
            if (!file.exists()) {
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


    private class DoSaveAtSDcard extends AsyncTask<Void, Void, String> {
        private ProgressDialog doSaveDialog;

        protected void onPreExecute() {
            super.onPreExecute();
            doSaveDialog = ProgressDialog.show(thisActivity, "", "식단을 자동 업데이트 하고 있습니다\n잠시 기다려 주세요.", true);
            doSaveDialog.setCancelable(true);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            HyoamManager hyoamManager = new HyoamManager();
            if (hyoamManager.saveAtSDcard()) {
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
        HyoamManager hyoamManager = new HyoamManager();
        try {
            hyoamManager.setting();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        if (day < 10) {
            daystr = "0" + daystr;
        }
        try {
            for (int j = 0; j < menu_length; j++) {
                searchstr = hyoamManager.getStringDayOfWeek(j).substring(4, 6);
                if (searchstr.equals(daystr)) {
                    i = j;
                    break;
                }
            }

            al_hyoam = hyoamManager.getList(i);
            hyoam_date.setText(hyoamManager.getStringDayOfWeek(i));
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException obe) {
            obe.printStackTrace();
        }

        // adapter setting
        adapter = new HyoamAdapter(thisActivity, R.layout.eachhangmok_for_sickdan_menu, al_hyoam);
        // adapter register
        lv_hyoam.setAdapter(adapter);
        lv_hyoam.setVerticalScrollBarEnabled(false);
    }

    private void updateDisplay() {
        HyoamManager hyoamManager = new HyoamManager();
        try {
            hyoamManager.setting();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        try {
            al_hyoam = hyoamManager.getList(i);
            hyoam_date.setText(hyoamManager.getStringDayOfWeek(i));

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException obe) {
            obe.printStackTrace();
        }

        // adapter setting
        adapter = new HyoamAdapter(thisActivity, R.layout.eachhangmok_for_sickdan_menu, al_hyoam);
        // adapter register
        lv_hyoam.setAdapter(adapter);
        lv_hyoam.setVerticalScrollBarEnabled(false);
    }
}






