package ghost.android.ghosthguapp.bus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;

import ghost.android.ghosthguapp.R;

public class Bus_alarm_page extends Activity {
    MediaPlayer mMediaPlayer;
    ImageButton turnoff;
    TextView now_time, select_time, before_minute;
    String KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_alarm_page_layout);
        Intent intent = getIntent();
        String time = intent.getStringExtra("time");
        String before = intent.getStringExtra("before");
        KEY = intent.getStringExtra("prefKEY");
        Calendar c = Calendar.getInstance();
        String now_hour = String.valueOf(c.get(Calendar.HOUR));
        int now_minint = c.get(Calendar.MINUTE);
        String now_min;
        if(now_minint < 10)
            now_min = "0" + String.valueOf(now_minint);
        else
            now_min = String.valueOf(now_minint);

        now_time = (TextView) findViewById(R.id.now_time);
        now_time.setText(now_hour + ":" + now_min);
        select_time = (TextView) findViewById(R.id.select_time);
        select_time.setText("선택시간 " + time.substring(0, 2) + "시" + time.substring(3, 5) + "분");
        before_minute = (TextView) findViewById(R.id.before_minute);
        before_minute.setText(before + "분전");

        turnoff = (ImageButton) findViewById(R.id.turnoff);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        turnoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnoff.setBackgroundResource(R.drawable.bus_checking);
                removePreferences(KEY);
                stopMusic();
                finish();
            }
        });
        playMusic();
    }

    private void playMusic() {
        stopMusic();  // 플레이 할 때 가장 먼저 음악 중지 실행
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);  // 기본 벨소리(알람)의 URI
        mMediaPlayer = new MediaPlayer(); // 1. MediaPlayer 객체 생성

        try {
            mMediaPlayer.setDataSource(this, alert);  // 2. 데이터 소스 설정 (인터넷에 있는 음악 파일도 가능함)
            startAlarm(mMediaPlayer);
        } catch (Exception ex) {
            try {
                mMediaPlayer.reset();    // MediaPlayer의 Error 상태 초기화
                mMediaPlayer.setDataSource(this, alert); // fallbackring.ogg 사용	D+	D0	F	P
                startAlarm(mMediaPlayer);
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
    }

    private void startAlarm(MediaPlayer player) throws java.io.IOException, IllegalArgumentException, IllegalStateException {
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {   // 현재 Alarm 볼륨 구함
            player.setAudioStreamType(AudioManager.STREAM_ALARM);    // Alarm 볼륨 설정
            player.prepare();
            player.start();    // 4. 재생 시작
        }
    }

    public void stopMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();     // 5. 재생 중지
            mMediaPlayer.release();    // 6. MediaPlayer 리소스 해제
            mMediaPlayer = null;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        stopMusic();    // 음악 중지.
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                stopMusic();
                finish();
            default:
                return false;
        }
    }

    // 값(Key Data) 삭제하기
    public void removePreferences(String KEY) {
        SharedPreferences pref = getSharedPreferences("bus_alarm", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(KEY);
        editor.commit();
    }
}
