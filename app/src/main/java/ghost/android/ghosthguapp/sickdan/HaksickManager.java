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

import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.common.XMLParser;

public class HaksickManager {
    private ArrayList<HaksickData> menuList;
    private ArrayList<HaksickData> nullList;
    private File file = GlobalVariables.fHaksick;
    int non = 0;

    //SD카드에서 식단 받아오기
    public void setting() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        nullList = new ArrayList<HaksickData>();
        HaksickData nullData = new HaksickData();
        nullData.setDate("");
        nullData.setType(1);
        nullData.setMenu("받아올 식단 정보가 없습니다.\n(학생식당 휴무 여부를 확인해주세요)");
        nullData.setPrice("");
        nullList.add(0, nullData);

        //XML node keys
        final String KEY_DATE = "date";
        final String KEY_MENU = "menu";
        final String KEY_PRICE = "price";

        //List 열기
        //menuList = new ArrayList<HaksickData>();
        XMLParser parser = new XMLParser();

        try {
            //DOM 선언
            Document dom = parser.getDomElementFromFile(file);
            XPath xpath = XPathFactory.newInstance().newXPath();

            //NodeList 선언
            NodeList nl_kotebre = (NodeList)xpath.evaluate("/haksik/kotebre[1]", dom, XPathConstants.NODESET);
            NodeList nl_date = (NodeList)xpath.evaluate("/haksik[1]", dom, XPathConstants.NODESET);
            NodeList nl_kotelun = (NodeList)xpath.evaluate("/haksik/kotelun[1]", dom, XPathConstants.NODESET);
            NodeList nl_kotedin = (NodeList)xpath.evaluate("/haksik/kotedin[1]", dom, XPathConstants.NODESET);
            NodeList nl_fryfry = (NodeList)xpath.evaluate("/haksik/fryfry[1]", dom, XPathConstants.NODESET);
            NodeList nl_noodle = (NodeList)xpath.evaluate("/haksik/noodle[1]", dom, XPathConstants.NODESET);
            NodeList nl_hao = (NodeList)xpath.evaluate("/haksik/hao[1]", dom, XPathConstants.NODESET);
            NodeList nl_grace = (NodeList)xpath.evaluate("/haksik/grace[1]", dom, XPathConstants.NODESET);
            NodeList nl_mix = (NodeList)xpath.evaluate("/haksik/mix[1]", dom, XPathConstants.NODESET);

            menuList = new ArrayList<HaksickData>();

            Element date = (Element) nl_date.item(0);
            HaksickData haksickkt = new HaksickData();
            haksickkt.setCorner("Korean Table");
            haksickkt.setType(0);
            if(parser.getValue(date, KEY_DATE).equals("")){
                non++;}
            else
                haksickkt.setDate(parser.getValue(date, KEY_DATE));
            menuList.add(0,haksickkt);


            HaksickData haksickktmorn = new HaksickData();
            haksickktmorn.setCorner("아침");
            haksickktmorn.setType(2);
            menuList.add(haksickktmorn);

            //NodeList가 정보를 받아오면
            if (non == 0 && nl_kotebre != null && nl_kotebre.getLength() > 0) {
                //NodeList에서 받은 정보 infoList에 뿌리기
                for (int i = 0; i < nl_kotebre.getLength(); i++) {
                    Element kotebre = (Element) nl_kotebre.item(i);
                    HaksickData haksick = new HaksickData();
                    haksick.setType(1);
                    if (parser.getValue(kotebre, KEY_MENU).equals("-") || parser.getValue(kotebre, KEY_MENU).equals("")){
                        haksick.setMenu("");
                        haksick.setPrice("");}
                    else{
                        haksick.setMenu(parser.getValue(kotebre, KEY_MENU));
                        haksick.setPrice(parser.getValue(kotebre, KEY_PRICE));
                    }
                    menuList.add(haksick);
                    //for 종료
                }
            }

            HaksickData haksickktlun = new HaksickData();
            haksickktlun.setCorner("점심");
            haksickktlun.setType(2);
            menuList.add(haksickktlun);

            //NodeList가 정보를 받아오면
            if (nl_kotelun != null && nl_kotelun.getLength() > 0) {
                //NodeList에서 받은 정보 infoList에 뿌리기
                for (int i = 0; i < nl_kotelun.getLength(); i++) {
                    Element kotelun = (Element) nl_kotelun.item(i);
                    HaksickData haksick = new HaksickData();
                    haksick.setType(1);
                    if (parser.getValue(kotelun, KEY_MENU).equals("-") || parser.getValue(kotelun, KEY_MENU).equals("")){
                        haksick.setMenu("");
                        haksick.setPrice("");}
                    else{
                        haksick.setMenu(parser.getValue(kotelun, KEY_MENU));
                        haksick.setPrice(parser.getValue(kotelun, KEY_PRICE));
                    }
                    menuList.add(haksick);
                    //for 종료
                }
            }

            HaksickData haksickktdin = new HaksickData();
            haksickktdin.setCorner("저녁");
            haksickktdin.setType(2);
            menuList.add(haksickktdin);

            //NodeList가 정보를 받아오면
            if (nl_kotedin != null && nl_kotedin.getLength() > 0) {
                //NodeList에서 받은 정보 infoList에 뿌리기
                for (int i = 0; i < nl_kotedin.getLength(); i++) {
                    Element kotedin = (Element) nl_kotedin.item(i);
                    HaksickData haksick = new HaksickData();
                    haksick.setType(1);
                    if (parser.getValue(kotedin, KEY_MENU).equals("-") || parser.getValue(kotedin, KEY_MENU).equals("")){
                        haksick.setMenu("");
                        haksick.setPrice("");}
                    else{
                        haksick.setMenu(parser.getValue(kotedin, KEY_MENU));
                        haksick.setPrice(parser.getValue(kotedin, KEY_PRICE));
                    }
                    menuList.add(haksick);
                    //for 종료
                }
            }

            HaksickData haksickline = new HaksickData();
            haksickline.setType(3);
            menuList.add(haksickline);

            HaksickData haksickfryfry = new HaksickData();
            haksickfryfry.setCorner("Fry Fry");
            haksickfryfry.setType(0);
            menuList.add(haksickfryfry);

            //NodeList가 정보를 받아오면
            if (nl_fryfry != null && nl_fryfry.getLength() > 0) {
                //NodeList에서 받은 정보 infoList에 뿌리기
                for (int i = 0; i < nl_fryfry.getLength(); i++) {
                    Element fryfry = (Element) nl_fryfry.item(i);
                    HaksickData haksick = new HaksickData();
                    haksick.setType(1);
                    if (parser.getValue(fryfry, KEY_MENU).equals("-")){
                        haksick.setMenu("");
                        haksick.setPrice("");}
                    else{
                        haksick.setMenu(parser.getValue(fryfry, KEY_MENU));
                        haksick.setPrice(parser.getValue(fryfry, KEY_PRICE));
                    }
                    menuList.add(haksick);
                    //for 종료
                }
            }
            haksickline.setType(3);
            menuList.add(haksickline);

            HaksickData haksicknoodle = new HaksickData();
            haksicknoodle.setCorner("Noodle Road");
            haksicknoodle.setType(0);
            menuList.add(haksicknoodle);


            //NodeList가 정보를 받아오면
            if (nl_noodle != null && nl_noodle.getLength() > 0) {
                //NodeList에서 받은 정보 infoList에 뿌리기
                for (int i = 0; i < nl_noodle.getLength(); i++) {
                    Element noodle = (Element) nl_noodle.item(i);
                    HaksickData haksick = new HaksickData();
                    haksick.setType(1);
                    if (parser.getValue(noodle, KEY_MENU).equals("-")){
                        haksick.setMenu("");
                        haksick.setPrice("");}
                    else{
                        haksick.setMenu(parser.getValue(noodle, KEY_MENU));
                        haksick.setPrice(parser.getValue(noodle, KEY_PRICE));
                    }
                    menuList.add(haksick);
                    //for 종료
                }
            }
            haksickline.setType(3);
            menuList.add(haksickline);

            HaksickData haksickhao = new HaksickData();
            haksickhao.setCorner("Hao");
            haksickhao.setType(0);
            menuList.add(haksickhao);


            //NodeList가 정보를 받아오면
            if (nl_hao != null && nl_hao.getLength() > 0) {
                //NodeList에서 받은 정보 infoList에 뿌리기
                for (int i = 0; i < nl_hao.getLength(); i++) {
                    Element hao = (Element) nl_hao.item(i);
                    HaksickData haksick = new HaksickData();
                    haksick.setType(1);
                    if (parser.getValue(hao, KEY_MENU).equals("-")){
                        haksick.setMenu("");
                        haksick.setPrice("");}
                    else{
                        haksick.setMenu(parser.getValue(hao, KEY_MENU));
                        haksick.setPrice(parser.getValue(hao, KEY_PRICE));
                    }
                    menuList.add(haksick);
                    //for 종료
                }
            }
            haksickline.setType(3);
            menuList.add(haksickline);

            HaksickData haksickgrace = new HaksickData();
            haksickgrace.setCorner("Grace Garden");
            haksickgrace.setType(0);
            menuList.add(haksickgrace);


            //NodeList가 정보를 받아오면
            if (nl_grace != null && nl_grace.getLength() > 0) {
                //NodeList에서 받은 정보 infoList에 뿌리기
                for (int i = 0; i < nl_grace.getLength(); i++) {
                    Element grace = (Element) nl_grace.item(i);
                    HaksickData haksick = new HaksickData();
                    haksick.setType(1);
                    if (parser.getValue(grace, KEY_MENU).equals("-")){
                        haksick.setMenu("");
                        haksick.setPrice("");}
                    else{
                        haksick.setMenu(parser.getValue(grace, KEY_MENU));
                        haksick.setPrice(parser.getValue(grace, KEY_PRICE));
                    }
                    menuList.add(haksick);
                    //for 종료
                }
            }
            haksickline.setType(3);
            menuList.add(haksickline);

            HaksickData haksickmix = new HaksickData();
            haksickmix.setCorner("Mix Rice");
            haksickmix.setType(0);
            menuList.add(haksickmix);


            //NodeList가 정보를 받아오면
            if (nl_mix != null && nl_mix.getLength() > 0) {
                //NodeList에서 받은 정보 infoList에 뿌리기
                for (int i = 0; i < nl_mix.getLength(); i++) {
                    Element mix = (Element) nl_mix.item(i);
                    HaksickData haksick = new HaksickData();
                    haksick.setType(1);
                    if (parser.getValue(mix, KEY_MENU).equals("-")){
                        haksick.setMenu("");
                        haksick.setPrice("");}
                    else{
                        haksick.setMenu(parser.getValue(mix, KEY_MENU));
                        haksick.setPrice(parser.getValue(mix, KEY_PRICE));
                    }
                    menuList.add(haksick);
                    //for 종료
                }
            }

            haksickline.setType(3);
            menuList.add(haksickline);
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    // 처음 getList
    public ArrayList<HaksickData> getList(){
        if (non == 1){
            return  nullList;}
        return menuList;
    }

    //업데이트 or 처음 정보 받아 SD카드에 저장
    public boolean saveAtSDcard(){
        //InputStream 객체생성
        BufferedInputStream is = null;
        //FileOutputStream 객체 생성
        BufferedOutputStream fos = null;

        try{
            file.createNewFile();

            // 서버 연결
            URL stuUrl = new URL(GlobalVariables.SERVER_ADDR+"getHaksik.jsp");
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