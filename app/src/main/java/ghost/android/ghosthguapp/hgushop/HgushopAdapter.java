package ghost.android.ghosthguapp.hgushop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;

/**
 * Created by SEC on 2015-01-29.
 */
public class HgushopAdapter extends ArrayAdapter<HgushopData> {

    private Context context;
    ArrayList<HgushopData> shopList;

    public HgushopAdapter(Context context, int resource, ArrayList<HgushopData> objects) {
        super(context, resource, objects);

        this.context = context;
        this.shopList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.hgushop_shops_item, null);
        }

        HgushopData shop = shopList.get(position);

        TextView name = (TextView) v.findViewById(R.id.hgushop_row_name);
        name.setText(shop.getShopName());

        TextView contents = (TextView) v.findViewById(R.id.hgushop_row_contents);
        contents.setText(shop.getContents());

        return v;
    }



}
