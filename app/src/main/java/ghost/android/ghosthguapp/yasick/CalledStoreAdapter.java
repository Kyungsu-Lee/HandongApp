package ghost.android.ghosthguapp.yasick;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;

/**
 * Created by SEC on 2015-01-13.
 */
public class CalledStoreAdapter extends ArrayAdapter<CalledStoreData> {
    private ArrayList<CalledStoreData> calledStoreList;
    private Context context;

    public CalledStoreAdapter(Context con, int id, ArrayList<CalledStoreData> list) {
        super(con, id, list);
        context = con;
        calledStoreList = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        CallButtonInfo c_info;
        ItemInfo i_info;
        CalledStoreData store;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.called_store_item, null);
        }

        store = calledStoreList.get(position);

        c_info = new CallButtonInfo();
        i_info = new ItemInfo();

        // storeId 처리
        i_info.setStoreId(store.getStoreId());
        c_info.setStoreId(store.getStoreId());

        // 업체 이름 처리
        TextView name = (TextView) v.findViewById(R.id.calledstore_item_name);
        name.setText(store.getStoreName());
        c_info.setName(store.getStoreName());
        i_info.setName(store.getStoreName());

        // 전화 번호 처리
        c_info.setPhone(store.getCallNumber());
        i_info.setPhone(store.getCallNumber());

        // 통화 시간 처리
        TextView callTime = (TextView) v.findViewById(R.id.calledstore_item_date);
        callTime.setText(store.getCallTime());

        // 통화 버튼 셋팅
        ImageButton callButton = (ImageButton) v.findViewById(R.id.c_callButton);
        callButton.setOnClickListener(callbutton_listener);
        callButton.setTag(c_info);

        // 아이템 레이아웃 버튼 셋팅
        RelativeLayout itemClickLayout = (RelativeLayout) v.findViewById(R.id.c_layout_for_itemClick);
        itemClickLayout.setOnClickListener(itemClick_listener);
        itemClickLayout.setTag(i_info);

        return v;
    }

    /* 전화 버튼 눌렀을 시, 커스텀 다이얼로그 띄움. (전화걸기 기능) */
    private final View.OnClickListener callbutton_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof ImageButton) {
                // 버튼에서 name, phone 정보가 담긴 태그를 가져옴
                CallButtonInfo c_info = (CallButtonInfo) v.getTag();
                if (c_info != null) {

                    // shared preference 에 통화 기록 저장은 다이얼로그 측에서!!! (혹시 다이얼로그 바꿀거면 그 부분도 설정해주어야 한다.)

                    // name, phone 띄우며 다이얼로그 창 띄우기
                    YasickDialog dialog = new YasickDialog(context);
                    dialog.setName(c_info.getName());
                    dialog.setPhone(c_info.getPhone());
                    dialog.setStoreId(c_info.getStoreId());
                    dialog.titleSetting();

                    //dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    //dialog.getWindow().setDimAmount(0.7f);
                    dialog.show();
                }
            }
        }
    };

    /* 아이템 레이아웃 클릭 시 : 해당 업체 페이지 액티비티로 넘어 감 */
    private final View.OnClickListener itemClick_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof RelativeLayout) {
                // 아이템 레이아웃에서 storeId, phone 정보가 담긴 태그를 가져온다
                ItemInfo i_info = (ItemInfo) v.getTag();
                if(i_info != null) {
                    // 업체 페이지로 던질 인텐트 선언
                    Intent intent = new Intent(context, YasickStorePage.class);
                    // 인텐트에 정보들을 구겨 넣어서
                    intent.putExtra("storeId", i_info.getStoreId());
                    intent.putExtra("storePhone", i_info.getPhone());
                    intent.putExtra("storeName", i_info.getName());
                    // 던지자!
                    context.startActivity(intent);
                }
            }
        }
    };

    /* 전화 버튼에 태그로 달 정보 */
    static class CallButtonInfo{
        private String name, phone, storeId;

        public void setName(String sName) { name = sName; }

        public String getName() { return name; }

        public void setPhone(String sPhone) { phone = sPhone; }

        public String getPhone() { return phone; }

        public void setStoreId(String sStoreId) { storeId = sStoreId; }

        public String getStoreId() { return storeId; }
    }

    /* 각 아이템 레이아웃(버튼)에 태그로 달 정보 */
    static class ItemInfo {
        private String name, storeId, phone;

        public void setName(String sName) { name = sName; }

        public String getName() { return name; }

        public void setStoreId(String sId) { storeId = sId; }

        public String getStoreId() { return storeId; }

        public void setPhone(String sPhone) { phone = sPhone; }

        public String getPhone() { return phone; }
    }
}
