package ghost.android.ghosthguapp.sickdan;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import ghost.android.ghosthguapp.common.GlobalMethods;
import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.common.XMLParser;

public class HyoamManager {
    private ArrayList<HyoamData> menuList;
    private ArrayList<HyoamData> nullList;
    private ArrayList<ArrayList<HyoamData>> arrayarray;
    private ArrayList<ArrayList<HyoamData>> nullarray;
    private File file = GlobalVariables.fHyoam;
    int i, counter = 0;

    //SD카드에서 식단 받아오기
    public void setting() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        //보낼 것이 없을때 보여줄 NullList 생성
        nullList = new ArrayList<HyoamData>();
        HyoamData nullData = new HyoamData();
        nullarray = new ArrayList<ArrayList<HyoamData>>();
        nullData.setType(1);
        nullData.setDate("날짜 정보 없음");
        nullData.setName("받아올 식단 정보가 없습니다.\n(또랑 휴무 여부를 확인해주세요)");
        nullData.setPrice("");
        nullList.add(0, nullData);
        nullarray.add(0, nullList);

        //XML node keys
        final String KEY_DATE = "date";
        final String KEY_NAME = "name";
        final String KEY_PRICE = "price";

        //List 열기
        menuList = new ArrayList<HyoamData>();
        arrayarray = new ArrayList<ArrayList<HyoamData>>();
        arrayarray.add(0, nullList);
        arrayarray.add(1, nullList);
        arrayarray.add(2, nullList);
        arrayarray.add(3, nullList);
        arrayarray.add(4, nullList);
        arrayarray.add(5, nullList);
        arrayarray.add(6, nullList);

        XMLParser parser = new XMLParser();

        //한주의 날짜를 저장하는 배열
        String rangeDateOfWeek[] = new String[7];
        try {
            rangeDateOfWeek = GlobalMethods.weekCalendar("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try{
            //DOM 선언
            Document dom = parser.getDomElementFromFile(file);
            XPath xpath = XPathFactory.newInstance().newXPath();

            //NodeList 선언
            NodeList nl_menu = (NodeList) xpath.evaluate("//menu", dom, XPathConstants.NODESET);

            if (nl_menu != null && nl_menu.getLength() > 0) {
                for (int z = 0; z < nl_menu.getLength(); z++) {
                    menuList = new ArrayList<HyoamData>();

                    NodeList nl_special = (NodeList) xpath.evaluate("/hyoam/menu[" + (z + 1) + "]/special/unit", dom, XPathConstants.NODESET);
                    NodeList nl_normal = (NodeList) xpath.evaluate("/hyoam/menu[" + (z + 1) + "]/normal/unit", dom, XPathConstants.NODESET);
                    NodeList nl_date = (NodeList) xpath.evaluate("/hyoam/menu[" + (z + 1) + "]", dom, XPathConstants.NODESET);

                    Element date = (Element) nl_date.item(0);
                    HyoamData hyoamts = new HyoamData();
                    hyoamts.setDate(parser.getValue(date, KEY_DATE));
                    hyoamts.setCorner("특선");
                    hyoamts.setType(0);
                    menuList.add(0, hyoamts);

                    //NodeList가 정보를 받아오면
                    if (nl_special != null && nl_special.getLength() > 0) {
                        //NodeList에서 받은 정보 infoList에 뿌리기
                        for (i = 0; i < nl_special.getLength(); i++) {
                            Element special = (Element) nl_special.item(i);
                            HyoamData hyoam = new HyoamData();
                            hyoam.setType(1);
                            hyoam.setName(parser.getValue(special, KEY_NAME));
                            hyoam.setPrice(parser.getValue(special, KEY_PRICE));

                            menuList.add(hyoam);
                            //for 종료
                        }
                    }

                    HyoamData hyoamline = new HyoamData();
                    hyoamline.setType(2);
                    menuList.add(hyoamline);

                    HyoamData hyoamib = new HyoamData();
                    hyoamib.setCorner("일반메뉴");
                    hyoamib.setType(0);
                    menuList.add(hyoamib);

                    //NodeList가 정보를 받아오면
                    if (nl_normal != null && nl_normal.getLength() > 0) {
                        //NodeList에서 받은 정보 infoList에 뿌리기
                        for (i = 0; i < nl_normal.getLength(); i++) {
                            Element normal = (Element) nl_normal.item(i);
                            HyoamData hyoam = new HyoamData();
                            hyoam.setType(1);
                            hyoam.setName(parser.getValue(normal, KEY_NAME));
                            hyoam.setPrice(parser.getValue(normal, KEY_PRICE));
                            menuList.add(hyoam);
                        }
                    }

                    hyoamline.setType(2);
                    menuList.add(hyoamline);

                    if (parser.getValue(date, KEY_DATE).substring(4, 6).equals(rangeDateOfWeek[0].substring(4, 6))) {
                        arrayarray.add(0, menuList);
                        continue;
                    }

                    if (parser.getValue(date, KEY_DATE).substring(4, 6).equals(rangeDateOfWeek[1].substring(4, 6))) {
                        arrayarray.add(1, menuList);
                        continue;
                    }

                    if (parser.getValue(date, KEY_DATE).substring(4, 6).equals(rangeDateOfWeek[2].substring(4, 6))) {
                        arrayarray.add(2, menuList);
                        continue;
                    }

                    if (parser.getValue(date, KEY_DATE).substring(4, 6).equals(rangeDateOfWeek[3].substring(4, 6))) {
                        arrayarray.add(3, menuList);
                        continue;
                    }

                    if (parser.getValue(date, KEY_DATE).substring(4, 6).equals(rangeDateOfWeek[4].substring(4, 6))) {
                        arrayarray.add(4, menuList);
                        continue;
                    }

                    if (parser.getValue(date, KEY_DATE).substring(4, 6).equals(rangeDateOfWeek[5].substring(4, 6))) {
                        arrayarray.add(5, menuList);
                        continue;
                    }

                    if (parser.getValue(date, KEY_DATE).substring(4, 6).equals(rangeDateOfWeek[6].substring(4, 6))) {
                        arrayarray.add(6, menuList);
                        continue;
                    }

                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    // 처음 getList
    public ArrayList<HyoamData> getList(int i) {
        if (arrayarray.size() == 0)
            return nullarray.get(0);
        try {
            return arrayarray.get(i);
        } catch (IndexOutOfBoundsException obe) {
            obe.printStackTrace();
        }
        return nullarray.get(0);
    }

    public String getStringDayOfWeek(int i){
        String rangeDateOfWeek[] = new String[7];
        try {
            rangeDateOfWeek = GlobalMethods.weekCalendar("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rangeDateOfWeek[i];
    }

    //업데이트 or 처음 정보 받아 SD카드에 저장
    public boolean saveAtSDcard() {
        //InputStream 객체생성
        BufferedInputStream is = null;
        //FileOutputStream 객체 생성
        BufferedOutputStream fos = null;

        try {
            file.createNewFile();

            // 서버 연결

            URL stuUrl = new URL(GlobalVariables.SERVER_ADDR + "getHyoam.jsp");
            HttpURLConnection con = (HttpURLConnection) stuUrl.openConnection();
            // 연결 참조 변수 선언
            int responseCode = con.getResponseCode();

            // 연결되면
            if (responseCode == HttpURLConnection.HTTP_OK) {
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
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}


