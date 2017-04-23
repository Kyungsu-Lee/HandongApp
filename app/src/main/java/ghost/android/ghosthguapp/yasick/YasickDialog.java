package ghost.android.ghosthguapp.yasick;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

import ghost.android.ghosthguapp.R;

/**
 * Created by Administrator on 02-02 002.
 */
public class YasickDialog extends Dialog implements View.OnClickListener {

    private String phone ="", name="", storeId="";
    public Button call, cancel;
    TextView title;
    private Context context;

    public YasickDialog(Context context) {
        super(context);
        this.context = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.customdialog_for_yasick);

        call = (Button) findViewById(R.id.btn_yasick_call);
        call.setOnClickListener(this);

        cancel = (Button) findViewById(R.id.btn_yasick_cancel);
        cancel.setOnClickListener(this);

        title = (TextView) findViewById(R.id.yasick_call_title);
    }

    public void onClick(View view){
        if(view.getId() == R.id.btn_yasick_call){
            dismiss();
            // sharedPreferences 에 통화기록 추가
            RecentCallManager callManager = new RecentCallManager(context);
            callManager.addRecentInfo(storeId, new Date());

            // 콜 카운트 증가 후, 인터넷 연결되었을 때만 콜카운트 보내기
            CallCountService.startActionCountCall(context, storeId);

            // 전화 걸기
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
            context.startActivity(intent);

            // 다이얼로그 사라짐
            dismiss();
        }
        else if(view.getId() == R.id.btn_yasick_cancel) {
            dismiss();
        }
    }

    public void setPhone(String num) {
        phone = num;
    }

    public void setName(String name) { this.name = name; }

    public void setStoreId(String storeId) { this.storeId = storeId; }

    public void changeButtonTitle(String newTitle) { call.setText(newTitle); }

    public void setTitleContents(String newTitle) { title.setText(newTitle); }

    public void titleSetting() { title.setText(name+" (으)로 전화를 거시겠습니까? ("+phone+")"); }
}