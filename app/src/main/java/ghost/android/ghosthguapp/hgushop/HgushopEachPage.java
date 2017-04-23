package ghost.android.ghosthguapp.hgushop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalMethods;
import ghost.android.ghosthguapp.yasick.YasickDialog;

public class HgushopEachPage extends Activity {

    double latitude;
    double longitude;
    String shopName;
    Bitmap bitmap;
    String imageUrl;
    ImageView imageView;
    String phoneNumber;

    private MapPOIItem marker;
    private MapView mapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hgushop_each_page);

        // shop 에 대한 기본적 정보 셋팅과 지도에 찍을 위도경도 받아오기
        setBasicInfo();

        // Map View 셋팅
        setMapView();
    }

    /* shop 에 대한 기본적 정보 셋팅과 지도에 찍을 위도경도 받아오기 */
    private void setBasicInfo() {

        final int INDEX_LOCATION_YANGDUK = 1;
        final int INDEX_LOCATION_YOOK = 2;
        final int INDEX_LOCATION_ETC = 3;
        final int INDEX_CATEGORY_FOOD = 1;
        final int INDEX_CATEGORY_BEAUTY = 2;
        final int INDEX_CATEGORY_CAFE = 3;
        final int INDEX_CATEGORY_ENTERTAIN = 4;
        final int INDEX_CATEGORY_ETC = 5;
        final int INDEX_CATEGORY_HOSPITAL = 6;

        /* 페이지에 띄울 shop 에 대한 정보 받아오기 */
        Intent intent = getIntent();
        int location = intent.getIntExtra("location", -1);
        int category = intent.getIntExtra("category", -1);
        shopName = intent.getStringExtra("shopName");
        String contents = intent.getStringExtra("contents");
        latitude = intent.getDoubleExtra("latitude", 36.0796224);
        longitude = intent.getDoubleExtra("longitude", 129.3973618);
        imageUrl = intent.getStringExtra("imageUrl");
        phoneNumber = intent.getStringExtra("phoneNumber");


        /* 뷰를 불러와서 해당 정보로 셋팅 */
        // shop 이름
        TextView tv_name = (TextView) findViewById(R.id.hgushop_each_name);
        if(shopName != null && tv_name != null)
            tv_name.setText(shopName);

        // shop 의 장소 정보
        TextView tv_location = (TextView) findViewById(R.id.hgushop_each_location);
        if(location != -1 && tv_location != null) {
            switch (location) {
                case INDEX_LOCATION_YANGDUK : tv_location.setText("위치 : 장성/양덕/환여동"); break;
                case INDEX_LOCATION_YOOK : tv_location.setText("위치 : 육거리"); break;
                case INDEX_LOCATION_ETC : tv_location.setText("위치 : 기타"); break;
            }
        }

        // shop 의 카테고리 정보
        TextView tv_category = (TextView) findViewById(R.id.hgushop_each_category);
        if(category != -1 && tv_location != null) {
            switch (category) {
                case INDEX_CATEGORY_FOOD : tv_category.setText("카테고리 : 음식점"); break;
                case INDEX_CATEGORY_BEAUTY : tv_category.setText("카테고리 : 뷰티"); break;
                case INDEX_CATEGORY_CAFE : tv_category.setText("카테고리 : 카페/베이커리"); break;
                case INDEX_CATEGORY_ENTERTAIN : tv_category.setText("카테고리 : 엔터테인먼트"); break;
                case INDEX_CATEGORY_ETC : tv_category.setText("카테고리 : 기타"); break;
                case INDEX_CATEGORY_HOSPITAL : tv_category.setText("카테고리 : 병원"); break;
            }
        }

        // shop 의 혜택 정보
        TextView tv_contents = (TextView) findViewById(R.id.hgushop_contents);
        if(contents != null && tv_contents != null)
            tv_contents.setText(contents);

        // shop 의 사진
        loadImage();

        // shop 으로 전화걸기 버튼
        ImageButton btnCall = (ImageButton) findViewById(R.id.btn_hgushop_call);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 폰 번호가 있을 때는 : 전화걸기 다이얼로그
                if(!phoneNumber.equals("No Phone")) {
                    YasickDialog dialog = new YasickDialog(HgushopEachPage.this);
                    dialog.setName(shopName);
                    dialog.setPhone(phoneNumber);
                    dialog.titleSetting();
                    dialog.show();
                }
                // 폰 번호가 없으면 : 없다는 다이얼로그
                else {
                    new AlertDialog.Builder(HgushopEachPage.this)
                            .setTitle(shopName + " 으로 전화걸기")
                            .setMessage(shopName + " 의 전화번호 정보가 없습니다.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });
    }

    /* Map View 설정하는 메소드 */
    private void setMapView() {
        // 해당 업체 마커
        final MapPoint SHOP_MARKER_POINT = MapPoint.mapPointWithGeoCoord(latitude, longitude);

        /* 인터넷 연결확인!! */
        // 인터넷 연결 확인
        AlertDialog.Builder netConDlgBuilder = GlobalMethods.checkInternet(HgushopEachPage.this);
        // 인터넷 안되면
        if (netConDlgBuilder != null) {
            Toast.makeText(HgushopEachPage.this, "인터넷이 안되므로 사진 및 지도가 서비스되지 않습니다.", Toast.LENGTH_SHORT).show();
        }

        // 맵뷰 레아아웃 불러오기
        mapView = (MapView) findViewById(R.id.hgushop_each_map_view);

        // API키 설정
        mapView.setDaumMapApiKey("8fbce84edabf4dd4979749501a891dcc");

        // 지도가 처음 떳을 때의 중심점 + 줌레벨 기본 값 설정 (영일대 해수욕장)
        mapView.setMapCenterPointAndZoomLevel(SHOP_MARKER_POINT, 2, true);

        //지도상에 마커표시
        if(shopName != null)
            createMarker(mapView, shopName, SHOP_MARKER_POINT);
        else
            createMarker(mapView, "HGU SHOP", SHOP_MARKER_POINT);
    }

    /* 지도 상에 마커 표시해주는 메소드 */
    private void createMarker(MapView mv, String ItemName, MapPoint mp) { // 전달 받은 mp값에 마커를 찍어줌. 각각의 마커 구분을 위해 ItemName은 필수적
        marker = new MapPOIItem();
        marker.setItemName(ItemName);
        marker.setTag(0);
        marker.setMapPoint(mp);
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);    // 마커타입을 커스텀 마커로 지정.
        marker.setCustomImageResourceId(R.drawable.hgushop_marker); // 마커 이미지.
        marker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
        mv.addPOIItem(marker);
        mv.selectPOIItem(marker, true);         // 처음에 지도 켰을 때 마커가 켜져있는 상태이도록 설정

    }


    /* 이미지 로딩 해주는 스레드 */
    private void loadImage() {
        Thread backgroundThread = new Thread() {
            URL newurl;

            @Override
            public void run() {
                try {
                    // URL 생성
                    newurl = new URL(imageUrl);

                    // 이미지 뷰와 뷰의 크기 얻어오기
                    imageView = (ImageView) findViewById(R.id.hgushop_photo);
                    imageView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int targetW = imageView.getMeasuredWidth();
                    int targetH = imageView.getMeasuredHeight();

                    // 받아 올 이미지 크기 얻어오기
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(newurl.openConnection().getInputStream(), null, options);
                    int photoW = options.outWidth;
                    int photoH = options.outHeight;

                    // 이미지 크기 결정
                    int scaleFactor = 2;
                    if(targetH == 0 || targetW == 0) {
                        scaleFactor = 2;
                    } else {
                        scaleFactor = (photoH / targetH > photoW / targetW) ? photoW / targetW : photoH / targetH;
                    }

                    // 결정한 크기로 이미지를 디코드해오기
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = scaleFactor/2;
                    bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream(), null, options);

                } catch (MalformedURLException e) {
                } catch (IOException e) {
                }
                Message msg = Message.obtain();
                msg.what = 123;
                handler.sendMessage(msg);
            }
        };

        // 정의한 스레드 실행
        backgroundThread.start();
    }

    /* 위의 스레드에서 이미지 로딩 후 UI 만지는 작업 */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 123:
                    imageView.setImageBitmap(bitmap);
                    break;
            }
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
            default:
                return false;
        }
    }

    /* 다시 돌아가는 애니매이션 효과 주기 위해서 (back 키 누를 때도 finish() 가 호출된다는 점을 이용해서)
     * 비트맵 recycle 해서 메모리 절약하기 위해서 */
    @Override
    public void finish() {

        // 비트맵 리사이클
        if(bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        } else {
            Log.e("recycle 할 때 null 임", "HgushopEachPage 의 finish() 에서");
        }

        super.finish();

        // 액티비티 전환 애니매이션 설정 (슬라이딩)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
