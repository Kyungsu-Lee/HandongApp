package ghost.android.ghosthguapp.hgushop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalMethods;

public class HgushopMap extends Activity implements MapView.POIItemEventListener {

    private MapPOIItem marker;
    private MapView mapView;
    private ArrayList<HgushopData> shops;

    private static final MapPoint SCHOOL_MARKER_POINT = MapPoint.mapPointWithGeoCoord(36.10363741, 129.3907629);    //좌표정보를 이런형식으로 줘야함!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hgushop_map_page);

        //인터넷 연결 확인
        AlertDialog.Builder netConDlgBuilder = GlobalMethods.checkInternet(HgushopMap.this);

        //인터넷 안되면
        if (netConDlgBuilder != null) {
            Toast.makeText(HgushopMap.this, "인터넷 안됨", Toast.LENGTH_SHORT).show();
            netConDlgBuilder.show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    HgushopMap.this.finish();
                }
            });
        }

        /* 지도 기본 셋팅 */
        // 뷰 연결
        mapView = (MapView) findViewById(R.id.hgushop_map_view);
        // API키 설정
        mapView.setDaumMapApiKey("8fbce84edabf4dd4979749501a891dcc");
        // 중심점 + 줌레벨 기본 값
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(36.0456847, 129.3761712), 4, true);



        /* 지도상에 마커표시 */
        // HgushopManager 생성
        HgushopFileManager fileManager = new HgushopFileManager();
        fileManager.openHgushopFile();
        HgushopManager manager = new HgushopManager(fileManager);

        // manager 에서 shop 리스트 얻어오기
        manager.setting();
        shops = manager.getShops();

        // 얻어온 shop 리스트 정보를 가지고 지도에 마커 찍기
        for(int i = 0; i < shops.size(); i++) {
            HgushopData shop = shops.get(i);
            String shopName = shop.getShopName();
            if(shopName == null) shopName = "HGU SHOP"; // shop 이름 제대로 받아오지 못했으면 HGU SHOP 으로 띄움
            createMarker(mapView, shopName, MapPoint.mapPointWithGeoCoord(shop.getLatitude(), shop.getLongitude()));
        }

        // 마커의 풍선창을 클릭했을 때의 이벤트 리스너 달기
        mapView.setPOIItemEventListener(this);

    }

    private void createMarker(MapView mv, String ItemName, MapPoint mp) { // 전달 받은 mp값에 마커를 찍어줌. 각각의 마커 구분을 위해 ItemName은 필수적
        marker = new MapPOIItem();
        marker.setItemName(ItemName);
        marker.setTag(0);
        marker.setMapPoint(mp);
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);    // 마커타입을 커스텀 마커로 지정.
        marker.setCustomImageResourceId(R.drawable.hgushop_marker); // 마커 이미지.
        marker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
        mv.addPOIItem(marker);
    }

    /* BACK 키 오버라이드 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                 /* 다시 돌아가는 애니매이션 효과 주고 목록 페이지로 돌아가는 듯하게 보이기 위해서 (다음 샵으로 넘어가는 BETWEEN 페이지 떄문에 해주어야 한다) */
                Intent intent = new Intent(getApplicationContext(), Hgushop.class);
                startActivity(intent);

                finish();

                // 액티비티 전환 애니매이션 설정 (슬라이딩)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            default:
                return false;
        }
    }


    /* 마커 풍선 클릭시 이벤트 처리하기 위한 메소드들 */
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

        // 모든 샵 리스트 훑기
        for(int i = 0; i < shops.size(); i++) {
            HgushopData shop = shops.get(i);
            // 현재 마커의 아이템 이름과 temp 의 shop name 이 같다면 : 해당 shop 의 HgushopEachPage 로 Intent 날리기
            if(shop.getShopName().equals(mapPOIItem.getItemName())) {
                // Intent 생성
                Intent intent = new Intent(getApplicationContext(), HgushopBetweenPage.class);
                // Intent 에 필요한 정보 넣기
                intent.putExtra("location", shop.getLocationIndex());
                intent.putExtra("category", shop.getCategoryIndex());
                intent.putExtra("shopName", shop.getShopName());
                intent.putExtra("contents", shop.getContents());
                intent.putExtra("latitude", shop.getLatitude());
                intent.putExtra("longitude", shop.getLongitude());
                intent.putExtra("imageUrl", shop.getImageUrl());
                intent.putExtra("phoneNumber", shop.getPhoneNumber());
                // Intent 던지기!
                startActivity(intent);
                // 액티비티 전환 애니매이션 설정 (슬라이딩)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        }
    }
    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }


}
