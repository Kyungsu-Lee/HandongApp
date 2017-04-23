package ghost.android.ghosthguapp.notice;

/**
 * Created by SEC on 2015-01-16.
 */
public class ClassArticleData {

    private String num;
    private String title;
    private String date;
    private String url;
    private String submitDue;
    private String submitStatus;

    public void setNum(String sNum) { num = sNum; }

    public String getNum() { return num; }

    public void setTitle(String sTitle) { title = sTitle; }

    public String getTitle() { return title; }

    public void setDate(String sDate) { date = sDate; }

    public String getDate() { return date; }

    public void setUrl(String sUrl) { url = sUrl; }

    public String getUrl() { return url; }

    public void setSubmitDue(String due) { submitDue = due; }

    public String getSubmitDue() { return submitDue; }

    public void setSubmitStatus(String status) { submitStatus = status; }

    public String getSubmitStatus() { return submitStatus; }

}
