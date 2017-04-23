package ghost.android.ghosthguapp.sickdan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;

public class MomsAdapter extends ArrayAdapter<MomsData> {
    public static final int TYPE_CORNER = 0;
    public static final int TYPE_MENU = 1;
    public static final int TYPE_CORNERLINE = 2;
    private Context context;
    private ArrayList<MomsData> menuList;

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return menuList.get(position).getType();
    }

    public MomsAdapter(Context con, int id, ArrayList<MomsData> list) {
        super(con, id, list);
        context = con;
        menuList = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        final MomsData moms = menuList.get(position);
        int listViewItemType = moms.getType();

        if (listViewItemType == TYPE_CORNER) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.eachhangmok_for_sickdan_corner, null);
            TextView corner = (TextView) v.findViewById(R.id.corner);
            corner.setText(moms.getCorner());

        } else if (listViewItemType == TYPE_MENU) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.eachhangmok_for_sickdan_menu, null);
            TextView menu_name = (TextView) v.findViewById(R.id.menu_name);
            menu_name.setText(moms.getName());
            TextView menu_price = (TextView) v.findViewById(R.id.menu_price);
            menu_price.setText(moms.getPrice());

        } else if (listViewItemType == TYPE_CORNERLINE) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.eachhangmok_for_sickdan_corner_line, null);
        }

        return v;
    }
}