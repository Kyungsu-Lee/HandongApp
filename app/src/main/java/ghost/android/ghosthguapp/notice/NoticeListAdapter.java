package ghost.android.ghosthguapp.notice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;

/**
 * Created by SEC on 2015-01-16.
 */
public class NoticeListAdapter extends ArrayAdapter<ClassArticleData> {

    Context context;
    ArrayList<ClassArticleData> articlesList;
    String flag;

    public NoticeListAdapter(Context con, int id, ArrayList<ClassArticleData> list, String flag) {
        super(con, id, list);
        context = con;
        articlesList = list;

        this.flag = flag;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ClassArticleData articleData;
        TextView artNum, artTitle, artDate;
        String date;

        switch(flag) {
            /* 일반 공지 */
            case "general":
                            if (v == null) {
                                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                v = inflater.inflate(R.layout.general_article_item, null);
                            }

                            // 해당 포지션의 article data 를 불러온다
                            articleData = articlesList.get(position);

                            // 공지 번호 TextView 를 article data 에서 긁어와서 설정한다
                            artNum = (TextView) v.findViewById(R.id.general_article_row_num);
                            artNum.setText(articleData.getNum());

                            // 공지 제목 TextView 를 article data 에서 긁어와서 설정한다
                            artTitle = (TextView) v.findViewById(R.id.general_article_row_title);
                            artTitle.setText(articleData.getTitle());

                            // 서버에서 받아 온 url 정보를 해당 뷰에 태그로 단다.
                            v.setTag(articleData.getUrl());

                            break;

            /* 수업 공지 */
            case "class":
                            if (v == null) {
                                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                v = inflater.inflate(R.layout.class_article_item, null);
                            }

                            // 해당 포지션의 article data 를 불러온다
                            articleData = articlesList.get(position);

                            // 공지 번호 TextView 를 article data 에서 긁어와서 설정한다
                            artNum = (TextView) v.findViewById(R.id.class_article_row_num);
                            artNum.setText(articleData.getNum());

                            // 공지 제목 TextView 를 article data 에서 긁어와서 설정한다
                            artTitle = (TextView) v.findViewById(R.id.class_article_row_title);
                            artTitle.setText(articleData.getTitle());

                            // 날짜 TextView 를 article data 에서 긁어와서 설정한다
                            artDate = (TextView) v.findViewById(R.id.class_article_row_date);
                            // 2014-12-25 를 14-12-25 로 끊기
                            date = articleData.getDate().substring(2);
                            // 14-12-25 를 14/12/25로 바꾸기
                            artDate.setText(date.replace("-", "/"));

                            // 서버에서 받아 온 url 정보를 해당 뷰에 태그로 단다.
                            v.setTag(articleData.getUrl());
                            break;

            /* 자료실 */
            case "material":
                            if (v == null) {
                                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                v = inflater.inflate(R.layout.class_article_item, null);
                            }

                            // 해당 포지션의 article data 를 불러온다
                            articleData = articlesList.get(position);

                            // 공지 번호 TextView 를 article data 에서 긁어와서 설정한다
                            artNum = (TextView) v.findViewById(R.id.class_article_row_num);
                            artNum.setText(articleData.getNum());

                            // 공지 제목 TextView 를 article data 에서 긁어와서 설정한다
                            artTitle = (TextView) v.findViewById(R.id.class_article_row_title);
                            artTitle.setText(articleData.getTitle());

                            // 날짜 TextView 를 article data 에서 긁어와서 설정한다
                            artDate = (TextView) v.findViewById(R.id.class_article_row_date);
                            // 2014-12-25 를 14-12-25 로 끊기
                            date = articleData.getDate().substring(2);
                            // 14-12-25 를 14/12/25로 바꾸기
                            artDate.setText(date.replace("-", "/"));

                            // 서버에서 받아 온 url 정보를 해당 뷰에 태그로 단다.
                            v.setTag(articleData.getUrl());

                            break;

            /* 과제 공지 */
            case "hw":
                        if (v == null) {
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            v = inflater.inflate(R.layout.hw_article_item, null);
                        }

                        // 해당 포지션의 article data 를 불러온다
                        articleData = articlesList.get(position);

                        // 제목 TextView 를 article data 에서 긁어와서 설정한다
                        artTitle = (TextView) v.findViewById(R.id.hw_article_row_title);
                        artTitle.setText(articleData.getTitle());

                        // 제출기간 TextView 를 article data 에서 긁어와서 설정한다
                        TextView artSubmitDue = (TextView) v.findViewById(R.id.hw_article_row_submitdue);
                        artSubmitDue.setText(articleData.getSubmitDue());

                        // 제출상태 TextView 를 article data 에서 긁어와서 설정한다
                        TextView artSubmitStatus = (TextView) v.findViewById(R.id.hw_article_row_submitstatus);
                        artSubmitStatus.setText(articleData.getSubmitStatus());

                        // 서버에서 받아 온 url 정보를 해당 뷰에 태그로 단다.
                        v.setTag(articleData.getUrl());

                        break;

        }
        return v;
    }

}
