package ghost.android.ghosthguapp.timetable;

public class TimeTableData {
    private String day;
    private String period;
    private String place;
    private String prof;
    private String subject;

    public String getDay() {
        return day;
    }

    public String getPeriod() {
        return period;
    }

    public String getPlace() {
        return place;
    }

    public String getProf() {
        return prof;
    }

    public String getSubject() {
        return subject;
    }

    public void setDay(String nDay) {
        this.day = nDay;
    }

    public void setPeriod(String nPeriod) {
        this.period = nPeriod;
    }

    public void setPlace(String nPlace) {
        this.place = nPlace;
    }

    public void setProf(String nProf) {
        this.prof = nProf;
    }

    public void setSubject(String nSubject) {
        this.subject = nSubject;
    }
}
