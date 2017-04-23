package ghost.android.ghosthguapp.bus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;

public class BusAdapter_for_heunghae extends ArrayAdapter<BusData_for_heunghae> {

    public static final int TYPE_TZONE = 0;
    public static final int TYPE_BUS = 1;
    private Context context;
    private ArrayList<BusData_for_heunghae> busList;

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return busList.get(position).getType();
    }

    public BusAdapter_for_heunghae(Context con, int id, ArrayList<BusData_for_heunghae> list) {
        super(con, id, list);
        context = con;
        busList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        BusData_for_heunghae bus = busList.get(position);
        int listViewItemType = bus.getType();

        if (listViewItemType == TYPE_TZONE) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.eachhangmok_for_heunghae_tzone, null);
            TextView tzone = (TextView) v.findViewById(R.id.tzone);
            tzone.setText(bus.getTzone());
            TextView timesplit = (TextView) v.findViewById(R.id.timesplit);
            timesplit.setText(bus.getTimesplit());
            TextView times = (TextView) v.findViewById(R.id.times);
            times.setText(bus.getTimes());

        } else if (listViewItemType == TYPE_BUS) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.eachhangmok_for_heunghae, null);
            TextView rotary = (TextView) v.findViewById(R.id.rotary);
            rotary.setText(bus.getRotary());
            TextView hgu = (TextView) v.findViewById(R.id.hgu);
            hgu.setText(bus.getHgu());
            TextView gokgang = (TextView) v.findViewById(R.id.gokgang);
            gokgang.setText(bus.getGokgang());
            TextView heunghae = (TextView) v.findViewById(R.id.heunghae);
            heunghae.setText(bus.getHeunghae());
        }
        return v;
    }
}
