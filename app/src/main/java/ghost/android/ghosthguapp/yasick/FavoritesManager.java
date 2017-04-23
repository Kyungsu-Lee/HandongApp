package ghost.android.ghosthguapp.yasick;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by SEC on 2015-01-10.
 */
public class FavoritesManager {
    private Context context;

    public FavoritesManager(Context con) {
        context = con;
    }

    /* 즐겨찾기 추가한 업체 리스트를 sharedPreferences 에 저장하는 메소드 */
    public void renewFavoritesInfo(ArrayList<String> favoritesList) {

        SharedPreferences sp = context.getSharedPreferences("FavoritesPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // arraylist 의 사이즈부터 저장
        editor.putInt("FavoritesList_size", favoritesList.size());

        // 즐겨찾기로 설정한 업체 이름들의 리스트를 저장
        for(int i = 0; i < favoritesList.size(); i++) {
            editor.remove("Favorites_" + i);
            editor.putString("Favorites_" + i, favoritesList.get(i));
        }
        editor.commit();
    }

    /* sharedPreferences 에 저장된 즐겨찾기 목록을 불러온다. */
    public ArrayList<String> getFavoritesInfo() {
        ArrayList<String> favoritesList = new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences("FavoritesPreference", Context.MODE_PRIVATE);

        // 사이즈 불러오기
        int size = sp.getInt("FavoritesList_size", 0);

        // 즐겨찾기로 설정된 업체 이름들의 리스트를 불러오기
        for(int i = 0; i < size; i++) {
            favoritesList.add(sp.getString("Favorites_" + i, null));
        }

        // 불러온 리스트를 반환
        return favoritesList;
    }

    /* 해당 업체의 아이디가 즐겨찾기 목록에 있는지, 있으면 position 이 어디인지 검사해주는 메소드 */
    public int whereIsThisInFavorites(String storeId, ArrayList<String> favoritesList) {

        // 만약 arraylist 가 빈 것이면 바로 -1 반환
        if (favoritesList.size() <= 0)
            return -1;

        int i = 0;

        for(i = 0; i < favoritesList.size(); i++) {
            if(favoritesList.get(i).equals(storeId))
                return i;
        }

        return -1;
    }
}
