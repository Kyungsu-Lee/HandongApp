package ghost.android.ghosthguapp.yasick;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;

import ghost.android.ghosthguapp.R;

/**
 * Created by SEC on 2015-01-10.
 */
public class YasickStoreCatalogPage extends Activity {

    private String storeId;
    private String storeName;
    private String file_path;

    ImageView image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yasick_catalog);

        // 인텐트에서 필요한 정보 받아 옴
        Intent intent = getIntent();
        storeId = intent.getStringExtra("storeId");
        storeName = intent.getStringExtra("storeName");

        // 액션바 뷰의 타이틀을 업체 이름으로 설정
        TextView actionbar_title = (TextView) findViewById(R.id.yasick_catalog_actionbar_title);
        actionbar_title.setText(storeName);

        // file_path 설정
        file_path = "" + Environment.getExternalStorageDirectory() + "/HGUapp/yasick/" + storeId + "/catalog.jpg";
        //setContentView(new ZoomCatalogImage(getApplicationContext(), file_path));

        // 이미지 설정
        image = (ImageView) findViewById(R.id.iv_catalog);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap bitmap = BitmapFactory.decodeFile(file_path, options);
        image.setImageBitmap(bitmap);

    }

    /* 이미지가 올라가는 액티비티이므로 메모리 관리를 위해서 비트맵을 리사이클시켜주기 위해 override한다 */
    @Override
    protected void onDestroy() {
        Drawable d = image.getDrawable();
        if (d instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            if(bitmap != null)
                bitmap.recycle();
            bitmap = null;
        }
        if( d!= null)
            d.setCallback(null);
        super.onDestroy();
    }
}
