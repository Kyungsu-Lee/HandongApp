package ghost.android.ghosthguapp.notice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import ghost.android.ghosthguapp.R;

public class ClassNoticeFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener, AdapterView.OnItemClickListener {

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
    private String selectedPage = "1";
    private NoticeListAdapter adapter_articles;
    private Button page1, page2, page3, page4, page5;
    private ImageButton page_left, page_right;
    private boolean isNext = true;
    private int neighborPages = 5;
    private ListView list_articles;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup fragment_container, Bundle savedInstanceState) {

        thisActivity = getActivity();

        // Create or inflate the Fragment's UI, and return it. If this Fragment has no UI then return null
        rootView = inflater.inflate(R.layout.notice_class_fragment, fragment_container, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
        builder.setTitle("한동어플");
        builder.setMessage("서비스 준비중입니다.")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        thisActivity.finish();
                    }
                });

        /* manager 생성 */
  //      manager = new ClassNoticeManager(thisActivity.getApplicationContext(), "getCourseList.jsp");
        // 아이디, 비밀번호 체크해보고 없으면 : 다이얼로그 띄우기 -> 로그인 화면으로 이동 후 액티비티 종료
  /*      if(!manager.getNcheck()) {
            new AlertDialog.Builder(thisActivity)
                    .setTitle("수업공지")
                    .setMessage("히즈넷 로그인이 필요한 서비스입니다.")
                    .setPositiveButton("로그인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // 로그인 화면으로 이동
                            Intent intent = new Intent(thisActivity.getApplicationContext(), Login.class);
                            thisActivity.startActivity(intent);
                            thisActivity.finish();
                        }
                    })
                    .setNegativeButton("나가기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // 액티비티 나가기
                            thisActivity.finish();
                        }
                    })
                    .show();
        }*/
        // 있으면 : 학기 정보 받아오기
   /*     else {

        /* 현재 학기에 대한 정보를 받아오고 셋팅하기 */
