package smikhlevskiy.uafinance.UI;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;

import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.Net.FinanceUAAsyncTask;
import smikhlevskiy.uafinance.Net.InterBankAsyncTask;
import smikhlevskiy.uafinance.Net.PrivatAsyncTask;
import smikhlevskiy.uafinance.Utils.UAFinancePreference;
import smikhlevskiy.uafinance.adapters.OrganizationListAdapter;
import smikhlevskiy.uafinance.model.Currencie;
import smikhlevskiy.uafinance.model.FinanceUA;
import smikhlevskiy.uafinance.model.Organization;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //private EditText resultTextEdit;
    final static String TAG = MainActivity.class.getSimpleName();
    private OrganizationListAdapter organizationListAdapter;
    private UAFinancePreference uaFinancePreference;
    private ListView organizationListView;

    private Handler mainActivityReDrawHandler;
    private boolean startRefresh = true;
    private boolean prFirstMenuCreate = true;
    private Location deviceLocation = null;
    private GoogleApiClient mGoogleApiClient = null;


    private HashMap<String, Currencie> privatHashMap = null;
    private HashMap<String, Currencie> ibHashMap = null;


    private DrawerLayout mDrawerLayout = null;
    private NavigationView mNavigationView = null;
    private ActionBarDrawerToggle mDrawerToggle = null;

    private MenuItem refreshMenuItem = null;

    private void startAnimation() {
        //--Start Animation---
        LayoutInflater inflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView) inflater.inflate(R.layout.animation_refresh, null);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);
        refreshMenuItem.setActionView(iv);
    }

    /*-----------*/
    public void startRefreshDatas() {


        (new FinanceUAAsyncTask(
                this,
                uaFinancePreference.getCity(),
                false,
                mainActivityReDrawHandler,
                organizationListAdapter,
                (Spinner) findViewById(R.id.spinerCurrency),
                (Spinner) findViewById(R.id.spinerCity)
        )).execute(getString(R.string.financeua_json_path));

        (new PrivatAsyncTask(mainActivityReDrawHandler)).execute();


        (new InterBankAsyncTask(mainActivityReDrawHandler, new String[]{getString(R.string.USD),
                getString(R.string.EUR),
                getString(R.string.RUB)})).execute();


    }

    /*---*/
    public void showCurrencyCashFragment(FinanceUA financeUA) {
        Fragment fragment = getFragmentManager().findFragmentByTag(CurrencyCashFragment.TAG);
        if (fragment == null) {

            fragment = new CurrencyCashFragment();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.CurencieFrame, fragment, CurrencyCashFragment.TAG);
            ft.commit();

        }


        if (financeUA != null)
            ((IShowFragment) fragment).setFinanceUA(financeUA);

    }

    /*---*/
    public void showInterBankFragment(FinanceUA financeUA) {
        Fragment fragment = getFragmentManager().findFragmentByTag(InterBankFragment.TAG);
        if (fragment == null) {

            fragment = new InterBankFragment();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.CurencieFrame, fragment, InterBankFragment.TAG);
            ft.commit();

        }


        if (ibHashMap != null) ((InterBankFragment) fragment).setIBHashMap(ibHashMap);

    }

    /*---*/
    public void showNBUFragment(FinanceUA financeUA) {
        Fragment fragment = getFragmentManager().findFragmentByTag(NBUFragment.TAG);
        if (fragment == null) {

            fragment = new NBUFragment();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.CurencieFrame, fragment, NBUFragment.TAG);
            ft.commit();

        }


        if (privatHashMap != null)
            ((NBUFragment) fragment).setPrivatHashMap(privatHashMap);

    }

    /*---*/
    public void showPreciousMetalsFragment() {
        Fragment fragment = getFragmentManager().findFragmentByTag(PreciousMetalsFragment.TAG);
        if (fragment == null) {

            fragment = new PreciousMetalsFragment();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.CurencieFrame, fragment, PreciousMetalsFragment.TAG);
            ft.commit();

        }

        if (privatHashMap != null)
            ((PreciousMetalsFragment) fragment).setPrivatHashMap(privatHashMap);


    }

    /*-----*/
    public void reDrawInterBank() {
        Fragment fragment = getFragmentManager().findFragmentByTag(InterBankFragment.TAG);
        if (fragment != null) {
            ((InterBankFragment) fragment).setIBHashMap(ibHashMap);
            ((InterBankFragment) fragment).drawIB();
        }
    }

    /*-----*/
    public void reDrawNBU() {
        Fragment fragment = getFragmentManager().findFragmentByTag(NBUFragment.TAG);
        if (fragment != null) {
            ((NBUFragment) fragment).setPrivatHashMap(privatHashMap);
            ((NBUFragment) fragment).drawNBU();
        }
        fragment = getFragmentManager().findFragmentByTag(PreciousMetalsFragment.TAG);
        if (fragment != null) {
            ((PreciousMetalsFragment) fragment).setPrivatHashMap(privatHashMap);
            ((PreciousMetalsFragment) fragment).drawNBU();
        }
    }

    /* ------------*/
    public void reDrawMainActivity() {
        FinanceUA financeUA = ((OrganizationListAdapter) organizationListView.getAdapter()).getFinanceUA();
        if (financeUA == null) return;
        financeUA.sort((uaFinancePreference.getAskBid().equals(MainActivity.this.getResources().getStringArray(R.array.askbid)[0])),
                uaFinancePreference.getCity(), uaFinancePreference.getCurrancie());


        ((BaseAdapter) organizationListView.getAdapter()).notifyDataSetChanged();
        Fragment fragment;
        fragment = getFragmentManager().findFragmentByTag(CurrencyCashFragment.TAG);
        if (fragment != null) {
            ((IShowFragment) fragment).setFinanceUA(financeUA);
            ((IShowFragment) fragment).drawFinanceUA();
        }


//

    }
