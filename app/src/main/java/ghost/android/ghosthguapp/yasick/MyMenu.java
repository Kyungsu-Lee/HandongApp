package ghost.android.ghosthguapp.yasick;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import ghost.android.ghosthguapp.R;

public class MyMenu extends Activity {

    YasickFileManager fileManager;
    FavoritesManager favoritesManager;
    RecentCallManager recentCallManager;
    YasickStoreManager storeManager;

    private ArrayList<String> al_id_favorites;
    private ArrayList<HashMap<String, String>> al_info_recents;
    private ArrayList<YasickStoreData> al_stores;
    private ArrayList<YasickStoreData> al_favorites;
    private ArrayList<CalledStoreData> al_recents;
    private YasickStoreAdapter f_adapter;
    private CalledStoreAdapter r_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yasick_mymenu_page);

        /* 관리자들 생성하기 */
        fileManager = new YasickFileManager();
        favoritesManager = new FavoritesManager(getApplicationContext());
        recentCallManager = new RecentCallManager(getApplicationContext());
        storeManager = new YasickStoreManager(fileManager);

        /* 정보 가져오기 */
        fileManager.openListFile();
        storeManager.setting();
        al_stores = storeManager.getList();

        /* 즐겨찾기, 최근 통화목록에 있는 업체만 추려낸다 */
        al_favorites = new ArrayList<>();
        al_recents = new ArrayList<>();
        selectFavoriteList();
        selectCallList();

        /* 즐겨찾기 레이아웃 셋팅 */
        //TextView title_favorites = (TextView) findViewById(R.id.title_favorites);
        //title_favorites.setTypeface(boldFace);
        ListView f_listView = (ListView) findViewById(R.id.list_favorites);
        f_adapter = new YasickStoreAdapter(MyMenu.this, R.layout.yasick_store_item, al_favorites);
        f_listView.setAdapter(f_adapter);

        /* 통화목록 레이아웃 셋팅 */
        //TextView title_recent = (TextView) findViewById(R.id.title_recent);
        //title_recent.setTypeface(boldFace);
        ListView r_listView = (ListView) findViewById(R.id.list_recent);
        r_adapter = new CalledStoreAdapter(MyMenu.this, R.layout.called_store_item, al_recents);
        r_listView.setAdapter(r_adapter);


        /* 지우기 버튼 셋팅 */
        Button btnRemove = (Button) findViewById(R.id.btn_recent_remove);
        //btnRemove.setTypeface(boldFace);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final YasickDialog dialog = new YasickDialog(MyMenu.this);
                dialog.setTitleContents("정말 지우시겠습니까?");
                dialog.changeButtonTitle("지우기");
                dialog.call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 목록 재정비
                        recentCallManager.removeAll();
                        al_recents.clear();
                        // 리스트뷰 갱신
                        r_adapter.notifyDataSetInvalidated();

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    /* ArrayList<YasickStoreData> 에서 해당 업체 아이디를 가진 StoreData 객체를 찾는 메소드. 못 찾으면 null 반환 */
    private YasickStoreData getStoreData(String storeId) {
        YasickStoreData store;

        for(int i = 0; i < al_stores.size(); i++) {
            store = al_stores.get(i);
            if(storeId.equals(store.getStoreId()))
                return store;
        }

        return null;
    }

    /* 즐겨찾기 목록에 있는 업체만 추려내는 메소드 */
    private void selectFavoriteList() {

        al_id_favorites = favoritesManager.getFavoritesInfo();
        ArrayList<YasickStoreData> temp = new ArrayList<>();
        for (int i = 0; i < al_id_favorites.size(); i++) {
            try {
                String storeId = al_id_favorites.get(i);
                temp.add(getStoreData(storeId));
            } catch (NullPointerException e) {
                // 띄울 리스트에 ADD 안 하고 넘어가기
            }
        }
        al_favorites.clear();
        al_favorites.addAll(temp);
    }

    /* 최근 통화 목록에 있는 업체만 추려내는 메소드 */
    private void selectCallList() {

        al_info_recents = recentCallManager.getRecentInfo();
        ArrayList<CalledStoreData> temp = new ArrayList<>();
        int size = al_info_recents.size();
        for(int i = 0; i < size; i++) {
            try {
                // 업체 정보 얻어오기
                HashMap<String, String> info = al_info_recents.get((size - 1) - i);
                String storeId = info.get("storeId");
                String date = info.get("date");
                YasickStoreData store = getStoreData(storeId);

                // 통화 기록 정보 저장
                CalledStoreData calledStore = new CalledStoreData();
                calledStore.setStoreId(store.getStoreId());
                calledStore.setCallNumber(store.getPhone());
                calledStore.setStoreName(store.getName());
                calledStore.setCallTime(date);
                temp.add(calledStore);
            } catch (NullPointerException e) {
                // 띄울 리스트에 ADD 안 하고 넘어가기
            }
        }
        al_recents.clear();
        al_recents.addAll(temp);
    }

    /* 페이지가 다시 업데이트 되도록 만들기 위해서 오버라이드 */
    @Override
    public void onResume() {
        super.onResume();

        /* 즐겨찾기, 최근 통화목록에 있는 업체 업데이트 */
        selectFavoriteList();
        selectCallList();

        /* 리스트뷰 갱신 */
        f_adapter.notifyDataSetChanged();
        r_adapter.notifyDataSetChanged();
    }

    /* 다시 돌아가는 애니매이션 효과 주기 위해서 (back 키 누를 때도 finish() 가 호출된다는 점을 이용해서) */
    @Override
    public void finish() {

        super.finish();

        // 액티비티 전환 애니매이션 설정 (슬라이딩)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
