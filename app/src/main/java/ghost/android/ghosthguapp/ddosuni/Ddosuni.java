package ghost.android.ghosthguapp.ddosuni;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.yasick.YasickDialog;

public class Ddosuni extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ddosuni_main);

        LinearLayout call = (LinearLayout) findViewById(R.id.ll_ddosuni_call);
        LinearLayout cafe = (LinearLayout) findViewById(R.id.ll_ddosuni_cafe);

        call.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                YasickDialog dialog = new YasickDialog(Ddosuni.this);
                dialog.setName("또순이부동산");
                dialog.setPhone("010-4441-6001");
                dialog.titleSetting();
                dialog.show();
            }

        });

        cafe.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("http://cafe.daum.net/cellorecord");
                intent.setData(uri);
                startActivity(intent);
            }
        });
    }
}