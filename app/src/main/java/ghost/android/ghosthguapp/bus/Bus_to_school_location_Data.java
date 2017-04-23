package ghost.android.ghosthguapp.bus;

public class Bus_to_school_location_Data {
    String Ways;
    String Startname;
    String Endname;
    String CarNo;
    String ShelterNo;
    String Over;
    String TrunFlag;
    String Latitude;
    String Longitude;
    String XmlTime;

    public String getWays() {
        return Ways;
    }   // 방향(시내방향(01), 학교방향(02))
    public void setWays(String nWays) {
        this.Ways = nWays;
    }

    public String getStartname() {
        return Startname;
    }   // 정류장명
    public void setStartname(String nStartname) {
        this.Startname = nStartname;
    }

    public String getEndname() {
        return Endname;
    }   // 도착(종점)정류장
    public void setEndname(String nEndname) {
        this.Endname = nEndname;
    }

    public String getCarNo() {
        return CarNo;
    }   // 차량번호
    public void setCarNo(String nCarNo) {
        this.CarNo = nCarNo;
    }

    public String getShelterNo() {
        return ShelterNo;
    }   // 정류장번호
    public void setShelterNo(String nShelterNo) {
        this.ShelterNo = nShelterNo;
    }

    public String getOver() {
        return Over;
    }   // 정류장도착(0), 출발(1)
    public void setOver(String nOver) {
        this.Over = nOver;
    }

    public String getTrunFlag() {
        return TrunFlag;
    }   // 종점에서 턴할때(1)
    public void setTrunFlag(String nTrunFlag) {
        this.TrunFlag = nTrunFlag;
    }

    public String getLatitude() {
        return Latitude;
    }   // 위도
    public void setLatitude(String nLatitude) {
        this.Latitude = nLatitude;
    }

    public String getLongitude() {
        return Longitude;
    }   // 경도
    public void setLongitude(String nLongitude) {
        this.Longitude = nLongitude;
    }

    public String getXmlTime() {
        return XmlTime;
    }   // TimeStamp
    public void setXmlTime(String nXmlTime) {
        this.XmlTime = nXmlTime;
    }
}