package ghost.android.ghosthguapp;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GhostActionBarActivity extends ActionBarActivity {

    protected ActionBar ghostActionBar;
    protected View ghostCustomView;
    protected TextView ghostActionBarTitle;
    protected ImageView ghostActionBarLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ghostActionBar = getSupportActionBar();
        if(ghostActionBar != null) {
           ghostActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
           ghostActionBar.setDisplayShowHomeEnabled(false);
           ghostActionBar.setDisplayShowTitleEnabled(false);
           ghostActionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.notice_background));
            ghostActionBar.setDefaultDisplayHomeAsUpEnabled(false);
        }

        LayoutInflater inflater = LayoutInflater.from(this);

        ghostCustomView = inflater.inflate(R.layout.ghost_action_bar_layout, null);

        ghostActionBarTitle = (TextView) ghostCustomView.findViewById(R.id.ghost_actionbar_title);
        ghostActionBarTitle.setText(R.string.app_name);

        ghostActionBarLogo = (ImageView) ghostCustomView.findViewById(R.id.ghost_actionbar_logo);
        ghostActionBarLogo.setImageResource(R.drawable.buffering);

        ActionBar.LayoutParams params = new
                ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);

        if(ghostActionBar != null) {
            ghostActionBar.setCustomView(ghostCustomView, params);
            ghostActionBar.setDisplayShowCustomEnabled(true);
        }
    }
}
