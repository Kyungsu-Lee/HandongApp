package ghost.android.ghosthguapp.yasick;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import ghost.android.ghosthguapp.common.GlobalVariables;
import ghost.android.ghosthguapp.common.XMLParser;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class CallCountService extends IntentService {

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_COUNT_CALL = "ghost.android.ghosthguapp.yasick.action.COUNT_CALL";

    private static final String EXTRA_STOREID = "ghost.android.ghosthguapp.yasick.extra.STOREID";

    SharedPreferences sp;

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionCountCall(Context context, String storeID) {
        Intent intent = new Intent(context, CallCountService.class);
        intent.setAction(ACTION_COUNT_CALL);
        intent.putExtra(EXTRA_STOREID, storeID);
        context.startService(intent);
    }


    public CallCountService() {
        super("CallCountService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_COUNT_CALL.equals(action)) {
                final String storeId = intent.getStringExtra(EXTRA_STOREID);
                handleAction(storeId);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleAction(String storeId) {
        /* 콜 수 증가 시키기 */
        // shared preferences 불러오기
        sp  = getSharedPreferences("CallCount", Context.MODE_PRIVATE);

        // 에디터 생성
        SharedPreferences.Editor editor = sp.edit();

        // 콜수 저장된 거 불러오기
        int calls = sp.getInt("calls_" + storeId, -1);

        // 저장되어있던 콜 수 없으면 : calls 를 0으로 초기화
        if(calls == -1)
            calls = 0;
        // 있으면 : 기존에 있던 것 삭제
        else
            editor.remove("calls_" + storeId);

        // 콜 수 1 증가
        calls = calls + 1;

        // 증가한 값을 shared preference 에 저장
        editor.putInt("calls_" + storeId, calls);
        editor.commit();


        /* 인터넷 연결되어있을 때만 저장된 콜수 서버에 보내고 shared preferences 에서 초기화 */
        /* 서버 연결 */
        // 네트워크가 연결되어있으면
        if(isConnected()) {
            /* 전체 업체 아이디 받아오기 */
            ArrayList<String> storeList = requestStoreList();

            // 서버에 모든 업체의 콜 카운트 수를 보내기
            sendAllCallCount(storeList);

        }
    }


    /* network connection 체크하는 메소드 */
    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    /* 서버에서 전체 업체 리스트 받아오는 메소드 */
    private ArrayList<String> requestStoreList() {
        ArrayList<String> storeList = new ArrayList<>();

        // URL 주소
        String storeUrl = GlobalVariables.SERVER_ADDR + "yasick/getCallList.jsp";
        // 파서 생성
        XMLParser parser = new XMLParser();

        // Dom 선언
        Document dom = parser.getDomElement(parser.getXmlFromUrl(storeUrl));
        // Dom 을 살 받아왔다면
        if(dom != null) {
            // Element 선언
            Element docElement = dom.getDocumentElement();

            // Node List 선언
            NodeList nodeList = docElement.getElementsByTagName("id");

            // NodeList 가 정보를 받아오면
            if(nodeList != null && nodeList.getLength() > 0) {
                // NodeList 에서 받은 정보를 파싱
                for(int i = 0; i < nodeList.getLength(); i++) {
                    Element store = (Element) nodeList.item(i);
                    storeList.add(store.getFirstChild().getNodeValue());
                }
            }
        }
        return storeList;
    }

    /* 모든 업체의 콜카운트 정보를 서버에 보내는 메소드 */
    private void sendAllCallCount(ArrayList<String> storeList) {
        // 파서 생성
        XMLParser parser = new XMLParser();

        for (int i = 0; i < storeList.size(); i++) {
            // 업체 아이디 얻어오기
            String storeId = storeList.get(i);

            // shared preference 에서 해당 업체의 콜 수 받아오기
            int calls = sp.getInt("calls_" + storeId, 0);

            // 서버에 해당 업체의 콜수를 전송하고 결과 받아올 URL
            String url = GlobalVariables.SERVER_ADDR + "yasick/getCountCall.jsp?storeName=" + storeId + "&count=" + calls;

            // DOM 선언 (서버에 해당 업체의 콜수를 전송 & 결과 받아오기)
            Document dom = parser.getDomElement(parser.getXmlFromUrl(url));

            // dom 을 잘 받아왔다면
            if (dom != null) {
                // Element 선언
                Element docElement = dom.getDocumentElement();

                // 결과 파싱
                String thankyou = docElement.getFirstChild().getNodeValue();

                // 콜 수를 서버에 성공적으로 저장했다는 메세지를 받으면 : shared preference 초기화
                if (thankyou.equals("Thank yoy") || thankyou.equals("Thankyoy") || thankyou.equals("Thankyou") || thankyou.equals("Thank you")) {

                    SharedPreferences.Editor editor = sp.edit();
                    editor.remove("calls_" + storeId);
                    editor.putInt("calls_" + storeId, 0);
                    editor.commit();
                }
            }
        }
    }
}
