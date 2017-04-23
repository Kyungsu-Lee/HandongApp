package ghost.android.ghosthguapp.yasick;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.common.XMLParser;

/**
 * Created by SEC on 2015-01-07.
 */

public class YasickStoresEachManager {

    private ArrayList<MainMenuItem> mainMenuItems;
    private ArrayList<AllMenuItem> allMenuItems;
    private StoreInfo storeInfo;
    private String catalogURL;
    private StoresFileManager fileManager;

    /* 생성할 때, 파일 매니저 객체를 받아 온다 */
    public YasickStoresEachManager(StoresFileManager manager) {
        fileManager = manager;
    }

    /* SD카드에 저장 되어있는 야식집 리스트 파일을 받아 파싱하기 */
    public void setting(String storeId) {

        //XML node keys
        final String KEY_HOLIDAY = "holiday";
        final String KEY_RUNTIME = "runTime";
        final String KEY_SPECIAL = "special";
        final String KEY_LIST_MAINMENU = "mainMenu";    //parent node
        final String KEY_MAIN_PHOTO = "photo";
        final String KEY_MAIN_NAME = "name";
        final String KEY_MAIN_PRICE = "price";
        final String KEY_LIST_ALLMENU = "allMenu";      //parent node
        final String KEY_ALL_NAME = "name";
        final String KEY_ALL_PRICE = "price";
        final String KEY_ALL_SETINFO = "set";
        final String KEY_CATALOG = "catalog";

        // 업체 페이지에 대한 파일 얻기
        File file = fileManager.getStorePageFile();

        // List 생성
        mainMenuItems = new ArrayList<>();
        allMenuItems = new ArrayList<>();

        // XML 파서 생성
        XMLParser parser = new XMLParser();

        try {
            // DOM 선언
            Document dom = parser.getDomElementFromFile(file);
            // Element 선언
            Element docElement = dom.getDocumentElement();


            /* 업체정보 (StoreInfo) 받아오기 */
            storeInfo = new StoreInfo();
            storeInfo.holiday = dom.getElementsByTagName(KEY_HOLIDAY)       // NodeList 때는 Element 타입에 두고 했지만 이건 Document 타입에다 대고서 한다
                    .item(0)
                    .getFirstChild()
                    .getNodeValue()
                    .replace("\\n", "\n");
            storeInfo.runTime = dom.getElementsByTagName(KEY_RUNTIME)
                    .item(0)
                    .getFirstChild()
                    .getNodeValue()
                    .replace("\\n", "\n");
            storeInfo.special = dom.getElementsByTagName(KEY_SPECIAL)
                    .item(0)
                    .getFirstChild()
                    .getNodeValue()
                    .replace("\\n", "\n");

            /* 메인 메뉴 받아오기 */
            // NodeList 선언  (KEY_LIST_MAINMENU 라는 이름의 태그를 가진 노드들의 리스트)
            NodeList nodeList = docElement.getElementsByTagName(KEY_LIST_MAINMENU);

            // NodeList 가 정보를 받아오면
            if (nodeList != null && nodeList.getLength() > 0) {
                // NodeList 에서 받은 정보를 파싱
                for (int i = 0; i < nodeList.getLength(); i++) {
                    MainMenuItem menuItem = new MainMenuItem();
                    Element menu = (Element) nodeList.item(i);
                    menuItem.setMenuImageURL(parser.getValue(menu, KEY_MAIN_PHOTO));
                    menuItem.setMenuName(parser.getValue(menu, KEY_MAIN_NAME));
                    menuItem.setPrice(parser.getValue(menu, KEY_MAIN_PRICE));

                    mainMenuItems.add(menuItem);
                }
            }

            /* ALL메뉴 받아오기 */
            // NodeList 선언 (KEY_LIST_ALLMENU 라는 이름의 태그를 가진 노드들의 리스트)
            NodeList nodeList1 = docElement.getElementsByTagName(KEY_LIST_ALLMENU);

            // NodeList 가 정보를 받아오면
            if (nodeList1 != null && nodeList1.getLength() > 0) {
                // NodeList 에서 받은 정보를 파싱
                for (int i = 0; i < nodeList1.getLength(); i++) {
                    AllMenuItem menuItem = new AllMenuItem();
                    Element menu = (Element) nodeList1.item(i);
                    menuItem.setMenuName(parser.getValue(menu, KEY_ALL_NAME));
                    menuItem.setPrice(parser.getValue(menu, KEY_ALL_PRICE));
                    menuItem.setSetInfo(parser.getValue(menu, KEY_ALL_SETINFO));

                    allMenuItems.add(menuItem);
                }
            }

            /* catalog URL 받아오기 */
            catalogURL = dom.getElementsByTagName(KEY_CATALOG)
                    .item(0)
                    .getFirstChild()
                    .getNodeValue();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /* get methods */
    public ArrayList<MainMenuItem> getMainMenuItems() {
        return mainMenuItems;
    }

    public ArrayList<AllMenuItem> getAllMenuItems() {
        return allMenuItems;
    }

    public StoreInfo getStoreInfo() {
        return storeInfo;
    }

    public String getCatalogURL() {
        return catalogURL;
    }

    /* 업체 페이지와 필요한 사진들을 업데이트 or 처음 정보 받아서 SD 카드에 저장 (카탈로그, 메인메뉴 사진들) */
    public boolean checkNSave_PageNImages(String storeId) {

        boolean vChanged = true;

        // InputStream 객체 생성
        BufferedInputStream is = null;
        // FileOutputStream 객체 생성
        BufferedOutputStream fos = null;

        // SD 카드 열기
        File file = fileManager.getStorePageFile();
        File catalogFile = fileManager.openCatalogFile(storeId);
        File menusPath = fileManager.openMenusDir(storeId);

        // 업체 페이지, 카탈로그이미지, 메뉴이미지들의 디렉토리 이 세 가지가 모두 존재하면 : 버전 정보를 서버로부터 받아온다.
        if (file.exists() && catalogFile.exists() && menusPath.exists()) {
            XMLParser parser = new XMLParser();
            Document clientDocument = parser.getDomElementFromFile(file);
            try {
                // 해당 야식 업체 페이지에서 버전을 관리하므로 업체 페이지 URL 생성
                URL url = new URL(GlobalVariables.SERVER_ADDR + "yasick/get" + storeId + ".jsp");

                // 현재 사용자가 가지고 있는 야식집 리스트 정보가 담긴 파일을 받아서 사용자의 현재 버전을 얻는다.
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

                // vChanged 값을 서버에서 받아 온대로 설정
                vChanged = vResult.equals("change") ? true : false;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        // 셋 중에 한 개라도 없거나, 다 있더라도 버전이 바뀌었으면 : 셋 다 다운로드
        if (!file.exists() || !catalogFile.exists() || !menusPath.exists() || vChanged) {
            // 폴더 생성
            File path = fileManager.openMenusDir(storeId);
            if (!path.exists()) {
                path.mkdirs();
            }

            /* 업체 페이지 다운로드 */
            try {
                // 파일 생성
                File saveFile = fileManager.getStorePageFile();
                saveFile.createNewFile();

                /* 서버 연결 */
                URL url = new URL(GlobalVariables.SERVER_ADDR + "yasick/get" + storeId + ".jsp");
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                // 연결 참조 변수 선언
                int responseCode = con.getResponseCode();

                // 연결되면 :
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    // Input Stream 생성
                    is = new BufferedInputStream(con.getInputStream());

                    // FileOutputStream 생성
                    fos = new BufferedOutputStream(new FileOutputStream(saveFile));

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

            /* 파싱해서 멤버 변수들 업데이트 or 초기화 (이미지 URL 들 때문에 다시 파싱해줘야 함) */
            setting(storeId);

            /* 카탈로그 이미지 다운로드 */
            try {
                // 파일 생성
                File saveFile = fileManager.openCatalogFile(storeId);
                saveFile.createNewFile();

                // 카탈로그 이미지 URL 에서 비트맵으로 다운로드. 다운받은 비트맵을 SD 카드에 저장
                downloadNwrite(catalogURL, saveFile);

            } catch (IOException e) {
                e.printStackTrace();
            }

            /* 메인 메뉴 이미지들 다운로드 */
            try {
                // 메인 메뉴 갯수 만큼 for 문을 돌린다
                for(int i = 0; i < getMainMenuItems().size(); i++) {
                    // i 번째 메뉴 이미지에 대한 파일 생성
                    File saveFile = fileManager.openEachMenuFile(storeId, i);
                    saveFile.createNewFile();

                    // 이미지 URL 에서 비트맵으로 다운로드. 다운받은 비트맵을 SD 카드에 저장
                    downloadNwrite(getMainMenuItems().get(i).getMenuImageURL(), saveFile);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    /* httpClient 이미지 파일을 Bitmap 으로 다운로드 후, 파일에 JPEG 형식으로 쓴다. */
    private void downloadNwrite(String imageUrl, File file) {

        /* 다운로드 */
        Bitmap bmp = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet(imageUrl);
        HttpResponse response;
        try {
            response = httpClient.execute(getRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("egg", "Error " + statusCode + " while retrieving bitmap from " + imageUrl);
                return;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                BufferedInputStream inputStream = null;
                try {
                    inputStream = new BufferedInputStream(entity.getContent());
                    bmp = BitmapFactory.decodeStream(inputStream);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (ClientProtocolException e) { e.printStackTrace();
        } catch (IOException e) { e.printStackTrace();
        }

        /* 파일 쓰기 */
        BufferedOutputStream out = null;
        try {
            out =new BufferedOutputStream(new FileOutputStream(file));
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 스트림 닫기
                if (out != null)
                    out.close();

                // 메모리 절약 위해서 비트맵 recycle
                bmp.recycle();
                bmp = null;
            } catch (Exception ex) {
            }
        }
    }
}