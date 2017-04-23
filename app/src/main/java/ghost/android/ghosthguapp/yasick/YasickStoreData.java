package ghost.android.ghosthguapp.yasick;

/**
 * Created by SEC on 2015-01-01.
 */
public class YasickStoreData implements Comparable<YasickStoreData> {
    private String name;
    private String phone;
    private String runTime;
    private String category;
    private String storeId;

    public String getName() { return name; }

    public void setName(String nName) { this.name = nName; }

    public String getPhone() { return phone; }

    public void setPhone(String nPhone) { this.phone = nPhone; }

    public String getRunTime() { return runTime; }

    public void setRunTime(String nRunTime) { this.runTime = nRunTime; }

    public String getCategory() { return category; }

    public void setCategory(String nCategory) { this.category = nCategory; }

    public String getStoreId() { return storeId; }

    public void setStoreId(String nStoreId) { this.storeId = nStoreId; }


    @Override
    public int compareTo(YasickStoreData o) {
        return name.compareTo(o.name);
    }
}
