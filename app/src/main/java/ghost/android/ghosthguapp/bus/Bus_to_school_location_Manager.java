package ghost.android.ghosthguapp.bus;

import android.os.StrictMode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import ghost.android.ghosthguapp.common.XMLParser;

public class Bus_to_school_location_Manager {
    private ArrayList<Bus_to_school_location_Data> locationList;
    public void setting() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        if(android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //XML node keys
        final String KEY_WAYS = "Ways";
        final String KEY_STARTNAME = "Startname";
        final String KEY_ENDNAME = "Endname";
        final String KEY_CARNO = "CarNo";
        final String KEY_SHELTERNO = "ShelterNo";
        final String KEY_OVER = "Over";
        final String KEY_TURNFLAG = "TurnFlag";
        final String KEY_LATITUDE = "Latitude";
        final String KEY_LONGITUDE = "Longitude";
        final String KEY_XMLTIME = "XmlTime";

        //List 열기
        locationList = new ArrayList<Bus_to_school_location_Data>();
        XMLParser parser = new XMLParser();

        try {
            //DOM 선언
            String xml = parser.getXmlFromUrl("http://118.41.84.132/bis/mobilegw/appAllBusPosition.php");
            Document dom = parser.getDomElement(xml);
            XPath xpath = XPathFactory.newInstance().newXPath();

            //NodeList 선언
            NodeList nl_itemss = (NodeList) xpath.evaluate("//items", dom, XPathConstants.NODESET);

            if(nl_itemss != null && nl_itemss.getLength()>0){
                for(int i = 0; i < nl_itemss.getLength(); i++){
                    Bus_to_school_location_Data Bld = new Bus_to_school_location_Data();
                    NodeList nl_items = (NodeList) xpath.evaluate("//items[" + (i + 1) + "]", dom, XPathConstants.NODESET);

                    Element el_loc = (Element) nl_items.item(0);
                    Bld.setWays(parser.getValue(el_loc, KEY_WAYS));
                    Bld.setStartname(parser.getValue(el_loc, KEY_STARTNAME));
                    Bld.setEndname(parser.getValue(el_loc, KEY_ENDNAME));
                    Bld.setCarNo(parser.getValue(el_loc, KEY_CARNO));
                    Bld.setShelterNo(parser.getValue(el_loc, KEY_SHELTERNO));
                    Bld.setOver(parser.getValue(el_loc, KEY_OVER));
                    Bld.setTrunFlag(parser.getValue(el_loc, KEY_TURNFLAG));
                    Bld.setLatitude(parser.getValue(el_loc, KEY_LATITUDE));
                    Bld.setLongitude(parser.getValue(el_loc, KEY_LONGITUDE));
                    Bld.setXmlTime(parser.getValue(el_loc, KEY_XMLTIME));

                    locationList.add(Bld);
                }
            }

        } catch (
                NullPointerException e
                )

        {
            e.printStackTrace();
        }
    }

    public ArrayList<Bus_to_school_location_Data> getList() {
        return locationList;
    }
}