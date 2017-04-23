package ghost.android.ghosthguapp.sickdan;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import ghost.android.ghosthguapp.R;

public class Tabs_for_sickdan extends Activity implements OnClickListener {
    Button haksick_button, moms_button, hyoam_button;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs_layout_for_haksick);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new Haksick());
        fragmentTransaction.commit();

        haksick_button = (Button)findViewById(R.id.haksick_button);
        moms_button = (Button)findViewById(R.id.moms_button);
        hyoam_button = (Button)findViewById(R.id.hyoam_button);

        haksick_button.setSelected(true);
        moms_button.setSelected(false);
        hyoam_button.setSelected(false);

        haksick_button.setOnClickListener(this);
        moms_button.setOnClickListener(this);
        hyoam_button.setOnClickListener(this);

    }

    public void onClick(View v){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch(v.getId())
        {
            case R.id.haksick_button:
                fragmentTransaction.replace(R.id.fragment_container, new Haksick());
                fragmentTransaction.commit();
                haksick_button.setSelected(true);
                moms_button.setSelected(false);
                hyoam_button.setSelected(false);
                break;
            case R.id.moms_button:
                fragmentTransaction.replace(R.id.fragment_container, new Moms());
                fragmentTransaction.commit();
                moms_button.setSelected(true);
                haksick_button.setSelected(false);
                hyoam_button.setSelected(false);
                break;
            case R.id.hyoam_button:
                fragmentTransaction.replace(R.id.fragment_container, new Hyoam());
                fragmentTransaction.commit();
                hyoam_button.setSelected(true);
                haksick_button.setSelected(false);
                moms_button.setSelected(false);
                break;
        }

    }

}