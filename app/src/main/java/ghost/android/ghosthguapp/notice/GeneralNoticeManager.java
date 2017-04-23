package ghost.android.ghosthguapp.notice;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.common.XMLParser;

/**
 * Created by SEC on 2015-01-22.
 */

public class GeneralNoticeManager {

    private String id;
    private String pw;
    private Context context;
    private String mainArticlesUrl = GlobalVariables.SERVER_ADDR + "Gongji/getJuyoGongji.jsp";
    private String allArticlesUrl = GlobalVariables.SERVER_ADDR + "Gongji/getIlbanGongji.jsp";
    private String readUrl = GlobalVariables.SERVER_ADDR + "Gongji/getNoticeContents.jsp";
    private XMLParser parser;
    private ArrayList<ClassArticleData> mainArticles;
    private ArrayList<ClassArticleData> allArticles;
    private String webTags = "<html></html>";

    /* 이 매니져가 생성됨과 동시에 shared preferences 에서 id, pw 값 받아와서 여기서 관리하기 */
    public GeneralNoticeManager(Context con) {
        context = con;
        parser = new XMLParser();
    }

    /* 사용자 정보를 SharedPreferences 에서 가져오고 존재 여부를 리턴한다 */
    public boolean getNcheck() {

        SharedPreferences sp = context.getSharedPreferences("Login", Context.MODE_PRIVATE);
        id = sp.getString("id", "getNcheckhasnoid");
        pw = sp.getString("pw", "getNcheckhasnopw");

        if(id.equals("getNcheckhasnoid") || pw.equals("getNcheckhasnopw")) {
            return false;
        }
        else
            return true;
    }

