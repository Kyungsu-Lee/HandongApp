package ghost.android.ghosthguapp.phoneBook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;

public class PhoneBookAdapter extends ArrayAdapter<PhoneBookData> {
    private ArrayList<PhoneBookData> phoneList;
    private Context context;

    public PhoneBookAdapter(Context con, int id, ArrayList<PhoneBookData> list) {
        super(con, id, list);
        context = con;
        phoneList = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.phonebook_item, null);
        }

        PhoneBookData phoneData = phoneList.get(position);

        TextView name = (TextView) v.findViewById(R.id.phonebook_item_name);
        name.setText(phoneData.getName());
        TextView phone = (TextView) v.findViewById(R.id.phonebook_item_phone);
        phone.setText(phoneData.getPhone());
        TextView category = (TextView) v.findViewById(R.id.phonebook_item_category);

        String str = phoneData.getCategory();
        if (str.equals("hdh")) {
            category.setText("현동홀");
        } else if (str.equals("nth")) {
            category.setText("뉴턴홀");
        } else if (str.equals("nmh")) {
            category.setText("느헤미야홀");
        } else if (str.equals("glc")) {
            category.setText("언어교육원");
        } else if (str.equals("su")) {
            category.setText("학관");
        } else if (str.equals("anh")) {
            category.setText("올네이션스홀");
        } else if (str.equals("oh")) {
            category.setText("오석관");
        } else if (str.equals("dorm")) {
            category.setText("기숙사");
        } else {
            category.setText("기타");
        }

        return v;
    }
}
