package smikhlevskiy.uafinance.ui;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.resivers.AlarmBroadcastReciver;

/**
 * Created by SMikhlevskiy on 31-Jan-16.
 *  Activty for set base parameters of program
 *  Now That are parameters of notifications
 *  parameters save in Prefernces
 */

public class UAFPreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uafprefernces);
//        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        ActionBar ab = getSupportActionBar();
        //ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbargradient));

        ab.setDisplayHomeAsUpEnabled(true);




        getFragmentManager().beginTransaction().replace(R.id.preferFrame, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_list_item);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify aaaa parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
