package ghost.android.ghosthguapp.mainpage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ghost.android.ghosthguapp.common.GlobalMethods;
import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.common.XMLParser;

public class MainBusManager {
    private ArrayList<String> sixTimeList;
    private ArrayList<String> schoolTimeList;
    private int yoil = GlobalMethods.getToday(); // 일요일(1) ~ 토요일(7)
    private int hour = GlobalMethods.getCurHour();
    private int min = GlobalMethods.getCurMin();
    private int ap = GlobalMethods.getAmPm();
    private XMLParser parser = new XMLParser();
    private URL url;
    // XML node keys
    private final String KEY_LIST = "Bus"; // parent node
    private final String KEY_SIX = "six";
    private final String KEY_HWAN = "hwan";
    private final String KEY_SCHOOL = "school";
    private final String KEY_TZONE = "tZone";
    private final String KEY_tzone = "tzone";
    private final String KEY_SPLIT = "timesplit";
    private final String KEY_BUS = "Bus";

    public ArrayList<String> sixFromServer() {
        int cnt = 0;
        try {
            //평일(월요일(2)~금요일(6))
            if (yoil > 1 && yoil < 7) {
                if (yoil == 2 && ap == 0 && hour < 3) { // 일->월 오전 03시 이전은 주말
                    url = new URL(GlobalVariables.SERVER_ADDR + "busWidget/getBus_Weekend(toSix).jsp");
                } else {
                    url = new URL(GlobalVariables.SERVER_ADDR + "busWidget/getBus_Weekday(toSix).jsp");
                }
            }//주말(토요일(7) or 일요일(1))
            else {
                if (yoil == 7 && ap == 0 && hour < 3) { // 금->토 오전 03시 이전은 평일
                    url = new URL(GlobalVariables.SERVER_ADDR + "busWidget/getBus_Weekday(toSix).jsp");
                } else {
                    url = new URL(GlobalVariables.SERVER_ADDR + "busWidget/getBus_Weekend(toSix).jsp");
                }
            }

            // List 열기
            sixTimeList = new ArrayList();

            // Element 선언
            Document doc = parser.getDomElement(parser.getXmlFromUrl(url.toString()));

            // NodeList 선언
            NodeList nl = doc.getElementsByTagName(KEY_LIST);

            // NodeList가 정보를 받아오면
            if (nl != null && nl.getLength() > 0) {
                // NodeList에서 받은 정보 infoList에 뿌리기
                for (int i = 0; i < nl.getLength(); i++) {
                    Element e = (Element) nl.item(i);
                    sixTimeList.add(cnt++, parser.getValue(e, KEY_SCHOOL));
                    sixTimeList.add(cnt++, parser.getValue(e, KEY_HWAN));
                    sixTimeList.add(cnt++, parser.getValue(e, KEY_SIX));
                    // for 종료
                }
                // if nl is not null 종료
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        for (; cnt < 9; cnt++) {
            sixTimeList.add(cnt, "-");
        }
        return sixTimeList;
    }

    public ArrayList<String> schoolFromServer() {
        int cnt = 0;
        try {
            //평일(월요일(2)~금요일(6))
            if (yoil > 1 && yoil < 7) {
                if (yoil == 2 && ap == 0 && hour < 3) { // 일->월 오전 03시 이전은 주말
                    url = new URL(GlobalVariables.SERVER_ADDR + "busWidget/getBus_Weekend(toSchool).jsp");
                } else {
                    url = new URL(GlobalVariables.SERVER_ADDR + "busWidget/getBus_Weekday(toSchool).jsp");
                }
            }//주말(토요일(7) or 일요일(1))
            else {
                if (yoil == 7 && ap == 0 && hour < 3) { // 금->토 오전 03시 이전은 평일
                    url = new URL(GlobalVariables.SERVER_ADDR + "busWidget/getBus_Weekday(toSchool).jsp");
                } else {
                    url = new URL(GlobalVariables.SERVER_ADDR + "busWidget/getBus_Weekend(toSchool).jsp");
                }
            }

            // List 열기
            schoolTimeList = new ArrayList();

            // Element 선언
            Document doc = parser.getDomElement(parser.getXmlFromUrl(url.toString()));

            // NodeList 선언
            NodeList nl = doc.getElementsByTagName(KEY_LIST);

            // NodeList가 정보를 받아오면
            if (nl != null && nl.getLength() > 0) {
                // NodeList에서 받은 정보 infoList에 뿌리기
                for (int i = 0; i < nl.getLength(); i++) {
                    Element e = (Element) nl.item(i);
                    schoolTimeList.add(cnt++, parser.getValue(e, KEY_SIX));
                    schoolTimeList.add(cnt++, parser.getValue(e, KEY_HWAN));
                    schoolTimeList.add(cnt++, parser.getValue(e, KEY_SCHOOL));
                    // for 종료
                }
                // if nl is not null 종료
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        for (; cnt < 9; cnt++) {
            schoolTimeList.add(cnt, "-");
        }
        return schoolTimeList;
    }

    public ArrayList<String> sixFromFile() {
        File file;
        int cnt = 0;
        boolean nextTime = false;

        //평일(월요일(2)~금요일(6))
        if (yoil > 1 && yoil < 7) {
            if (yoil == 2 && ap == 0 && hour < 3) { // 일->월 오전 03시 이전은 주말
                file = GlobalVariables.fSixMal;
            } else {
                file = GlobalVariables.fSix;
            }
        }//주말(토요일(7) or 일요일(1))
        else {
            if (yoil == 7 && ap == 0 && hour < 3) { // 금->토 오전 03시 이전은 평일
                file = GlobalVariables.fSix;
            } else {
                file = GlobalVariables.fSixMal;
            }
        }
        // List 열기
        sixTimeList = new ArrayList();

        // 버스시간표 파일이 있으면
        if (file.exists()) {
            try {
                // DOM 선언
                Document dom = parser.getDomElementFromFile(file);
                // Element 선언
                Element docEle = dom.getDocumentElement();
                // NodeList 선언
                NodeList nl = docEle.getElementsByTagName(KEY_TZONE);

                // NodeList가 정보를 받아오면
                if (nl != null && nl.getLength() > 0) {
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element e = (Element) nl.item(i);
                        NodeList busList = e.getElementsByTagName(KEY_BUS);
                        if (nextTime) {
                            for (int j = 0; j < busList.getLength(); j++) {
                                Element bus = (Element) busList.item(j);
                                sixTimeList.add(cnt++, parser.getValue(bus, KEY_SCHOOL));
                                sixTimeList.add(cnt++, parser.getValue(bus, KEY_HWAN));
                                sixTimeList.add(cnt++, parser.getValue(bus, KEY_SIX));
                                if (cnt == 9) {
                                    i = nl.getLength();
                                    break;
                                }
                            }
                        } else {
                            for (int j = 0; j < busList.getLength(); j++) {
                                Element bus = (Element) busList.item(j);
                                String time = parser.getValue(bus, KEY_SIX);
                                if (!time.matches("[0-9]{2}" + ":" + "[0-9]{2}")) {
                                    time = parser.getValue(bus, KEY_HWAN);
                                    if (!time.matches("[0-9]{2}" + ":" + "[0-9]{2}")) {
                                        time = parser.getValue(bus, KEY_SCHOOL);
                                    }
                                }
                                if ((hour > 2 && hour < 7) && ap == 0) {
                                    sixTimeList.add(cnt++, parser.getValue(bus, KEY_SCHOOL));
                                    sixTimeList.add(cnt++, parser.getValue(bus, KEY_HWAN));
                                    sixTimeList.add(cnt++, parser.getValue(bus, KEY_SIX));
                                    nextTime = true;
                                    if (cnt == 9) {
                                        i = nl.getLength();
                                        break;
                                    }
                                } else if (toBeShown(time, parser.getValue(e, KEY_SPLIT), parser.getValue(e, KEY_tzone))) {
                                    sixTimeList.add(cnt++, parser.getValue(bus, KEY_SCHOOL));
                                    sixTimeList.add(cnt++, parser.getValue(bus, KEY_HWAN));
                                    sixTimeList.add(cnt++, parser.getValue(bus, KEY_SIX));
                                    nextTime = true;
                                    if (cnt == 9) {
                                        i = nl.getLength();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            for (; cnt < 9; cnt++) {
                sixTimeList.add(cnt, "-");
            }
        } else {
            for (; cnt < 9; cnt++) {
                sixTimeList.add(cnt, "");
            }
            sixTimeList.add(3, "육거리행");
            sixTimeList.add(4, "데이터");
            sixTimeList.add(5, "없음");
            sixTimeList.add(6, "인터넷");
            sixTimeList.add(7, "연결");
            sixTimeList.add(8, "요망");
        }
        return sixTimeList;
    }


    public ArrayList<String> schoolFromFile() {
        File file;
        int cnt = 0;
        boolean nextTime = false;

        //평일(월요일(2)~금요일(6))
        if (yoil > 1 && yoil < 7) {
            if (yoil == 2 && ap == 0 && hour < 3) { // 일->월 오전 03시 이전은 주말
                file = GlobalVariables.fSchMal;
            } else {
                file = GlobalVariables.fSch;
            }
        }//주말(토요일(7) or 일요일(1))
        else {
            if (yoil == 7 && ap == 0 && hour < 3) { // 금->토 오전 03시 이전은 평일
                file = GlobalVariables.fSch;
            } else {
                file = GlobalVariables.fSchMal;
            }
        }
        // List 열기
        schoolTimeList = new ArrayList();

        // 버스시간표 파일이 있으면
        if (file.exists()) {
            try {
                // DOM 선언
                Document dom = parser.getDomElementFromFile(file);
                // Element 선언
                Element docEle = dom.getDocumentElement();
                // NodeList 선언
                NodeList nl = docEle.getElementsByTagName(KEY_TZONE);

                // NodeList가 정보를 받아오면
                if (nl != null && nl.getLength() > 0) {
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element e = (Element) nl.item(i);
                        NodeList busList = e.getElementsByTagName(KEY_BUS);
                        if (nextTime) {
                            for (int j = 0; j < busList.getLength(); j++) {
                                Element bus = (Element) busList.item(j);
                                schoolTimeList.add(cnt++, parser.getValue(bus, KEY_SIX));
                                schoolTimeList.add(cnt++, parser.getValue(bus, KEY_HWAN));
                                schoolTimeList.add(cnt++, parser.getValue(bus, KEY_SCHOOL));
                                if (cnt == 9) {
                                    i = nl.getLength();
                                    break;
                                }
                            }
                        } else {
                            for (int j = 0; j < busList.getLength(); j++) {
                                Element bus = (Element) busList.item(j);
                                String time = parser.getValue(bus, KEY_SCHOOL);
                                if (!time.matches("[0-9]{2}" + ":" + "[0-9]{2}")) {
                                    time = parser.getValue(bus, KEY_HWAN);
                                    if (!time.matches("[0-9]{2}" + ":" + "[0-9]{2}")) {
                                        time = parser.getValue(bus, KEY_SIX);
                                    }
                                }
                                if ((hour > 2 && hour < 7) && ap == 0) {
                                    schoolTimeList.add(cnt++, parser.getValue(bus, KEY_SIX));
                                    schoolTimeList.add(cnt++, parser.getValue(bus, KEY_HWAN));
                                    schoolTimeList.add(cnt++, parser.getValue(bus, KEY_SCHOOL));
                                    nextTime = true;
                                    if (cnt == 9) {
                                        i = nl.getLength();
                                        break;
                                    }
                                } else if (toBeShown(time, parser.getValue(e, KEY_SPLIT), parser.getValue(e, KEY_tzone))) {
                                    schoolTimeList.add(cnt++, parser.getValue(bus, KEY_SIX));
                                    schoolTimeList.add(cnt++, parser.getValue(bus, KEY_HWAN));
                                    schoolTimeList.add(cnt++, parser.getValue(bus, KEY_SCHOOL));
                                    nextTime = true;
                                    if (cnt == 9) {
                                        i = nl.getLength();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            for (; cnt < 9; cnt++) {
                schoolTimeList.add(cnt, "-");
            }
        } else {
            for (; cnt < 9; cnt++) {
                schoolTimeList.add(cnt, "");
            }
            schoolTimeList.add(3, "학교행");
            schoolTimeList.add(4, "데이터");
            schoolTimeList.add(5, "없음");
            schoolTimeList.add(6, "인터넷");
            schoolTimeList.add(7, "연결");
            schoolTimeList.add(8, "요망");
        }
        return schoolTimeList;
    }

    public boolean toBeShown(String sTime, String sAP, String sTZ) {
        int sHour = Integer.valueOf(sTime.substring(0, 2));
        int sMin = Integer.valueOf(sTime.substring(3, 5));
        int sAp = sAP.equals("am") ? 0 : 1;
        int sTz = Integer.valueOf(sTZ);

        if (sTz == 11 && sHour == 12) {
            sAp = (sAp + 1) % 2;    // 11시 시간대의 12시는 am 과 pm을 바꿔줘야 함
        }

        if (sAp == 0 && sHour == 12) { // 오전 12시는 24시로
            sHour = sHour + 12;
        } else if (sAp == 1 && sHour != 12) {// 12시가 아닌 오후 시간은 +12시간
            sHour = sHour + 12;
        }

        if (hour == 0 || hour == 1 || hour == 2) {
            //새벽시간대(0시~2시) 비교에서는 24시를 0시로 바꾼뒤 비교
            sHour = sHour % 24;
            if (sHour == 0 || sHour == 1 || sHour == 2) {
                if (hour < sHour) {
                    return true;
                } else if (hour == sHour) {
                    if (min <= sMin) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            if (hour < sHour) {
                return true;
            } else if (hour == sHour) {
                if (min <= sMin) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
