package ghost.android.ghosthguapp.runInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ghost.android.ghosthguapp.R;

public class RunInfoAdapter extends BaseExpandableListAdapter {
    private ArrayList<RunInfoGroupData> groupDatas;
    private ArrayList<ArrayList<RunInfoChildData>> childDatas;
    private Context context;
    private LayoutInflater inflater;

    public RunInfoAdapter(Context con, ArrayList<RunInfoGroupData> group, ArrayList<ArrayList<RunInfoChildData>> child) {
        super();
        this.context = con;
        this.groupDatas = group;
        this.childDatas = child;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return groupDatas.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childDatas.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupDatas.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childDatas.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.runinfo_item_group, parent, false);
        }

        RunInfoGroupData group = groupDatas.get(groupPosition);

        TextView name = (TextView) convertView.findViewById(R.id.runinfo_item_group_name);
        name.setText(group.getName());

        ImageView iv = (ImageView) convertView.findViewById(R.id.runinfo_group_indicator);
        // check if GroupView is expanded and set imageview for expand/collapse-action
        if(isExpanded){
            iv.setImageResource(R.drawable.up_arrow);
        }
        else{
            iv.setImageResource(R.drawable.down_arrow);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.runinfo_item_child, null);
        }

        RunInfoChildData child = childDatas.get(groupPosition).get(childPosition);

        TextView time = (TextView) convertView.findViewById(R.id.runinfo_item_child_time);
        time.setText(child.getTime());

        TextView note = (TextView) convertView.findViewById(R.id.runinfo_item_child_note);
        note.setText(child.getNote());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
