package ghost.android.ghosthguapp.sickdan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;

public class HyoamAdapter extends ArrayAdapter<HyoamData> {
    public static final int TYPE_CORNER = 0;
    public static final int TYPE_MENU = 1;
    public static final int TYPE_CORNERLINE = 2;
    private Context context;
    private ArrayList<HyoamData> menuList;

    public HyoamAdapter(Context con, int id, ArrayList<HyoamData> list) {
        super(con, id, list);
        context = con;
        menuList = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final HyoamData hyoam = menuList.get(position);
        int listViewItemType = hyoam.getType();

        if (listViewItemType == TYPE_CORNER) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.eachhangmok_for_sickdan_corner, null);
            TextView corner = (TextView) v.findViewById(R.id.corner);
            corner.setText(hyoam.getCorner());

        } else if (listViewItemType == TYPE_MENU) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.eachhangmok_for_sickdan_menu, null);
            TextView menu_name = (TextView) v.findViewById(R.id.menu_name);
            menu_name.setText(hyoam.getName());
            TextView menu_price = (TextView) v.findViewById(R.id.menu_price);
            menu_price.setText(hyoam.getPrice());

            if(hyoam.getName().equals("")){
                v.setVisibility(View.GONE);
            }
        }
            else if (listViewItemType == TYPE_CORNERLINE) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.eachhangmok_for_sickdan_corner_line, null);
        }
        return v;
    }
}
