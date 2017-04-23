package ghost.android.ghosthguapp.hgutube;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;

import ghost.android.ghosthguapp.GhostActionBarActivity;
import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalMethods;

public class Hgutube extends GhostActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private HgutubeManager manager;
    private ArrayList<HgutubeData> allVideos;
    private ArrayList<HgutubeData> selectedVideos = new ArrayList<>();
    private Spinner spinner;
    private HgutubeAdapter adapter_listview;
    private EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hgutube_main_page);

        /* 기본적인 레이아웃 셋팅 */
        setBasic();
    }

    /* 기본적인 레이아웃 셋팅 */
    private void setBasic() {

        // 액션바 숨기기 (spinner 스타일 때문에 액션바 액티비티를 상속 받았기 때문에)
       ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.hide();
        else Log.e("action bar 얻어온 것이 null 이다", "Hgutube 의 setBasic() 에서");

        // 게시 문의 메일로 연결해주는 버튼 셋팅
        ImageButton mailBtn = (ImageButton) findViewById(R.id.btn_hgutube_mail);
        mailBtn.setOnClickListener(this);

        /* 서버 또는 파일로부터 HguTube 영상 리스트 받아오기 */
        // 매니져들 생성
        HgutubeFileManager fileManager = new HgutubeFileManager();
        manager = new HgutubeManager(fileManager);

        // 파일 열어놓기
        File file = fileManager.openHgutubeFile();

        // 인터넷 연결 확인 (되면 다이얼로그에 null, 안 되면 안 된다는 다이얼로그를 반환)
        AlertDialog.Builder netConDlgBuilder = GlobalMethods.checkInternet(Hgutube.this);

        // 인터넷 연결 되면 :
        if (netConDlgBuilder == null) {
            // DoCheckAndSave 로 가서
            // 파일 있으면 : 버전 체크 후 업데이트 or 가져오기 결정  (SD카드에 담기기만 하거나 PASS)
            // 파일 없으면 : 바로 업데이트 (SD카드에 담기기만 한다)
            new DoCheckNSaveTube().execute();
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
            // 파일이 있으면 : 인터넷 안 된다는 다이얼로그 띄우고 파일 가져와서 레이아웃 셋팅
            else {
                netConDlgBuilder.show();
                setLayouts();
            }
        }

    }


    /* 버튼 클릭 리스너 */
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_hgutube_mail) {
            // 게시에 대한 설명글 다이얼로그 띄우기
            new AlertDialog.Builder(Hgutube.this)
                    .setTitle("HguTube 게시 요청")
                    .setMessage("HguTube는 한동대학교 학우들이 함께 동영상을 공유하며 만들어가는 공간입니다.\n\n다른 학우들과 공유하고픈 영상을 자유롭게 요청해주세요 :)\n")
                    .setPositiveButton("게시요청할래요", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // 고스트로 문의 메일 보내는 화면 띄우기
                            Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "handongapp@gmail.com", null));
                            try {
                                String str = "동영상 제목 : \n" + "게시자 이름 : \n" + "동영상 전달 방법 : (YouTube URL 도 좋고, 직접 전해주셔도 좋습니다)\n";
                                i.putExtra(Intent.EXTRA_SUBJECT, "HguTube 게시요청");
                                i.putExtra(Intent.EXTRA_TEXT, str);
                                startActivity(Intent.createChooser(i, "handongapp@gmail.com"));

                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("계속 볼래요", null)
                    .show();
        }
    }

    /* 리스트뷰 아이템 클릭 시 */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String videoUrl = selectedVideos.get(position).getVideoUrl();

        new AlertDialog.Builder(Hgutube.this)
                .setTitle("HguTube 시청")
                .setMessage("3G나 LTE 의 경우 많은 데이터 요금이 부과될 수 있으니 WIFI 연결을 권장합니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 동영상 스트리밍
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                        intent.setDataAndType(Uri.parse(videoUrl), "video/*");
                        startActivity(intent);
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }


    /* AsyncTask 클래스를 inner class로 구현
    파일 있으면 : 버전 체크 후 업데이트 or 가져오기 결정
    파일 없으면 : 바로 업데이트  */
    private class DoCheckNSaveTube extends AsyncTask<Void, Void, String> {
        private ProgressDialog doCheckNSaveTubeDialog;

        /* 실행 전에 Main 스레드의 UI 작업 : Progress Dialog */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show(띄울 액티비티, 타이틀, 내용)
            doCheckNSaveTubeDialog = ProgressDialog.show(Hgutube.this, "", "HguTube 리스트를 자동 업데이트 중입니다...\n기다려주세요.");
            doCheckNSaveTubeDialog.setCancelable(false);
        }

        /* 다른 스레드로 백그라운드 작업 수행 */
        @Override
        protected String doInBackground(Void... params) {
            if(manager.checkNSaveTube()) {
                return "success";
            } else {
                return "network error";
            }
        }

        /* 백그라운드 작업 수행 후 UI 작업 : 서버에서 받아 온 리스트 띄우기 or 실패 다이얼로그 띄우기 */
        @Override
        protected void onPostExecute(String result) {
            doCheckNSaveTubeDialog.dismiss();

            // 백그라운드 작업이 성공했으면 : 레이아웃 셋팅
            if (result.equals("success")) {
                setLayouts();
            }
            // 백그라운드 작업이 실패했으면 : 다이얼로그 띄우고 액티비티 강제종료
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
    }

    /* 첫 화면 레이아웃 셋팅 */
    private void setLayouts() {

        // 우선 셋팅할 내용이 될 파일을 파싱해서 array list 로 가져오기
        manager.setting();
        allVideos = manager.getVideos();

        // 화면에 띄우는 용도의 array list 설정 (현재 설정된 정보의 리스트)
        selectedVideos.addAll(allVideos);

        // 리스트뷰
        final ListView listView = (ListView) findViewById(R.id.hgutube_listview);

        // 리스트뷰 Footer 설정
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View footer = inflater.inflate(R.layout.yasick_store_page_footer, null, false);
        listView.addFooterView(footer);

        // 리스트뷰 어댑터 설정
        adapter_listview = new HgutubeAdapter(getApplicationContext(), R.layout.hgutube_item, selectedVideos);
        listView.setAdapter(adapter_listview);

        // 리스트뷰 아이템 클릭 리스너 설정
        listView.setOnItemClickListener(this);

        // 스피너 뷰
        spinner = (Spinner) findViewById(R.id.hgutube_spinner);

        // 스피너와 어댑터 연결
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.hgutube_category_list, R.layout.spinner_item);
        spinner.setAdapter(adapter);

        // 스피너 선택리스너 설정
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 선택된 항목에 대해 리스트를 거르기
                selectedVideos.clear();
                selectedVideos.addAll(filterList());

                // 리스트 뷰 갱신
                adapter_listview.notifyDataSetChanged();
                listView.setSelection(0);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /* 검색창 */
        et_search = (EditText) findViewById(R.id.hgutube_et);
        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력한 내용에 대해 리스트를 거르기
                selectedVideos.clear();
                selectedVideos.addAll(filterList());

                // 리스트뷰 갱신
                adapter_listview.notifyDataSetChanged();
                listView.setSelection(0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // 사용자가 엔터키 입력 시 : 키보드 창 내리기
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() ==       KeyEvent.KEYCODE_ENTER)
                {
                    InputMethodManager ipm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    ipm.toggleSoftInput(0, 0);
                    return false;
                }
                return false;
            }
        });

    }

    /* 선택된 스피너의 값과 검색창의 값에 따라 리스트를 걸러주는 메소드 */
    private ArrayList<HgutubeData> filterList() {

        /* 먼저 스피너 값에 의해 거르기 */
        ArrayList<HgutubeData> temp_by_spinner = new ArrayList<>();
        temp_by_spinner.addAll(allVideos);                      // temp_by_spinner = allVideos; 가 아니라 반드시 addAll 써야하는 이유는 그냥 치환하면 같은 참조값을 가지게 되서 독립적인 두개의 리스트가 되지 못 한다

        final int INDEX_ALL = 0;

        // 스피너의 값을 얻어온다.
        int category = spinner.getSelectedItemPosition();

        // '전체'가 아닌 특정 장소를 선택했을 때만 걸러주기
        if(category != INDEX_ALL) {
            // 일단 초기화
            temp_by_spinner.clear();

            // 모든 비디오 리스트 훑어보기
            for(int i = 0; i < allVideos.size(); i++) {
                // category index 가 현재 스피너에서 선택된 것과 같다면 : temp_by_spinner 에 추가
                if(category == allVideos.get(i).getCategory())
                    temp_by_spinner.add(allVideos.get(i));
            }
        }

        /* 그 다음, 검색창의 값에 따라 리스트를 거르기 */
        // 위에서 걸러온 리스트를 새 리스트에 값 복사
        ArrayList<HgutubeData> temp_by_edittext = new ArrayList<>();
        temp_by_edittext.addAll(temp_by_spinner);

        // 검색창 EditText 의 값을 얻어온다.
        String str = et_search.getText().toString().toUpperCase().replace(" ", "").replace("\n", "");

        // EditText 에 아무 내용도 없다면 : 통과
        if(str != null && !str.equals("")) {
            // 있다면 : 일단 temp_by_edittext 를 일단 초기화한다
            temp_by_edittext.clear();
            // 리스트 쭉 훑기
            for(int i = 0; i < temp_by_spinner.size(); i++) {
                // 지금 EditText 에 있는 값과 같다면 : temp_by_edittext 에 추가
                if(temp_by_spinner.get(i).getTitle().toUpperCase().replace(" ", "").replace("\n", "").contains(str)
                        || temp_by_spinner.get(i).getWriter().toUpperCase().replace(" ", "").replace("\n", "").contains(str))
                    temp_by_edittext.add(temp_by_spinner.get(i));
            }
        }

        return temp_by_edittext;
    }
}
