package ghost.android.ghosthguapp.notice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.login.Login;

public class HwNoticeFragment extends Fragment implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private Activity thisActivity;
    private View rootView;
    private ArrayList<String> subjects;
    private ArrayList<ClassArticleData> articles;
    private int current_semester;
    private ClassNoticeManager manager;
    private Spinner spinYear, spinSemester, spinSubject;
    private ArrayAdapter<String> adapter_subject;
    private boolean first_year = true;
    private boolean first_semester = true;
    private boolean first_subject = true;
    private NoticeListAdapter adapter_articles;
    private ListView list_articles;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup fragment_container, Bundle savedInstanceState) {

        thisActivity = getActivity();

        // Create or inflate the Fragment's UI, and return it. If this Fragment has no UI then return null
        rootView = inflater.inflate(R.layout.notice_hw_fragment, fragment_container, false);

        /* manager 생성 */
        manager = new ClassNoticeManager(thisActivity.getApplicationContext(), "getAssignmentList.jsp");
        // 아이디, 비밀번호 체크해보고 없으면 : 로그인 화면으로 이동 후 액티비티 종료
        if(!manager.getNcheck()) {
            Intent intent = new Intent(thisActivity.getApplicationContext(), Login.class);
            thisActivity.startActivity(intent);
            thisActivity.finish();
        }

        /* 현재 학기에 대한 정보를 받아오고 셋팅하기 */
        new GetCurrent().execute();

        return rootView;
    }


    /* 기본 레이아웃 셋팅 */
    private void setBasicLayout() {

        /* year spinner */
        // spinner 에 띄울 year 을 ArrayList 로 담기
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 1995; i--)
            years.add(Integer.toString(i));
        // ArrayList 와 Spinner 연결
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(thisActivity, android.R.layout.simple_spinner_dropdown_item, years);
        spinYear = (Spinner) rootView.findViewById(R.id.hw_spinner_year);
        spinYear.setAdapter(adapter);
        spinYear.setOnItemSelectedListener(this);
        // 1월에는 현재 년도가 아닌 작년을 default 로 띄우기 (겨울학기는 때, 년도는 넘어갔지만 그 이전년도의 겨울학기이니까)
        int thisMonth = Calendar.getInstance().get(Calendar.MONTH);
        if (thisMonth == 0) {
            spinYear.setSelection(1);
        }

        /* semester spinner */
        // spinner 에 띄울 학기를 ArrayList 로 담기
        ArrayList<String> semesters = new ArrayList<String>();
        semesters.add("1");
        semesters.add("2");
        semesters.add("summer");
        semesters.add("winter");
        // ArrayList 와 Spinner 연결
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(thisActivity, android.R.layout.simple_spinner_dropdown_item, semesters);
        spinSemester = (Spinner) rootView.findViewById(R.id.hw_spinner_semester);
        spinSemester.setAdapter(adapter1);
        // 현재 학기로 셋팅되도록 설정
        spinSemester.setSelection(current_semester - 1);
        spinSemester.setOnItemSelectedListener(this);

        /* subject spinner */
        // ArrayList 와 Spinner 연결
        adapter_subject = new ArrayAdapter<>(thisActivity, android.R.layout.simple_spinner_dropdown_item, subjects);
        spinSubject = (Spinner) rootView.findViewById(R.id.hw_spinner_subject);
        spinSubject.setAdapter(adapter_subject);
        spinSubject.setOnItemSelectedListener(this);

        /* 공지 글 띄울 리스트뷰 셋팅 */
        articles = new ArrayList<>();
        list_articles = (ListView) rootView.findViewById(R.id.hw_article_listview);
        adapter_articles = new NoticeListAdapter(thisActivity, R.layout.hw_article_item , articles, "hw");
        list_articles.setAdapter(adapter_articles);
        list_articles.setOnItemClickListener(this);


    }

    /* 과목 리스트가 변화된 레이아웃 셋팅 */
    private void setSubjectsChanged() {
        // subject spinner 가 갱신된 subjects (ArrayList) 맞게 갱신되도록 손봐야 한다
        adapter_subject.notifyDataSetChanged();
        adapter_articles.notifyDataSetChanged();
        // select 되어있는 포지션도 갱신
        spinSubject.setSelection(0);
    }

    /* 공지글 리스트가 변화된 레이아웃 셋팅 */
    private void setArticlesChanged() {
        // 공지글 리스트뷰가 갱신된 ArrayList 내용에 맞게 갱신되도록 설정
        adapter_articles.notifyDataSetChanged();
        list_articles.setSelection(0);


    }

    /* spinner 선택 이벤트 리스너 */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId())
        {
            // spinYear 에서 이벤트 발생 시: year, semester 정보를 가지고 과목 리스트 서버에 요청
            case R.id.hw_spinner_year :
                if(first_year)  // initialization 때는 실행 안 하기 위해서
                    first_year = false;
                else
                    new GetSubjectList().execute();

                break;

            // spinSemester 에서 이벤트 발생 시 : year, semester 정보를 가지고 과목 리스트 서버에 요청
            case R.id.hw_spinner_semester :
                if(first_semester)  // initialization 때는 실행 안 하기 위해서
                    first_semester = false;
                else
                    new GetSubjectList().execute();

                break;

            // spinSubject 에서 이벤트 발생 시 : 해당 과목에 대한 공지글 리스트 정보를 서버에 요청, 페이지 버튼 초기화
            case R.id.hw_spinner_subject :
                if(first_subject)   // initialization 때는 실행 안 하기 위해서
                    first_subject = false;
                else
                    new GetArticleList().execute();

                break;
        }
    }

    /* 스피너에 선택된 아이템이 없어졌을 때 */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }

    /* 공지 글 리스트뷰 클릭 */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /* 해당 공지글을 보여주는 activity 로 intent 보냄 */

        Intent intent = new Intent(thisActivity.getApplicationContext(), NoticeContents.class);
        // 서버에 요청할 때 필요한 정보를 intent 안에 구겨 넣어서
        intent.putExtra("link", (String) view.getTag());
        intent.putExtra("url", "getAssignmentContents.jsp");
        // 던진다!
        thisActivity.startActivity(intent);

    }

    /* 선택된 학기의 과목 리스트를 받아오는 작업 */
    private class GetSubjectList extends AsyncTask<Void, Void, String> {
        private ProgressDialog getSubjectListDialog;

        /* 실행 전에 Main 스레드의 UI 작업 : Progress Dialog */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show(띄울 액티비티, 타이틀, 내용)
            getSubjectListDialog = ProgressDialog.show(thisActivity, "", "해당 학기의 과목 리스트를 불러오는 중입니다.. \n기다려주세요");
            getSubjectListDialog.setCancelable(false);
        }

        /* 다른 스레드로 백그라운드 작업 수행 */
        @Override
        protected String doInBackground(Void... arg0) {
            if (manager.requestSubjectList(spinYear.getSelectedItem().toString(), spinSemester.getSelectedItem().toString())) {
                // arraylist 내용 갱신
                subjects.clear();
                subjects.addAll(manager.getSubjectList());
                // articles 의 arraylist 내용도 지우기
                articles.clear();
                return "success";
            } else {
                return "fail";
            }
        }

        /* 백그라운드 작업 수행 후 UI 작업 : 서버에서 받아 온 리스트 띄우기 or 실패 다이얼로그 띄우기 */
        @Override
        protected void onPostExecute(String result) {
            getSubjectListDialog.dismiss();
            if (result.equals("success")) {
                // 변화된 레이아웃 수정 (과목 spinner 에 리스트 다시 뿌리기)
                setSubjectsChanged();
            } else if (result.equals("fail")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                builder.setTitle("연결 오류");
                builder.setMessage("정보를 받아올 수 없습니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                thisActivity.finish();
                            }
                        })
                        .show();
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            getSubjectListDialog.dismiss();
        }
    }

    /* 현재 학기의 정보와 과목 리스트 받아오는 작업 */
    private class GetCurrent extends AsyncTask<Void, Void, String> {
        private ProgressDialog getCurrentDialog;

        /* 실행 전에 Main 스레드의 UI 작업 : Progress Dialog */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show(띄울 액티비티, 타이틀, 내용)
            getCurrentDialog = ProgressDialog.show(thisActivity, "", "현재 학기의 정보를 불러오는 중입니다.. \n기다려주세요");
            getCurrentDialog.setCancelable(false);
        }

        /* 다른 스레드로 백그라운드 작업 수행 */
        @Override
        protected String doInBackground(Void... arg0) {
            if(manager.receiveCurrentInfo()) {
                current_semester = manager.getCurrentSemester();
                subjects = manager.getSubjectList();
                return "success";
            }
            else return "fail";
        }

        /* 백그라운드 작업 수행 후 UI 작업 : 서버에서 받아 온 정보로 레이아웃 셋팅 */
        @Override
        protected void onPostExecute(String result) {
            getCurrentDialog.dismiss();

            if (result.equals("success")) {
                // 레이아웃 셋팅
                setBasicLayout();
            }
            else if (result.equals("fail")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                builder.setTitle("연결 오류");
                builder.setMessage("정보를 받아올 수 없습니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                thisActivity.finish();
                            }
                        });
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            getCurrentDialog.dismiss();
        }
    }

    /* 선택된 과목, 페이지의 공지글 리스트를 받아오는 작업 */
    private class GetArticleList extends AsyncTask<Void, Void, String> {
        private ProgressDialog getArticleListDialog;

        /* 실행 전에 Main 스레드의 UI 작업 : Progress Dialog */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show(띄울 액티비티, 타이틀, 내용)
            getArticleListDialog = ProgressDialog.show(thisActivity, "", "공지를 불러오는 중입니다.. \n기다려주세요");
            getArticleListDialog.setCancelable(false);
        }

        /* 다른 스레드로 백그라운드 작업 수행 */
        @Override
        protected String doInBackground(Void... arg0) {
            if (manager.requestHwArticleList(spinYear.getSelectedItem().toString(), spinSemester.getSelectedItem().toString(), spinSubject.getSelectedItem().toString())) {
                // arraylist 내용 갱신
                articles.clear();
                articles.addAll(manager.getArticles());

                return "success";
            } else {
                return "fail";
            }
        }

        /* 백그라운드 작업 수행 후 UI 작업 : 서버에서 받아 온 리스트 띄우기 or 실패 다이얼로그 띄우기 */
        @Override
        protected void onPostExecute(String result) {
            getArticleListDialog.dismiss();
            if (result.equals("success")) {
                // 변화된 레이아웃 수정 (공지글 리스트뷰 내용 갱신)
                setArticlesChanged();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                builder.setTitle("연결 오류");
                builder.setMessage("정보를 받아올 수 없습니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                thisActivity.finish();
                            }
                        });
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            getArticleListDialog.dismiss();
        }
    }


}