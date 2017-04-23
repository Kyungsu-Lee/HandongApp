package ghost.android.ghosthguapp.hgutube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;

/**
 * Created by SEC on 2015-01-31.
 */
public class HgutubeAdapter extends ArrayAdapter<HgutubeData> {

    private Context context;
    private ArrayList<HgutubeData> videos;

    final int INDEX_LECTURE = 1;        // 강연
    final int INDEX_PERFORMANCE = 3;    // 공연
    final int INDEX_CHRISTIANITY = 5;   // 신앙
    final int INDEX_PROMOTION = 4;      // 홍보
    final int INDEX_KNOWLEDGE = 2;      // 지식
    final int INDEX_ETC = 6;            // 기타

    public HgutubeAdapter(Context con, int id, ArrayList<HgutubeData> list) {
        super(con, id, list);

        context = con;
        videos = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.hgutube_item, null);
        }

        HgutubeData video = videos.get(position);

        // 동영상 타이틀
        TextView title = (TextView) v.findViewById(R.id.hgutube_row_title);
        title.setText(video.getTitle());

        // 동영상 카테고리
        TextView category = (TextView) v.findViewById(R.id.hgutube_row_category);
        switch (video.getCategory()) {
            case INDEX_LECTURE : category.setText("강연");    break;
            case INDEX_PERFORMANCE : category.setText("공연");    break;
            case INDEX_CHRISTIANITY : category.setText("신앙");   break;
            case INDEX_PROMOTION : category.setText("홍보");      break;
            case INDEX_KNOWLEDGE : category.setText("지식");      break;
            case INDEX_ETC : category.setText("기타");            break;
        }

        // 동영상 러닝타임
        TextView runTime = (TextView) v.findViewById(R.id.hgutube_row_runtime);
        runTime.setText(video.getRunTime());

        // 동영상 게시자
        TextView writer = (TextView) v.findViewById(R.id.hgutube_row_writer);
        writer.setText("게시자 : " + video.getWriter());

        // 동영상 미리보기 이미지
        ImageView preview = (ImageView) v.findViewById(R.id.hgutube_row_image);
        ImageDownloader.download(video.getImageUrl(), preview);

        return v;
    }
}
