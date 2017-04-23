package ghost.android.ghosthguapp.bus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalMethods;
import ghost.android.ghosthguapp.common.GlobalVariables;

public class Bus_to_heunghae extends Activity { //흥해행 버스를 보여주는 activity이다.
    Button go_to_back;  //돌아가기 버튼을 위함.
    private ListView lv_bus;
    private BusAdapter_for_heunghae adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_layout_for_heunghae);

        lv_bus = (ListView) findViewById(R.id.listView2);
        go_to_back = (Button) findViewById(R.id.back_to_the_future);

        go_to_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //인터넷 연결 확인
        AlertDialog.Builder netConDlgBuilder = GlobalMethods.checkInternet(Bus_to_heunghae.this);

        //인터넷 연결 되면
        if (netConDlgBuilder == null) {
            //Toast.makeText(Bus_to_heunghae.this, "인터넷 연결 완료", Toast.LENGTH_SHORT).show();
            //다운로드
            //Toast.makeText(getActivity(), "파일 다운로드", Toast.LENGTH_SHORT).show();
            new DoCheckAndSave().execute();
        }

        //인터넷 안되면
        else {
            //Toast.makeText(Bus_to_heunghae.this, "인터넷 안됨", Toast.LENGTH_SHORT).show();
            //파일이 없으면 : 에러 다이얼로그 띄움
            if (!GlobalVariables.fHh.exists()) {
                //Toast.makeText(Bus_to_heunghae.this, "파일 없음", Toast.LENGTH_SHORT).show();
                netConDlgBuilder.show().setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        Bus_to_heunghae.this.finish();
                    }
                });
            }
            //파일 있으면 : 가져오기
            else {
                //Toast.makeText(Bus_to_heunghae.this, "파일 있음", Toast.LENGTH_SHORT).show();
                display();
            }

        }
    }
    private class DoCheckAndSave extends AsyncTask<Void, Void, String> {
        private ProgressDialog doSaveDialog;

        protected void onPreExecute() {
            super.onPreExecute();
            doSaveDialog = ProgressDialog.show(Bus_to_heunghae.this, "", "버스 시간표를 자동 업데이트 하고 있습니다\n잠시 기다려 주세요.", true);
            doSaveDialog.setCancelable(true);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            BusManager_for_heunghae busManager = new BusManager_for_heunghae();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Bus_to_heunghae.this);
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
        BusManager_for_heunghae busManager = new BusManager_for_heunghae();
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
        ArrayList<BusData_for_heunghae> arrayforbusdata = new ArrayList<>();
        HashMap<String, ArrayList<BusData_for_heunghae>> map = busManager.getHashMap();

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
        adapter = new BusAdapter_for_heunghae(Bus_to_heunghae.this, R.layout.eachhangmok_for_heunghae, arrayforbusdata);
        // adapter register
        lv_bus.setAdapter(adapter);
        lv_bus.setVerticalScrollBarEnabled(false);

    }
}