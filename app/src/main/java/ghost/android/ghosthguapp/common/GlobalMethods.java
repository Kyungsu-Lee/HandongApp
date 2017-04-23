package ghost.android.ghosthguapp.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;
import java.util.Calendar;

public class GlobalMethods {

    // 인터넷 연결 확인
    public static AlertDialog.Builder checkInternet(Context con) {
        ConnectivityManager manager =
                (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);

        //연결 되있으면 null 리턴
        if (manager.getActiveNetworkInfo() != null) {
            return null;
        }
        // 연결 안되어있으면 다이얼로그 리턴
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(con);
            builder.setTitle("네트워크 오류");
            builder.setMessage("\n네트워크 상태를 확인 해주세요.\n")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            return builder;
        }
    }

    public static String getMonth() {
        Calendar c = Calendar.getInstance();
        return GlobalVariables.month[c.get(Calendar.MONTH)];
    }

    public static String getDate() {
        Calendar c = Calendar.getInstance();
        return GlobalVariables.date[c.get(Calendar.DATE)];
    }

    //현재 요일 가져오기
    public static int getToday() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_WEEK); // 일요일(1) ~ 토요일(7)
    }

    // 현재 시간의 오전 오후 정보 가져오기
    public static int getAmPm() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.AM_PM); // 오전(0), 오후(1)
    }

    //현재 시간을 가지고 온다.(24시간제) : 0시~23시
    public static int getCurHour() {
        int hour = 0;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public static int getCurMin() {
        int min = 0;
        Calendar c = Calendar.getInstance();
        min = c.get(Calendar.MINUTE);
        return min;
    }

    public static void numberPickerTextColor(NumberPicker np, int black) {
        for (int i = 0, j = np.getChildCount(); i < j; i++) {
            View t0 = np.getChildAt(i);
            if (t0 instanceof EditText) {
                try {
                    Field t1 = np.getClass().getDeclaredField("mSelectorWheelPaint");
                    t1.setAccessible(true);
                    ((Paint) t1.get(np)).setColor(black);
                    ((EditText) t0).setTextColor(black);
                    np.invalidate();
                } catch (Exception e) {
                }
            }
        }
    }

    public static boolean toBeShownRed(String sTime, String sAP, String sTS) {
        int hour = GlobalMethods.getCurHour();
        int min = GlobalMethods.getCurMin();
        int sHour = Integer.valueOf(sTime.substring(0, 2));
        int sMin = Integer.valueOf(sTime.substring(3, 5));
        int sAp = sAP.equals("AM") ? 0 : 1;
        int sTs = Integer.valueOf(sTS);

        if (sTs == 11 && sHour == 12) {
            sAp = (sAp + 1) % 2;    // 11시 시간대의 12시는 am 과 pm을 바꿔줘야 함
        }

        if (sAp == 0 && sHour == 12) { // 오전 12시는 0시로
            sHour = sHour - 12;
        } else if (sAp == 1 && sHour != 12) {// 12시가 아닌 오후 시간은 +12시간
            sHour = sHour + 12;
        }

        if (sHour >= 0 && sHour < 3) {
            sHour += 24;
        }
        if (hour >= 0 && hour < 3) {
            hour += 24;
        }

        if (hour == 0 || hour == 1 || hour == 2) {
            if (sHour == 0 || sHour == 1 || sHour == 2) {
                if (hour < sHour) {
                    return true;
                } else if (hour == sHour) {
                    if (min <= sMin) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            if (hour < sHour) {
                return true;
            } else if (hour == sHour) {
                if (min <= sMin) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    //일주일치 날짜를 리턴
    public static String[] weekCalendar(String yyyymmdd) throws Exception{
        Calendar cal = Calendar.getInstance();
        int toYear = 0;
        int toMonth = 0;
        int toDay = 0;

        if(yyyymmdd == null || yyyymmdd.equals("")){   //파라메타값이 없을경우 오늘날짜
            toYear = cal.get(cal.YEAR);
            toMonth = cal.get(cal.MONTH)+1;
            toDay = cal.get(cal.DAY_OF_MONTH);

            int yoil = cal.get(cal.DAY_OF_WEEK); //요일나오게하기(숫자로)

            if(yoil != 1){   //해당요일이 일요일이 아닌경우
                yoil = yoil-2;
            }else{           //해당요일이 일요일인경우
                yoil = 6;
            }
            cal.set(toYear, toMonth-1, toDay-yoil);  //해당주월요일로 세팅
        }else{
            int yy =Integer.parseInt(yyyymmdd.substring(0, 4));
            int mm =Integer.parseInt(yyyymmdd.substring(4, 6))-1;
            int dd =Integer.parseInt(yyyymmdd.substring(6, 8));
            cal.set(yy, mm,dd);
        }
        String[] arrYMD = new String[7];

        int inYear = cal.get(cal.YEAR);
        int inMonth = cal.get(cal.MONTH);
        int inDay = cal.get(cal.DAY_OF_MONTH);

        for(int i = 0; i < 7;i++){
            cal.set(inYear, inMonth, inDay+i);  //
            String m = Integer.toString(cal.get(cal.MONTH)+1);
            String d = Integer.toString(cal.get(cal.DAY_OF_MONTH));
            if(m.length() == 1) m = "0" + m;
            if(d.length() == 1) d = "0" + d;

            arrYMD[i] = m+"월 "+d+"일 "+ GlobalVariables.daysTwo[i];
        }
        return arrYMD;
    }
}
