package ghost.android.ghosthguapp.yasick;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.ExpandableHeightGridView;
import ghost.android.ghosthguapp.common.GlobalMethods;

/**
 * Created by SEC on 2015-01-05.
 */

public class YasickStorePage extends Activity implements View.OnClickListener {
    String storeId;
    String storePhone;
    String storeName;
    ArrayList<MainMenuItem> mainMenuItems;
    ArrayList<AllMenuItem> allMenuItems;
    StoreInfo storeInfo;

    // 즐겨찾기 관련 멤버들
    FavoritesManager favoritesManager;
    ArrayList<String> favoritesList;
    boolean isFavorite;
    int position;
    ImageButton addToFavorites;

    // common 패키지에서 정의된 커스텀 뷰들
    ExpandableHeightGridView grid_mainmenu;
    ListView list_allmenu;

    YasickMainMenuAdapter yasickMainMenuAdapter;

    // File 을 관리하는 객체
    private StoresFileManager fileManager = new StoresFileManager();

    private YasickStoresEachManager storeManager = new YasickStoresEachManager(fileManager);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yasick_store_page);


        // 야식집 리스트에서 해당 야식집 정보를 인텐트를 통해서 받아 옴
        Intent intent = getIntent();
        storeId = intent.getStringExtra("storeId");
        storePhone = intent.getStringExtra("storePhone");
        storeName = intent.getStringExtra("storeName");

        // 액션바 뷰의 타이틀을 업체 이름으로 설정
        TextView actionbar_title = (TextView) findViewById(R.id.yasick_each_actionbar_title);
        actionbar_title.setText(storeName);

        /* 전화 걸기 버튼 셋팅 */
        Button callStore = (Button) findViewById(R.id.btn_callStore);
        callStore.setOnClickListener(this);

        /* 전단지 보기 버튼 셋팅 */
        Button showCatalog = (Button) findViewById(R.id.btn_showMenu);
        showCatalog.setOnClickListener(this);

        /* 즐겨찾기 관련된 정보 셋팅 */
        setFavoritesInfo();


        /* 서버에서 업체에 대한 정보들 받아오기 (xml 파일 내용들은 여기 멤버 변수로 파싱까지, 이미지는 그냥 URL 에 저장한 채로) */
        // 먼저 파일 열어 놓기
        File file = fileManager.openStorePageFile(storeId);

        // 인터넷 연결 확인 (되면 다이얼로그에 null, 안 되면 안 된다는 다이얼로그를 반환)
        AlertDialog.Builder netConDlgBuilder = GlobalMethods.checkInternet(YasickStorePage.this);

        // 인터넷 연결 되면 :
        if (netConDlgBuilder == null) {
            // DoCheckAndSave_PageNImages 로 가서
            // 파일 있으면 : 버전 체크 후 업데이트 or 가져오기 결정  (SD카드에 담기기만 하거나 PASS)
            // 파일 없으면 : 바로 업데이트 (SD카드에 담기기만 한다)
            // 어쨋든 해당 파일을 가지고 레이아웃을 셋팅한다
            new DoCheckAndSave_PageNImages().execute();
        }

        // 인터넷 안 되면
        else {
            // 파일이 없으면 : 에러 다이얼로그 띄우기
            if(!file.exists()) {
                netConDlgBuilder.show();
            }
            // 파일이 있으면 : 파일 가져와서 첫 화면 셋팅
            else {
                setLayouts();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /* 전화 걸기 버튼 */
            case R.id.btn_callStore:
                // shared preference 에 통화 기록 저장은 다이얼로그 측에서!!! (혹시 다이얼로그 바꿀거면 그 부분도 설정해주어야 한다.)

                //name, phone 정보가 있는 다이얼로그 창 : 거기로 전화 걸기
                YasickDialog dialog = new YasickDialog(YasickStorePage.this);
                dialog.setName(storeName);
                dialog.setPhone(storePhone);
                dialog.setStoreId(storeId);
                dialog.titleSetting();

                //dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                //dialog.getWindow().setDimAmount(0.7f);
                dialog.show();
                break;

            /* 전단지 보기 버튼 */
            case R.id.btn_showMenu:
                // 해당 액티비티 띄우기
                Intent intent = new Intent(getApplicationContext(), YasickStoreCatalogPage.class);
                intent.putExtra("storeId", storeId);
                intent.putExtra("storeName", storeName);
                startActivity(intent);
                break;

            /* 즐겨찾기 버튼 */
            case R.id.btn_favorites:
                /* isFavorites 값에 따라 흐름 분기 */
                // 클릭 이전에 즐겨찾기에 설정되어 있었던 경우 : 버튼이미지와 isFavorite 을 즐겨찾기 비설정 모드로
                if(isFavorite)
                {
                    // 이미지 바꾸기
                    addToFavorites.setImageResource(R.drawable.unselected_star);

                    /* sharedPreferences 에 즐겨찾기 변경사항을 갱신한다. (처음에 설정 되어있었다면 -> 현재는 삭제 되어야 한다는 의미!) */

                    // 만약 저장된 position 이 유효한 값이 아니면 : 다시 위치 불러오기
                    if(position == -1)
                        position = favoritesManager.whereIsThisInFavorites(storeId, favoritesList);

                    // position 이 확실히 유효한 값일 때만
                    if(position != -1)
                        favoritesList.remove(position);                // 지우고
                    favoritesManager.renewFavoritesInfo(favoritesList); // 갱신

                    // 즐겨찾기 비설정 모드
                    position = -1;
                    isFavorite = false;
                }

                // 클릭 이전에 즐겨찾기에 설정되어 있지 않았던 경우 : 버튼이미지와 isFavorite 을 즐겨찾기 설정 모드로
                else
                {
                    // 이미지 바꾸기
                    addToFavorites.setImageResource(R.drawable.selected_star);

                    /* sharedPreferences 에 즐겨찾기 변경사항을 갱신한다. (처음에 설정 되어있지 않았다면 -> 현재는 추가 되어야 한다는 의미!) */
                    favoritesList.add(storeId);                         // 추가하고
                    favoritesManager.renewFavoritesInfo(favoritesList); // 갱신

                    // 위치 저장해 두기
                    position = favoritesManager.whereIsThisInFavorites(storeId, favoritesList);
                    isFavorite = true;
                }
                break;
        }
    }



    /* AsyncTask 클래스를 inner class로 구현
            파일 있으면 : 버전 체크 후 업데이트 or 가져오기 결정
            파일 없으면 : 바로 업데이트
       그런 후, 파일의 내용을 액티비티의 멤버로 가져온다 */
    private class DoCheckAndSave_PageNImages extends AsyncTask<Void, Void, String> {
        private ProgressDialog doCheckNSaveDialog;

        /* 실행 전에 Main 스레드의 UI 작업 : Progress Dialog */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show(띄울 액티비티, 타이틀, 내용)
            doCheckNSaveDialog = ProgressDialog.show(YasickStorePage.this, "", "야식업체 페이지를 자동 업데이트 중입니다...\n업데이트 사항이 있을 시 시간이 다소 걸릴 수 있습니다.");
            doCheckNSaveDialog.setCancelable(false);
        }

        /* 다른 스레드로 백그라운드 작업 수행 */
        @Override
        protected String doInBackground(Void... arg0) {
            if (storeManager.checkNSave_PageNImages(storeId)) {
                return "success";
            } else {
                return "network error";
            }
        }

        /* 백그라운드 작업 수행 후 UI 작업 : 서버에서 받아 온 리스트 띄우기 or 실패 다이얼로그 띄우기 */
        @Override
        protected void onPostExecute(String result) {
            doCheckNSaveDialog.dismiss();

            //백그라운드 작업이 성공했으면 : 레이아웃 셋팅
            if (result.equals("success")) {
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

    /* 즐겨찾기 관련된 설정 */
    private void setFavoritesInfo() {
        /* 즐겨찾기 버튼 셋팅 */
        addToFavorites = (ImageButton) findViewById(R.id.btn_favorites);
        addToFavorites.setOnClickListener(this);

        /* 즐겨찾기 관리자 실행해서 즐겨찾기 정보 셋팅 */
        favoritesManager = new FavoritesManager(getApplicationContext());
        favoritesList = favoritesManager.getFavoritesInfo();
        // 즐겨찾기 설정 여부와 설정되어있다면 그 위치까지 받아오기
        position = favoritesManager.whereIsThisInFavorites(storeId, favoritesList);
        if (position == -1) {
            isFavorite = false;
        } else {
            isFavorite = true;
        }

        // 즐겨찾기 여부에 따라 버튼 이미지 셋팅하기
        if (isFavorite) {
            addToFavorites.setImageResource(R.drawable.selected_star);
        }
        else {
            addToFavorites.setImageResource(R.drawable.unselected_star);
        }
    }

    /* 파일의 정보를 액티비티의 멤버로 가져오고, 레이아웃으로도 셋팅하는 메소드 */
    private void setLayouts() {
        // YasickStoresEachManager 에서 업체정보와 메뉴 리스트들 현재 Activity 로 가져오기
        storeManager.setting(storeId);
        storeInfo = storeManager.getStoreInfo();
        mainMenuItems = storeManager.getMainMenuItems();
        allMenuItems = storeManager.getAllMenuItems();
        // 메뉴에선 sort 안 하는게 좋다


        /* ListView 의 header 로 넣을 뷰를 inflate 한다. */
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View header = inflater.inflate(R.layout.yasick_store_page_info, null, false);
        View footer = inflater.inflate(R.layout.yasick_store_page_footer, null, false);

        /* 헤더 뷰의 내용을 설정 */
        /* 업체 정보를 레이아웃에 설정 */
        TextView tv_holiday = (TextView) header.findViewById(R.id.tv_holiday);
        TextView tv_runTime = (TextView) header.findViewById(R.id.tv_runTime);
        TextView tv_special = (TextView) header.findViewById(R.id.tv_special);

        // 정보가 없는 경우 '미정'으로 통일
        if(storeInfo.holiday.equals("null") || storeInfo.holiday == null)
            storeInfo.holiday = "미정";
        if(storeInfo.runTime.equals("null") || storeInfo.runTime == null)
            storeInfo.runTime = "미정";
        if(storeInfo.special.equals("null") || storeInfo.special == null)
            storeInfo.special = "미정";


        // TextView 가 존재하면 setText 하기
        if (tv_holiday != null)
            tv_holiday.setText(storeInfo.holiday);
        if (tv_runTime != null )
            tv_runTime.setText(storeInfo.runTime);
        if (tv_special != null)
            tv_special.setText(storeInfo.special);

        /* 메인 메뉴를 그리드뷰에 배치 */
        grid_mainmenu = new ExpandableHeightGridView(this);
        grid_mainmenu = (ExpandableHeightGridView) header.findViewById(R.id.grid_mainmenu);
        grid_mainmenu.setNumColumns(2);
        yasickMainMenuAdapter = new YasickMainMenuAdapter(getApplicationContext(), R.layout.yasick_mainmenu_item, mainMenuItems, storeId);
        grid_mainmenu.setAdapter(yasickMainMenuAdapter);
        grid_mainmenu.setExpanded(true);


        /* 리스트뷰 본문 내용 설정 */
        /* ALL 메뉴를 리스트뷰에 배치 */
        list_allmenu = new ListView(this);
        list_allmenu = (ListView) findViewById(R.id.list_allmenu);
        // Header, Footer 설정
        list_allmenu.addHeaderView(header);
        list_allmenu.addFooterView(footer);
        // 어댑터 설정
        list_allmenu.setAdapter(new YasickAllMenuAdapter(getApplicationContext(), R.layout.yasick_allmenu_item, allMenuItems, storeId));


    }


    /* 이미지가 올라가는 액티비티이므로 메모리 관리를 위해서 비트맵을 리사이클시켜주기 위해 override한다 */
    @Override
    protected void onDestroy() {

        /* 비트맵을 recycle 해준다 */
        if(yasickMainMenuAdapter != null)
            yasickMainMenuAdapter.doRecycle();

        super.onDestroy();
    }

}


