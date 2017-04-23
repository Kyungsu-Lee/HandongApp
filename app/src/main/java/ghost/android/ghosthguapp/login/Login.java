package ghost.android.ghosthguapp.login;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ghost.android.ghosthguapp.R;

public class Login extends Activity {
    private EditText id;
    private EditText pw;
    private Button btn_login;
    private String sendID;
    private String sendPW;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        id = (EditText) findViewById(R.id.lo_login_et_id);
        pw = (EditText) findViewById(R.id.lo_login_et_pw);
        btn_login = (Button) findViewById(R.id.lo_login_bt_login);
        btn_login.setEnabled(false);

        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendID = id.getText().toString();
                sendPW = pw.getText().toString();
                if (sendID.isEmpty() || sendPW.isEmpty()) {
                    btn_login.setEnabled(false);
                    btn_login.setTextColor(getApplicationContext().getResources().getColorStateList(R.color.text_grey));
                } else {
                    btn_login.setEnabled(true);
                    btn_login.setTextColor(getApplicationContext().getResources().getColorStateList(R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendID = id.getText().toString();
                sendPW = pw.getText().toString();
                if (sendID.equals("") || sendPW.equals("")) {
                    btn_login.setEnabled(false);
                    btn_login.setTextColor(getApplicationContext().getResources().getColorStateList(R.color.text_grey));
                } else {
                    btn_login.setEnabled(true);
                    btn_login.setTextColor(getApplicationContext().getResources().getColorStateList(R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendID = id.getText().toString();
                sendPW = pw.getText().toString();
                String[] params = {sendID, sendPW};
                new LoginTask(Login.this).execute(params);
            }
        });
    }
}
