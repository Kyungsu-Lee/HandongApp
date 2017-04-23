package ghost.android.ghosthguapp.runInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.common.XMLParser;

public class RunInfoManager {
    private ArrayList<ArrayList<RunInfoChildData>> childDatas;
    private ArrayList<RunInfoGroupData> groupDatas;
    private File file = GlobalVariables.fRunInfo;


    // SD카드에서 전화번호부 받아오기
    public void setting() {
        // XML node keys
        final String KEY_LIST = "info"; // parent node
        final String KEY_NAME = "name";
        final String KEY_TIME = "runtime";
        final String KEY_NOTE = "note";

        // List 열기
        groupDatas = new ArrayList<RunInfoGroupData>();
        childDatas = new ArrayList<ArrayList<RunInfoChildData>>();

        XMLParser parser = new XMLParser();

        if (file.exists()) {
            // DOM 선언
            Document dom = parser.getDomElementFromFile(file);
            // Element 선언
            Element docEle = dom.getDocumentElement();
            // NodeList 선언
            NodeList nl = docEle.getElementsByTagName(KEY_LIST);

            // NodeList가 정보를 받아오면
            if (nl != null && nl.getLength() > 0) {
                // NodeList에서 받은 정보 infoList에 뿌리기
                for (int i = 0; i < nl.getLength(); i++) {
                    RunInfoGroupData group = new RunInfoGroupData();
                    RunInfoChildData child = new RunInfoChildData();

                    Element e = (Element) nl.item(i);
                    group.setName(parser.getValue(e, KEY_NAME));
                    group.setGroupId(String.valueOf(i));

                    child.setTime(parser.getValue(e, KEY_TIME));
                    child.setNote(parser.getValue(e, KEY_NOTE));

                    ArrayList<RunInfoChildData> children = new ArrayList<RunInfoChildData>();

                    children.add(child);
                    childDatas.add(children);
                    groupDatas.add(group);
                    // for 종료
                }
                // if nl is not null 종료
            }
        }
    }

    // 처음 getList
    public ArrayList<RunInfoGroupData> getGroupDataList() {
        return groupDatas;
    }

    public ArrayList<ArrayList<RunInfoChildData>> getChildDataList() {
        return childDatas;
    }

    public boolean checkNSave() {
        boolean vChanged = true;

        // InputStream 객체 생성
        BufferedInputStream is = null;
        // FileOutputStream 객체 생성
        BufferedOutputStream fos = null;

        try {
            URL url = new URL(GlobalVariables.SERVER_ADDR + "getOptime.jsp");
            if (file.exists()) {
                XMLParser parser = new XMLParser();
                Document clientDoc = parser.getDomElementFromFile(file);

                ArrayList param = new ArrayList();
                param.add(new BasicNameValuePair("version",
                                clientDoc.getElementsByTagName("version")
                                        .item(0)
                                        .getFirstChild()
                                        .getNodeValue())
                );

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url.toString());

                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(param, "utf-8");
                httpPost.setEntity(ent);

                HttpResponse responsePost = httpClient.execute(httpPost);
                HttpEntity resEntity = responsePost.getEntity();

                // Element 선언
                Document doc = parser.getDomElement(EntityUtils.toString(resEntity));
                // NodeList 선언
                String vResult = doc.getElementsByTagName("vResult")
                        .item(0)
                        .getFirstChild()
                        .getNodeValue();

                vChanged = vResult.equals("change") ? true : false;
            }

            if (!file.exists() || vChanged) {

                // 서버 연결
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                // 연결 참조 변수 선언
                int responseCode = con.getResponseCode();

                // 연결되면
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    file.createNewFile();
                    // InputStream 변수 선언
                    is = new BufferedInputStream(con.getInputStream());

                    // FileOutputStream 변수 선언
                    fos = new BufferedOutputStream(new FileOutputStream(file));

                    // 파일 복사
                    int c;
                    while ((c = is.read()) != -1) {
                        fos.write((char) c);
                    }

                    // 스트림 닫기
                    is.close();
                    fos.close();
                } else {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;

        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        return true;
   }

}
