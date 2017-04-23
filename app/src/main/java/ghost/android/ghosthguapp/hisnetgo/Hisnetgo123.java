package ghost.android.ghosthguapp.hisnetgo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.Stack;
import java.util.concurrent.ExecutionException;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalMethods;
import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.login.Login;

public class Hisnetgo123 extends Activity {
    String stuid = "";
    WebView hisnetgo;
    WebSettings webSettings;
    ProgressBar progressBar;
    Stack<WebView> webViewsList = new Stack<WebView>();
    RelativeLayout webViewLayout;
    RelativeLayout.LayoutParams params;
    int cntWebView = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS); // Progress
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hisnetgo_layout);

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        webViewLayout = (RelativeLayout) findViewById(R.id.layout_rl_hisnetgo);
        hisnetgo = (WebView) findViewById(R.id.hisnetgo_webview);
        webSettings = hisnetgo.getSettings();
        progressBar = (ProgressBar) findViewById(R.id.hisnetgo_progressbar);

        /* Login 여부 체크 */
        SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
        String id = sp.getString("id", "");
        String pw = sp.getString("pw", "");

        if (id.equals("") || pw.equals("")) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle("");
            dlg.setMessage("로그인이 필요한 서비스입니다.");
            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent i = new Intent(Hisnetgo123.this, Login.class);
                    finish();
                    startActivity(i);
                }
            }).show();
        } else {
            AlertDialog.Builder netConDlgBuilder = GlobalMethods.checkInternet(Hisnetgo123.this);
            //인터넷 안되면
            if (netConDlgBuilder != null) {
                netConDlgBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
            }
            //인터넷 연결 되면
            else {
                try {
                    stuid = new GetStuidTask().execute(GlobalVariables.SERVER_ADDR + "getStudentNumber.jsp?id=" + id + "&password=" + pw).get();
                } catch (ExecutionException | InterruptedException e) {
                }

                byte[] encodeValue = Base64.encode(stuid.getBytes(), Base64.DEFAULT);
                stuid = new String(encodeValue);

                String url = "http://smart.handong.edu/mobile/sample/hisnetgo/" + stuid;

                webSettings.setJavaScriptEnabled(true);
                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                webSettings.setSupportMultipleWindows(true);
                webSettings.setDefaultTextEncodingName("UTF-8");
                webSettings.setSupportZoom(true);
                webSettings.setBuiltInZoomControls(true);
                webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                webSettings.setLoadWithOverviewMode(true);
                webSettings.setUseWideViewPort(true);
                hisnetgo.setInitialScale(100);
                hisnetgo.loadUrl(url);

                hisnetgo.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int progress) {
                        if (progress < 100) {
                            progressBar.setProgress(progress);
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
                        final WebView newWebView = new WebView(Hisnetgo123.this);
                        webViewsList.push(newWebView);
                        newWebView.setLayoutParams(params);
                        newWebView.setInitialScale(100);
                        WebSettings settings = newWebView.getSettings();
                        settings.setJavaScriptEnabled(true);
                        settings.setJavaScriptCanOpenWindowsAutomatically(true);
                        settings.setSupportMultipleWindows(true);
                        settings.setSupportZoom(true);
                        settings.setBuiltInZoomControls(true);
                        settings.setLoadWithOverviewMode(true);
                        settings.setUseWideViewPort(true);
                        newWebView.setWebViewClient(new WebViewClient() {
                                                        @Override
                                                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                                            view.loadUrl(url);
                                                            return true;
                                                        }

                                                        @Override
                                                        public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                                                            super.onPageStarted(view, url, favicon); //페이지 로딩 시작
                                                            progressBar.setVisibility(View.VISIBLE);
                                                        }

                                                        @Override
                                                        public void onPageFinished(WebView view, String url) { //페이지 로딩 완료
                                                            super.onPageFinished(view, url);
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    }
                        );

                        newWebView.setWebChromeClient(new WebChromeClient() {
                            @Override
                            public void onProgressChanged(WebView view, int progress) {
                                if (progress < 100) {
                                    progressBar.setProgress(progress);
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCloseWindow(WebView window) {
                                Animation alpha = AnimationUtils.loadAnimation(Hisnetgo123.this, R.anim.closing_webview);
                                window.startAnimation(alpha);
                                webViewsList.pop();
                                window.setVisibility(View.GONE);
                                webViewLayout.removeView(window);
                            }
                        });

                        webViewLayout.addView(newWebView);
                        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                        transport.setWebView(newWebView);
                        resultMsg.sendToTarget();
                        return true;
                    }
                });

                hisnetgo.setWebViewClient(new WebViewClient() {
                                              @Override
                                              public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                                                  AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                                                  builder.setTitle("네트워크 오류");
                                                  builder.setMessage("\n네트워크 상태를 확인 해주세요.\n")
                                                          .setCancelable(false)
                                                          .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                              public void onClick(DialogInterface dialog, int id) {
                                                                  dialog.dismiss();
                                                                  finish();
                                                              }
                                                          }).show();
                                              }

                                              @Override
                                              public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                                                  super.onPageStarted(view, url, favicon); //페이지 로딩 시작
                                                  progressBar.setVisibility(View.VISIBLE);
                                              }

                                              @Override
                                              public void onPageFinished(WebView view, String url) { //페이지 로딩 완료
                                                  super.onPageFinished(view, url);
                                                  progressBar.setVisibility(View.GONE);
                                              }
                                          }
                );
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            WebView webView;
            if (webViewsList.isEmpty()) {
                webView = hisnetgo;
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    webView.clearCache(false);
                    finish();
                }
            } else {
                webView = webViewsList.pop();
                Animation alpha = AnimationUtils.loadAnimation(this, R.anim.closing_webview);
                webView.startAnimation(alpha);
                webViewLayout.removeView(webView);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}