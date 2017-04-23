
package ghost.android.ghosthguapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoadingActivity extends Activity {
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        ImageView image = (ImageView)findViewById(R.id.loading_buffering);
        Animation anim_buffering = AnimationUtils.loadAnimation(this, R.anim.loading_buffering);
        image.startAnimation(anim_buffering);
        final TextView tv = (TextView)findViewById(R.id.loading_text);
        final LinearLayout rl = (LinearLayout) findViewById(R.id.ll_loading);
        Animation anim_text = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.loading_text);
        tv.startAnimation(anim_text);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                flag = 1;
                finish();
            }
        }, 2000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(flag == 0) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_MENU:
                    return true;
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
