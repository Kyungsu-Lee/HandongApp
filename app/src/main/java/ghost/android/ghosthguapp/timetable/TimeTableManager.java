package ghost.android.ghosthguapp.timetable;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.common.XMLParser;

public class TimeTableManager {
    private TimeTableList timeTableList;
    private final int MAX_DAY = 6;
    private final int MAX_PERIOD = 10;
    private File file = GlobalVariables.fTt;

    // XML node keys
    final String KEY_LIST = "schedule"; // parent node
    final String KEY_DAY = "day";
    final String KEY_PERIOD = "period";
    final String KEY_PROF = "prof";
    final String KEY_PLACE = "place";
    final String KEY_SUB = "subject";

    // SD카드에서 시간표 받아오기
    public void setting() {
        // List 열기
        timeTableList = new TimeTableList(MAX_DAY, MAX_PERIOD);

        XMLParser parser = new XMLParser();

        if (file.exists()) {
            try {
                // DOM 선언
                Document dom = parser.getDomElementFromFile(file);
                // Element 선언
                Element docEle = dom.getDocumentElement();
                // NodeList 선언
                NodeList nl = docEle.getElementsByTagName(KEY_LIST);

                // NodeList가 정보를 받아오면
                if (nl != null && nl.getLength() > 0) {
                    // NodeList에서 받은 정보 infoList에 뿌리기
                    for (int i = 0; i < nl.getLength(); i++) {
                        TimeTableData timetable = new TimeTableData();
                        Element e = (Element) nl.item(i);
                        String day = parser.getValue(e, KEY_DAY);
                        String period = parser.getValue(e, KEY_PERIOD);
                        timetable.setDay(day);
                        timetable.setPeriod(period);
                        timetable.setPlace(parser.getValue(e, KEY_PLACE));
                        timetable.setProf(parser.getValue(e, KEY_PROF));
                        timetable.setSubject(parser.getValue(e, KEY_SUB));
                        timeTableList.add(day, period, timetable);
                        // for 종료
                    }
                    // if nl is not null 종료
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
                file.delete();
            }
        }
    }

    public TimeTableList getList() {
        return timeTableList;
    }

    public void setList(TimeTableList editedList) {
        timeTableList = editedList;
    }

    // 업데이트 or 처음 정보 받아 SD카드에 저장
    public boolean updateNSave(Activity thisActivity) {
        ArrayList params = new ArrayList();
        // InputStream 객체 생성
        BufferedInputStream is = null;
        // FileOutputStream 객체 생성
        BufferedOutputStream fos = null;

        SharedPreferences prefs = thisActivity.getSharedPreferences("Login", Context.MODE_PRIVATE);
        String id = prefs.getString("id", "");
        String pw = prefs.getString("pw", "");

        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("password", pw));

        try {
            // 서버 연결
            URL url = new URL(GlobalVariables.SERVER_ADDR + "getTimetable.jsp");

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url.toString());

            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, "utf-8");

            httpPost.setEntity(ent);

            HttpResponse responsePost = httpClient.execute(httpPost);
            Log.d("결과", responsePost.toString());
            HttpEntity resEntity = responsePost.getEntity();

            // 연결되면
            if (resEntity != null && responsePost.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                file.createNewFile();

                // InputStream 변수 선언
                is = new BufferedInputStream(resEntity.getContent());

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

    public boolean savingTimeTable() {
        int day = 1;
        int period = 1;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // 루트 엘리먼트
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("timeTable");
            doc.appendChild(rootElement);

            for (day = 1; day <= MAX_DAY; day++) {
                for (period = 1; period <= MAX_PERIOD; period++) {
                    TimeTableData editedData = timeTableList.get(day, period);
                    if (editedData != null) {

                        // schedule 엘리먼트 리스트
                        Element eSchedule = doc.createElement(KEY_LIST);
                        rootElement.appendChild(eSchedule);

                        // day 엘리먼트
                        Element eDay = doc.createElement(KEY_DAY);
                        eDay.appendChild(doc.createTextNode(editedData.getDay()));
                        eSchedule.appendChild(eDay);

                        // period 엘리먼트
                        Element ePeriod = doc.createElement(KEY_PERIOD);
                        ePeriod.appendChild(doc.createTextNode(editedData.getPeriod()));
                        eSchedule.appendChild(ePeriod);

                        // subject 엘리먼트
                        Element eSub = doc.createElement(KEY_SUB);
                        eSub.appendChild(doc.createTextNode(editedData.getSubject()));
                        eSchedule.appendChild(eSub);

                        // place 엘리먼트
                        Element ePlace = doc.createElement(KEY_PLACE);
                        ePlace.appendChild(doc.createTextNode(editedData.getPlace()));
                        eSchedule.appendChild(ePlace);

                        // prof 엘리먼트
                        Element eProf = doc.createElement(KEY_PROF);
                        eProf.appendChild(doc.createTextNode(editedData.getProf()));
                        eSchedule.appendChild(eProf);
                    }
                }
            }

            // XML 파일로 쓰기
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            StreamResult result = new StreamResult(new BufferedOutputStream(new FileOutputStream(file)));
            transformer.transform(source, result);

        } catch (TransformerException e) {
            e.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}