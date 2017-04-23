package ghost.android.ghosthguapp.yasick;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;

/**
 * Created by SEC on 2015-01-01.
 */
public class ZzimdarkFragment extends Fragment {

    private Activity thisActivity;
    private ArrayList<YasickStoreData> al_yasickStore_zzimdark;
    private ListView lv_zzimdark_yasickStore;
    private YasickStoreAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup fragment_container, Bundle savedInstanceState){
        thisActivity = getActivity();

        // Create or inflate the Fragment's UI, and return it. If this Fragment has no UI then return null
        View rootView = inflater.inflate(R.layout.zzimdark_fragment_layout, fragment_container, false);

        // YasickMain 액티비티에서 yasick 리스트 정보 ArrayList 를 받아오기
        al_yasickStore_zzimdark = filterZzimdarkStore(((YasickMain) thisActivity).getYasickStoreList());

        // al_yasickStore 를 뿌릴 리스트뷰 레이아웃 찾기 (fragment 레이아웃인 rootView에 있음)
        lv_zzimdark_yasickStore = (ListView) rootView.findViewById(R.id.lv_zzimdarklist);

        // 어댑터 선언
        adapter = new YasickStoreAdapter(thisActivity, R.layout.yasick_store_item, al_yasickStore_zzimdark);
        // 리스트뷰 레이아웃에 어댑터 설정
        lv_zzimdark_yasickStore.setAdapter(adapter);

        return rootView;
    }

    // 전체 야식집 리스트 중 찜닭집만 골라내는 메소드
    private ArrayList<YasickStoreData> filterZzimdarkStore(ArrayList<YasickStoreData> al_all) {

        YasickStoreData store = new YasickStoreData();
        ArrayList<YasickStoreData> al_result_zzimdark = new ArrayList<YasickStoreData>();

        // ArrayList 가 null 일 경우 : null 반환
        if(al_all == null)
            return null;
            // ArrayList 가 null 이 아닐 경우 : 찜닭집만 골라낸 ArrayList 를 반환
        else {
            for (int i = 0; i < al_all.size(); i++) {
                store = al_all.get(i);
                if( store.getCategory().equals("nonChic"))
                    al_result_zzimdark.add(store);
            }
        }

        return al_result_zzimdark;
    }
}
