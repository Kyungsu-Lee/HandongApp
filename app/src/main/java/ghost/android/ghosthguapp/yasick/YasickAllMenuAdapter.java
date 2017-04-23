package ghost.android.ghosthguapp.yasick;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;

/**
 * Created by SEC on 2015-01-08.
 */
public class YasickAllMenuAdapter extends ArrayAdapter<AllMenuItem> {

    private ArrayList<AllMenuItem> allMenuItems;
    private Context context;
    String storeId;
    String SDPath = "" + Environment.getExternalStorageDirectory();

    public YasickAllMenuAdapter(Context con, int id, ArrayList<AllMenuItem> list, String storeId) {
        super(con, id, list);
        allMenuItems = list;
        context = con;
        this.storeId = storeId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        AllMenuItem menu;


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.yasick_allmenu_item, null);


        menu = allMenuItems.get(position);

        /* 아이템의 내용들을 allMenuItems.get(position) 의 값들로 설정하기 */

        // 메뉴 이름 설정
        TextView menuName = (TextView) v.findViewById(R.id.allmenu_name);
        if(menu.getMenuName() == null || menu.getMenuName().equals("") || menu.getMenuName().equals("null") || menu.getMenuName().equals("-"))
            menuName.setVisibility(View.INVISIBLE);
        else
            menuName.setText(menu.getMenuName());

        // 메뉴 가격 설정
        TextView menuPrice = (TextView) v.findViewById(R.id.allmenu_price);
        if(menu.getPrice() == null || menu.getPrice().equals("") || menu.getPrice().equals("null") || menu.getPrice().equals("-"))
            menuPrice.setVisibility(View.INVISIBLE);    // 가격 정보 없으면 텍스트뷰 INVISIBLE
        else
            menuPrice.setText(menu.getPrice());         // 가격 정보 있으면

        // 세트 구성 정보 설정
        TextView setInfo = (TextView) v.findViewById(R.id.allmenu_setinfo);
        if(menu.getSetInfo() == null || menu.getSetInfo().equals("") || menu.getSetInfo().equals("null") || menu.getSetInfo().equals("-"))   // 세트 정보 없으면 텍스트뷰 INVISIBLE
            setInfo.setVisibility(View.GONE);
        else                                            // 세트 정보 있으면 표시하기
            setInfo.setText(menu.getSetInfo());

        return v;
    }
}
