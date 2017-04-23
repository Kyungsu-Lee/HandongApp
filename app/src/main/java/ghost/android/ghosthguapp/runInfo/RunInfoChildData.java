package ghost.android.ghosthguapp.runInfo;

public class RunInfoChildData{
    private String time;
    private String note;

    public void setNote(String nNote) {
        this.note = nNote;
    }

    public void setTime(String nTime) {
        this.time = nTime;
    }

    public String getTime(){
        return time;
    }

    public String getNote(){
        return note;
    }
}
