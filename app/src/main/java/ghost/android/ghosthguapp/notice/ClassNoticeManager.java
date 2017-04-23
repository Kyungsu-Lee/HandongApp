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
 * Created by SEC on 2015-01-14.
 */

public class ClassNoticeManager {

    private String id;
    private String pw;
    private Context context;
    private String url = GlobalVariables.SERVER_ADDR;
    private XMLParser parser;
    private int current_semester;
    private ArrayList<String> subjectList;
    private ArrayList<ClassArticleData> articles;
    private int neighborPages = 5;
    private boolean isNext = true;
    private String webTags = "<html></html>";

    /* 이 매니져가 생성됨 */
    public ClassNoticeManager(Context con, String url) {
        context = con;
        this.url = this.url + url;
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


    /* 맨 처음 현재 학기 정보와 과목 받아오기 */
    public boolean receiveCurrentInfo() {

        final String KEY_SEMESTER = "semester";

        // post 로 보낼 parameter 설정
        ArrayList param = new ArrayList();
        param.add(new BasicNameValuePair("id", id));
        param.add(new BasicNameValuePair("password", pw));

        // post 보내기
        HttpEntity resEntity = sendPostNreceive(url, param);
        if (resEntity == null)
            return false;

        try {
            // DOM 선언
            Document dom = parser.getDomElement(EntityUtils.toString(resEntity));
            if (dom == null)
                return false;

            // Element 선언
            Element docElement = dom.getDocumentElement();

            /* 현재 학기 받아오기 */
            String curSemester = parser.getValue(docElement, KEY_SEMESTER);
            current_semester = Integer.parseInt(curSemester);
            //selected_semester = "0" + curSemester;

            /* 현재 학기의 수강 과목 리스트 받아오기 */
            parseSubjectNames(docElement);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* 설정된 년도, 학기에 따른 과목 리스트를 받아오는 메소드 */
    public boolean requestSubjectList(String year, String semester) {

        // post 로 보낼 parameter 설정
        ArrayList param = new ArrayList();
        param.add(new BasicNameValuePair("id", id));
        param.add(new BasicNameValuePair("password", pw));
        String year_semester = "" + year + semesterFilter(semester);
        param.add(new BasicNameValuePair("semester", year_semester));

        // post 보내기
        HttpEntity resEntity = sendPostNreceive(url, param);
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
            parseSubjectNames(docElement);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /* 설정된 년도, 학기, 과목에 따른 공지글 제목들의 리스트를 서버에서 받아오는 메소드 */
    public boolean requestArticleList(String year, String semester, String subject, String page) {
        // post 로 보낼 parameter 설정
        ArrayList param = new ArrayList();
        param.add(new BasicNameValuePair("id", id));
        param.add(new BasicNameValuePair("password", pw));
        param.add(new BasicNameValuePair("semester", "" + year + semesterFilter(semester)));
        param.add(new BasicNameValuePair("code", subjectFilter(subject)));
        param.add(new BasicNameValuePair("page", page));


        // post 보내기
        HttpEntity resEntity = sendPostNreceive(url, param);
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
            parseArticleList(docElement, page);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* 설정된 년도, 학기, 과목에 따른 '과제'공지글 제목들의 리스트를 서버에서 받아오는 메소드 */
    public boolean requestHwArticleList(String year, String semester, String subject) {
        // post 로 보낼 parameter 설정
        ArrayList param = new ArrayList();
        param.add(new BasicNameValuePair("id", id));
        param.add(new BasicNameValuePair("password", pw));
        param.add(new BasicNameValuePair("semester", "" + year + semesterFilter(semester)));
        param.add(new BasicNameValuePair("code", subjectFilter(subject)));


        // post 보내기
        HttpEntity resEntity = sendPostNreceive(url, param);
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
            parseHwArticleList(docElement);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /* 공지글 내용을 서버에서 받아오는 메소드 */
    public boolean requestArticle(String link) {
        // post 로 보낼 parameter 설정
        ArrayList param = new ArrayList();
        param.add(new BasicNameValuePair("id", id));
        param.add(new BasicNameValuePair("password", pw));
        param.add(new BasicNameValuePair("link", link));

        // post 보내기
        HttpEntity resEntity = sendPostNreceive(url, param);
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
                Node e = nodeList.item(0);
                webTags = parser.getElementValue(e);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /* 과목 이름 리스트 파싱하는 메소드 */
    private void parseSubjectNames(Element docElement) {

        final String KEY_COURSELIST = "course";

        // NodeList 선언  (KEY_COURSELIST 라는 이름의 태그를 가진 노드들의 리스트)
        NodeList nodeList = docElement.getElementsByTagName(KEY_COURSELIST);

        // 수강과목 저장할 arrayList 셋팅
        subjectList = new ArrayList<>();
        subjectList.add("과목 선택");

        // NodeList 가 정보를 받아오면
        if (nodeList != null && nodeList.getLength() > 0) {
            // NodeList 에서 받은 정보를 파싱
            for(int i = 0; i < nodeList.getLength(); i++) {
                Element course = (Element) nodeList.item(i);
                String str = course.getFirstChild().getNodeValue();
                str = str.replace("<![CDATA[", "");
                str = str.replace("]]>", "");
                subjectList.add(str.trim());
            }
        }
    }

    /* 공지글 리스트 파싱하는 메소드 */
    private void parseArticleList(Element docElement, String currentPage) {

        final String KEY_ARTICLELIST = "article";
        final String KEY_NUM = "num";
        final String KEY_TITLE = "title";
        final String KEY_DATE = "date";
        final String KEY_URL = "url";
        final String KEY_PAGE = "page";
        final String KEY_ISNEXT = "isNext";


        // NodeList 선언  (KEY_ARTICLELIST 라는 이름의 태그를 가진 노드들의 리스트)
        NodeList nodeList = docElement.getElementsByTagName(KEY_ARTICLELIST);

        // 수강과목 저장할 arrayList 셋팅
        articles = new ArrayList<>();

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
                articles.add(articleData);
            }
        }

        // 현재 페이지가 속한 페이지의 오른쪽 이동버튼의 활성여부를 받아오기
        String strNext = "true";
        NodeList nodeList_n = docElement.getElementsByTagName(KEY_ARTICLELIST);
        // NodeList 가 정보를 받아오면
        if (nodeList_n != null && nodeList_n.getLength() > 0) {
            // NodeList 에서 받은 정보를 파싱
            Element e = (Element) nodeList_n.item(0);
            strNext = parser.getValue(e, KEY_ISNEXT);
        }

        if(strNext != null) {
            // <isNext> 가 true 이면 : 멤버 isNext 를 true 로
            if (strNext.equals("true"))
                isNext = true;
            // false 이면 : 멤버 isNext 를 false 로
            else if(strNext.equals("false"))
                isNext = false;
            // 이것도 저것도 아니면 : 그냥 true
            else
                isNext = true;
        }


        // 현재 페이지가 속한 이웃 페이지 수 받아오기
        String pages = "5";
        NodeList nodeList_p = docElement.getElementsByTagName(KEY_ARTICLELIST);
        // NodeList 가 정보를 받아오면
        if (nodeList_p != null && nodeList_p.getLength() > 0) {
            // NodeList 에서 받은 정보를 파싱
            Element e = (Element) nodeList_p.item(0);
            pages = parser.getValue(e, KEY_PAGE);
        }
        if(pages != null) {
            int current_page;
            try {
                neighborPages = Integer.parseInt(pages);
            } catch (NumberFormatException e) {     /* 만약 읽어온 페이지가 제대로 된 형태가 아니면, 그냥 5개 있는 걸로 설정 */
                neighborPages = 5;
            }

            try {
                current_page = (Integer.parseInt(currentPage)) % 10;
            } catch (NumberFormatException e) {     /* 만약 읽어온 페이지가 제대로 된 형태가 아니면, 그냥 현재 페이지가 1인 걸로 설정 */
                current_page = 1;
            }
            // 현재 페이지가 5 이하라면
            if (current_page <= 5 && current_page >= 1) {
                // 이웃 페이지 수가 5 페이지 이상이라면 : 어플 상 이웃페이지 수는 5개라고 설정, 다음 페이지 그룹이 존재하는 걸로 설정.
                if (neighborPages > 5) {
                    neighborPages = 5;
                    isNext = true;
                }
                // 이하라면 : 그냥 그대로 가면 되므로 do nothing
            }
            // 현재 페이지가 6 이상이라면 : 이웃 페이지 수를 어플 상의 기준인 5개에 맞게 자른다.
            else if (current_page >= 6 || current_page == 0) {
                // 10 인 경우 : mod 5 한 값 말고 그냥 5로 설정
                if (neighborPages == 10) {
                    neighborPages = 5;
                }
                // 10 아닌 경우 : mod 5
                else {
                    neighborPages = neighborPages % 5;
                }
            }
        }
    }

    /* 과제 공지글 리스트 파싱하는 메소드 */
    private void parseHwArticleList(Element docElement) {
        final String KEY_ARTICLELIST = "article";
        final String KEY_TITLE = "title";
        final String KEY_SUBMITDUE = "date";
        final String KEY_LINK = "url";
        final String KEY_SUBMITSTATUS = "status";


        // NodeList 선언  (KEY_ARTICLELIST 라는 이름의 태그를 가진 노드들의 리스트)
        NodeList nodeList = docElement.getElementsByTagName(KEY_ARTICLELIST);

        // 수강과목 저장할 arrayList 셋팅
        articles = new ArrayList<>();

        // NodeList 가 정보를 받아오면
        if (nodeList != null && nodeList.getLength() > 0) {
            // NodeList 에서 받은 정보를 파싱
            for(int i = 0; i < nodeList.getLength(); i++) {
                Element article = (Element) nodeList.item(i);
                ClassArticleData articleData = new ClassArticleData();
                articleData.setTitle(parser.getValue(article, KEY_TITLE));
                articleData.setSubmitDue(formatSubmitDue(parser.getValue(article, KEY_SUBMITDUE)));
                articleData.setSubmitStatus(formatSubmitStatus(parser.getValue(article, KEY_SUBMITSTATUS)));
                articleData.setUrl(parser.getValue(article, KEY_LINK));
                articles.add(articleData);
            }
        }
    }

    /* http post 보내는 메소드 */
    private HttpEntity sendPostNreceive(String url, ArrayList param) {

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

    /* 학기를 서버에 보내기 위한 내용으로 바꿔주는 메소드 */
    private String semesterFilter(String sem) {
        String semester = "01";

        if (sem.equals("1"))
            semester = "01";
        else if (sem.equals("2"))
            semester = "02";
        else if (sem.equals("summer"))
            semester = "03";
        else if (sem.equals("winter"))
            semester = "04";

        return semester;
    }

    /* 과목 전체 이름을 받아 서버에 요청할 수 있는 형태의 코드로 바꿔주는 메소드 */
    private String subjectFilter(String subject) {
        // String 이 충분히 길 때만 10까지 자르기 (과목코드가 담긴 String 의 인덱스가 딱 10까지임)
        if(subject.length() > 11)
            subject = subject.substring(0, 11);
        String subject_code[] = subject.split("-");

        if(subject_code.length == 1)
            return subject_code[0];
        else
            return subject_code[1] + subject_code[0];
    }

    /* subject due 형식에 맞춰 잘라주는 메소드 */
    private String formatSubmitDue(String due) {
        if (due == null || due.equals("null") || due.equals(""))
            return " ";
        due = due.replace("-", "/");
        String dues[] = due.split("~");
        if(dues.length != 2)
            return " ";
        dues[1] = dues[1].substring(2);
        String temp[] = dues[1].split(" ");
        if(temp.length != 2)
            return " ";
        return temp[0] + "\n" + temp[1] + " 까지";
    }

    /* subject status 형식에 맞춰 잘라주는 메소드 */
    private String formatSubmitStatus(String status) {
        if(status == null|| status.equals(""))
            return " ";
        if(status.equals("null"))
            return "미제출";
        status = status.replace("-", "/");
        status = status.substring(9);
        String sta[] = status.split(" ");
        if(sta.length != 2)
            return " ";
        return sta[0] + "\n" + sta[1];
    }

    /* 현재 학기를 알려주는 메소드 */
    public int getCurrentSemester() { return current_semester; }

    /* 지금 저장되어 있는 과목 리스트를 전해주는 메소드 */
    public ArrayList<String> getSubjectList() { return subjectList; }

    /* 지금 저장되어 있는 공지글 리스트(한 페이지 분량)를 전해주는 메소드 */
    public ArrayList<ClassArticleData> getArticles() { return articles; }

    /* 현재 페이지가 속한 이웃 페이지 수를 전해주는 메소드 */
    public int getNeighborPages() { return neighborPages;  }

    /* 현재 페이지의 오른쪽 이동 버튼 활성화 여부를 전해주는 메소드 */
    public boolean getIsNext() { return isNext; }

    /* 공지글 내용을 구성하는 html 태그 멤버변수 webTags 를 외부에 전달해주는 메소드 */
    public String getWebTags() { return webTags; }

}
