package ghost.android.ghosthguapp.hgushop;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.common.XMLParser;

/**
 * Created by SEC on 2015-01-29.
 */
public class HgushopManager {

    private HgushopFileManager fileManager;
    private ArrayList<HgushopData> shops = new ArrayList<>();

    public HgushopManager(HgushopFileManager manager) {
        fileManager = manager;
    }

    /* SD카드에 저장 되어있는 HGU SHOP 리스트 파일을 받아 파싱하기 */
    public void setting() {

        //XML node keys
        final String KEY_LIST = "hguShopList"; //parent node
        final String KEY_LOCATION = "location";
        final String KEY_CATEGORY = "category";
        final String KEY_SHOPNAME = "name";
        final String KEY_CONTENTS = "contents";
        final String KEY_COORDI = "coordinate";
        final String KEY_IMAGEURL = "url";
        final String KEY_PHONENUMBER = "phone";

        // SD 카드 열기
        File file = fileManager.getHgushopFile();

        // List 열기
        shops = new ArrayList<>();
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
                    HgushopData shop = new HgushopData();
                    Element e = (Element) nodeList.item(i);
                    shop.setLocationIndex(toIntegerIndex(parser.getValue(e, KEY_LOCATION)));
                    shop.setCategoryIndex(toIntegerIndex(parser.getValue(e, KEY_CATEGORY)));
                    shop.setShopName(parser.getValue(e, KEY_SHOPNAME));
                    shop.setContents(parser.getValue(e, KEY_CONTENTS));
                    double[] temp = toDoubleCoordi(parser.getValue(e, KEY_COORDI));
                    shop.setLatitude(temp[0]);
                    shop.setLongitude(temp[1]);
                    shop.setImageUrl(encodeIfNedd(parser.getValue(e, KEY_IMAGEURL)));
                    // 전화번호 있을 때만 번호 저장 아니면 No Phone 으로
                    String phone = parser.getValue(e, KEY_PHONENUMBER);
                    if(phone == null || phone.equals("null") || phone.equals("-"))
                        shop.setPhoneNumber("No Phone");
                    else
                        shop.setPhoneNumber(parser.getValue(e, KEY_PHONENUMBER));

                    shops.add(shop);
                }
            }
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
    }

    /* 파일의 버전체크를 하고 서버에서 새 버전을 받아오는 메소드 */
    public boolean checkNSaveShops() {
        boolean vChanged = true;

        // InputStream 객체 생성
        BufferedInputStream is = null;
        // FileOutputStream 객체 생성
        BufferedOutputStream fos = null;

        // 파일 얻기
        File file = fileManager.getHgushopFile();

        // 파일이 존재하면 : 버전 정보를 서버로부터 받아온다.
        if(file.exists()) {
            XMLParser parser = new XMLParser();
            Document clientDocument = parser.getDomElementFromFile(file);
            try {
                URL url = new URL(GlobalVariables.SERVER_ADDR + "getHguShop.jsp");

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
            /* 다운로드 */
            try {
                // 파일 새로 생성
                File saveFile = fileManager.openHgushopFile();
                saveFile.createNewFile();

                /* 서버 연결 */
                URL url = new URL(GlobalVariables.SERVER_ADDR + "getHguShop.jsp");
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                // 연결 참조 변수 선언
                int responseCode = con.getResponseCode();

                // 연결되면 :
                if(responseCode == HttpsURLConnection.HTTP_OK) {
                    // Input Stream 변수 선언
                    is = new BufferedInputStream(con.getInputStream());

                    // FileOutputStream 변수 선언
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
        }
        return true;
    }

    /* HGU SHOP 리스트를 바깥으로 전달해주는 메소드 */
    public ArrayList<HgushopData> getShops() { return shops; }

    /* 받아온 값을 적절한 int 인덱스로 바꿔주는 메소드 */
    private int toIntegerIndex(String str) {
        int num = 1;

        try {
            // Integer 값으로
            num = Integer.parseInt(str);
            // 1 감소 (실제 인덱스는 0부터인데 서버에서는 1부터이다)는 하지 않는다. 왜냐하면 대신 0에는 '전체' 가 들어가야 하기 때문이다


        } catch(NumberFormatException e) {
           Log.e("NumberFormatException", "HgushopManager 의 toIntegerIndex의 Integer.parseInt 부분");
        }

        return num;
    }

    /* 받아온 좌표 string 을 각각 double 형의 위도, 경도로 바꿔주는 메소드 */
    private double[] toDoubleCoordi(String str) {
        double[] coordi = {36.0796224, 129.3973618};
        try {

            String[] temp = str.split(",");

            if (temp.length == 2) {
                temp[0] = temp[0].trim();
                temp[1] = temp[1].trim();

                coordi[0] = Double.parseDouble(temp[0]);
                coordi[1] = Double.parseDouble(temp[1]);
            }
        } catch(NumberFormatException e) {
            Log.e("NumberFormatException", "HgushopManager 의 toDoubleCoordi의 Double.parseDouble 부분");
        }
        return coordi;
    }

    private String encodeIfNedd(String input) {
        Pattern KOREAN_PATTERN = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]");

        if(input.isEmpty())
            return input;

        Matcher matcher = KOREAN_PATTERN.matcher(input);
        while(matcher.find()) {
            String group = matcher.group();

            try {
                input = input.replace(group, URLEncoder.encode(group, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return input.replace(" ", "%20");       // 공백도 인코딩해주어야 한다

    }
}
