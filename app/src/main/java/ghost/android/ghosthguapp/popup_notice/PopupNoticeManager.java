package ghost.android.ghosthguapp.popup_notice;

import org.w3c.dom.Document;
import java.net.URL;

import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.common.XMLParser;

public class PopupNoticeManager {

    public String getNoticeDate() {
        try {
            URL url = new URL(GlobalVariables.SERVER_ADDR + "getMainNotice.jsp");
            XMLParser parser = new XMLParser();

            Document doc = parser.getDomElement(parser.getXmlFromUrl(url.toString()));

            // NodeList 선언
            String date = doc.getElementsByTagName("version")
                    .item(0)
                    .getFirstChild()
                    .getNodeValue();

            return date;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}

