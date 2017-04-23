package ghost.android.ghosthguapp.hgushop;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;

import ghost.android.ghosthguapp.GhostActionBarActivity;
import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalMethods;

public class Hgushop extends GhostActionBarActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private Spinner spinner_location, spinner_category;
    private ArrayAdapter<HgushopData> adapter_listview;
    private ListView listView;
    private ArrayList<HgushopData> allHgushops;
    private ArrayList<HgushopData> selectedShops = new ArrayList<>();
    private HgushopFileManager fileManager;
    private HgushopManager manager;
    private boolean first_location = true;
    private boolean first_category = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hgushop_main_page);

        // 기본적인 셋팅
        setBasic();
    }

    private void setBasic() {

        /* 액션바 없애기 (스피너 스타일을 위해서 GhostActionBarActivity 를 상속받은 후에 날려버렸음) */
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        /* 서버 or SD카드로부터 HGU SHOP 리스트 받아오기 */
        // 파일 매니져 생성
        fileManager = new HgushopFileManager();
        // HGUSHOP 매니저 생성
        manager = new HgushopManager(fileManager);

        // 파일 열어놓기
        File file = fileManager.openHgushopFile();

        // 인터넷 연결 확인 (되면 다이얼로그에 null, 안 되면 안 된다는 다이얼로그를 반환)
        AlertDialog.Builder netConDlgBuilder = GlobalMethods.checkInternet(Hgushop.this);

        // 인터넷 연결 되면 :
        if (netConDlgBuilder == null) {
            // DoCheckAndSave 로 가서
            // 파일 있으면 : 버전 체크 후 업데이트 or 가져오기 결정  (SD카드에 담기기만 하거나 PASS)
            // 파일 없으면 : 바로 업데이트 (SD카드에 담기기만 한다)
            new DoCheckNSaveShops().execute();
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
            // 파일이 있으면 : 파일 가져와서 레이아웃 셋팅
            else {
                setLayouts();
            }
        }
    }

    /* 레이아웃 셋팅하는 메소드 */
    private void setLayouts() {

        // 파일 내용 파싱해서 array list 로 가져오기
        manager.setting();
        allHgushops = manager.getShops();

        // 화면에 띄우는 용도의 array list 설정 (현재 선택된 정보의 리스트)
        selectedShops.addAll(allHgushops);

        // 스피너 뷰
        spinner_location = (Spinner) findViewById(R.id.hgushop_spinner_location);
        spinner_category = (Spinner) findViewById(R.id.hgushop_spinner_category);


        // 스피너 어댑터 연결
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.hgushop_location_list, R.layout.spinner_item);
        spinner_location.setAdapter(adapter);
        adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.hgushop_category_list, R.layout.spinner_item);
        spinner_category.setAdapter(adapter);


        // 스피너 선택리스너 설정
        spinner_location.setOnItemSelectedListener(this);
        spinner_category.setOnItemSelectedListener(this);


        // 리스트뷰
        listView = (ListView) findViewById(R.id.hgushop_list);

        // 리스트뷰 어댑터 설정
        adapter_listview = new HgushopAdapter(getApplicationContext(), R.layout.hgushop_shops_item, selectedShops);
        listView.setAdapter(adapter_listview);

        // 리스트뷰 아이템 클릭 리스너 설정
        listView.setOnItemClickListener(this);

        // 전체 shop 지도 보러 가기 버튼 설정
        ImageButton goMap = (ImageButton) findViewById(R.id.btn_hgushop_map);
        goMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), HgushopMap.class);
                startActivity(intent);
                // 어차피 지도 페이지에서 BACK 버튼 누르면 shop 페이지 뜨도록 했기 때문에 여기서 죽이는게 더 자연 스럽다
                finish();
            }
        });
    }

    /* 스피너 아이템 선택 시 */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch(parent.getId())
        {
            // 장소 스피너 선택시
            case R.id.hgushop_spinner_location :
                if(first_location) {             // Activity 가 처음 시작될 때 자동 선택되는 걸 막으려고
                    first_location = false;
                }
                else {
                    // 선택된 사항에 대해 리스트를 거르기
                    selectedShops.clear();
                    selectedShops.addAll(filterList());

                    // 리스트뷰 갱신
                    adapter_listview.notifyDataSetChanged();
                    listView.setSelection(0);
                }

                break;

            // 카테고리 스피너 선택시
            case R.id.hgushop_spinner_category :
                if(first_category)                  // Activity 가 처음 시작될 때 자동 선택되는 걸 막으려고
                    first_category = false;
                else {
                    // 선택된 사항에 대해 리스트를 거르기
                    selectedShops.clear();
                    selectedShops.addAll(filterList());

                    // 리스트뷰 갱신
                    adapter_listview.notifyDataSetChanged();
                    listView.setSelection(0);
                }
                break;
        }
    }

    /* 스피너가 아무것도 선택되지 않았을 때 */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /* 리스트뷰 아이템 클릭 시 */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 클릭된 아이템의 정보를 받아오기
        HgushopData shop = selectedShops.get(position);

        // 인텐트 생성
        Intent intent = new Intent(getApplicationContext(), HgushopEachPage.class);
        intent.putExtra("location", shop.getLocationIndex());
        intent.putExtra("category", shop.getCategoryIndex());
        intent.putExtra("shopName", shop.getShopName());
        intent.putExtra("contents", shop.getContents());
        intent.putExtra("latitude", shop.getLatitude());
        intent.putExtra("longitude", shop.getLongitude());
        intent.putExtra("imageUrl", shop.getImageUrl());
        intent.putExtra("phoneNumber", shop.getPhoneNumber());
        startActivity(intent);
        // 액티비티 전환 애니매이션 설정 (슬라이딩)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    /* AsyncTask 클래스를 inner class로 구현
    파일 있으면 : 버전 체크 후 업데이트 or 가져오기 결정
    파일 없으면 : 바로 업데이트  */
    private class DoCheckNSaveShops extends AsyncTask<Void, Void, String> {
        private ProgressDialog doCheckNSaveShopsDialog;

        /* 실행 전에 Main 스레드의 UI 작업 : Progress Dialog */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show(띄울 액티비티, 타이틀, 내용)
            doCheckNSaveShopsDialog = ProgressDialog.show(Hgushop.this, "", "HGU SHOP 리스트를 자동 업데이트 중입니다...\n기다려주세요.");
            doCheckNSaveShopsDialog.setCancelable(false);
        }

        /* 다른 스레드로 백그라운드 작업 수행 */
        @Override
        protected String doInBackground(Void... params) {
            if(manager.checkNSaveShops()) {
                return "success";
            } else {
                return "network error";
            }
        }

        /* 백그라운드 작업 수행 후 UI 작업 : 서버에서 받아 온 리스트 띄우기 or 실패 다이얼로그 띄우기 */
        @Override
        protected void onPostExecute(String result) {
            doCheckNSaveShopsDialog.dismiss();

            // 백그라운드 작업이 성공했으면 : 레이아웃 셋팅
            if(result.equals("success")) {
                setLayouts();
            }
            // 백그라운드 작업이 실패했으면 : 다이얼로그 띄우고 액티비티 강제종료
            else if(result.equals("network error")) {
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
            doCheckNSaveShopsDialog.dismiss();
        }
    }


    /* 스피너에서 선택했을 때 선택된 값에 해당되는 shop 만 걸러내 주는 메소드 */
    private ArrayList<HgushopData> filterList() {

        ArrayList<HgushopData> temp_loc = new ArrayList<>();
        temp_loc.addAll(allHgushops);
        ArrayList<HgushopData> temp_cat = new ArrayList<>();


        final int INDEX_ALL = 0;

        // 우선 스피너들의 값을 얻어온다
        int location = spinner_location.getSelectedItemPosition();
        int category = spinner_category.getSelectedItemPosition();

        /* 먼저 장소에 대해 걸러내기 */
        // '전체'가 아닌 특정 장소를 선택했을 때만 걸러주기
        if(location != INDEX_ALL) {
            // '전체'가 아니라면 일단 temp_loc 초기화
            temp_loc.clear();

            // 모든 샵들에 대하여
            for(int i = 0; i < allHgushops.size(); i++) {
                // location index 가 현재 스피너에서 선택된 것과 같다면 : temp_loc 에 추가
                if(location == allHgushops.get(i).getLocationIndex())
                    temp_loc.add(allHgushops.get(i));
            }
        }
        // 장소만 걸러진 상태의 리스트를 temp_cat 에 기본값으로 준다.
        temp_cat.addAll(temp_loc);

        /* 카테고리에 대해 걸러내기 */
        // '전체'가 아닌 특정 장소를 선택했을 때만 걸러주기
        if(category != INDEX_ALL) {
            // '전체'가 아니라면 일단 temp_cat 초기화
            temp_cat.clear();

            // 모든 샵들에 대하여
            for(int i = 0; i < temp_loc.size(); i++) {
                // category index 가 현재 스피너에서 선택된 것과 같다면 : temp_cat 에 추가
                if(category == temp_loc.get(i).getCategoryIndex())
                    temp_cat.add(temp_loc.get(i));
            }
        }

        // 장소, 카테고리를 모두 거른 리스트를 반환
        return temp_cat;
    }
}