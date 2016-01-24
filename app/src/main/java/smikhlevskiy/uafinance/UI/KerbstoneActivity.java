package smikhlevskiy.uafinance.UI;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import smikhlevskiy.uafinance.R;

/**
 * Created by tcont98 on 24-Jan-16.
 */
public class KerbstoneActivity extends AppCompatActivity {
    public static  final String TAG=KerbstoneActivity.class.getSimpleName();
private  WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kerbstone);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbargradient));

        ab.setDisplayHomeAsUpEnabled(true);

webView=(WebView)findViewById(R.id.kerbstone_webview);


        webView.loadUrl(getString(R.string.kerbstone_financeua_URL));

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Log.i(TAG, url);
                if (url.contains(getString(R.string.kerbstone_financeua_URL))) {
                    view.loadUrl(url);

                }
                return true;
            }

        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch (item.getItemId()) {
            case android.R.id.home:

                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }

                return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
