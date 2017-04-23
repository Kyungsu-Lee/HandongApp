package ghost.android.ghosthguapp.yasick;

import android.os.Environment;

import java.io.File;

/**
 * Created by SEC on 2015-01-12.
 */

public class YasickFileManager {
    private String SDPath = "" + Environment.getExternalStorageDirectory();
    private File listFile;
    private File listPath;

    public File openListFile() {
        listFile = new File(SDPath + "/HGUapp/yasick/yasickStore_list.xml");
        return listFile;
    }

    public File getListFile() {
        return listFile;
    }

    public File openListPath() {
        listPath = new File(SDPath + "/HGUapp/yasick");
        return listPath;
    }

    public File getListPath() {
        return listPath;
    }
}
