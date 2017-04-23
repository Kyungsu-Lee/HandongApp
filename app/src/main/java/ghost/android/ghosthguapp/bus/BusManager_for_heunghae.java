package ghost.android.ghosthguapp.bus;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.common.XMLParser;

public class BusManager_for_heunghae {
    private ArrayList<BusData_for_heunghae> busList;
    HashMap<String, ArrayList<BusData_for_heunghae>> map = new HashMap<String, ArrayList<BusData_for_heunghae>>();
    ArrayList<String> tzone = new ArrayList<String>();
    private File file = GlobalVariables.fHh;

    //SD카드에서 식단 받아오기
    public void setting() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        //XML node keys
        final String KEY_ROTARY = "lotari";
        final String KEY_HGU = "HGU";
        final String KEY_GOKGANG = "gokgang";
        final String KEY_HEUNGHAE = "heungHae";
        final String KEY_TIMES = "times";
        final String KEY_TZONE = "tZone";
        final String KEY_TIMESPLIT = "timesplit";

        //List 열기
        busList = new ArrayList<BusData_for_heunghae>();
        XMLParser parser = new XMLParser();
        try {
            //DOM 선언
            Document dom = parser.getDomElementFromFile(file);
            XPath xpath = XPathFactory.newInstance().newXPath();

            //NodeList 선언
            NodeList nl_bus = (NodeList) xpath.evaluate("//Bus", dom, XPathConstants.NODESET);

            if (nl_bus != null && nl_bus.getLength() > 0) {
                for (int z = 0; z < nl_bus.getLength(); z++) {    //Bus의 갯수만큼 반복
                    ArrayList<BusData_for_heunghae> formap = new ArrayList<>();
                    NodeList nl_eachbus = (NodeList) xpath.evaluate("//Bus[" + (z + 1) + "]", dom, XPathConstants.NODESET);

                    Element el_tzone = (Element) nl_eachbus.item(0);
                    String eachtzone = parser.getValue(el_tzone, KEY_TZONE);
                    if (parser.getValue(el_tzone, KEY_TIMESPLIT).equals("pm") && !(eachtzone.equals("12"))) {
                        eachtzone = String.valueOf(Integer.valueOf(eachtzone) + 12);
                        if (parser.getValue(el_tzone, KEY_TIMESPLIT).equals("am") && (eachtzone.equals("12"))) {
                            eachtzone = "24";
                        }
                    }
                    tzone.add(z, eachtzone);

                    BusData_for_heunghae bud = new BusData_for_heunghae();
                    bud.setType(0);
                    bud.setTimes(parser.getValue(el_tzone, KEY_TIMES));
                    bud.setTimesplit(parser.getValue(el_tzone, KEY_TIMESPLIT));
                    bud.setTzone(parser.getValue(el_tzone, KEY_TZONE));
                    formap.add(0, bud);

                    if (nl_eachbus != null && nl_eachbus.getLength() > 0) {
                        for (int i = 0; i < nl_eachbus.getLength(); i++) {
                            Element bus = (Element) nl_eachbus.item(i);
                            BusData_for_heunghae bd = new BusData_for_heunghae();
                            bd.setType(1);
                            bd.setTimesplit(parser.getValue(el_tzone, KEY_TIMESPLIT));
                            bd.setRotary(parser.getValue(bus, KEY_ROTARY));
                            bd.setHgu(parser.getValue(bus, KEY_HGU));
                            bd.setGokgang(parser.getValue(bus, KEY_GOKGANG));
                            bd.setHeunghae(parser.getValue(bus, KEY_HEUNGHAE));

                            busList.add(bd);
                            formap.add(bd);
                        }   // for문 종료
                    }
                    map.put(eachtzone, formap);
                }
            }
        }
        catch (
                NullPointerException e
                )

        {
            e.printStackTrace();
        }

    }

    // 처음 getList
    public ArrayList<BusData_for_heunghae> getList() {
        return busList;
    }

    public HashMap<String, ArrayList<BusData_for_heunghae>> getHashMap() {
        return map;
    }

    public ArrayList<String> getTzone() {
        return tzone;
    }

    //업데이트 or 처음 정보 받아 SD카드에 저장
    public boolean checkNSave() {
        boolean vChanged = true;

        //InputStream 객체생성
        BufferedInputStream is = null;
        //FileOutputStream 객체 생성
        BufferedOutputStream fos = null;

        if (file.exists()) {
            XMLParser parser = new XMLParser();
            Document clientDoc = parser.getDomElementFromFile(file);

            try {
                String clientVersion = clientDoc.getElementsByTagName("version")
                        .item(0)
                        .getFirstChild()
                        .getNodeValue();

                URL Url = new URL(GlobalVariables.SERVER_ADDR + "bus/getBus_HeungHae.jsp");


                // Element 선언
                Document doc = parser.getDomElement(parser.getXmlFromUrl(Url + "?version=" + clientVersion));

                // NodeList 선언
                String vResult = doc.getElementsByTagName("vResult")
                        .item(0)
                        .getFirstChild()
                        .getNodeValue();

                vChanged = vResult.equals("change") ? true : false;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        if (!file.exists() || vChanged) {
            try {
                file.createNewFile();

                // 서버 연결

                URL url = new URL(GlobalVariables.SERVER_ADDR + "bus/getBus_HeungHae.jsp");

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
        }
        return true;
    }
}
