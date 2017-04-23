package ghost.android.ghosthguapp.professor;

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

public class Professor extends Fragment {
    private EditText et_search;
    private ListView lv_professor;
    private ArrayList<ProfessorData> al_professor;
    private ProfessorAdapter adapter;
    private String seleted;
    private Activity thisActivity;

    public View onCreateView(LayoutInflater inflater, ViewGroup fragment_container, Bundle savedInstanceState) {

        thisActivity = getActivity();
        View rootView = inflater.inflate(R.layout.professor_main, fragment_container, false);

        al_professor = new ArrayList<>();

        et_search = (EditText) rootView.findViewById(R.id.professor_main_et);
        lv_professor = (ListView) rootView.findViewById(R.id.professor_main_lv);

        //인터넷 연결 확인
        AlertDialog.Builder netConDlgBuilder = GlobalMethods.checkInternet(thisActivity);

        //인터넷 연결 되면
        if (netConDlgBuilder == null ) {
            //DoCheckAndSave 로 가서 파일 있으면 버전 체크 후 업데이트 or 가져오기 결정,
            //DoCheckAndSave 로 가서 파일 없으면 바로 업데이트
            new DoCheckAndSave().execute();
        }
        //인터넷 안되면
        else{
            //파일이 없으면 : 에러 다이얼로그 띄움
            if (!GlobalVariables.fProf.exists()) {
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

        Spinner spinner = (Spinner) rootView.findViewById(R.id.professor_main_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(thisActivity, R.array.major_list, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        seleted = "전체";
                        break;
                    case 1:
                        seleted = "경영경제학부";
                        break;
                    case 2:
                        seleted = "공간환경시스템공학부";
                        break;
                    case 3:
                        seleted = "국제어문학부";
                        break;
                    case 4:
                        seleted = "글로벌리더십학부";
                        break;
                    case 5:
                        seleted = "글로벌에디슨아카데미";
                        break;
                    case 6:
                        seleted = "기계제어공학부";
                        break;
                    case 7:
                        seleted = "법학부";
                        break;
                    case 8:
                        seleted = "산업교육학부";
                        break;
                    case 9:
                        seleted = "산업정보디자인학부";
                        break;
                    case 10:
                        seleted = "상담심리사회복지학부";
                        break;
                    case 11:
                        seleted = "생명과학부";
                        break;
                    case 12:
                        seleted = "언론정보문화학부";
                        break;
                    case 13:
                        seleted = "전산전자공학부";
                        break;
                    case 14:
                        seleted = "창의융합교육원";
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

        lv_professor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {

                YasickDialog dialog = new YasickDialog(thisActivity);
                dialog.setTitleContents(al_professor.get(position).getName() + " 교수님께 전화를 거시겠습니까? (" + al_professor.get(position).getPhone() + ")");
                dialog.setPhone(al_professor.get(position).getPhone());
                dialog.show();

            }
        });

        return rootView;
    }

    private class DoCheckAndSave extends AsyncTask<Void, Void, String> {
        private ProgressDialog doCheckAndSave;

        protected void onPreExecute() {
            super.onPreExecute();
            doCheckAndSave = ProgressDialog.show(thisActivity, "", "교수님 연락처를 자동 업데이트 하고 있습니다.", true);
            doCheckAndSave.setCancelable(true);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            ProfessorManager professorManager = new ProfessorManager();
            if (professorManager.checkNSave()) {
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
        ProfessorManager professorManager = new ProfessorManager();
        professorManager.setting();
        al_professor = professorManager.getList(seleted);
        Collections.sort(al_professor);

        // adapter setting
        adapter = new ProfessorAdapter(thisActivity, R.layout.professor_item, al_professor);
        // adapter register
        lv_professor.setAdapter(adapter);
    }

    // 화면 업데이트
    private void updateDisplay(String search) {
        // List 청소 후 전체 List 받기
        ProfessorManager professorManager = new ProfessorManager();
        professorManager.setting();
        al_professor = professorManager.getList(search, seleted);
        Collections.sort(al_professor);
        // adapter setting
        adapter = new ProfessorAdapter(thisActivity, R.layout.professor_item, al_professor);
        // adapter register
        lv_professor.setAdapter(adapter);
    }

    // 화면 display
    private void display() {
        ProfessorManager professorManager = new ProfessorManager();
        professorManager.setting();
        al_professor = professorManager.getList();
        Collections.sort(al_professor);

        // adapter setting
        adapter = new ProfessorAdapter(thisActivity, R.layout.professor_item, al_professor);
        // adapter register
        lv_professor.setAdapter(adapter);
    }
}
