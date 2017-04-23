package ghost.android.ghosthguapp.bus;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import ghost.android.ghosthguapp.R;

/**
 * Created by Administrator on 02-02 002.
 */
public class CustomDialog_for_station extends Dialog implements View.OnClickListener {

    Button ok;

    public CustomDialog_for_station(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.customdialog_for_station);

        ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(this);

    }

    public void onClick(View view){
        if(view == ok){
            dismiss();
        }
    }
}
