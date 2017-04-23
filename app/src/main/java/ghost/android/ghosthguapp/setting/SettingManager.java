package ghost.android.ghosthguapp.setting;

import android.content.Context;
import android.content.pm.PackageManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.common.XMLParser;

public class SettingManager {
    private Context con;

    public SettingManager(Context context){
        this.con = context;
    }

    public void versionCheck() {
        boolean vChanged = true;

        XMLParser parser = new XMLParser();

        try {
            ArrayList param = new ArrayList();
            param.add(new BasicNameValuePair("version", con.getPackageManager().getPackageInfo(con.getPackageName(), 0).versionName));

            URL url = new URL(GlobalVariables.SERVER_ADDR + "getAppVersion.jsp");

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url.toString());

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(param, "utf-8");
            httpPost.setEntity(ent);

            HttpResponse responsePost = httpClient.execute(httpPost);
            HttpEntity resEntity = responsePost.getEntity();

            // Element 선언
            Document doc = parser.getDomElement(EntityUtils.toString(resEntity));

            // NodeList 선언
            String vResult = doc.getElementsByTagName("version")
                    .item(0)
                    .getFirstChild()
                    .getNodeValue();

            vChanged = vResult.equals("change") ? true : false;
            GlobalVariables.oldVersion = vChanged;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return ;
    }
}
