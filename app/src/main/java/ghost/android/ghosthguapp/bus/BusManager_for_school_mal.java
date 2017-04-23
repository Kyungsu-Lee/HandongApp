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

public class BusManager_for_school_mal {
    private ArrayList<BusData> busList;
    private File file = GlobalVariables.fSchMal;
    HashMap<String, ArrayList<BusData>> map = new HashMap<String, ArrayList<BusData>>();
    ArrayList<String> tzone = new ArrayList<String>();

    //SD카드에서 식단 받아오기
    public void setting() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        //XML node keys
        final String KEY_SCHOOL = "school";
        final String KEY_HWAN = "hwan";
        final String KEY_SIX = "six";
        final String KEY_TZONE = "tzone";
        final String KEY_TIMESPLIT = "timesplit";

        //List 열기
        busList = new ArrayList<BusData>();

        XMLParser parser = new XMLParser();

        try {
            //DOM 선언
            Document dom = parser.getDomElementFromFile(file);
            XPath xpath = XPathFactory.newInstance().newXPath();

            //NodeList 선언
            NodeList nl_tzone = (NodeList) xpath.evaluate("//tZone", dom, XPathConstants.NODESET);

            if (nl_tzone != null && nl_tzone.getLength() > 0) {
                for (int z = 0; z < nl_tzone.getLength(); z++) {    //tZone의 갯수만큼 반복
                    ArrayList<BusData> formap = new ArrayList<>();
                    NodeList node_tzone = (NodeList) xpath.evaluate("//tZone[" + (z + 1) + "]", dom, XPathConstants.NODESET);

                    Element el_tzone = (Element) node_tzone.item(0);
                    String eachtzone = parser.getValue(el_tzone, KEY_TZONE);
                    if (parser.getValue(el_tzone, KEY_TIMESPLIT).equals("pm") && !(eachtzone.equals("12")))
                        eachtzone = String.valueOf(Integer.valueOf(eachtzone) + 12);
                    else if (parser.getValue(el_tzone, KEY_TIMESPLIT).equals("am") && (eachtzone.equals("12")))
                        eachtzone = String.valueOf(24);
                    tzone.add(z, eachtzone);

                    BusData bud = new BusData();
                    bud.setType(0);
                    bud.setTimesplit(parser.getValue(el_tzone, KEY_TIMESPLIT));
                    bud.setTzone(parser.getValue(el_tzone, KEY_TZONE));
                    formap.add(0, bud);

                    NodeList nl_bus = (NodeList) xpath.evaluate("//tZone[" + (z + 1) + "]/Bus", dom, XPathConstants.NODESET);

                    if (nl_bus != null && nl_bus.getLength() > 0) {
                        for (int i = 0; i < nl_bus.getLength(); i++) {
                            Element bus = (Element) nl_bus.item(i);
                            Element time = (Element) node_tzone.item(0);
                            BusData bd = new BusData();
                            bd.setType(1);
                            bd.setSchool(parser.getValue(bus, KEY_SCHOOL));
                            bd.setHwan(parser.getValue(bus, KEY_HWAN));
                            bd.setSix(parser.getValue(bus, KEY_SIX));
                            bd.setTimesplit(parser.getValue(el_tzone, KEY_TIMESPLIT));
                            bd.setTzone(parser.getValue(el_tzone, KEY_TZONE));

                            parser.getValue(time, KEY_TIMESPLIT);
                            busList.add(bd);
                            formap.add(bd);
                        }   // for문 종료
                    }
                    map.put(eachtzone, formap);
                }
            }
        }   //전체 for문 종료
        catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    // 처음 getList
    public ArrayList<BusData> getList() {
        return busList;
    }

    public HashMap<String, ArrayList<BusData>> getHashMap() {
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

                URL Url = new URL(GlobalVariables.SERVER_ADDR + "bus/getBus_Weekend(toSchool).jsp");


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
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        if (!file.exists() || vChanged) {
            try {
                file.createNewFile();

                // 서버 연결

                URL url = new URL(GlobalVariables.SERVER_ADDR + "bus/getBus_Weekend(toSchool).jsp");

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
