package ghost.android.ghosthguapp.professor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;

public class ProfessorAdapter extends ArrayAdapter<ProfessorData> {
    private ArrayList<ProfessorData> professorList;
    private Context context;

    public ProfessorAdapter(Context con, int id, ArrayList<ProfessorData> list) {
        super(con, id, list);
        context = con;
        professorList = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.professor_item, null);
        }

        ProfessorData professor = professorList.get(position);

        TextView name = (TextView) v.findViewById(R.id.professor_item_name);
        name.setText(professor.getName());
        TextView phone = (TextView) v.findViewById(R.id.professor_item_phone);
        phone.setText(professor.getPhone());
        TextView office = (TextView) v.findViewById(R.id.professor_item_office);
        office.setText(professor.getOffice());
        TextView email = (TextView) v.findViewById(R.id.professor_item_email);
        email.setText(professor.getEmail());
        TextView major = (TextView) v.findViewById(R.id.professor_item_major);

        String str = professor.getMajor();
        if (str.equals("경영경제학부")) {
            major.setText("경경");
        } else if (str.equals("공간환경시스템공학부")) {
            major.setText("공시");
        } else if (str.equals("국제어문학부")) {
            major.setText("국제");
        } else if (str.equals("글로벌리더십학부")) {
            major.setText("GLS");
        } else if (str.equals("글로벌에디슨아카데미")) {
            major.setText("GEA");
        } else if (str.equals("기계제어공학부")) {
            major.setText("기계");
        } else if (str.equals("법학부")) {
            major.setText("법");
        } else if (str.equals("산업교육학부")) {
            major.setText("산업");
        } else if (str.equals("산업정보디자인학부")) {
            major.setText("산디");
        } else if (str.equals("상담심리사회복지학부")) {
            major.setText("상사");
        } else if (str.equals("생명과학부")) {
            major.setText("생명");
        } else if (str.equals("언론정보문화학부")) {
            major.setText("언정");
        } else if (str.equals("전산전자공학부")) {
            major.setText("전전");
        } else if (str.equals("창의융합교육원")) {
            major.setText("ICT");
        }

        return v;
    }
}
