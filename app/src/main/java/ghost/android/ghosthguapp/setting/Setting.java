package ghost.android.ghosthguapp.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.login.Login;

public class Setting extends Activity {
    private RelativeLayout login;
    private TextView tv_login;
    private TextView version;
    private ImageView update;
    private RelativeLayout reset;
    private RelativeLayout feedback;

    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("Login", Context.MODE_PRIVATE);
        if (prefs.getString("id", "").equals("") || prefs.getString("pw", "").equals("")) {
            tv_login.setText("로그인");
        } else {
            tv_login.setText("로그아웃");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        login = (RelativeLayout) findViewById(R.id.setting_rl_login);
        tv_login = (TextView) findViewById(R.id.setting_tv_login);
        version = (TextView) findViewById(R.id.setting_tv_version);
        update = (ImageView) findViewById(R.id.setting_iv_update);
        reset = (RelativeLayout) findViewById(R.id.setting_rl_reset);
        feedback = (RelativeLayout) findViewById(R.id.setting_rl_feedback);

        SharedPreferences prefs = getSharedPreferences("Login", Context.MODE_PRIVATE);
        if (prefs.getString("id", "").equals("") || prefs.getString("pw", "").equals("")) {
            tv_login.setText("로그인");
        } else {
            tv_login.setText("로그아웃");
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                marketLaunch.setData(Uri.parse("market://details?id=ghost.android.ghosthguapp"));
                startActivity(marketLaunch);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);
                if (prefs.getString("id", "").equals("") || prefs.getString("pw", "").equals("")) {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                } else {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("로그아웃")
                            .setMessage("정말 로그아웃 하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //로그인 정보 삭제
                                    delLoginInfo();
                                    Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                                    tv_login.setText("로그인");
                                }
                            }).setNegativeButton("취소", null).show();
                }
            }
        });

        try {
            version.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("데이터가 초기화됩니다.")
                        .setMessage("확인을 누르시면 로그인 정보,\n시간표 등 모든 정보가 초기화됩니다.\n계속 하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //로그인 정보 삭제
                                        delLoginInfo();
                                        //텍스트 크기 정보 삭제
                                        delTtInfo();
                                        //기타 정보 삭제
                                        delOtherInfo();
                                        //모든 파일 삭제
                                        delDirectory();
                                        tv_login.setText("로그인");
                                        Toast.makeText(getApplicationContext(), "데이터 초기화 완료", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        ).setNegativeButton("취소", null).show();
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "handongapp@gmail.com", null));
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("<사용자 정보>\n");
                    stringBuilder.append("한동어플 버전 : ").append(getPackageManager().getPackageInfo(getPackageName(), 0).versionName).append("\n");
                    stringBuilder.append("사용자 폰 기종 : ").append(Build.MODEL).append("\n");
                    stringBuilder.append("시스템 버전 : ").append(Build.VERSION.RELEASE).append("\n\n");
                    i.putExtra(Intent.EXTRA_SUBJECT, "한동어플 문의사항");
                    i.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
                    startActivity(Intent.createChooser(i, "handongapp@gmail.com"));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        if (GlobalVariables.oldVersion) {
            update.setVisibility(View.VISIBLE);
        }else{
            update.setVisibility(View.GONE);
        }
    }

    public void delLoginInfo() {
        SharedPreferences prefsLogin = getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor editorLogin = prefsLogin.edit();
        editorLogin.clear();

        editorLogin.commit();


        SharedPreferences prefs = getSharedPreferences("info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public void delTtInfo(){
        SharedPreferences prefsTt = getSharedPreferences("TimeTable", MODE_PRIVATE);
        SharedPreferences.Editor editorTt = prefsTt.edit();
        editorTt.clear();
        editorTt.commit();
    }

    public void delOtherInfo(){
        SharedPreferences prefs = getSharedPreferences("Notice", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public void delDirectory() {
        File file = GlobalVariables.Path;
        if (file.exists()) {
            File[] childFileList = file.listFiles();
            for (File childFile : childFileList) {
                if(!childFile.getName().equals(".nomedia")) {
                    //하위 파일삭제
                    childFile.delete();
                }
            }
        }
    }
}






