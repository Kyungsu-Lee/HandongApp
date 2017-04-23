package ghost.android.ghosthguapp.hgutube;

import android.os.Environment;

import java.io.File;

/**
 * Created by SEC on 2015-01-31.
 */
public class HgutubeFileManager {

    private String SDPath = "" + Environment.getExternalStorageDirectory();
    private File hgutubeFile;

    public File openHgutubeFile() {
        hgutubeFile = new File(SDPath + "/HGUapp/hgutube.xml");
        return hgutubeFile;
    }

    public File getHgutubeFile() { return hgutubeFile; }
}
