package ghost.android.ghosthguapp.yasick;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by SEC on 2015-01-12.
 */

public class RecentCallManager {
    private Context context;

    public RecentCallManager(Context con) {
        context = con;
    }

    /* 통화 목록에 추가하는 메소드 */
    public void addRecentInfo(String storeId, Date now) {

        // 날짜 정보 다듬기
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd HH:mm");
        String date = dateFormat.format(now);

        // shared preferences 에 저장
        SharedPreferences sp = context.getSharedPreferences("RecentCallPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // 통화 목록 개수를 읽어와서
        int size = sp.getInt("recent_size", -1);

        // 통화 목록 개수 정보가 없으면 : 0으로 초기환
        if (size == -1) {
            size = 0;
        }
        // 있으면 읽어왔으니 sharedPreferences 에서는 일단 제거
        else {
            editor.remove("recent_size");
        }

        // 만약 목록이 꽉 차있을 경우 (10개일 경우) : 맨 아랫 것을 지우고 한 개씩 당긴다.
        if (size == 10) {
            int i;
            // 0~8 까지는 지우고 다음 것을 가져와 저장하는 작업 반복
            for(i = 0; i < size - 1; i++) {
                editor.remove("Store_" + i);
                editor.remove("Date_" + i);
                editor.putString("Store_" + i, sp.getString("Store_" + (i+1), null));
                editor.putString("Date_" + i, sp.getString("Date_" + (i+1), null));
            }
            // 9 는 지워놓기만 함
            editor.remove("Store_" + (size-1));
            editor.remove("Date_" + (size-1));
            // 목록 개수를 9개로 설정
            size = 9;
        }

        // 현재 목록 마지막에 새로운 정보를 추가한다.
        editor.putString("Store_" + size, storeId);
        editor.putString("Date_" + size, date);

        // 목록 개수 갱신한다. (remove 는 미리 해뒀음!)
        editor.putInt("recent_size", size + 1);

        editor.commit();
    }

    /* 통화 목록 얻기 위한 메소드 */
    public ArrayList<HashMap<String, String>> getRecentInfo() {
        ArrayList<HashMap<String, String>> infoList = new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences("RecentCallPref", Context.MODE_PRIVATE);

        int size = sp.getInt("recent_size", 0);

        for(int i = 0; i < size; i++) {
            HashMap<String, String> info = new HashMap<>();
            info.put("storeId", sp.getString("Store_" + i, null));
            info.put("date", sp.getString("Date_" + i, null));
            infoList.add(info);
        }

        return infoList;
    }

    /* 통화 목록 지우는 메소드 */
    public void removeAll() {
        SharedPreferences sp = context.getSharedPreferences("RecentCallPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        int size = sp.getInt("recent_size", 0);

        for(int i = 0; i < size; i++) {
            editor.remove("Store_" + i);
            editor.remove("Date_" + i);
        }

        editor.remove("recent_size");
        editor.putInt("recent_size", 0);

        editor.commit();
    }
}