//            new GetCurrent().execute();
 //       }
        return rootView;
    }


    /* 기본 레이아웃 셋팅 */
    private void setBasicLayout() {


        /* year spinner */
        // spinner 에 띄울 year 을 ArrayList 로 담기
        ArrayList<String> years = new ArrayList();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 1995; i--)
        years.add(Integer.toString(i));
        // ArrayList 와 Spinner 연결
        ArrayAdapter<String> adapter = new ArrayAdapter<>(thisActivity, android.R.layout.simple_spinner_dropdown_item, years);
        spinYear = (Spinner) rootView.findViewById(R.id.class_spinner_year);
        spinYear.setAdapter(adapter);
        spinYear.setOnItemSelectedListener(this);
        // 1월에는 현재 년도가 아닌 작년을 default 로 띄우기 (겨울학기는 때, 년도는 넘어갔지만 그 이전년도의 겨울학기이니까)
        int thisMonth = Calendar.getInstance().get(Calendar.MONTH);
        if (thisMonth == 0) {
            spinYear.setSelection(1);
        }

        /* semester spinner */
        // spinner 에 띄울 학기를 ArrayList 로 담기
        ArrayList<String> semesters = new ArrayList<>();
        semesters.add("1");
        semesters.add("2");
        semesters.add("summer");
        semesters.add("winter");
        // ArrayList 와 Spinner 연결
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(thisActivity, android.R.layout.simple_spinner_dropdown_item, semesters);
        spinSemester = (Spinner) rootView.findViewById(R.id.class_spinner_semester);
        spinSemester.setAdapter(adapter1);
        // 현재 학기로 셋팅되도록 설정
        spinSemester.setSelection(current_semester-1);
        spinSemester.setOnItemSelectedListener(this);

        /* subject spinner */
        // ArrayList 와 Spinner 연결
        adapter_subject = new ArrayAdapter<>(thisActivity, android.R.layout.simple_spinner_dropdown_item, subjects);
        spinSubject = (Spinner) rootView.findViewById(R.id.class_spinner_subject);
        spinSubject.setAdapter(adapter_subject);
        spinSubject.setOnItemSelectedListener(this);

        /* 공지 글 띄울 리스트뷰 셋팅 */
        articles = new ArrayList<>();
        list_articles = (ListView) rootView.findViewById(R.id.class_article_listview);
        adapter_articles = new NoticeListAdapter(thisActivity, R.layout.class_article_item , articles, "class");
        list_articles.setAdapter(adapter_articles);
        list_articles.setOnItemClickListener(this);

        /* 페이지 버튼 셋팅 */
        page1 = (Button) rootView.findViewById(R.id.class_btn_1);
        page1.setTypeface(Typeface.DEFAULT);
        page1.setOnClickListener(this);

        page2 = (Button) rootView.findViewById(R.id.class_btn_2);
        page2.setTypeface(Typeface.DEFAULT);
        page2.setOnClickListener(this);

        page3 = (Button) rootView.findViewById(R.id.class_btn_3);
        page3.setTypeface(Typeface.DEFAULT);
        page3.setOnClickListener(this);

        page4 = (Button) rootView.findViewById(R.id.class_btn_4);
        page4.setTypeface(Typeface.DEFAULT);
        page4.setOnClickListener(this);

        page5 = (Button) rootView.findViewById(R.id.class_btn_5);
        page5.setTypeface(Typeface.DEFAULT);
        page5.setOnClickListener(this);

        page_left = (ImageButton) rootView.findViewById(R.id.class_btn_left);
        page_left.setOnClickListener(this);

        page_right = (ImageButton) rootView.findViewById(R.id.class_btn_right);
        page_right.setOnClickListener(this);
    }

    /* 과목 리스트가 변화된 레이아웃 셋팅 */
    private void setSubjectsChanged() {
        // subject spinner 가 갱신된 subjects (ArrayList) 맞게 갱신되도록 손봐야 한다
        adapter_subject.notifyDataSetChanged();
        adapter_articles.notifyDataSetChanged();
        // select 되어있는 포지션도 갱신
        spinSubject.setSelection(0);

        // 페이지 버튼 모두 숨김
        page1.setVisibility(View.INVISIBLE);
        page2.setVisibility(View.INVISIBLE);
        page3.setVisibility(View.INVISIBLE);
        page4.setVisibility(View.INVISIBLE);
        page5.setVisibility(View.INVISIBLE);
        page_left.setVisibility(View.INVISIBLE);
        page_right.setVisibility(View.INVISIBLE);
    }

    /* 공지글 리스트가 변화된 레이아웃 셋팅 */
    private void setArticlesChanged() {
        // 공지글 리스트뷰가 갱신된 ArrayList 내용에 맞게 갱신되도록 설정
        adapter_articles.notifyDataSetChanged();
        list_articles.setSelection(0);

        /* 받아온 기타 정보에 맞게 페이지 버튼을 셋팅 */
        // 만약 공지글 arraylist 가 비어있는 거라면 : 무조건 다 INVISIBLE
        if(articles.size() == 0) {
            page1.setVisibility(View.INVISIBLE);
            page2.setVisibility(View.INVISIBLE);
            page3.setVisibility(View.INVISIBLE);
            page4.setVisibility(View.INVISIBLE);
            page5.setVisibility(View.INVISIBLE);
            page_left.setVisibility(View.INVISIBLE);
            page_right.setVisibility(View.INVISIBLE);
        }
        // 공지글 arraylist 가 내용을 가지고 있다면 : 조건적으로 VISIBLE 하게 설정
        else {
            // 선택된 페이지가 1-5이면 : 왼쪽 이동 버튼을 비활성화
            if (Integer.parseInt(selectedPage) <= 5)
                page_left.setVisibility(View.INVISIBLE);
            // 그 이상이면 : 왼쪽 이동 버튼을 활성화
            else
                page_left.setVisibility(View.VISIBLE);

            // 다음 페이지 그룹(페이지 5개씩) 이 존재하면 : 오른쪽 이동 버튼 활성화
            if (isNext)
                page_right.setVisibility(View.VISIBLE);
            // 존재하지 않으면 : 오른쪽 이동 버튼 비활성화
            else
                page_right.setVisibility(View.INVISIBLE);

            // 현재 페이지 그룹의 페이지 수에 맞게 버튼 셋팅
            setPagesVisibility();
        }
    }

    /* spinner 선택 이벤트 리스너 */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId())
        {
            // spinYear 에서 이벤트 발생 시: year, semester 정보를 가지고 과목 리스트 서버에 요청
            case R.id.class_spinner_year :
                if(first_year)  // initialization 때는 실행 안 하기 위해서
                    first_year = false;
                else
                    new GetSubjectList().execute();

                break;

            // spinSemester 에서 이벤트 발생 시 : year, semester 정보를 가지고 과목 리스트 서버에 요청
            case R.id.class_spinner_semester :
                if(first_semester)  // initialization 때는 실행 안 하기 위해서
                    first_semester = false;
                else
                    new GetSubjectList().execute();

                break;

            // spinSubject 에서 이벤트 발생 시 : 해당 과목에 대한 공지글 리스트 정보를 서버에 요청, 페이지 버튼 초기화
            case R.id.class_spinner_subject :
                selectedPage = "1";
                if(first_subject)   // initialization 때는 실행 안 하기 위해서
                    first_subject = false;
                else
                    new GetArticleList().execute();

                // 페이지 버튼을 1-5로 초기화
                page1.setText("1");
                page2.setText("2");
                page3.setText("3");
                page4.setText("4");
                page5.setText("5");

                // 유효한 과목이 선택되었다면 : 1페이지를 클릭한 것처럼 버튼들을 셋팅
                if(spinSubject.getSelectedItem().toString().equals("과목 선택") || spinSubject.getSelectedItem().toString().equals("현 학기 수강중인 과목이 없습니다."));
                else {
                    setPageButtonDefault();
                    page1.setTypeface(Typeface.DEFAULT_BOLD);
                    page1.setTextColor(getResources().getColor(R.color.notice_background));
                }
                break;
        }
    }

    /* 스피너에 선택된 아이템이 없어졌을 때 */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }

    /* 버튼 리스너 */
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.class_btn_1 : selectedPage = (String) page1.getText();
                setPageButtonDefault();
                // 해당 버튼 도드라지게 표시
                page1.setTypeface(Typeface.DEFAULT_BOLD);
                page1.setTextColor(getResources().getColor(R.color.notice_background));
                new GetArticleList().execute();
                break;
            case R.id.class_btn_2 : selectedPage = (String) page2.getText();
                setPageButtonDefault();
                // 해당 버튼 도드라지게 표시
                page2.setTypeface(Typeface.DEFAULT_BOLD);
                page2.setTextColor(getResources().getColor(R.color.notice_background));
                new GetArticleList().execute();
                break;
            case R.id.class_btn_3 : selectedPage = (String) page3.getText();
                setPageButtonDefault();
                // 해당 버튼 도드라지게 표시
                page3.setTypeface(Typeface.DEFAULT_BOLD);
                page3.setTextColor(getResources().getColor(R.color.notice_background));
                new GetArticleList().execute();
                break;
            case R.id.class_btn_4 : selectedPage = (String) page4.getText();
                setPageButtonDefault();
                // 해당 버튼 도드라지게 표시
                page4.setTypeface(Typeface.DEFAULT_BOLD);
                page4.setTextColor(getResources().getColor(R.color.notice_background));
                new GetArticleList().execute();
                break;
            case R.id.class_btn_5 : selectedPage = (String) page5.getText();
                setPageButtonDefault();
                // 해당 버튼 도드라지게 표시
                page5.setTypeface(Typeface.DEFAULT_BOLD);
                page5.setTextColor(getResources().getColor(R.color.notice_background));
                new GetArticleList().execute();
                break;

            case R.id.class_btn_left : pagesMoreLeft();
                break;

            case R.id.class_btn_right : pagesMoreRight();
                break;
        }
    }

    /* 공지 글 리스트뷰 클릭 */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /* 해당 공지글을 보여주는 activity 로 intent 보냄 */

        Intent intent = new Intent(thisActivity.getApplicationContext(), NoticeContents.class);
        // 서버에 요청할 때 필요한 정보를 intent 안에 구겨 넣어서
        intent.putExtra("link", (String) view.getTag());
        intent.putExtra("url", "getCourseContents.jsp");
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
            getSubjectListDialog.setCancelable(true);
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
                builder.setMessage("정보를 받아올 수 없습니다.\n네트워크 상태를 확인하신 후 다시 로그인 해주세요.\n(메인화면 > 설정 > 로그아웃)")
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
            if (manager.requestArticleList(spinYear.getSelectedItem().toString(), spinSemester.getSelectedItem().toString(), spinSubject.getSelectedItem().toString(), selectedPage)) {
                // arraylist 내용 갱신
                articles.clear();
                articles.addAll(manager.getArticles());
                // 기타 내용 갱신
                neighborPages = manager.getNeighborPages();
                isNext = manager.getIsNext();
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
                        })
                        .show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            getArticleListDialog.dismiss();
        }
    }


    /* 페이지 버튼을 모두 기본 설정 값으로 바꿔주는 메소드 */
    private void setPageButtonDefault() {

        // 볼드체 모두 해제
        page1.setTypeface(Typeface.DEFAULT);
        page2.setTypeface(Typeface.DEFAULT);
        page3.setTypeface(Typeface.DEFAULT);
        page4.setTypeface(Typeface.DEFAULT);
        page5.setTypeface(Typeface.DEFAULT);

        // 글자색 강조 모두 해제
        page1.setTextColor(getResources().getColor(R.color.page_button_default));
        page2.setTextColor(getResources().getColor(R.color.page_button_default));
        page3.setTextColor(getResources().getColor(R.color.page_button_default));
        page4.setTextColor(getResources().getColor(R.color.page_button_default));
        page5.setTextColor(getResources().getColor(R.color.page_button_default));

    }

    /* 페이지 왼쪽 이동 버튼 눌렀을 때의 동작을 정의하는 메소드 */
    private void pagesMoreLeft() {
        int future_page1 = 1;
        // 추후에 이동할 페이지 숫자로 설정
        future_page1 = Integer.parseInt((String) page1.getText()) - 5;

        // 추후에 이동할 페이지 숫자가 유효한 페이지 숫자라면 (1 이상) : 그대로 버튼을 셋팅
        if(future_page1 >= 1) {
            page1.setText("" + future_page1);
            page2.setText("" + (future_page1 + 1));
            page3.setText("" + (future_page1 + 2));
            page4.setText("" + (future_page1 + 3));
            page5.setText("" + (future_page1 + 4));

            // 새로 셋팅된 5개의 페이지 중 마지막 페이지 버튼을 설정한 것처럼 셋팅
            // 서버에 요청할 때 넘길 값을 새로 셋팅된 것 중 가장 마지막 버튼 숫자로 설정
            selectedPage = (String) page5.getText();
            // 페이지 버튼 레이아웃 초기화
            setPageButtonDefault();
            // 해당 버튼만 도드라지게
            page5.setTypeface(Typeface.DEFAULT_BOLD);
            page5.setTextColor(getResources().getColor(R.color.notice_background));

            // 서버에 요청작업 + 레이아웃 적용
            new GetArticleList().execute();

        }
        // 혹시나 그렇지 않다면 : 왼쪽 이동 버튼을 비활성화하고 Toast 메세지 띄움
        else {
            page_left.setVisibility(View.INVISIBLE);
            Toast.makeText(thisActivity.getApplicationContext(), "더 이상 이동할 페이지가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /* 페이지 오른쪽 이동 버튼 눌렀을 때의 동작을 정의하는 메소드 */
    private void pagesMoreRight() {
        int future_page1 = 1;
        // 추후에 이동할 페이지 숫자로 설정
        future_page1 = Integer.parseInt((String) page1.getText()) + 5;

        // 추후에 이동할 페이지 숫자가 유효한 페이지 숫자라면 (3000 페이지까지만 허용하는 걸로 설정) : 그대로 버튼을 셋팅
        if(future_page1 <= 2996)
        {
            page1.setText("" + future_page1);
            page2.setText("" + (future_page1 + 1));
            page3.setText("" + (future_page1 + 2));
            page4.setText("" + (future_page1 + 3));
            page5.setText("" + (future_page1 + 4));

            // 새로 셋팅된 5개의 페이지 중 첫번째 페이지 버튼을 설정한 것처럼 셋팅
            // 서버에 넘길 변수 값을 설정
            selectedPage = (String) page1.getText();
            // 페이지 버튼 레이아웃 초기환
            setPageButtonDefault();
            // 해당 버튼만 도드라지게
            page1.setTypeface(Typeface.DEFAULT_BOLD);
            page1.setTextColor(getResources().getColor(R.color.notice_background));

            // 서버에 요청작업 + 레이아웃 갱신
            new GetArticleList().execute();

        }
        // 혹시나 그렇지 않다면 : 오른쪽 이동 버튼을 비활성화하고 Toast 메세지 띄움
        else {
            page_right.setVisibility(View.INVISIBLE);
            Toast.makeText(thisActivity.getApplicationContext(), "더 이상 이동할 페이지가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /* 이웃 페이지 수에 따라 페이지 버튼의 Visibility를 적절하게 셋팅해주는 메소드 */
    private void setPagesVisibility() {
        switch(neighborPages)
        {
            case 1 :
                page1.setVisibility(View.VISIBLE);
                page2.setVisibility(View.GONE);
                page3.setVisibility(View.GONE);
                page4.setVisibility(View.GONE);
                page5.setVisibility(View.GONE);
                break;
            case 2 :
                page1.setVisibility(View.VISIBLE);
                page2.setVisibility(View.VISIBLE);
                page3.setVisibility(View.GONE);
                page4.setVisibility(View.GONE);
                page5.setVisibility(View.GONE);
                break;
            case 3 :
                page1.setVisibility(View.VISIBLE);
                page2.setVisibility(View.VISIBLE);
                page3.setVisibility(View.VISIBLE);
                page4.setVisibility(View.GONE);
                page5.setVisibility(View.GONE);
                break;
            case 4 :
                page1.setVisibility(View.VISIBLE);
                page2.setVisibility(View.VISIBLE);
                page3.setVisibility(View.VISIBLE);
                page4.setVisibility(View.VISIBLE);
                page5.setVisibility(View.GONE);
                break;
            case 5 :
                page1.setVisibility(View.VISIBLE);
                page2.setVisibility(View.VISIBLE);
                page3.setVisibility(View.VISIBLE);
                page4.setVisibility(View.VISIBLE);
                page5.setVisibility(View.VISIBLE);
                break;
        }
    }

}
