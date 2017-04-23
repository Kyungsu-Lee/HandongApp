package ghost.android.ghosthguapp.sickdan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;

public class HaksickAdapter extends ArrayAdapter<HaksickData> {
    public static final int TYPE_CORNER = 0;
    public static final int TYPE_MENU = 1;
    public static final int TYPE_KOTE = 2;
    public static final int TYPE_CORNERLINE = 3;
    private Context context;
    private ArrayList<HaksickData> menuList;

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        return menuList.get(position).getType();
    }

    public HaksickAdapter(Context con, int id, ArrayList<HaksickData> list) {
        super(con, id, list);
        context = con;
        menuList = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        final HaksickData haksick = menuList.get(position);
        int listViewItemType = haksick.getType();

        if (listViewItemType == TYPE_CORNER) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.eachhangmok_for_sickdan_corner, null);
            TextView corner = (TextView) v.findViewById(R.id.corner);
            corner.setText(haksick.getCorner());

        } else if (listViewItemType == TYPE_MENU) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.eachhangmok_for_sickdan_menu, null);
            TextView menu_name = (TextView) v.findViewById(R.id.menu_name);
            menu_name.setText(haksick.getMenu());
            TextView menu_price = (TextView) v.findViewById(R.id.menu_price);
            menu_price.setText(haksick.getPrice());
        }

        else if(listViewItemType == TYPE_KOTE){
            v = LayoutInflater.from(getContext()).inflate(R.layout.eachhangmok_for_kote, null);
            TextView when = (TextView) v.findViewById(R.id.when);
            when.setText(haksick.getCorner());
        }

        else if(listViewItemType == TYPE_CORNERLINE){
            v = LayoutInflater.from(getContext()).inflate(R.layout.eachhangmok_for_sickdan_corner_line, null);
        }
        return v;
    }
}