private void showLocationMapActivity(){
    if (((OrganizationListAdapter) organizationListView.getAdapter()).getFinanceUA() != null) {
        Intent mapIntent = new Intent(MainActivity.this, LocationMapActivity.class);
        //FinanceUA financeUA=((OrganizationListAdapter) organizationListView.getAdapter()).getFinanceUA();
        //mapIntent.putExtra(FinanceUA.class.getSimpleName(),financeUA);
        Log.i(TAG, "Start " + LocationMapActivity.TAG);
        mapIntent.putExtra(Location.class.getSimpleName(), deviceLocation);
        startActivity(mapIntent);
        //mCurrentSelectedPosition = 0;
    }


}
    private void setupTolbarNavigationView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbargradient));
        String titleString = getResources().getString(R.string.title_activity_finance);
        Spannable span = new SpannableString(titleString);
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.myYellow)), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.myBlu)), 1, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBar.setTitle(span);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.navmenu_map:
                        showLocationMapActivity();
                        break;
                    case R.id.navmenu_cur:
                        //mCurrentSelectedPosition = 1;
                        break;
                    case R.id.navmenu_dist:
                        //mCurrentSelectedPosition = 2;
                        break;
                    case R.id.navmenu_opt:
                        //mCurrentSelectedPosition = 3;
                        break;
                }

                //setTabs(mCurrentSelectedPosition + 1);
                mDrawerLayout.closeDrawer(mNavigationView);
                return true;
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.openNavView, R.string.closeNawView) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle(getString(R.string.drawer_opened));
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Begin OnCreate");

        super.onCreate(savedInstanceState);


        startRefresh = true;
        setContentView(R.layout.activity_main);


        //CurrencieFragment

        if (savedInstanceState == null)
            showCurrencyCashFragment(null);


        //ActionBar


        setupTolbarNavigationView();


        uaFinancePreference = new UAFinancePreference(this);


        //---------CurrencieSpinner

        Spinner spinnerCurrencie = ((Spinner) findViewById(R.id.spinerCurrency));

        ArrayAdapter<String> CurrencieAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{uaFinancePreference.getCurrancie()});
        spinnerCurrencie.setAdapter(CurrencieAdapter);


        spinnerCurrencie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //ArrayAdapter <String> a=parent;

                uaFinancePreference.setCurrancie((String) parent.getAdapter().getItem(position));

                reDrawMainActivity();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //---------CitySpinner

        Spinner spinnerCity = ((Spinner) findViewById(R.id.spinerCity));

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{uaFinancePreference.getCity()});
        spinnerCity.setAdapter(cityAdapter);


        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //ArrayAdapter <String> a=parent;

                if (!((String) parent.getAdapter().getItem(position)).equals(uaFinancePreference.getCity())) {// city was changed
                    uaFinancePreference.setCity((String) parent.getAdapter().getItem(position));

                    reDrawMainActivity();
                    Log.i(TAG, "City was changed");
                    startAnimation();
                    startRefreshDatas();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //--------askBidSpinner---------
        String askBid[] = getResources().getStringArray(R.array.askbid);
        Spinner spinnerAskBid = ((Spinner) findViewById(R.id.spinerAskBid));

        ArrayAdapter<String> askBidAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, askBid);
        spinnerAskBid.setAdapter(askBidAdapter);

        spinnerAskBid.setSelection(askBidAdapter.getPosition(uaFinancePreference.getAskBid()));

        spinnerAskBid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //ArrayAdapter <String> a=parent;

                uaFinancePreference.setAskBid((String) parent.getAdapter().getItem(position));

                reDrawMainActivity();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinnerAskBid.setON`


        organizationListAdapter = new OrganizationListAdapter(this);
        organizationListView = (ListView) findViewById(R.id.listViewBanks);
        organizationListView.setAdapter(organizationListAdapter);
        organizationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Organization organization = (Organization) organizationListAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, OrganizationActivity.class);


                intent.putExtra("organization", organization);
                intent.putExtra("city", organizationListAdapter.getFinanceUA().getCities().get(organization.getCityId()));
                intent.putExtra("region", organizationListAdapter.getFinanceUA().getRegions().get(organization.getRegionId()));


                startActivity(intent);

            }
        });


        //ab.setIcon(R.mipmap.currency_exchange);
        //ab.setDisplayOptions(ab.DISPLAY_SHOW_HOME | ab.DISPLAY_SHOW_TITLE);

        //resultTextEdit = (EditText) findViewById(R.id.editTextResult);

        mainActivityReDrawHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1://on Read finance datas
                        reDrawMainActivity();

                        // Remove the animation.
                        refreshMenuItem.getActionView().clearAnimation();
                        refreshMenuItem.setActionView(null);

                        break;
                    case 2:
                        reDrawNBU();
                        privatHashMap = (HashMap<String, Currencie>) msg.obj;
                        break;
                    case 3:
                        reDrawInterBank();
                        ibHashMap = (HashMap<String, Currencie>) msg.obj;
                        break;

                }

            }
        };

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            if (mGoogleApiClient == null) Log.i(TAG, "GoogleApiClient init false");
        }
        //startRefreshDatas();
        Log.i(TAG, "End OnCreate");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        refreshMenuItem = menu.findItem(R.id.refreshmenuitem);
        if (prFirstMenuCreate) {
            startAnimation();
            prFirstMenuCreate = false;
        }

/*
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        ImageView a = (ImageView) inflater.inflate(R.id.refreshmenuitem, null);

        //ImageView a=((ImageView) menu.findItem(R.id.refreshmenuitem).getActionView());
        if (a==null) return true;
        RotateAnimation ra =new RotateAnimation(0, 360);
        ra.setFillAfter(true);
        ra.setDuration(1000);

        a.startAnimation(ra);
*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (item.getItemId()) {
            case R.id.refreshmenuitem:
                startAnimation();
                startRefreshDatas();

                break;
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                    mDrawerLayout.closeDrawer(mNavigationView);
                } else {
                    mDrawerLayout.openDrawer(mNavigationView);
                }
//                finish();
                /*
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
                */
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (startRefresh)
            startRefreshDatas();

        startRefresh = false;
        if (mGoogleApiClient != null) mGoogleApiClient.connect();

    }


    public void onclick(View v) {
        switch (v.getId()) {
            case R.id.currencyCashButton:

                showCurrencyCashFragment(((OrganizationListAdapter) organizationListView.getAdapter()).getFinanceUA());

                break;
            case R.id.InterBankButton:
                showInterBankFragment(((OrganizationListAdapter) organizationListView.getAdapter()).getFinanceUA());

                break;
            case R.id.NBUButton:
                showNBUFragment(((OrganizationListAdapter) organizationListView.getAdapter()).getFinanceUA());

                break;
            case R.id.PreciousMetalsButton:
                showPreciousMetalsFragment();

                break;

            case R.id.showMapButton:
                showLocationMapActivity();
                break;


        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Google API client: On connected");
        deviceLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (deviceLocation != null) {
            Log.i(TAG, deviceLocation.getLatitude() + ":" + deviceLocation.getLongitude());
            organizationListAdapter.setDeviceLocation(deviceLocation);
        } else
            Log.i(TAG, "Google API client: GetLastLocation failed");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
