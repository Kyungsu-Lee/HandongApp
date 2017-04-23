package ghost.android.ghosthguapp.hgushop;

import android.os.Environment;

import java.io.File;

/**
 * Created by SEC on 2015-01-29.
 */
public class HgushopFileManager {

    private String SDPath = "" + Environment.getExternalStorageDirectory();
    private File hgushopFile;

    public File openHgushopFile() {
        hgushopFile = new File(SDPath + "/HGUapp/hgushop.xml");
        return hgushopFile;
    }

    public File getHgushopFile() { return hgushopFile; }

}
