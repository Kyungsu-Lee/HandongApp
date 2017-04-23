package ghost.android.ghosthguapp.common;

import android.os.Environment;

import java.io.File;
import java.util.Calendar;

public class GlobalVariables {

    public static final String SERVER_ADDR = "https://hgughost.com/HandongServer/";
    public static final Calendar cal = Calendar.getInstance();
   // xml 저장 폴더 및 파일 정보
    public static final String SDPath = Environment.getExternalStorageDirectory() + "/HGUapp";
    public static final File Path = new File(SDPath);
    public static final File fNoMedia = new File(SDPath + "/.nomedia");
    public static final File fWTt = new File(SDPath + "/tt_widget.jpeg");
    public static final File fTt = new File(SDPath + "/tt.xml");
    public static final File fPhone = new File(SDPath + "/phonebook.xml");
    public static final File fRunInfo = new File(SDPath + "/run.xml");
    public static final File fProf = new File(SDPath + "/prof.xml");
    public static final File fHh = new File(SDPath + "/heunghae1.xml");
    public static final File fSix = new File(SDPath + "/yookgeory.xml");
    public static final File fSixMal = new File(SDPath + "/yookgeory_mal.xml");
    public static final File fSchMal = new File(SDPath + "/school_mal.xml");
    public static final File fSch = new File(SDPath + "/school.xml");
    public static final File fMoms = new File(SDPath + "/moms.xml");
    public static final File fHaksick = new File(SDPath + "/haksick.xml");
    public static final File fHyoam = new File(SDPath + "/hyoam.xml");
	
    // 시간표 요일
    public static final String[] days = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};

    public static boolean oldVersion = false;
	// 식단 월
    public static final  String[] month = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    // 식단 일
    public static final String[] date = {"","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
    // 식단 요일
    public static final String[] daysTwo = {"월요일","화요일","수요일","목요일","금요일","토요일","일요일"};

    public static boolean flag = true;    //back스택일 경우 flag를 false로 바꿔서 로딩화면이 나타나지 않게끔 함
}