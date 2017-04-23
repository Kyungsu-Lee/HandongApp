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
 * Created by SEC on 2015-01-01.
 */
public class YasickStoreAdapter extends ArrayAdapter<YasickStoreData> {
    private ArrayList<YasickStoreData> yasickStoreList;
    private RecentCallManager callManager;
    private Context context;

    public YasickStoreAdapter(Context con, int id, ArrayList<YasickStoreData> list){
        super(con, id, list);
        context = con;
        yasickStoreList = list;
        callManager = new RecentCallManager(con);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        CallButtonInfo c_info;
        ItemInfo i_info;
        YasickStoreData yasickStore;

        if(v == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.yasick_store_item, null);
        }

        yasickStore = yasickStoreList.get(position);

        c_info = new CallButtonInfo();
        i_info = new ItemInfo();

        i_info.setStoreId(yasickStore.getStoreId());
        c_info.setStoreId(yasickStore.getStoreId());

        TextView name = (TextView) v.findViewById(R.id.yasickstore_item_name);
        name.setText(yasickStore.getName());
        c_info.setName(yasickStore.getName());
        i_info.setName(yasickStore.getName());

        TextView phone = (TextView) v.findViewById(R.id.yasickstore_item_phone);
        phone.setText(yasickStore.getPhone());
        c_info.setPhone(yasickStore.getPhone());
        i_info.setPhone(yasickStore.getPhone());

        TextView runTime = (TextView) v.findViewById(R.id.yasickstore_item_runtime);
        runTime.setText(yasickStore.getRunTime());

        ImageButton callButton = (ImageButton) v.findViewById(R.id.callButton);
        callButton.setOnClickListener(callbutton_listener);
        callButton.setTag(c_info);

        RelativeLayout itemClickLayout = (RelativeLayout) v.findViewById(R.id.layout_for_itemClick);
        itemClickLayout.setOnClickListener(itemClick_listener);
        itemClickLayout.setTag(i_info);

        return v;
    }

    /* 전화 버튼 눌렀을 시, 커스텀 다이얼로그 띄움. (전화걸기 기능) */
    private final View.OnClickListener callbutton_listener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if( v instanceof ImageButton) {
                // 버튼에서 name, phone 정보가 담긴 태그를 가져옴
                CallButtonInfo c_info = (CallButtonInfo) v.getTag();
                if(c_info != null){

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
    private final View.OnClickListener itemClick_listener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if( v instanceof RelativeLayout ) {
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
