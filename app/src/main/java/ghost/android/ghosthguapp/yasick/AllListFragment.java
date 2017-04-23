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


/** 야식 메뉴 중 전체보기 리스트를 담당할 fragment */
public class AllListFragment extends Fragment {

    private ListView lv_all_yasickStore;
    private ArrayList<YasickStoreData> al_yasickStore;
    private YasickStoreAdapter adapter;
    private Activity thisActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup fragment_container, Bundle savedInstanceState) {

        thisActivity = getActivity();

        // Create or inflate the Fragment's UI, and return it. If this Fragment has no UI then return null
        View rootView = inflater.inflate(R.layout.alllist_fragment_layout, fragment_container, false);

        // YasickMain 액티비티에서 yasick 리스트 정보 ArrayList 를 받아오기
        al_yasickStore = ((YasickMain)thisActivity).getYasickStoreList();
        // al_yasickStore 를 뿌릴 리스트뷰 레이아웃 찾기 (fragment 레이아웃인 rootView에 있음)
        lv_all_yasickStore = (ListView) rootView.findViewById(R.id.lv_alllist);

        // 어댑터 선언
        adapter = new YasickStoreAdapter(thisActivity, R.layout.yasick_store_item, al_yasickStore);
        // 리스트뷰 레이아웃에 어댑터 설정
        lv_all_yasickStore.setAdapter(adapter);

        return rootView;
    }

}