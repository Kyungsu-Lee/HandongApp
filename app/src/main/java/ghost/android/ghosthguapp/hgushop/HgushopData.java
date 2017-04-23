package ghost.android.ghosthguapp.hgushop;

import ghost.android.ghosthguapp.common.GlobalVariables;

/**
 * Created by SEC on 2015-01-29.
 */
public class HgushopData {
    private int location_index = 0;
    private int category_index = 0;
    private String shopName;
    private String contents;
    private double latitude; // 위도
    private double longitude; // 경도
    private String imageUrl;
    private String phoneNumber;

    public void setLocationIndex(int index) { location_index = index; }

    public int getLocationIndex() { return location_index; }

    public void setCategoryIndex(int index) { category_index = index; }

    public int getCategoryIndex() { return category_index; }

    public void setShopName(String name) { shopName = name; }

    public String getShopName() { return shopName; }

    public void setContents(String contents) { this.contents = contents; }

    public String getContents() { return contents; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLatitude() { return latitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getLongitude() { return longitude; }

    // 절대 경로로 만들어주기
    public void setImageUrl(String url) { imageUrl = GlobalVariables.SERVER_ADDR + url; }

    public String getImageUrl() { return imageUrl; }

    public void setPhoneNumber(String number) { phoneNumber = number; }

    public String getPhoneNumber() { return phoneNumber; }
}
