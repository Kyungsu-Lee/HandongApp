package ghost.android.ghosthguapp.hgushop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import ghost.android.ghosthguapp.R;

public class HgushopBetweenPage extends Activity implements View.OnClickListener{

    private Intent intent;
    private String shopName;
    private String contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hgushop_between_page);

        /* 인텐트에서 정보 받아오기 */
        intent = getIntent();
        shopName = intent.getStringExtra("shopName");
        contents = intent.getStringExtra("contents");

        /* shop 이름 셋팅하기 */
        TextView title = (TextView) findViewById(R.id.hgushop_between_shopname);
        if(shopName != null)
            title.setText(shopName);
        else
            title.setText("HGU SHOP");

        /* shop 할인 정보 셋팅하기 */
        TextView tv_contents = (TextView) findViewById(R.id.hgushop_between_contents);
        if(tv_contents != null)
            tv_contents.setText(contents);


        /* 버튼 리소스들 받아오기 */
        ImageButton imgLeft = (ImageButton) findViewById(R.id.hgushop_between_left_image);
        Button left = (Button) findViewById(R.id.hgushop_between_left);
        ImageButton imgRight = (ImageButton) findViewById(R.id.hgushop_between_right_image);
        Button right = (Button) findViewById(R.id.hgushop_between_right);

        /* 버튼 리스너 달기 */
        imgLeft.setOnClickListener(this);
        left.setOnClickListener(this);
        imgRight.setOnClickListener(this);
        right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.hgushop_between_left :
            case R.id.hgushop_between_left_image :
                                                    /* 전체 지도 페이지로 넘기기 */
                                                    Intent mapIntent = new Intent(getApplicationContext(), HgushopMap.class);
                                                    startActivity(mapIntent);
                                                    // 액티비티 전환 애니매이션 설정 (슬라이딩)
                                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                                    finish();
                                                    break;


            case R.id.hgushop_between_right :
            case R.id.hgushop_between_right_image :
                                                    /* 다음 페이지로 넘길 정보를 Intent 에서 받아오기 */
                                                    int location = intent.getIntExtra("location", -1);
                                                    int category = intent.getIntExtra("category", -1);
                                                    double latitude = intent.getDoubleExtra("latitude", 36.0796224);
                                                    double longitude = intent.getDoubleExtra("longitude", 129.3973618);
                                                    String imageUrl = intent.getStringExtra("imageUrl");
                                                    String phoneNumber = intent.getStringExtra("phoneNumber");

                                                    /* 다음 페이지로 넘길 새로운 인텐트를 생성하고 정보를 집어넣기 */
                                                    Intent newIntent = new Intent(getApplicationContext(), HgushopEachPage.class);
                                                    // Intent 에 필요한 정보 넣기
                                                    newIntent.putExtra("location", location);
                                                    newIntent.putExtra("category", category);
                                                    newIntent.putExtra("shopName", shopName);
                                                    newIntent.putExtra("contents", contents);
                                                    newIntent.putExtra("latitude", latitude);
                                                    newIntent.putExtra("longitude", longitude);
                                                    newIntent.putExtra("imageUrl", imageUrl);
                                                    newIntent.putExtra("phoneNumber", phoneNumber);
                                                    // Intent 던지기!
                                                    startActivity(newIntent);
                                                    // 액티비티 전환 애니매이션 설정 (슬라이딩)
                                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                    break;
        }

    }

    /* BACK 키 눌렀을 때는 반드시 finish() 호출하도록 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                /* 다시 돌아가는 애니매이션 효과 주고 지도 페이지로 돌아가는 듯하게 보이기 위해서 */
                Intent mapIntent = new Intent(getApplicationContext(), HgushopMap.class);
                startActivity(mapIntent);

                finish();

                // 액티비티 전환 애니매이션 설정 (슬라이딩)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            default:
                return false;
        }
    }
}
