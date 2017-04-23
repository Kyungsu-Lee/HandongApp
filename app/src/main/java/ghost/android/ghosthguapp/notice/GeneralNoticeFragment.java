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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.login.Login;

/**
 * Created by SEC on 2015-01-13.
 */
public class GeneralNoticeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private View rootView;
    private Activity thisActivity;
    private GeneralNoticeManager manager;
    private Button btnMain, btnAll;
    private ArrayList<ClassArticleData> articles;
    private NoticeListAdapter adapter;
    private Button page1, page2, page3, page4, page5;
    private ImageButton page_left, page_right;
    private String selectedPage = "1";
    private boolean flag_main = true;
    ListView listView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup fragment_container, Bundle savedInstanceState) {
        thisActivity = getActivity();

        // Create or inflate the Fragment's UI, and return it. If this Fragment has no UI then return null
        rootView = inflater.inflate(R.layout.notice_general_fragment, fragment_container, false);

        /* 매니저 생성 및 로그인 체크 */
        manager = new GeneralNoticeManager(thisActivity.getApplicationContext());
        // 아이디, 비밀번호 체크해보고 없으면 : 로그인 화면으로 이동 후 액티비티 종료
        if(!manager.getNcheck()) {
            Intent intent = new Intent(thisActivity.getApplicationContext(), Login.class);
            thisActivity.startActivity(intent);
            thisActivity.finish();
        }

        setBasicLayout();

        return rootView;
    }


    /* 기본 레이아웃 셋팅 */
    private void setBasicLayout() {

        /* 버튼 셋팅 */
        btnMain = (Button) rootView.findViewById(R.id.button_general_mainnotice);
        btnAll = (Button) rootView.findViewById(R.id.button_general_allnotice);
        btnMain.setOnClickListener(this);
        btnAll.setOnClickListener(this);
        // 버튼 폰트
        btnMain.setTypeface(Typeface.DEFAULT_BOLD);
        btnAll.setTypeface(Typeface.DEFAULT);

        // set listview
        articles = new ArrayList<>();
        listView = (ListView) rootView.findViewById(R.id.general_article_listview);
        adapter = new NoticeListAdapter(thisActivity.getApplicationContext(), R.layout.general_article_item, articles, "general");
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        /* 중요공지글 불러오는 작업 */
        new GetMainArticles().execute();

        /* 페이지 버튼 셋팅 */
        page1 = (Button) rootView.findViewById(R.id.general_btn_1);
        page1.setOnClickListener(this);

        page2 = (Button) rootView.findViewById(R.id.general_btn_2);
        page2.setTypeface(Typeface.DEFAULT);
        page2.setOnClickListener(this);

        page3 = (Button) rootView.findViewById(R.id.general_btn_3);
        page3.setOnClickListener(this);

        page4 = (Button) rootView.findViewById(R.id.general_btn_4);
        page4.setOnClickListener(this);

        page5 = (Button) rootView.findViewById(R.id.general_btn_5);
        page5.setOnClickListener(this);

        page_left = (ImageButton) rootView.findViewById(R.id.general_btn_left);
        page_left.setOnClickListener(this);

        page_right = (ImageButton) rootView.findViewById(R.id.general_btn_right);
        page_right.setOnClickListener(this);
    }

    /* 공지글 리스트가 변화된 레이아웃 셋팅 */
    private void setArticlesChanged() {
        // 공지글 리스트뷰가 갱신된 ArrayList 내용에 맞게 갱신되도록 설정
        adapter.notifyDataSetChanged();
        listView.setSelection(0);

        /* 받아온 기타 정보에 맞게 페이지 버튼을 셋팅 */
        // 만약 중요공지를 띄우는 중이거나 공지글 arraylist 가 비어있다면 : 무조건 다 INVISIBLE
        if(flag_main || articles.size() == 0) {
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

            page1.setVisibility(View.VISIBLE);
            page2.setVisibility(View.VISIBLE);
            page3.setVisibility(View.VISIBLE);
            page4.setVisibility(View.VISIBLE);
            page5.setVisibility(View.VISIBLE);

            // 선택된 페이지가 1-5이면 : 왼쪽 이동 버튼을 비활성화
            if (Integer.parseInt(selectedPage) <= 5)
                page_left.setVisibility(View.INVISIBLE);
            // 그 이상이면 : 왼쪽 이동 버튼을 활성화
            else
                page_left.setVisibility(View.VISIBLE);

            page_right.setVisibility(View.VISIBLE);
        }
    }


    /* 주요/일반 선택 버튼과 페이지 선택 버튼에 대한 리스너 */
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_general_mainnotice :
                /* 버튼 레이아웃 셋팅 */
                btnMain.setTextColor(getResources().getColor(R.color.white));
                btnAll.setTextColor(getResources().getColor(R.color.notice_background));
                btnMain.setBackgroundResource(R.color.notice_background);
                btnAll.setBackgroundResource(R.drawable.notice_general_button_border);

                /* 현재 주요공지를 띄우고 있는지 일반공지를 띄우고 있는지 표기 */
                flag_main = true;

                /* 서버에 중요공지글 요청하고 띄우는 작업 */
                new GetMainArticles().execute();

                break;

            case R.id.button_general_allnotice :
                /* 버튼 레이아웃 셋팅 */
                btnAll.setTextColor(getResources().getColor(R.color.white));
                btnMain.setTextColor(getResources().getColor(R.color.notice_background));
                btnAll.setBackgroundResource(R.color.notice_background);
                btnMain.setBackgroundResource(R.drawable.notice_general_button_border);

                /* 현재 주요공지를 띄우고 있는지 일반공지를 띄우고 있는지 표기 */
                flag_main = false;

                /* 페이지 초기화 */
                selectedPage = "1";
                new GetAllArticles().execute();

                // 페이지 버튼을 초기화
                page1.setText("1");
                page2.setText("2");
                page3.setText("3");
                page4.setText("4");
                page5.setText("5");
                setPageButtonDefault();
                page1.setTypeface(Typeface.DEFAULT_BOLD);
                page1.setTextColor(getResources().getColor(R.color.notice_background));

                break;

            /* Page Buttons */
            case R.id.general_btn_1 : selectedPage = (String) page1.getText();
                setPageButtonDefault();
                // 해당 버튼 도드라지게 표시
                page1.setTypeface(Typeface.DEFAULT_BOLD);
                page1.setTextColor(getResources().getColor(R.color.notice_background));

                // 일반 공지로 서버에 요청하기
                new GetAllArticles().execute();

                break;
            case R.id.general_btn_2 : selectedPage = (String) page2.getText();
                setPageButtonDefault();
                // 해당 버튼 도드라지게 표시
                page2.setTypeface(Typeface.DEFAULT_BOLD);
                page2.setTextColor(getResources().getColor(R.color.notice_background));

                // 일반 공지로 서버에 요청하기
                new GetAllArticles().execute();

                break;
            case R.id.general_btn_3 : selectedPage = (String) page3.getText();
                setPageButtonDefault();
                // 해당 버튼 도드라지게 표시
                page3.setTypeface(Typeface.DEFAULT_BOLD);
                page3.setTextColor(getResources().getColor(R.color.notice_background));

                // 일반 공지로 서버에 요청하기
                new GetAllArticles().execute();

                break;
            case R.id.general_btn_4 : selectedPage = (String) page4.getText();
                setPageButtonDefault();
                // 해당 버튼 도드라지게 표시
                page4.setTypeface(Typeface.DEFAULT_BOLD);
                page4.setTextColor(getResources().getColor(R.color.notice_background));

                // 일반 공지로 서버에 요청하기
                new GetAllArticles().execute();

                break;
            case R.id.general_btn_5 : selectedPage = (String) page5.getText();
                setPageButtonDefault();
                // 해당 버튼 도드라지게 표시
                page5.setTypeface(Typeface.DEFAULT_BOLD);
                page5.setTextColor(getResources().getColor(R.color.notice_background));

                // 일반공지로 서버에 요청하기
                new GetAllArticles().execute();

                break;

            case R.id.general_btn_left : pagesMoreLeft();
                break;

            case R.id.general_btn_right : pagesMoreRight();
                break;
        }

    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /* 해당 공지글을 보여주는 activity 로 intent 보냄 */

        Intent intent = new Intent(thisActivity.getApplicationContext(), GeneralNoticeContents.class);
        // 서버에 요청할 때 필요한 정보를 intent 안에 구겨 넣어서
        intent.putExtra("link", (String) view.getTag());
        // 던진다!
        thisActivity.startActivity(intent);
    }

    /* 주요 공지글 리스트를 서버에 요청하는 작업 */
    private class GetMainArticles extends AsyncTask<Void, Void, String> {
        private ProgressDialog getMainArticlesDialog;

        /* 실행 전에 Main 스레드의 UI 작업 : Progress Dialog */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // dialog show
            getMainArticlesDialog = ProgressDialog.show(thisActivity, "", "공지를 불러오는 중입니다.. \n기다려주세요");
            getMainArticlesDialog.setCancelable(false);
        }

        /* 다른 스레드로 백그라운드 작업 수행 */
        @Override
        protected String doInBackground(Void... params) {
            // 서버에 중요 공지글 리스트 요청
            if(manager.requestMainArticleList()) {
                // arraylist 내용 갱신
                articles.clear();
                articles.addAll(manager.getMainArticles());

                return "success";
            } else {
                return "fail";
            }
        }

        /* 백그라운드 작업 수행 후 UI 작업 : 서버에서 받아 온 리스트 띄우기 or 실패 다이얼로그 띄우기 */
        @Override
        protected void onPostExecute(String result) {
            getMainArticlesDialog.dismiss();
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
            getMainArticlesDialog.dismiss();
        }
    }

    /* 일반 공지글 리스트를 서버에 요청하는 작업 */
    private class GetAllArticles extends AsyncTask<Void, Void, String> {
        private ProgressDialog getAllArticlesDialog;

        /* 실행 전에 Main 스레드의 UI 작업 : Progress Dialog */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show(띄울 액티비티, 타이틀, 내용)
            getAllArticlesDialog = ProgressDialog.show(thisActivity, "", "공지를 불러오는 중입니다.. \n기다려주세요");
            getAllArticlesDialog.setCancelable(false);
        }

        /* 다른 스레드로 백그라운드 작업 수행 */
        @Override
        protected String doInBackground(Void... params) {
            // 서버에 해당 페이지의 전체 공지글 리스트 요청
            if (manager.requestAllArticleList(selectedPage)) {
                // arraylist 내용 갱신
                articles.clear();
                articles.addAll(manager.getAllArticles());

                return "success";
            } else {
                return "fail";
            }
        }

        /* 백그라운드 작업 수행 후 UI 작업 : 서버에서 받아 온 리스트 띄우기 or 실패 다이얼로그 띄우기 */
        @Override
        protected void onPostExecute(String result) {
            getAllArticlesDialog.dismiss();
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

            /* 서버에 요청 작업과 레이아웃 갱신 */
            // 주요공지를 띄우고 있는 중이면
            if(flag_main)
                new GetMainArticles().execute();
            // 일반공지를 띄우고 있는 중이면
            else
                new GetAllArticles().execute();

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
        if (future_page1 <= 2996) {
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

            /* 서버에 요청 작업과 레이아웃 갱신 */
            // 주요공지를 띄우고 있는 중이면
            if (flag_main)
                new GetMainArticles().execute();
                // 일반공지를 띄우고 있는 중이면
            else
                new GetAllArticles().execute();

        }
        // 혹시나 그렇지 않다면 : 오른쪽 이동 버튼을 비활성화하고 Toast 메세지 띄움
        else {
            page_right.setVisibility(View.INVISIBLE);
            Toast.makeText(thisActivity.getApplicationContext(), "더 이상 이동할 페이지가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

}

