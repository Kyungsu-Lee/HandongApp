package ghost.android.ghosthguapp.timetable;

public class TimeTableList {
    private TimeTableData[][] timeTableDatas;

    public TimeTableList(int day, int period) {
        timeTableDatas = new TimeTableData[day][period];
    }

    public void add(String day, String period, TimeTableData timetable) {
        timeTableDatas[Integer.parseInt(day) - 1][Integer.parseInt(period) - 1] = timetable;
    }

    public TimeTableData get(int day, int period) {
        TimeTableData data = timeTableDatas[day - 1][period - 1];
        if (data != null) {
            return data;
        } else
            return null;
    }
}
