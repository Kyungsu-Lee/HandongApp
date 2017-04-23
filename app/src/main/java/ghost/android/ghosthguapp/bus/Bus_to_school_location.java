package ghost.android.ghosthguapp.bus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

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

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalMethods;
import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.common.XMLParser;

public class Bus_to_school_location extends Activity implements MapView.MapViewEventListener, MapView.POIItemEventListener {    //학교행 버스 정류장과 위치정보를 나타냄. Daum Map API를 사용
    private MapPOIItem marker;  //각 정류장 지점과 현재 버스를 지도 상에 표시하기 위한 아이템.
    private MapView mapView;
    TextView Startname, Endname, ShelterNo, XmlTime;
    TextView station_name, where;
    WebView stationpic;

    private int i = 14;
    Bus_to_school_location_Manager lm = new Bus_to_school_location_Manager();
    //서버에서 받아오는 정류장 번호로 현재 위치한 정류장을 표시하기 위함 (1번부터 시작하기에 index 0에 해당하는 것은 "")
    String ShelterNono[] = {"", "학교", "양덕 한독 셀프세차장 앞", "하나로마트 버스승강장 10m 후", "EI 주유소 교차로 골목길 앞", "장흥 초등학교 교차로 20m 전", "양덕 바로크 가구점 건너편", "환호동 종점", "해맞이 그린빌 버스 승강장 50m후", "명지탕 건너편 버스 승강장 10m후", "두호 농협 지나서 어림지 시내버스 승강장 앞", "항구 우체국 20m후", "경북광유 주유소전 교차로 앞", "기쁨의교회 교육관 앞", "육거리 중앙 아트홀 앞", "기쁨의교회 교육관 앞 시내버스 승강장 15m전", "르노 삼성자동차 북 포항점 앞", "현대 로데오타워 한화 대청마루 앞", "두호동 주민센터 입구 지나서", "명지목욕탕 지나서 20m후", "환호교회 소화전 앞", "환호동 종점", "양덕사거리 바로크가구점 앞", "장흥 초등학교 후문 횡단보도 앞", "EI 주유소 우회전 시내버스 승강장 10m전", "샤브한쌈 50m전 양덕건축자재백화점 앞", "한독 셀프세차장 건너편 시내버스 승강장", "학교(도착)"};
    //정류장에 도착한 상태인지 그 정류장에서 출발했는지 나타냄
    String Over[] = {" 정류장 도착", " 정류장 출발"};

    //각 정류장에 대한 좌표
    private static final MapPoint SCHOOL_MARKER_POINT = MapPoint.mapPointWithGeoCoord(36.10363741, 129.3907629);
    private static final MapPoint WASH_CAR_MARKER_POINT = MapPoint.mapPointWithGeoCoord(36.08183942, 129.4058);
    private static final MapPoint HANARO_MARKER_POINT = MapPoint.mapPointWithGeoCoord(36.08180556, 129.3998333 );
    private static final MapPoint E1_MARKER_POINT = MapPoint.mapPointWithGeoCoord(36.08180272, 129.3928546);
    private static final MapPoint ZANGHEUNG_MARKER_POINT = MapPoint.mapPointWithGeoCoord(36.07894683, 129.3923627);
    private static final MapPoint BAROK_MARKER_POINT = MapPoint.mapPointWithGeoCoord(36.07648661, 129.3958915);
    private static final MapPoint HWANHO_MARKER_POINT = MapPoint.mapPointWithGeoCoord(36.06967195, 129.3979538);
    private static final MapPoint HWANHOCHURCH_MARKER_POINT = MapPoint.mapPointWithGeoCoord(36.0687268, 129.3926671);
    private static final MapPoint MYEONGJI_MARKER_POINT = MapPoint.mapPointWithGeoCoord(36.06589215, 129.3853464);
    private static final MapPoint DUHO_MARKER_POINT = MapPoint.mapPointWithGeoCoord(36.06107726, 129.3798617);
    private static final MapPoint HANHWA_MARKER_POINT = MapPoint.mapPointWithGeoCoord(36.05526118, 129.3767338);
    private static final MapPoint SM_MARKER_POINT = MapPoint.mapPointWithGeoCoord(36.05058298, 129.3717763);
    private static final MapPoint HAPPYCHURCH_MARKER_POINT = MapPoint.mapPointWithGeoCoord(36.04544546, 129.3692947);
    private static final MapPoint SIX_MARKER_POINT = MapPoint.mapPointWithGeoCoord(36.04083627, 129.3668841);

