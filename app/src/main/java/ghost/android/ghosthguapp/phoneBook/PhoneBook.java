package ghost.android.ghosthguapp.phoneBook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalMethods;
import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.yasick.YasickDialog;

public class PhoneBook extends Fragment {
    private EditText et_search;
    private ListView lv_phone;
    private ArrayList<PhoneBookData> al_phone;
    private PhoneBookAdapter adapter;
    private String seleted;
    private Activity thisActivity;

    public View onCreateView(LayoutInflater inflater, ViewGroup fragment_container, Bundle savedInstanceState) {
        thisActivity = getActivity();
        View rootView = inflater.inflate(R.layout.phonebook_main, fragment_container, false);

        al_phone = new ArrayList<>();

        et_search = (EditText) rootView.findViewById(R.id.phonebook_main_et);
        lv_phone = (ListView) rootView.findViewById(R.id.phonebook_main_lv);

        //인터넷 연결 확인
        AlertDialog.Builder netConDlgBuilder = GlobalMethods.checkInternet(thisActivity);

        //인터넷 연결 되면
        if (netConDlgBuilder == null) {
            //DoCheckAndSave 로 가서 파일 있으면 버전 체크 후 업데이트 or 가져오기 결정,
            //DoCheckAndSave 로 가서 파일 없으면 바로 업데이트
            new DoCheckAndSave().execute();
            //인터넷 안되면
        } else {
            //파일이 없으면 : 에러 다이얼로그 띄움
            if (!GlobalVariables.fPhone.exists()) {
                netConDlgBuilder.show().setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        thisActivity.finish();
                    }
                });
            }
            //파일 있으면 : 가져오기
            else {
                display();
            }
        }

        Spinner spinner = (Spinner) rootView.findViewById(R.id.phonebook_main_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(thisActivity, R.array.phone_category_list, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        seleted = "전체";
                        break;
                    case 1:
                        seleted = "hdh";
                        break;
                    case 2:
                        seleted = "nmh";
                        break;
                    case 3:
                        seleted = "nth";
                        break;
                    case 4:
                        seleted = "glc";
                        break;
                    case 5:
                        seleted = "su";
                        break;
                    case 6:
                        seleted = "dorm";
                        break;
                    case 7:
                        seleted = "oh";
                        break;
                    case 8:
                        seleted = "anh";
                        break;
                    case 9:
                        seleted = "etc";
                        break;
                }
                updateDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = et_search.getText().toString();
                if (str.length() < 1) {
                    //화면 업데이트
                    updateDisplay();
                } else {
                    updateDisplay(str);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        lv_phone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
                YasickDialog dialog = new YasickDialog(thisActivity);
                dialog.setName(al_phone.get(position).getName());
                dialog.setPhone(al_phone.get(position).getPhone());
                dialog.titleSetting();
                dialog.show();
            }
        });

        return rootView;
    }

    private class DoCheckAndSave extends AsyncTask<Void, Void, String> {
        private ProgressDialog doCheckAndSave;

        protected void onPreExecute() {
            super.onPreExecute();
            doCheckAndSave = ProgressDialog.show(thisActivity, "", "전화번호부를 자동 업데이트 하고 있습니다.", true);
            doCheckAndSave.setCancelable(true);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            PhoneManager phoneManager = new PhoneManager();
            if (phoneManager.checkNSave()) {
                return "success";
            } else {
                return "network error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            doCheckAndSave.dismiss();
            if (result.equals("success")) {
                // 화면 display
                display();
            } else if (result.equals("network error")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                builder.setTitle("네트워크 오류");
                builder.setMessage("네트워크 상태를 확인 해주세요.")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            doCheckAndSave.dismiss();
        }
    }

    // 화면 업데이트
    private void updateDisplay() {
        // List 청소 후 전체 List 받기
        PhoneManager phoneManager = new PhoneManager();
        phoneManager.setting();
        al_phone = phoneManager.getList(seleted);
        Collections.sort(al_phone);

        // adapter setting
        adapter = new PhoneBookAdapter(thisActivity, R.layout.phonebook_item, al_phone);
        // adapter register
        lv_phone.setAdapter(adapter);
    }

    // 화면 업데이트
    private void updateDisplay(String search) {
        // List 청소 후 전체 List 받기
        PhoneManager phoneManager = new PhoneManager();
        phoneManager.setting();
        al_phone = phoneManager.getList(search, seleted);
        Collections.sort(al_phone);
        // adapter setting
        adapter = new PhoneBookAdapter(thisActivity, R.layout.phonebook_item, al_phone);
        // adapter register
        lv_phone.setAdapter(adapter);
    }

    // 화면 display
    private void display() {
        PhoneManager phoneManager = new PhoneManager();
        phoneManager.setting();
        al_phone = phoneManager.getList();
        Collections.sort(al_phone);

        // adapter setting
        adapter = new PhoneBookAdapter(thisActivity, R.layout.phonebook_item, al_phone);
        // adapter register
        lv_phone.setAdapter(adapter);
    }
}
