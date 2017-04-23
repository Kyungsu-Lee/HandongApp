package ghost.android.ghosthguapp.yasick;

import android.os.Environment;

import java.io.File;

/**
 * Created by SEC on 2015-01-12.
 */

public class StoresFileManager {

    private String SDPath = "" + Environment.getExternalStorageDirectory();
    private String storePagePath;
    private File storePageFile;
    private String catalogPath;
    private File catalogFile;
    private String menusDirPath;
    private File menusDir;


    public File openStorePageFile(String storeId) {
        storePagePath = SDPath + "/HGUapp/yasick/" + storeId + "/getStorePage.xml";
        storePageFile = new File(storePagePath);
        return storePageFile;
    }

    public File getStorePageFile() {
        return storePageFile;
    }

    public File openCatalogFile(String storeId) {
        catalogPath = SDPath + "/HGUapp/yasick/" + storeId + "/catalog.jpg";
        catalogFile = new File(catalogPath);
        return catalogFile;
    }

    public File getCatalogFile() {
        return catalogFile;
    }

    public File openMenusDir(String storeId) {
        menusDirPath = SDPath + "/HGUapp/yasick/" + storeId + "/menus";
        menusDir = new File(menusDirPath);
        return menusDir;
    }

    public File getMenusDir() {
        return menusDir;
    }

    public File openEachMenuFile(String storeId, int number) {
        File file = new File(SDPath + "/HGUapp/yasick/" + storeId + "/menus/" + number + ".jpg");
        return file;
    }
}
