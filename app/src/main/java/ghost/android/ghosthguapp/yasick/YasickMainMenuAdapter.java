package ghost.android.ghosthguapp.yasick;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import ghost.android.ghosthguapp.R;

/**
 * Created by SEC on 2015-01-08.
 */
public class YasickMainMenuAdapter extends ArrayAdapter<MainMenuItem> {

    private ArrayList<MainMenuItem> mainMenuItems;
    private Context context;
    String SDPath = "" + Environment.getExternalStorageDirectory();
    String storeId;

    public Hashtable<Integer, ImageView> hashConvertImageView = new Hashtable<Integer, ImageView>();

    public YasickMainMenuAdapter(Context con, int id, ArrayList<MainMenuItem> list, String storeId){
        super(con, id, list);
        context = con;
        mainMenuItems = list;
        this.storeId = storeId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        MainMenuItem menu;

        if(v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.yasick_mainmenu_item, null);
        }

        menu = mainMenuItems.get(position);

        /* 아이템의 내용들을 mainMenuItems.get(position) 의 값들로 설정하기 */

        /* 메뉴 이미지 설정 */
        // 이미지 뷰와 뷰의 크기 얻어오기

        ImageView menuImage = (ImageView) v.findViewById(R.id.mainmenu_image);
/*        menuImage.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int targetW = menuImage.getMeasuredWidth();
        int targetH = menuImage.getMeasuredHeight();
*/
        int targetW = 150;
        int targetH = 150;

        // 받아올 이미지 크기 얻어오기
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(SDPath + "/HGUapp/yasick/" + storeId + "/menus/" + position + ".jpg", options);
        int photoW = options.outWidth;
        int photoH = options.outHeight;

        // 이미지 크기 결정
        int scaleFactor = 2;
        if(targetH == 0 || targetW == 0){
            scaleFactor = 2;
        } else {
            scaleFactor = (photoH / targetH > photoW / targetW) ? photoW / targetW : photoH / targetH;
        }

        // 결정한 크기로 이미지를 decode
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;

        Bitmap image = BitmapFactory.decodeFile(SDPath + "/HGUapp/yasick/" + storeId + "/menus/" + position + ".jpg", options);
        menuImage.setImageBitmap(image);

        // 지울 때를 대비하여 해쉬테이블에 포지션과 함께 저장
        if (hashConvertImageView.containsKey(position) == false)
            hashConvertImageView.put(position, menuImage);


        /* 메뉴 이름과 가격 설정 */
        TextView menuName = (TextView) v.findViewById(R.id.mainmenu_name);
        menuName.setText(menu.getMenuName());

        TextView menuPrice = (TextView) v.findViewById(R.id.mainemenu_price);
        if(menu.getPrice() == null || menu.getPrice().equals("") || menu.getPrice().equals("null") || menu.getPrice().equals("-"))
            menuPrice.setVisibility(View.INVISIBLE);    // 가격 정보 없으면 텍스트뷰 INVISIBLE
        else
            menuPrice.setText(menu.getPrice());

        return v;
    }


    /* 메모리 아끼려고 구현. 액티비티의 onDestroy() 에서 콜할 것임 */
    public void doRecycle() {
        if (hashConvertImageView == null || hashConvertImageView.size() == 0)
            return;

        ImageView imageView;
        Enumeration<Integer> e = hashConvertImageView.keys();

        while(e.hasMoreElements()) {
            imageView = (ImageView) hashConvertImageView.get(e.nextElement());
            if (imageView != null) {
                Drawable d = imageView.getDrawable();
                if (d instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                    if(bitmap != null) {
                        bitmap.recycle();
                        bitmap = null;
                    } else {
                        Log.e("recycle 할 때 null 임", "");
                    }
                }
                d.setCallback(null);
            }
        }
    }


}
