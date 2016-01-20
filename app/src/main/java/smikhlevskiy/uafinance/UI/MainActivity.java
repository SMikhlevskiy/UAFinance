package smikhlevskiy.uafinance.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
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
import smikhlevskiy.uafinance.UI.wigets.SlidingTabLayout;
import smikhlevskiy.uafinance.Utils.UAFConstansts;
import smikhlevskiy.uafinance.Utils.UAFinancePreference;
import smikhlevskiy.uafinance.adapters.OrganizationListAdapter;
import smikhlevskiy.uafinance.model.Currencie;
import smikhlevskiy.uafinance.model.FinanceUA;
import smikhlevskiy.uafinance.model.Organization;
import smikhlevskiy.uafinance.adapters.CurrencyFragmentPagerAdapter;

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
    private FinanceUA financeUA = null;


    private DrawerLayout mDrawerLayout = null;
    private NavigationView mNavigationView = null;
    private ActionBarDrawerToggle mDrawerToggle = null;

    private MenuItem refreshMenuItem = null;

    private CurrencyFragmentPagerAdapter adapterViewPager = null;

    private void startRefreshButtonAnimation() {
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

    /* ------------*/
    public void reDrawMainActivity() {

        if (financeUA == null) return;
        financeUA.sort((uaFinancePreference.getAskBid().equals(MainActivity.this.getResources().getStringArray(R.array.askbid)[0])),
                uaFinancePreference.getCity(), uaFinancePreference.getCurrancie());


        ((BaseAdapter) organizationListView.getAdapter()).notifyDataSetChanged();

        adapterViewPager.setFinanceUA(financeUA);
        adapterViewPager.notifyDataSetChanged();


//

    }

    private void showLocationMapActivity() {
        if (financeUA == null) return;
        Intent mapIntent = new Intent(MainActivity.this, LocationMapActivity.class);
        Log.i(TAG, "Start " + LocationMapActivity.TAG);
        mapIntent.putExtra(Location.class.getSimpleName(), deviceLocation);
        startActivity(mapIntent);
        //mCurrentSelectedPosition = 0;


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

    public void setTabs(int count) {
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new CurrencyFragmentPagerAdapter(getSupportFragmentManager(), this, count);
        vpPager.setAdapter(adapterViewPager);
//////
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        //slidingTabLayout.setTextColor(getResources().getColor(R.color.tab_text_color));
        //slidingTabLayout.setTextColorSelected(getResources().getColor(R.color.tab_text_color_selected));
        slidingTabLayout.setDistributeEvenly();
        slidingTabLayout.setViewPager(vpPager);
        slidingTabLayout.setTabSelected(0);

        // Change indicator color
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.uafredtext /*tab_indicator*/);
            }
        });

    }

    /*--------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Begin OnCreate");

        super.onCreate(savedInstanceState);


        startRefresh = true;
        setContentView(R.layout.activity_main);

        setTabs(UAFConstansts.CURRENCY_FRAGMENT_COUNT);


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
                    startRefreshButtonAnimation();
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
                intent.putExtra("city", financeUA.getCities().get(organization.getCityId()));
                intent.putExtra("region", financeUA.getRegions().get(organization.getRegionId()));


                startActivity(intent);

            }
        });


        mainActivityReDrawHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1://on Read finance datas
                        financeUA = (FinanceUA) msg.obj;
                        reDrawMainActivity();

                        // Remove the animation.
                        refreshMenuItem.getActionView().clearAnimation();
                        refreshMenuItem.setActionView(null);

                        break;
                    case 2:

                        privatHashMap = (HashMap<String, Currencie>) msg.obj;

                        adapterViewPager.setPrivat(privatHashMap);
                        adapterViewPager.notifyDataSetChanged();
                        break;
                    case 3:

                        ibHashMap = (HashMap<String, Currencie>) msg.obj;
                        adapterViewPager.setInterBank(ibHashMap);
                        adapterViewPager.notifyDataSetChanged();
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
            startRefreshButtonAnimation();
            prFirstMenuCreate = false;
        }

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
                startRefreshButtonAnimation();
                startRefreshDatas();

                break;
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                    mDrawerLayout.closeDrawer(mNavigationView);
                } else {
                    mDrawerLayout.openDrawer(mNavigationView);
                }
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


            case R.id.showMapButton:
                showLocationMapActivity();
                break;


        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Google API client: On connected");

        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
/*
        if ((locationManager==null) ||
                (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
*/
        try {
            deviceLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            if (deviceLocation != null) {
                Log.i(TAG, deviceLocation.getLatitude() + ":" + deviceLocation.getLongitude());
                organizationListAdapter.setDeviceLocation(deviceLocation);
            }

            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1000, 50, new LocationListener() {


                @Override
                public void onLocationChanged(Location location) {
                    deviceLocation = location;
                    organizationListAdapter.setDeviceLocation(deviceLocation);
                    organizationListAdapter.notifyDataSetChanged();

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });

        } catch (SecurityException se)
        {

            Log.i(TAG,"Program is not have permission");
        }

        /*
        deviceLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (deviceLocation != null) {
            Log.i(TAG, deviceLocation.getLatitude() + ":" + deviceLocation.getLongitude());
            organizationListAdapter.setDeviceLocation(deviceLocation);
        } else
            Log.i(TAG, "Google API client: GetLastLocation failed");
            */

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
