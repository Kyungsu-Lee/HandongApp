package ghost.android.ghosthguapp.alarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import ghost.android.ghosthguapp.R;

public class AlarmPage extends Activity implements View.OnClickListener {   //알람이 울렸을 때 표시되는 페이지.
    MediaPlayer mMediaPlayer;   //알롬 소리 재생을 위해 필요함
    Vibrator tVibrator; //알람시 진동을 위해 필요
    ImageButton gotonfc;
    ImageButton turnoff;
    TextView alarmtext;
    Intent intenter;
    long[] vibratePattern = {100, 100, 300};    //진동의 패턴을 설정한다

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarmpage_layout);
        SharedPreferences prefs = getSharedPreferences("Alarmpref", MODE_PRIVATE);  //몇분전인지를 표시하기 위해 sharedpreference 파일을 불러온다.
        tVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Intent intent = getIntent();
        String alarm_name = intent.getStringExtra("alarm_name");    //어떤 알람인지 구분을 위해 가져옴. Alarm.java -> AlarmReceiver를 거쳐서 온다.
        turnoff = (ImageButton) findViewById(R.id.turnoff);
        alarmtext = (TextView) findViewById(R.id.alarmtext);
        gotonfc = (ImageButton) findViewById(R.id.gotonfc);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED   //알람이 울릴 시에 ScreenOn 해주는 것과 관련이 있음.
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        switch (alarm_name) {   //알람 이름에 따른 페이지 내용, 심야와 외박 같은 경우에는 한동NFC 어플로 연결시켜주는 이미지버튼을 활성화시킴.
            case "owebak":
                alarmtext.setText("외박신청 마감 " + prefs.getInt("owebakminpref", 10) + "분전입니다.\n한동NFC어플로 이동하시려면 아래의 버튼을 눌러주세요.");
                alarmtext.setGravity(Gravity.CENTER);
                gotonfc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intenter = getPackageManager().getLaunchIntentForPackage("edu.handong.smartcampus.launcher");
                        try {
                            startActivity(intenter);
                            finish();
                            tVibrator.cancel();
                        } catch (NullPointerException exex) {
                            exex.printStackTrace();
                        }
                        tVibrator.cancel();
                    }
                });
                break;
            case "maejeom":
                gotonfc.setVisibility(View.GONE);
                gotonfc.setClickable(false);
                alarmtext.setText("매점 영업종료 " + prefs.getInt("maejeomminpref", 20) + "분전입니다.");
                alarmtext.setGravity(Gravity.CENTER);
                break;
            case "sundayworshipmorn":
                gotonfc.setVisibility(View.GONE);
                gotonfc.setClickable(false);
                alarmtext.setText("주일 오전 예배 시작 " + prefs.getInt("sundayworshipmornminpref", 10) + "분전입니다.");
                alarmtext.setGravity(Gravity.CENTER);
                break;
            case "sundayworship":
                gotonfc.setVisibility(View.GONE);
                gotonfc.setClickable(false);
                alarmtext.setText("주일 저녁 예배 시작 " + prefs.getInt("sundayworshipminpref", 10) + "분전입니다.");
                alarmtext.setGravity(Gravity.CENTER);
                break;
            case "firsttime1":
                gotonfc.setVisibility(View.GONE);
                gotonfc.setClickable(false);
                alarmtext.setText("한동인의 첫시간 1부 시작 " + prefs.getInt("oseokminpref", 10) + "분전입니다.");
                alarmtext.setGravity(Gravity.CENTER);
                break;
            case "firsttime":
                gotonfc.setVisibility(View.GONE);
                gotonfc.setClickable(false);
                alarmtext.setText("한동인의 첫시간 2부 시작 " + prefs.getInt("firsttimeminpref", 10) + "분전입니다.");
                alarmtext.setGravity(Gravity.CENTER);
                break;
            case "endtime":
                gotonfc.setVisibility(View.GONE);
                gotonfc.setClickable(false);
                alarmtext.setText("한동인의 끝시간 시작 " + prefs.getInt("endtimeminpref", 10) + "분전입니다.");
                alarmtext.setGravity(Gravity.CENTER);
                break;
            case "riverworship":
                gotonfc.setVisibility(View.GONE);
                gotonfc.setClickable(false);
                alarmtext.setText("강물예배 시작 " + prefs.getInt("riverworshipminpref", 10) + "분전입니다.");
                alarmtext.setGravity(Gravity.CENTER);
                break;
        }
        if (prefs.getBoolean("ovibe",false))
            playVibe();
        else
            playMusic();
        turnoff.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        SharedPreferences prefs = getSharedPreferences("Alarmpref", MODE_PRIVATE);  //몇분전인지를 표시하기 위해 sharedpreference 파일을 불러온다.
        if (prefs.getBoolean("ovibe",false )) {
            stopVibe();
        } else
            stopMusic();
        finish();
    }

    private void playMusic() {
        stopMusic();  // 플레이 할 때 가장 먼저 음악 중지 실행
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);  // 기본 벨소리(알람)의 URI
        mMediaPlayer = new MediaPlayer(); // 1. MediaPlayer 객체 생성
        try {
            mMediaPlayer.setDataSource(this, alert);  // 2. 데이터 소스 설정 (인터넷에 있는 음악 파일도 가능함)
            startAlarm(mMediaPlayer);
            tVibrator.vibrate(300);
            tVibrator.vibrate(vibratePattern, 0);
        } catch (Exception ex) {
            try {
                mMediaPlayer.reset();    // MediaPlayer의 Error 상태 초기화
                mMediaPlayer.setDataSource(this, alert);
                startAlarm(mMediaPlayer);
                tVibrator.vibrate(300);
                tVibrator.vibrate(vibratePattern, 0);
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
    }

    private void playVibe() {
        try {
            tVibrator.vibrate(300);
            tVibrator.vibrate(vibratePattern, 0);
        } catch (Exception ex) {
            try {
                tVibrator.vibrate(300);
                tVibrator.vibrate(vibratePattern, 0);
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
    }

    private void startAlarm(MediaPlayer player) throws java.io.IOException, IllegalArgumentException, IllegalStateException {
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {   // 현재 Alarm 볼륨 구함
            player.setAudioStreamType(AudioManager.STREAM_ALARM);    // Alarm 볼륨 설정
            player.setLooping(true);
            player.prepare();
            player.start();    // 4. 재생 시작
        }
    }

    public void stopMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(false);
            mMediaPlayer.stop();     // 5. 재생 중지
            mMediaPlayer.release();    // 6. MediaPlayer 리소스 해제
            tVibrator.cancel();
            mMediaPlayer = null;
        }
    }

    public void stopVibe() {
        tVibrator.cancel();
    }

    public void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs = getSharedPreferences("Alarmpref", MODE_PRIVATE);
        if(prefs.getBoolean("ovibe", false))
            stopVibe();
        else
            stopMusic();    // 음악 중지.
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { //뒤로가기 버튼을 눌렀을 때의 활동을 설정함. 여기서는 AlarmPage activity를 종료함.
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                stopMusic();
                finish();
            default:
                return false;
        }
    }
}