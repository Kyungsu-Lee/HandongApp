package ghost.android.ghosthguapp.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.common.XMLParser;

public class LoginTask extends AsyncTask<String, Void, Boolean> {
    private String params[];
    private static final String KEY_RESULT = "result";

    private XMLParser parser = new XMLParser();

    private Context context;
    ProgressDialog dialog;

    public LoginTask(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("로그인하는 중입니다.");
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        this.params = params;
        return LoginCheck(params[0], params[1]);
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        dialog.dismiss();
        if (result) {
            SharedPreferences prefs = context.getSharedPreferences("Login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("id", params[0]);
            editor.putString("pw", params[1]);
            editor.commit();
            ((Activity) context).finish();
            return;
        } else {
            popLoginFailMessage();
            dialog.cancel();
            return;
        }
    }

    public boolean LoginCheck(String sendID, String sendPW) {
        ArrayList param = new ArrayList();
        param.add(new BasicNameValuePair("id", sendID));
        param.add(new BasicNameValuePair("password", sendPW));

        try {
            URL url = new URL(GlobalVariables.SERVER_ADDR + "getLoginResult.jsp");

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url.toString());

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(param, "utf-8");
            httpPost.setEntity(ent);

            HttpResponse responsePost = httpClient.execute(httpPost);
            HttpEntity resEntity = responsePost.getEntity();

            // Element 선언
            Document doc = parser.getDomElement(EntityUtils.toString(resEntity));

            // NodeList 선언
            String result = doc.getElementsByTagName(KEY_RESULT)
                    .item(0)
                    .getFirstChild()
                    .getNodeValue();

            return result.equals("true") ? true : false;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void popLoginFailMessage() {
        new AlertDialog.Builder(context)
                .setTitle("로그인 실패!")
                .setMessage(
                        "ID, Password를 확인해 주세요. (Hisnet ID, Hisnet Password)")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
    }

}