package ghost.android.ghosthguapp.notice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import ghost.android.ghosthguapp.GhostActionBarActivity;
import ghost.android.ghosthguapp.R;

/**
 * Created by SEC on 2015-01-24.
 */
public class GeneralNoticeContents extends GhostActionBarActivity {

    private String link = "";
    private String url = "hisnet.handong.edu";
    private String webTags = "<html></html>";
    private GeneralNoticeManager manager;
    private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_contents_page);

        /* 필요한 정보 받아오기 */
        Intent intent = getIntent();
        link = intent.getStringExtra("link");
        /* 기본 레이아웃 셋팅 */
        setBasicLayout();

        new GetArticle().execute();

    }

    /* 액션바, 탭 버튼, 첫 화면 등 기본 레이아웃 셋팅 */
    private void setBasicLayout() {
        //set actionbar title for yasick activity
        if (ghostActionBarTitle != null) {
            ghostActionBarTitle.setText(R.string.title_notice_main);
        }

        // set actionbar logo for yasick activity
        if (ghostActionBarLogo != null) {
            ghostActionBarLogo.setImageResource(R.drawable.notice_logo);
        }

        /* 매니져 클래스 생성 */
        manager = new GeneralNoticeManager(getApplicationContext());
        manager.getNcheck();

        /* 웹뷰 레이아웃 맵핑 */
        webView = (WebView) findViewById(R.id.notice_webView);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setInitialScale(150);
    }

    /* 서버에서 받아온 정보를 스크린에 띄워주는 메소드 */
    private void setChanged() {
        try {
            webView.loadData(URLEncoder.encode(webTags, "utf-8").replaceAll("\\+", " "), "text/html; charset=utf-8", null);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private class GetArticle extends AsyncTask<Void, Void, String> {

        private ProgressDialog getArticleDialog;

        /* 실행 전에 Main 스레드의 UI 작업 : Progress Dialog */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show(띄울 액티비티, 타이틀, 내용)
            getArticleDialog = ProgressDialog.show(GeneralNoticeContents.this, "", "해당 공지글을 불러오는 중입니다.. \n기다려주세요");
            getArticleDialog.setCancelable(false);
        }

        /* 다른 스레드로 백그라운드 작업 수행 */
        @Override
        protected String doInBackground(Void... arg0) {
            if (manager.requestArticle(link)) {
                // 태그 string 받아오기
                webTags = manager.getWebTags();
                return "success";
            } else {
                return "fail";
            }
        }

        /* 백그라운드 작업 수행 후 UI 작업 : 서버에서 받아 온 리스트 띄우기 or 실패 다이얼로그 띄우기 */
        @Override
        protected void onPostExecute(String result) {
            getArticleDialog.dismiss();
            if (result.equals("success")) {
                // 변화된 레이아웃 수정
                setChanged();
            } else if (result.equals("fail")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GeneralNoticeContents.this);
                builder.setTitle("연결 오류");
                builder.setMessage("정보를 받아올 수 없습니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .show();
            }
        }
    }
}
