package smikhlevskiy.uafinance.ui;

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
 * Created by SMikhlevskiy on 24-Jan-16.
 * Activty BlackMarket by WebView
 */
public class KerbstoneActivity extends AppCompatActivity {
    public static final String TAG = KerbstoneActivity.class.getSimpleName();
    public static final String URL_PAR_NAME = "url_name";
    private String cur_url = "";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kerbstone);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        ActionBar ab = getSupportActionBar();
//        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbargradient));

        ab.setDisplayHomeAsUpEnabled(true);

        cur_url = getIntent().getExtras().getString(URL_PAR_NAME);

        webView = (WebView) findViewById(R.id.kerbstone_webview);


        webView.loadUrl(cur_url);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, url);
                if (url.contains(cur_url)) {
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
