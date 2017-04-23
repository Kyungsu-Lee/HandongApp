package ghost.android.ghosthguapp.yasick;

/**
 * Created by SEC on 2015-01-05.
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.common.XMLParser;

/** 야식 리스트에 대해 서버에서 받아 저장하고, 파싱하는 작업을 정의한 클래스 */
public class YasickStoreManager {
    private ArrayList<YasickStoreData> al_yasickStore;
    private YasickFileManager fileManager;

    public YasickStoreManager(YasickFileManager manager) {
        fileManager = manager;
    }

    /* SD카드에 저장 되어있는 야식집 리스트 파일을 받아 파싱하기 */
    public void setting() {

        //XML node keys
        final String KEY_LIST = "information"; //parent node
        final String KEY_STOREID = "id";
        final String KEY_NAME = "name";
        final String KEY_PHONE = "phone";
        final String KEY_RUNTIME = "runTime";
        final String KEY_CATEGORY = "category";

        // SD 카드 열기
        File file = fileManager.getListFile();

        // List 열기
        al_yasickStore = new ArrayList<YasickStoreData>();
        // XML 파서 생성
        XMLParser parser = new XMLParser();

        try {
            // DOM 선언
            Document dom = parser.getDomElementFromFile(file);
            // Element 선언
            Element docElement = dom.getDocumentElement();
            // NodeList 선언  (KEY_LIST 라는 이름의 태그를 가진 노드들의 리스트)
            NodeList nodeList = docElement.getElementsByTagName(KEY_LIST);

            // NodeList 가 정보를 받아오면
            if(nodeList != null && nodeList.getLength() > 0) {
                // NodeList 에서 받은 정보를 파싱
                for(int i = 0; i < nodeList.getLength(); i++) {
                    YasickStoreData yasickStore = new YasickStoreData();
                    Element store = (Element) nodeList.item(i);
                    yasickStore.setName(parser.getValue(store, KEY_NAME));
                    yasickStore.setPhone(parser.getValue(store, KEY_PHONE));
                    yasickStore.setRunTime(parser.getValue(store, KEY_RUNTIME));
                    yasickStore.setCategory(parser.getValue(store, KEY_CATEGORY));
                    yasickStore.setStoreId(parser.getValue(store, KEY_STOREID));

                    al_yasickStore.add(yasickStore);
                }
            }
        } catch(NullPointerException e) {
            e.printStackTrace();
        }


    }

    /* getList */
    public ArrayList<YasickStoreData> getList() { return al_yasickStore; }

    /* 업데이트 or 처음 정보 받아서 SD 카드에 저장 */
    public boolean checkNSave() {
        boolean vChanged = true;

        // InputStream 객체 생성
        InputStream is = null;
        // FileOutputStream 객체 생성
        FileOutputStream fos = null;

        // 파일 얻기
        File file = fileManager.getListFile();

        // 파일이 존재하면 : 버전 정보를 서버로부터 받아온다.
        if(file.exists()) {
            XMLParser parser = new XMLParser();
            Document clientDocument = parser.getDomElementFromFile(file);
            try {
                URL url = new URL(GlobalVariables.SERVER_ADDR + "yasick/getYasickStoreList.jsp");

                // 현재 사용자가 가지고 있는 야식집 리스틑 정보가 담긴 파일을 받아서 사용자의 현재 버전을 얻는다.
                String clientVersion = clientDocument.getElementsByTagName("version")
                        .item(0)
                        .getFirstChild()
                        .getNodeValue();

                // 방금 얻은 사용자의 버전 정보를 서버 측에 보내어서 업데이트 할지 말지 (change/noChange) 를 묻는다.
                Document doc = parser.getDomElement(parser.getXmlFromUrl(url + "?version=" + clientVersion));
                String vResult = doc.getElementsByTagName("vResult")
                        .item(0)
                        .getFirstChild()
                        .getNodeValue();

                //vChanged 값을 서버에서 받아 온대로 설정
                vChanged = vResult.equals("change") ? true : false;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        // 파일이 없거나 버전 정보가 바뀌었으면
        if (!file.exists() || vChanged) {
            // 폴더 생성
            File path = fileManager.openListPath();
            if(!path.exists()) {
                path.mkdirs();
            }

            try {
                // 파일 생성
                File saveFile = fileManager.openListFile();
                saveFile.createNewFile();

                /* 서버 연결 */
                URL url = new URL(GlobalVariables.SERVER_ADDR + "yasick/getYasickStoreList.jsp");
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                // 연결 참조 변수 선언
                int responseCode = con.getResponseCode();

                // 연결되면 :
                if(responseCode == HttpsURLConnection.HTTP_OK) {
                    // Input Stream 변수 선언
                    is = con.getInputStream();

                    // FileOutputStream 변수 선언
                    fos = new FileOutputStream(saveFile);

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

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
