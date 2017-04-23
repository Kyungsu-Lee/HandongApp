package ghost.android.ghosthguapp.hgutube;

/**
 * Created by SEC on 2015-01-31.
 */
public class HgutubeData {

    private String title;
    private int category;
    private String runTime;
    private String writer;
    private String videoUrl;
    private String imageUrl;

    public void setTitle(String newTitle) { title = newTitle; }

    public String getTitle() { return title; }

    public void setCategory(int newCategory) { category = newCategory; }

    public int getCategory() { return category; }

    public void setRunTime(String newRunTime) { runTime = newRunTime; }

    public String getRunTime() { return runTime; }

    public void setWriter(String newWriter) { writer = newWriter; }

    public String getWriter() { return writer; }

    public void setVideoUrl(String newUrl) { videoUrl = newUrl; }

    public String getVideoUrl() { return  videoUrl; }

    public void setImageUrl(String newUrl) { imageUrl = newUrl; }

    public String getImageUrl() { return imageUrl; }

}