    // CalloutBalloonAdapter 인터페이스 구현 (마커를 눌렀을 때 뜨는 다이얼로그와 관련)
    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.customdialog_for_station, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_location_layout);

        Toast.makeText(this, "버스의 현재 위치는 참고용으로 사용하시길 바랍니다.", Toast.LENGTH_SHORT).show();

        //인터넷 연결 확인
        AlertDialog.Builder netConDlgBuilder = GlobalMethods.checkInternet(Bus_to_school_location.this);

        //인터넷 안되면
        if (netConDlgBuilder != null) {
            Toast.makeText(Bus_to_school_location.this, "인터넷 안됨", Toast.LENGTH_SHORT).show();
            netConDlgBuilder.show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    Bus_to_school_location.this.finish();
                }
            });
        }
        where = (TextView) findViewById((R.id.where));
        where.setText("학교행");
        where.bringToFront();

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.setDaumMapApiKey("8fbce84edabf4dd4979749501a891dcc");   // API키 설정

        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(36.0817792, 129.3969217), 3, true); // 중심점 + 줌레벨 기본 값

        //지도에 버스 노선을 따라 라인을 표시함
        MapPolyline polyline = new MapPolyline();
        polyline.setTag(1000);
        polyline.setLineColor(Color.argb(128, 255, 51, 0)); // Polyline 컬러 지정.

        // Polyline 좌표 지정.
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.10363741, 129.3907629)); // 학교
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.1043446, 129.3920857));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.1055392, 129.3952047));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.1057842, 129.3980654));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.1049915, 129.3985962));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.1044525, 129.399333));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.1040239, 129.4006646));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.1039301, 129.4010089));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.1039301, 129.4010089));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.101897, 129.403828));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.101133, 129.406043));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.101318, 129.406774));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.100871, 129.407881));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.100961, 129.409323));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.100561, 129.409902));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.100028, 129.410678));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.09999, 129.410764));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.099398, 129.4104021));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0958098, 129.4188897));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0944692, 129.4188494));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0920204, 129.4174182));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.090382, 129.416863));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.085915, 129.415448));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0808955, 129.4162007));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0805038, 129.4154971));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0798626, 129.411626));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0826708, 129.4097395));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0819589, 129.4062154));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.08183942, 129.4058));   // 한독 셀프세차장 건너편
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.08180556, 129.3998333));    // 하나로마트 건너편
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.08180272, 129.3928546));    // EI 주유소 우회전 시내버스 승강장 10m전
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.081835, 129.391968));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.07894683, 129.3923627));    // 장흥 초등학교 후문 횡단보도 앞
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0761073, 129.3926547));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.07648661, 129.3958915));    // 바로크 가구점
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0763469, 129.3971721));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0725665, 129.3983548));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0708161, 129.399482));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0698233, 129.3989884));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.06967195, 129.3979538));   // 환호동
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0687268, 129.3926671));  // 환호교회 소화전 앞
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0682139, 129.3902893));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0671573, 129.3888628));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0667996, 129.386506));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.06589215, 129.3853464));    // 명지목욕탕 지나서 20m후
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.06107726, 129.3798617));   // 두호동 주민센터 입구 지나서
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0578604, 129.3775911));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.05526118, 129.3767338));    // 현대 로데오타워 한화 대청마루 앞
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0539981, 129.3761317));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.053315, 129.3754272));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0525044, 129.3736507));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0514019, 129.3719261));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.05058298, 129.3717763));   // 르노 삼성자동차 북 포항점 앞
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0482078, 129.3708515));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.04544546, 129.3692947));   // 기쁨의교회 교육관 앞 시내버스 승강장 15m전
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.0434194, 129.3678966));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.04083627, 129.3668841));   // 육거리 중앙 아트홀 앞

        // Polyline 지도에 올리기.
        mapView.addPolyline(polyline);

        // 지도뷰의 중심좌표와 줌레벨을 Polyline이 모두 나오도록 조정.
        MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
        int padding = 100; // px
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));

        mapView.setMapViewEventListener(Bus_to_school_location.this);
        mapView.setPOIItemEventListener(Bus_to_school_location.this);

        //지도상에 마커표시

        createMarker(mapView, "육거리 중앙 아트홀 앞", SIX_MARKER_POINT);
        createMarker(mapView, "기쁨의 교회 교육관 앞 시내버스 승강장 15m전", HAPPYCHURCH_MARKER_POINT);
        createMarker(mapView, "르노 삼성자동차", SM_MARKER_POINT);
        createMarker(mapView, "한화 대청마루 앞", HANHWA_MARKER_POINT);
        createMarker(mapView, "두호동 주민센터", DUHO_MARKER_POINT);
        createMarker(mapView, "명지탕 지나서 20m 후", MYEONGJI_MARKER_POINT);
        createMarker(mapView, "환호교회 소화전 앞", HWANHOCHURCH_MARKER_POINT);
        createMarker(mapView, "환호동 종점", HWANHO_MARKER_POINT);
        createMarker(mapView, "바로크 가구점", BAROK_MARKER_POINT);
        createMarker(mapView, "장흥초 후문 횡단보도 앞", ZANGHEUNG_MARKER_POINT);
        createMarker(mapView, "E1 주유소 시내버스 승강장 10m전", E1_MARKER_POINT);
        createMarker(mapView, "샤브한쌈 50m전 양덕건축자재백화점 앞", HANARO_MARKER_POINT);
        createMarker(mapView, "세차장 건너편 시내버스 승강장", WASH_CAR_MARKER_POINT);
        createMarker(mapView, "학교", SCHOOL_MARKER_POINT);

        getLocinfo(mapView);
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
    }

    private void createMarker(MapView mv, String ItemName, MapPoint mp) {   //마커 생성 메소드
        marker = new MapPOIItem();
        marker.setItemName(ItemName);
        marker.setTag(i);
        marker.setMapPoint(mp);
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);    // 마커타입을 커스텀 마커로 지정.
        switch (i) { // 마커 이미지.
            case 27:
                marker.setCustomImageResourceId(R.drawable.bus_marker_school);
                break;
            case 26:
                marker.setCustomImageResourceId(R.drawable.bus_marker_washcar);
                break;
            case 25:
                marker.setCustomImageResourceId(R.drawable.bus_marker_hanaro);
                break;
            case 24:
                marker.setCustomImageResourceId(R.drawable.bus_marker_e1);
                break;
            case 23:
                marker.setCustomImageResourceId(R.drawable.bus_marker_zangheung);
                break;
            case 22:
                marker.setCustomImageResourceId(R.drawable.bus_marker_barok);
                break;
            case 21:
                marker.setCustomImageResourceId(R.drawable.bus_marker_hwanho);
                break;
            case 20:
                marker.setCustomImageResourceId(R.drawable.bus_marker_hwanhochurch);
                break;
            case 19:
                marker.setCustomImageResourceId(R.drawable.bus_marker_myeongji);
                break;
            case 18:
                marker.setCustomImageResourceId(R.drawable.bus_marker_duho);
                break;
            case 17:
                marker.setCustomImageResourceId(R.drawable.bus_marker_rodeo);
                break;
            case 16:
                marker.setCustomImageResourceId(R.drawable.bus_marker_sm);
                break;
            case 15:
                marker.setCustomImageResourceId(R.drawable.bus_marker_happychurch);
                break;
            case 14:
                marker.setCustomImageResourceId(R.drawable.bus_marker_six);
                break;
        }
        marker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
        mv.addPOIItem(marker);
        //mv.selectPOIItem(marker, false);
        i++;
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        ShowDlg(mapPOIItem);
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
    }

    public void ShowDlg(MapPOIItem mapPOIItem) {    //마커를 눌렀을 때 나오는 다이얼로그를 관리하는 메소드
        if ((!(mapPOIItem.getItemName().substring(0, 2).equals("학교"))) && (!(mapPOIItem.getItemName().substring(0, 3).equals("Bus")))) {
            CustomDialog_for_station dialog = new CustomDialog_for_station(this);
            station_name = (TextView) dialog.findViewById(R.id.bus_info);
            station_name.setText(mapPOIItem.getItemName());
            stationpic = (WebView) dialog.findViewById(R.id.stationpic);
            stationpic.getSettings().setSupportZoom(true);
            stationpic.getSettings().setBuiltInZoomControls(true);
            stationpic.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            stationpic.getSettings().setLoadWithOverviewMode(true);
            stationpic.getSettings().setUseWideViewPort(true);
            try {
                stationpic.loadUrl(getURL(mapPOIItem.getTag()));
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
            dialog.show();
        } else if ((!(mapPOIItem.getItemName().substring(0, 2).equals("학교"))) && (mapPOIItem.getItemName().substring(0, 3).equals("Bus"))){
            CustomDialog_for_bus dialog = new CustomDialog_for_bus(this);
            Startname = (TextView) dialog.findViewById(R.id.Startname);
            Endname = (TextView) dialog.findViewById(R.id.Endname);
            ShelterNo = (TextView) dialog.findViewById(R.id.ShelterNo);
            XmlTime = (TextView) dialog.findViewById(R.id.XmlTime);
            int tag = mapPOIItem.getTag();
            Startname.setText(lm.getList().get(tag).getStartname());
            Endname.setText(lm.getList().get(tag).getEndname());
            ShelterNo.setText(ShelterNono[Integer.valueOf(lm.getList().get(tag).getShelterNo())] +Over[Integer.valueOf(lm.getList().get(tag).getOver())]);
            XmlTime.setText(lm.getList().get(tag).getXmlTime());
            dialog.show();
        }
        else
            ;
    }

    public void getLocinfo(MapView mv) {
        try {
            lm.setting();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        ArrayList<Bus_to_school_location_Data> alld = lm.getList();

        if (alld.size() != 0 && alld.size() > 0) {
            for (int z = 0; z < alld.size(); z++) {
                if (alld.get(z).getWays().equals("02") && alld.get(z).getTrunFlag().equals("0") || (alld.get(z).getWays().equals("01") && alld.get(z).getTrunFlag().equals("1"))) {
                    marker = new MapPOIItem();
                    marker.setItemName("Bus" + " " + alld.get(z).getCarNo());
                    marker.setTag(z);
                    marker.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.valueOf(alld.get(z).getLatitude()), Double.valueOf(alld.get(z).getLongitude())));
                    marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);    // 마커타입을 커스텀 마커로 지정.
                    marker.setCustomImageResourceId(R.drawable.vehicle29);  // 마커 이미지
                    marker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
                    mv.addPOIItem(marker);
                }
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
            default:
                return false;
        }
    }

    public String getURL(int id) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        final String KEY_NAME = "name" + id;
        String name = "";
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //Parser 열기
        XMLParser parser = new XMLParser();
        try {
            //DOM 선언
            String xml = parser.getXmlFromUrl(GlobalVariables.SERVER_ADDR + "bus/getBusimageschool.jsp");
            Document dom = parser.getDomElement(xml);
            XPath xpath = XPathFactory.newInstance().newXPath();
            //NodeList 선언
            NodeList nl_name = (NodeList) xpath.evaluate("/Busimage", dom, XPathConstants.NODESET);
            Element el_loc = (Element) nl_name.item(0);
            name = parser.getValue(el_loc, KEY_NAME);
        } catch (
                NullPointerException e
                ) {
            e.printStackTrace();
        }
        return GlobalVariables.SERVER_ADDR + "bus/" + name;
    }
}
