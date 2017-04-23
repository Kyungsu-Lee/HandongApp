package ghost.android.ghosthguapp.convenient;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ghost.android.ghosthguapp.GhostActionBarActivity;
import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.phoneBook.PhoneBook;
import ghost.android.ghosthguapp.professor.Professor;
import ghost.android.ghosthguapp.runInfo.RunInfo;

public class Convenient extends GhostActionBarActivity {
    private FragmentManager fm;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.convenient_main);

        final Button btn_run = (Button) findViewById(R.id.btn_run);
        final Button btn_phone = (Button) findViewById(R.id.btn_phone);
        final Button btn_prof = (Button) findViewById(R.id.btn_prof);
        btn_run.setSelected(true);
        fm = getFragmentManager();
        // 새로운 fragment transaciton 시작
        FragmentTransaction ft = fm.beginTransaction();
        // fragment 를 transaction 에 add
        ft.add(R.id.fragment_container, new RunInfo());
        // transaction 을 UI 큐에 추가한다
        ft.commit();

        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_container, new RunInfo());
                ft.commit();
                btn_run.setSelected(true);
                btn_phone.setSelected(false);
                btn_prof.setSelected(false);
            }
        });

        btn_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_container, new PhoneBook());
                ft.commit();
                btn_run.setSelected(false);
                btn_phone.setSelected(true);
                btn_prof.setSelected(false);

            }
        });

        btn_prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_container, new Professor());
                ft.commit();
                btn_run.setSelected(false);
                btn_phone.setSelected(false);
                btn_prof.setSelected(true);
            }
        });
    }
}