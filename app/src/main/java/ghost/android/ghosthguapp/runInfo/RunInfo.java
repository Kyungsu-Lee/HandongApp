package ghost.android.ghosthguapp.runInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;
import ghost.android.ghosthguapp.common.GlobalMethods;
import ghost.android.ghosthguapp.common.GlobalVariables;

public class RunInfo extends Fragment {
    private ExpandableListView el;
    private RunInfoAdapter adapter;
    private ArrayList<RunInfoGroupData> groupData;
    private ArrayList<ArrayList<RunInfoChildData>> childData;
    private Activity thisActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup fragment_container, Bundle savedInstanceState) {
        thisActivity = getActivity();
        View rootView = inflater.inflate(R.layout.runinfo_main, fragment_container, false);
        el = (ExpandableListView) rootView.findViewById(R.id.runinfo_main_el);

        //인터넷 연결 확인
        AlertDialog.Builder netConDlgBuilder = GlobalMethods.checkInternet(thisActivity);

        //인터넷 연결 되면
        if (netConDlgBuilder == null) {
            //DoCheckAndSave 로 가서 파일 있으면 버전 체크 후 업데이트 or 가져오기 결정,
            //DoCheckAndSave 로 가서 파일 없으면 바로 업데이트
            new DoCheckAndSave().execute();
            //인터넷 안되면
        } else {
            //파일이 없으면 : 에러 다이얼로그 띄움
            if (!GlobalVariables.fRunInfo.exists()) {
                netConDlgBuilder.show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        thisActivity.finish();
                    }
                });
            }
            //파일 있으면 : 가져오기
            else {
                display();
            }
        }

        el.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup) {
                    el.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;
            }
        });

        return rootView;
    }

    private class DoCheckAndSave extends AsyncTask<Void, Void, String> {
        private ProgressDialog doCheckAndSave;

        protected void onPreExecute() {
            super.onPreExecute();
            doCheckAndSave = ProgressDialog.show(thisActivity, "", "운영 정보를 자동 업데이트 하고 있습니다.", true);
            doCheckAndSave.setCancelable(true);
        }

        @Override
        protected String doInBackground(Void... arg0) {
            RunInfoManager runInfoManager = new RunInfoManager();
            if (runInfoManager.checkNSave()) {
                return "success";
            } else {
                return "network error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            doCheckAndSave.dismiss();
            if (result.equals("success")) {
                // 화면 display
                display();
            } else if (result.equals("network error")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                builder.setTitle("네트워크 오류");
                builder.setMessage("네트워크 상태를 확인 해주세요.")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            doCheckAndSave.dismiss();
        }

    }

    // 화면 display
    private void display() {
        RunInfoManager runInfoManager = new RunInfoManager();
        runInfoManager.setting();
        groupData = runInfoManager.getGroupDataList();
        childData = runInfoManager.getChildDataList();
        // adapter setting
        adapter = new RunInfoAdapter(thisActivity, groupData, childData);
        // adapter register
        el.setAdapter(adapter);
    }
}