    /* 중요 공지글 리스트를 서버에서 받아오는 메소드 */
    public boolean requestMainArticleList() {
        // post 로 보낼 parameter 설정
        ArrayList param = new ArrayList();
        param.add(new BasicNameValuePair("id", id));
        param.add(new BasicNameValuePair("password", pw));

        // post 보내기
        HttpEntity resEntity = sendPostNreceive(mainArticlesUrl, param);
        if (resEntity == null)
            return false;

        try {
            // DOM 선언
            Document dom = parser.getDomElement(EntityUtils.toString(resEntity));
            if (dom == null)
                return false;

            // Element 선언
            Element docElement = dom.getDocumentElement();

            /* 중요 공지글 리스트 받아오기 */
            parseMainArticleList(docElement);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* 전체 공지글 리스트를 서버에서 받아오는 메소드 */
    public boolean requestAllArticleList(String page) {
        // post 로 보낼 parameter 설정
        ArrayList param = new ArrayList();
        param.add(new BasicNameValuePair("id", id));
        param.add(new BasicNameValuePair("password", pw));
        param.add(new BasicNameValuePair("pagenum", page));

        // post 보내기
        HttpEntity resEntity = sendPostNreceive(allArticlesUrl, param);
        if (resEntity == null)
            return false;

        try {
            // DOM 선언
            Document dom = parser.getDomElement(EntityUtils.toString(resEntity));
            if (dom == null)
                return false;

            // Element 선언
            Element docElement = dom.getDocumentElement();

            /* 현재 학기의 수강 과목 리스트 받아오기 */
            parseAllArticleList(docElement);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* 해당 공지글 내용을 서버에서 받아오는 메소드 */
    public boolean requestArticle(String link) {
        // post 로 보낼 parameter 설정
        ArrayList param = new ArrayList();
        param.add(new BasicNameValuePair("id", id));
        param.add(new BasicNameValuePair("password", pw));
        param.add(new BasicNameValuePair("link", link));

        // post 보내기
        HttpEntity resEntity = sendPostNreceive(readUrl, param);
        if (resEntity == null)
            return false;

        try {// DOM 선언
            Document dom = parser.getDomElement(EntityUtils.toString(resEntity));
            if (dom == null)
                return false;

            // Element 선언
            Element docElement = dom.getDocumentElement();

            /* 공지글의 웹뷰 태그들 파싱하기 */
            NodeList nodeList = docElement.getElementsByTagName("contents");
            // NodeList 가 정보를 받아오면
            if (nodeList != null && nodeList.getLength() > 0) {
                // NodeList 에서 받은 정보를 파싱
                Node e = (Node) nodeList.item(0);
                webTags = parser.getElementValue(e);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* http post 보내는 메소드 */
    public HttpEntity sendPostNreceive(String url, ArrayList param) {

        HttpEntity resEntity = null;

        try {
            // post 보내기
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(param, "utf-8");
            httpPost.setEntity(ent);

            HttpResponse responsePost = httpClient.execute(httpPost);
            resEntity = responsePost.getEntity();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resEntity;
    }


    /* 중요공지글 리스트 파싱하는 메소드 */
    private void parseMainArticleList(Element docElement) {

        final String KEY_ARTICLELIST = "Information";
        final String KEY_NUM = "Num";
        final String KEY_TITLE = "Title";
        final String KEY_DATE = "Date";
        final String KEY_URL = "URL";


        // NodeList 선언  (KEY_ARTICLELIST 라는 이름의 태그를 가진 노드들의 리스트)
        NodeList nodeList = docElement.getElementsByTagName(KEY_ARTICLELIST);

        // 수강과목 저장할 arrayList 셋팅
        mainArticles = new ArrayList<>();

        // NodeList 가 정보를 받아오면
        if (nodeList != null && nodeList.getLength() > 0) {
            // NodeList 에서 받은 정보를 파싱
            for(int i = 0; i < nodeList.getLength(); i++) {
                Element article = (Element) nodeList.item(i);
                ClassArticleData articleData = new ClassArticleData();
                articleData.setNum(parser.getValue(article, KEY_NUM));
                articleData.setTitle(parser.getValue(article, KEY_TITLE));
                articleData.setDate(parser.getValue(article, KEY_DATE));
                articleData.setUrl(parser.getValue(article, KEY_URL));
                mainArticles.add(articleData);
            }
        }
    }

    /* 전체공지글 리스트 파싱하는 메소드 */
    private void parseAllArticleList(Element docElement) {

        final String KEY_ARTICLELIST = "Information";
        final String KEY_NUM = "Num";
        final String KEY_TITLE = "Title";
        final String KEY_DATE = "Date";
        final String KEY_URL = "URL";


        // NodeList 선언  (KEY_ARTICLELIST 라는 이름의 태그를 가진 노드들의 리스트)
        NodeList nodeList = docElement.getElementsByTagName(KEY_ARTICLELIST);

        // 수강과목 저장할 arrayList 셋팅
        allArticles = new ArrayList<>();

        // NodeList 가 정보를 받아오면
        if (nodeList != null && nodeList.getLength() > 0) {
            // NodeList 에서 받은 정보를 파싱
            for(int i = 0; i < nodeList.getLength(); i++) {
                Element article = (Element) nodeList.item(i);
                ClassArticleData articleData = new ClassArticleData();
                articleData.setNum(parser.getValue(article, KEY_NUM));
                articleData.setTitle(parser.getValue(article, KEY_TITLE));
                articleData.setDate(parser.getValue(article, KEY_DATE));
                articleData.setUrl(parser.getValue(article, KEY_URL));
                allArticles.add(articleData);
            }
        }
    }


    /* 중요공지글 리스트를 외부로 전달하는 메소드 */
    public ArrayList<ClassArticleData> getMainArticles() { return mainArticles; }

    /* 전체공지글 리스트를 외부로 전달하는 메소드 */
    public ArrayList<ClassArticleData> getAllArticles() { return allArticles; }

    /* 해당 공지글 웹뷰의 html 태그 string 을 외부로 전해주는 메소드 */
    public String getWebTags() { return webTags; }
}
