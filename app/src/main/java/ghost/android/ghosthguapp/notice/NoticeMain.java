package ghost.android.ghosthguapp.notice;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ghost.android.ghosthguapp.GhostActionBarActivity;
import ghost.android.ghosthguapp.R;

public class NoticeMain extends GhostActionBarActivity implements View.OnClickListener {

    private FragmentManager fm;
    Button btnGeneral;
    Button btnClass;
    Button btnMaterial;
    Button btnHomework;

    private Context This;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_main_page);



        /* 기본 레이아웃 셋팅 */
        setBasicLayout();
    }

    /* 액션바, 탭 버튼, 첫 화면 등 기본 레이아웃 셋팅 */
    private void setBasicLayout() {
        //set actionbar title for yasick activity
        if(ghostActionBarTitle != null){
            ghostActionBarTitle.setText(R.string.title_notice_main);
        }

        // set actionbar logo for yasick activity
        if(ghostActionBarLogo != null){
            ghostActionBarLogo.setImageResource(R.drawable.notice_logo);
        }


        /* 버튼 설정 */
        btnGeneral = (Button) findViewById(R.id.tab_notice_general);
        btnClass = (Button) findViewById(R.id.tab_notice_class);
        btnMaterial = (Button) findViewById(R.id.tab_notice_material);
        btnHomework = (Button) findViewById(R.id.tab_notice_hw);

        btnGeneral.setOnClickListener(this);
        btnClass.setOnClickListener(this);
        btnMaterial.setOnClickListener(this);
        btnHomework.setOnClickListener(this);


        /* 처음으로 띄울 화면(fragment)를 inflate */
        fm = getFragmentManager();
        // 새로운 fragment transaciton 시작
        FragmentTransaction ft = fm.beginTransaction();
        // fragment 를 transaction 에 add
        ft.add(R.id.fragment_container_notice, new ClassNoticeFragment());
        // transaction 을 UI 큐에 추가한다
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.tab_notice_general :

                // 현재 탭버튼만 선택되어보이게 배경 설정
                btnGeneral.setBackgroundResource(R.drawable.notice_selected_border);
                btnClass.setBackgroundResource(R.drawable.listview_selector_yasick);
                btnMaterial.setBackgroundResource(R.drawable.listview_selector_yasick);
                btnHomework.setBackgroundResource(R.drawable.listview_selector_yasick);

                //현재 탭에 대한 화면(fragment) 실행
                FragmentTransaction ft1 = fm.beginTransaction();
                ft1.replace(R.id.fragment_container_notice, new GeneralNoticeFragment());
                ft1.commit();

                break;
            case R.id.tab_notice_class :

                // 현재 탭버튼만 선택되어보이게 배경 설정
                btnGeneral.setBackgroundResource(R.drawable.listview_selector_yasick);
                btnClass.setBackgroundResource(R.drawable.notice_selected_border);
                btnMaterial.setBackgroundResource(R.drawable.listview_selector_yasick);
                btnHomework.setBackgroundResource(R.drawable.listview_selector_yasick);

                //현재 탭에 대한 화면(fragment) 실행
                FragmentTransaction ft2 = fm.beginTransaction();
                ft2.replace(R.id.fragment_container_notice, new ClassNoticeFragment());
                ft2.commit();

                break;
            case R.id.tab_notice_material :

                // 현재 탭버튼만 선택되어보이게 배경 설정
                btnGeneral.setBackgroundResource(R.drawable.listview_selector_yasick);
                btnClass.setBackgroundResource(R.drawable.listview_selector_yasick);
                btnMaterial.setBackgroundResource(R.drawable.notice_selected_border);
                btnHomework.setBackgroundResource(R.drawable.listview_selector_yasick);

                //현재 탭에 대한 화면(fragment) 실행
                FragmentTransaction ft3 = fm.beginTransaction();
                ft3.replace(R.id.fragment_container_notice, new MaterialNoticeFragment());
                ft3.commit();

                break;
            case R.id.tab_notice_hw :

                // 현재 탭버튼만 선택되어보이게 배경 설정
                btnGeneral.setBackgroundResource(R.drawable.listview_selector_yasick);
                btnClass.setBackgroundResource(R.drawable.listview_selector_yasick);
                btnMaterial.setBackgroundResource(R.drawable.listview_selector_yasick);
                btnHomework.setBackgroundResource(R.drawable.notice_selected_border);

                //현재 탭에 대한 화면(fragment) 실행
                FragmentTransaction ft4 = fm.beginTransaction();
                ft4.replace(R.id.fragment_container_notice, new HwNoticeFragment());
                ft4.commit();

                break;
        }

    }
}
