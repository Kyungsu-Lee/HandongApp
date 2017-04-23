package ghost.android.ghosthguapp.bus;

public class BusData {
    private String school;
    private String hwan;
    private String six;
    private String tzone;
    private String timesplit;
    private int type;

    public String getSchool() {
        return school;
    }
    public void setSchool(String nSchool) {
        this.school = nSchool;
    }

    public String getHwan() {
        return hwan;
    }
    public void setHwan(String nHwan) {
        this.hwan = nHwan;
    }

    public String getSix() {
        return six;
    }
    public void setSix(String nSix) {
        this.six = nSix;
    }

    public String getTzone() {
        return tzone;
    }
    public void setTzone(String nTzone) {
        this.tzone = nTzone;
    }

    public String getTimesplit() {
        if (timesplit.equals("am")){
            return "AM";}
        else{
            return "PM";}
    }
    public void setTimesplit(String nTimesplit) {
        this.timesplit = nTimesplit;
    }

    public int getType() {
        return type;
    }
    public void setType(int nType) {
        this.type = nType;
    }
}



