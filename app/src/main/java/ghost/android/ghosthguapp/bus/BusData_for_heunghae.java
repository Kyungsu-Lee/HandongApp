package ghost.android.ghosthguapp.bus;

public class BusData_for_heunghae {
    private String rotary;
    private String hgu;
    private String gokgang;
    private String heunghae;
    private String timesplit;
    private String times;
    private String tzone;
    private int type;

    public String getRotary() {
        return rotary;
    }
    public void setRotary(String nRotary) {
        this.rotary = nRotary;
    }

    public String getHgu() {
        return hgu;
    }
    public void setHgu(String nHgu) {
        this.hgu = nHgu;
    }

    public String getGokgang() {
        return gokgang;
    }
    public void setGokgang(String nGokgang) {
        this.gokgang = nGokgang;
    }

    public String getHeunghae() {
        return heunghae;
    }
    public void setHeunghae(String nHeunghae) {
        this.heunghae = nHeunghae;
    }

    public String getTimesplit() {
        if (timesplit.equals("am"))
            return "AM";
        else
            return "PM";
    }
    public void setTimesplit(String nTimesplit) {
        this.timesplit = nTimesplit;
    }

    public String getTimes() {
        return times;
    }
    public void setTimes(String nTimes) {
        this.times = nTimes;
    }

    public String getTzone() {
        return tzone;
    }
    public void setTzone(String nTzone) {
        this.tzone = nTzone;
    }

    public int getType() {
        return type;
    }
    public void setType(int nType) {
        this.type = nType;
    }
}



