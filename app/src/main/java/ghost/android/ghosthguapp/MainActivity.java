package ghost.android.ghosthguapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import java.io.IOException;

import ghost.android.ghosthguapp.common.GlobalMethods;
import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.mainpage.MainFragment;
import ghost.android.ghosthguapp.timetable.TimeTable;

public class MainActivity extends FragmentActivity {
    private MainPagerAdapter adapter;
    private ViewPager vp;
    private TimeTable tt;
    private boolean homeKey = false;
    private MainFragment mf;
    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!GlobalVariables.Path.exists()) {
            GlobalVariables.Path.mkdir();
        }
        try {
            GlobalVariables.fNoMedia.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        backPressCloseHandler = new BackPressCloseHandler(this);

        tt = new TimeTable();
        mf = new MainFragment();

        vp = (ViewPager) findViewById(R.id.vp_main);
        adapter = new MainPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter);
        vp.setCurrentItem(1);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                if (GlobalMethods.getToday() == 4 && i == 0) {
                    Toast.makeText(getApplicationContext(), "\"수요일\" 수업시간 변경 안내\n  8교시 : 19:30 ~ 20:45\n  9교시 : 21:00 ~ 22:15\n10교시 : 22:30 ~ 23:45", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    public ViewPager getViewPager() {
        return this.vp;
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    @Override
    public void onUserLeaveHint() {
        homeKey = true;
    }

    @Override
    public void onResume(){
        homeKey = false;
        super.onResume();
    }

    @Override
    public void onPause() {
        if (homeKey && vp.getCurrentItem() == 0) {
            if (tt == null) {
                tt = new TimeTable();
            }
            tt.captureForWidget();
        }
        super.onPause();
    }

    public class MainPagerAdapter extends FragmentPagerAdapter {

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return tt;
                case 1:
                    return mf;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    public class BackPressCloseHandler {
        private long backKeyPressedTime = 0;
        private Toast toast;
        private Activity activity;

        public BackPressCloseHandler(Activity context) {
            this.activity = context;
        }

        public void onBackPressed() {
            if (vp.getCurrentItem() == 1) {
                if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                    backKeyPressedTime = System.currentTimeMillis();
                    showGuide();
                    return;
                }
                if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                    activity.finish();
                    toast.cancel();
                }
            } else {
                vp.setCurrentItem(1);
            }
        }

        public void showGuide() {
            toast = Toast.makeText(activity, "뒤로 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
