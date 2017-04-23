package ghost.android.ghosthguapp.yasick;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalMethods;

/**
 * Created by SEC on 2014-12-29.
 */

public class YasickMain extends Activity implements OnClickListener{

    Button allList;
    Button zzimdark;
    Button chicken;
    FragmentManager fm;
    private ArrayList<YasickStoreData> al_yasickStore;
    private YasickFileManager fileManager;
    private YasickStoreManager storeManager;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yasick_main_page);

        /* 액션바 버튼, 탭 버튼 등 기본 레이아웃 셋팅 */
        setBasicLayout();


        /* 서버 or SD카드로부터 야식집 리스트 받아오기 */
        // 먼저 파일 열어놓기
        File file = fileManager.openListFile();

        // 인터넷 연결 확인 (되면 다이얼로그에 null, 안 되면 안 된다는 다이얼로그를 반환)
        AlertDialog.Builder netConDlgBuilder = GlobalMethods.checkInternet(YasickMain.this);

        // 인터넷 연결 되면 :
        if (netConDlgBuilder == null) {
            // DoCheckAndSave 로 가서
            // 파일 있으면 : 버전 체크 후 업데이트 or 가져오기 결정  (SD카드에 담기기만 하거나 PASS)
            // 파일 없으면 : 바로 업데이트 (SD카드에 담기기만 한다)
            new DoCheckAndSave().execute();
        }

        // 인터넷 안 되면
        else {
            // 파일이 없으면 : 에러 다이얼로그 띄우고 액티비티 종료
            if (!file.exists()) {
                netConDlgBuilder.show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
            }
            // 파일이 있으면 : 파일 가져와서 첫 화면 셋팅
            else {
                setLayouts();
            }
        }
    }

    public ArrayList<YasickStoreData> getYasickStoreList() { return al_yasickStore; }

    @Override
    public void onClick(View v) {

        /*각 탭 버튼에 대해 onClick 구현*/
        switch(v.getId())
        {
            case R.id.tab_all_list:
                // 현재 탭 버튼을 bold 체로 표시
               //allList.setTypeface(boldFace);
                //zzimdark.setTypeface(normalFace);
                //chicken.setTypeface(normalFace);

                // 현재 탭 버튼을 selected 하게 설정
                allList.setSelected(true);
                zzimdark.setSelected(false);
                chicken.setSelected(false);

                // 현재 탭에 대한 화면(fragment) 실행
                FragmentTransaction ft1 = fm.beginTransaction();
                ft1.replace(R.id.fragment_container, new AllListFragment());
                ft1.commit();

                break;
            case R.id.tab_zzimdark:
                //현재 탭 버튼을 bold 체로 표시
                //allList.setTypeface(normalFace);
                //zzimdark.setTypeface(boldFace);
                //chicken.setTypeface(normalFace);

                // 현재 탭 버튼을 selected 하게 설정
                allList.setSelected(false);
                zzimdark.setSelected(true);
                chicken.setSelected(false);

                //현재 탭에 대한 화면(fragment) 실행
                FragmentTransaction ft2 = fm.beginTransaction();
                ft2.replace(R.id.fragment_container, new ZzimdarkFragment());
                ft2.commit();

                break;
            case R.id.tab_chicken:
                //현재 탭 버튼을 bold 체로 표시
                //allList.setTypeface(normalFace);
                //zzimdark.setTypeface(normalFace);
                //chicken.setTypeface(boldFace);

                // 현재 탭 버튼을 selected 하게 설정
                allList.setSelected(false);
                zzimdark.setSelected(false);
                chicken.setSelected(true);

                //현재 탭에 대한 화면(fragment) 실행
                FragmentTransaction ft3 = fm.beginTransaction();
                ft3.replace(R.id.fragment_container, new ChickenFragment());
                ft3.commit();
                break;
        }
    }

    /* AsyncTask 클래스를 inner class로 구현
    파일 있으면 : 버전 체크 후 업데이트 or 가져오기 결정
    파일 없으면 : 바로 업데이트  */
    private class DoCheckAndSave extends AsyncTask<Void, Void, String> {
        private ProgressDialog doCheckNSaveDialog;

        /* 실행 전에 Main 스레드의 UI 작업 : Progress Dialog */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show(띄울 액티비티, 타이틀, 내용)
            doCheckNSaveDialog = ProgressDialog.show(YasickMain.this, "", "야식 리스트를 자동 업데이트 중입니다...\n기다려주세요.");
            doCheckNSaveDialog.setCancelable(false);
        }

        /* 다른 스레드로 백그라운드 작업 수행 */
        @Override
        protected String doInBackground(Void... arg0){
            if(storeManager.checkNSave()) {
                return "success";
            } else {
                return "network error";
            }
        }
        /* 백그라운드 작업 수행 후 UI 작업 : 서버에서 받아 온 리스트 띄우기 or 실패 다이얼로그 띄우기 */
        @Override
        protected void onPostExecute(String result) {
            doCheckNSaveDialog.dismiss();

            //백그라운드 작업이 성공했으면 : 첫 화면 레이아웃 셋팅
            if(result.equals("success")) {
                setLayouts();
            }
            //백그라운드 작업이 실패했으면 : 다이얼로그 띄우고 액티비티 강제종료
            else if (result.equals("network error")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("네트워크 오류");
                builder.setMessage("네트워크 상태를 확인 해주세요.")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                finish();
                            }
                        });
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            doCheckNSaveDialog.dismiss();
        }

    }

    /* 액션바, 탭 버튼 등 기본 레이아웃 셋팅 */
    private void setBasicLayout() {

        /* 필요한 매니져들 다 생성 */
        fm = getFragmentManager();

        fileManager = new YasickFileManager();
        storeManager = new YasickStoreManager(fileManager);

 /*       //set actionbar title for yasick activity
        if(ghostActionBarTitle != null){
            ghostActionBarTitle.setText(R.string.title_yasick_main);
        }

        // set actionbar logo for yasick activity
        if(ghostActionBarLogo != null){
            ghostActionBarLogo.setImageResource(R.drawable.yasick_logo);
        }

        // set background color for yasick activity
        if(ghostActionBar != null){
            ghostActionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.yasick_actionbar_background));
        }
*/
        /* 액션바에 마이메뉴 버튼 셋팅 */
        Button myMenu = (Button) findViewById(R.id.btn_yasick_mymenu);
        myMenu.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyMenu.class);
                startActivity(intent);

                // 액티비티 전환 애니매이션 주기
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        /* 탭 버튼들 */
        allList = (Button)findViewById(R.id.tab_all_list);
        zzimdark = (Button)findViewById(R.id.tab_zzimdark);
        chicken = (Button)findViewById(R.id.tab_chicken);

        /* 폰트적용 */

        myMenu.setTypeface(Typeface.DEFAULT); // 마이메뉴 버튼
        allList.setTypeface(Typeface.DEFAULT);  // 탭_전체보기 버튼
        zzimdark.setTypeface(Typeface.DEFAULT);   // 탭_찜닭및기타 버튼
        chicken.setTypeface(Typeface.DEFAULT);    // 탭_치킨 버튼
  /*      if(ghostActionBarTitle != null)
            ghostActionBarTitle.setTypeface(normalFace);    //액션바 타이틀*/

        /* 각 탭 버튼의 OnClickListener 설정 */
        allList.setOnClickListener(this);
        zzimdark.setOnClickListener(this);
        chicken.setOnClickListener(this);

        /* 첫번째 켜질 탭 버튼을 selected 하게 보이도록 설정 */
        allList.setSelected(true);
        zzimdark.setSelected(false);
        chicken.setSelected(false);
    }

    /* 첫 화면 레이아웃 셋팅하는 메소드 */
    private void setLayouts() {
        // YasickStoreManager에서 리스트 가져오기
        storeManager.setting();
        al_yasickStore = storeManager.getList();

        /* 처음으로 띄울 화면(fragment)를 inflate */
        // 새로운 fragment transaciton 시작
        FragmentTransaction ft = fm.beginTransaction();
        // fragment 를 transaction 에 add
        ft.add(R.id.fragment_container, new AllListFragment());
        // transaction 을 UI 큐에 추가한다
        ft.commit();
    }

}
