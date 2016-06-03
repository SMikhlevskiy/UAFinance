package smikhlevskiy.uafinance.ui;


import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import smikhlevskiy.uafinance.net.FinanceUAAsyncTask;
import smikhlevskiy.uafinance.net.InterBankAsyncTask;
import smikhlevskiy.uafinance.net.NBUDatasAsyncTask;
import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.ui.wigets.SlidingTabLayout;
import smikhlevskiy.uafinance.utils.UAFConst;
import smikhlevskiy.uafinance.adapters.CurrencyFragmentPagerAdapter;
import smikhlevskiy.uafinance.adapters.OrganizationListAdapter;
import smikhlevskiy.uafinance.model.Currencie;
import smikhlevskiy.uafinance.model.FinanceUA;
import smikhlevskiy.uafinance.model.Organization;
import smikhlevskiy.uafinance.model.UAFPreferences;
import smikhlevskiy.uafinance.resivers.AlarmBroadcastReciver;

/*
* Created by SMikhlevskiy
*/

public class MainActivity extends AppCompatActivity {

    final static String TAG = MainActivity.class.getSimpleName();

    private OrganizationListAdapter organizationListAdapter;

    private UAFPreferences uafPreferences;


    private Handler mainActivityHandler;


    private Location deviceLocation = null;
    private LocationListener locationListener;
    private LocationListener locationNetworkListener;


    private HashMap<String, Currencie> privatHashMap = null;
    private HashMap<String, Currencie> ibHashMap = null;
    private FinanceUA financeUA = null;


    private ActionBarDrawerToggle mDrawerToggle = null;
    private ImageView animationImageView = null;


    private LocationManager locationManager;


    private CurrencyFragmentPagerAdapter adapterViewPager = null;


    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.drawerlayout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.navigation_view)
    NavigationView mNavigationView;

    @Bind(R.id.vpPager)
    ViewPager vpPager;

    @Bind(R.id.sliding_tabs)
    SlidingTabLayout slidingTabLayout;

    @Bind(R.id.spinerCity)
    Spinner spinnerCity;

    @Bind(R.id.spinerCurrency)
    Spinner spinnerCurrency;

    @Bind(R.id.spinerAskBid)
    Spinner spinnerAskBid;

    @Bind(R.id.listViewBanks)
    ListView organizationListView;

    @Bind(R.id.swLayout)
    RelativeLayout swLayout;

    @Bind(R.id.mySwitch)
    protected SwitchCompat mySwitch;

    private boolean isRefresh = false;



    void stopShowRefresh() {

        Log.i(TAG,"stop refresh");
        isRefresh = false;
        swipeContainer.setRefreshing(false);
        swLayout.setVisibility(View.VISIBLE);
    }


    /**
     * Remove Location Listener(end getting GPS location)
     */
    void removeLocationListener() {


        try {
            if ((locationListener != null) && (locationManager != null)) {
                locationManager.removeUpdates(locationListener);
                Log.i(TAG, "Remove Location Listener");
                locationListener = null;
            }
            if ((locationNetworkListener != null) && (locationManager != null)) {
                locationManager.removeUpdates(locationNetworkListener);
                Log.i(TAG, "Remove Location Listener");
                locationNetworkListener = null;
            }

        } catch (SecurityException se) {

            Log.i(TAG, "Program is not have permission");
        }

    }


    /**
     * get GPS location
     */
    void getDeviceLocation() {
        try {


            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (locationManager == null) {
                Log.d(TAG, "Location manager is null");
                return;
            }


            removeLocationListener();//remove old listener


            //listener for current GPS location
            locationListener = new LocationListener() {


                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "On GPS location changed");

                    if (location == null) return;
                    Log.d(TAG, "Have new GPS Location");


                    Location oldLocation = deviceLocation;
                    deviceLocation = location;

                    if ((oldLocation == null) ||
                            (oldLocation.distanceTo(location) > 500))
                        startRefreshDatas();


                    organizationListAdapter.setDeviceLocation(deviceLocation);
                    organizationListAdapter.notifyDataSetChanged();


                    Log.d(TAG, "Sucsessfull get GPS Location");

                    removeLocationListener();

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.d(TAG, "On status GPS provider changed");

                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.d(TAG, "On location GPS provider enabled");
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.e(TAG, "GPS provider is disabled");

                }
            };

            //listener for current Network location
            locationNetworkListener = new LocationListener() {


                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "On Network location changed");

                    if (location == null) return;
                    Log.d(TAG, "Have new Network Location");
                    Location oldLocation = deviceLocation;
                    deviceLocation = location;

                    if ((oldLocation == null) ||
                            (oldLocation.distanceTo(location) > 500))
                        startRefreshDatas();


                    organizationListAdapter.setDeviceLocation(deviceLocation);
                    organizationListAdapter.notifyDataSetChanged();


                    try {

                        locationManager.removeUpdates(locationNetworkListener);
                    } catch (SecurityException se) {

                    }
//                        // TODO: Send location to server & get new sorted stores list from server
//                        sortStores(storesAdapter.getStores());
//                        storesAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Sucsessfull get Network Location");

                    //removeLocationListener();

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.d(TAG, "On Network provider status changed");

                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.d(TAG, "On location  network provider enabled");
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.e(TAG, "Network provider is disabled");

                }
            };

            if (deviceLocation == null) {
                if ((locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)))
                    deviceLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


                if ((deviceLocation == null)
                        && (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)))
                    deviceLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


                if (deviceLocation != null) {
                    Log.d(TAG, "Last know location: " + deviceLocation.getLatitude() + ":" + deviceLocation.getLongitude());


                }

                if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationNetworkListener);
                if (deviceLocation != null)
                organizationListAdapter.setDeviceLocation(deviceLocation);
            }


            //start getting current GPS location
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


            if (deviceLocation == null)
                Log.d(TAG, "do not get last known location");

        } catch (SecurityException se) {
            deviceLocation = null;
            Log.e(TAG, "Program is not have Location permission");
        }


    }


    /**
     * Start getting datas from Internet
     */
    public void startRefreshDatas() {


        if (isRefresh) return;

        Log.i(TAG, "start refresh");
        isRefresh = true;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        swipeContainer.setProgressViewOffset(false, -200, height / 9);
        swipeContainer.setRefreshing(true);
        swLayout.setVisibility(View.GONE);


        getDeviceLocation();


        (new FinanceUAAsyncTask(
                this,
                uafPreferences.getCity(),
                uafPreferences.getCurrancie(),
                (uafPreferences.getAskBid().equals(MainActivity.this.getResources().getStringArray(R.array.askbid)[0])),
                uafPreferences.getSortCurrency(),

                false,
                deviceLocation,
                mainActivityHandler

        )).execute();

        (new NBUDatasAsyncTask(mainActivityHandler)).execute();


        (new InterBankAsyncTask(mainActivityHandler, new String[]{getString(R.string.USD),
                getString(R.string.EUR),
                getString(R.string.RUB)})).execute();


    }

    /**
     * start LocationMap Activty  - Big Map with all known organization
     */
    private void showLocationMapActivity() {
        if (financeUA == null) return;
        Intent mapIntent = new Intent(MainActivity.this, LocationMapActivity.class);
        Log.i(TAG, "Start " + LocationMapActivity.TAG);
        mapIntent.putExtra(Location.class.getSimpleName(), deviceLocation);
        startActivity(mapIntent);


    }

    /**
     * Setup NavigationView
     */
    private void setupNavigationView() {


        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(UAFConst.getSpanTitle(this));
            actionBar.setHomeButtonEnabled(true);
        }

//        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbargradient));


        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.navmenu_map://start LocationMap Activity
                        mDrawerLayout.closeDrawer(mNavigationView);

                        showLocationMapActivity();
                        break;


                    case R.id.navmenu_kerbstone_finance_ua:// BlackMarket by Finance.ua
                        mDrawerLayout.closeDrawer(mNavigationView);
                        Intent intent = new Intent(MainActivity.this, KerbstoneActivity.class);
                        intent.putExtra(KerbstoneActivity.URL_PAR_NAME, getString(R.string.kerbstone_finance_ua_URL));
                        startActivity(intent);
                        break;
                    case R.id.navmenu_kerbstone_finance_i:// BlackMarket by Finance.i.ua
                        mDrawerLayout.closeDrawer(mNavigationView);
                        Intent iintent = new Intent(MainActivity.this, KerbstoneActivity.class);
                        iintent.putExtra(KerbstoneActivity.URL_PAR_NAME, getString(R.string.kerbstone_finance_i_URL));
                        startActivity(iintent);
                        break;
                    case R.id.navmenu_opt:// Options
                        mDrawerLayout.closeDrawer(mNavigationView);

                        Intent optinent = new Intent(MainActivity.this, UAFPreferencesActivity.class);
                        startActivity(optinent);

                        break;


                    default:
                        mDrawerLayout.closeDrawer(mNavigationView);
                }


                return true;
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.openNavView, R.string.closeNawView) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

    }

    /**
     * Setup ViewPager in bottom hider for CurencyCashFragment,NBUFragment,InterbankFragment,PreciosMetalsFragment
     */
    public void setupButtomHiderViewPager() {
        int count = UAFConst.CURRENCY_FRAGMENT_COUNT;

        adapterViewPager = new CurrencyFragmentPagerAdapter(getSupportFragmentManager(), this, count);


        vpPager.setAdapter(adapterViewPager);

        // Hack to set datas to Fragment(becouse trouble when restore fragment)
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(CurrencyFragmentPagerAdapter.TAG, "Position=" + position);
                android.support.v4.app.Fragment fragment = adapterViewPager.getFragmentByPosition(position, vpPager);
                if (fragment != null)
                    adapterViewPager.getItemPosition(fragment);//for set datas to Fragment

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


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

    /**
     * Setup Content of Spinners in Up Hider (SpinerCity, SpinnerCurrency & SpinnerAskBid )
     */
    public void setupContentUpHiderSpinners() {


        if (spinnerCity != null) {

            String[] citiesArray = (String[]) financeUA.getCities().values().toArray(new String[0]);


            String[] cities = new String[citiesArray.length + 2];

            cities[0] = uafPreferences.getCity();
            cities[1] = this.getResources().getString(R.string.default_city);

            for (int i = 0; i < citiesArray.length; i++)
                cities[i + 2] = citiesArray[i];


            ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cities);
            spinnerCity.setAdapter(cityAdapter);
            spinnerCity.setSelection(cityAdapter.getPosition(uafPreferences.getCity()));
        }


        if (spinnerCurrency != null) {

            ArrayList currencyArrayList = new ArrayList<String>();

            currencyArrayList.add(this.getResources().getString(R.string.USD));
            currencyArrayList.add(this.getResources().getString(R.string.RUB));
            currencyArrayList.add(this.getResources().getString(R.string.EUR));

            String[] currencyArray = (String[]) financeUA.getCurrancies().keySet().toArray(new String[0]);
            for (int i = 0; i < currencyArray.length; i++)
                currencyArrayList.add(currencyArray[i]);
            String allCurrancies[] = (String[]) currencyArrayList.toArray(new String[0]);

            ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allCurrancies);
            spinnerCurrency.setAdapter(currencyAdapter);
            spinnerCurrency.setSelection(currencyAdapter.getPosition(uafPreferences.getCurrancie()));
        }

        if (organizationListAdapter != null) {
            organizationListAdapter.setFinanceUA(financeUA);
            organizationListAdapter.notifyDataSetChanged();
        }

    }

    /*---------------------------------------------------------------------------------------------
    * ------------------------------ON CREATE------------------------------------------------------
    ---------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Begin OnCreate");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        startRefresh = true;

        setupButtomHiderViewPager();


        setupNavigationView();


        uafPreferences = new UAFPreferences(this);


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                startRefreshDatas();
                Log.d(TAG, "OnSwipe to Refresh");
            }
        });

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                uafPreferences.setSortCurrency(b);
                startRefreshDatas();


            }
        });
        //---------CurrencieSpinner


        ArrayAdapter<String> CurrencieAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{uafPreferences.getCurrancie()});
        spinnerCurrency.setAdapter(CurrencieAdapter);


        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //ArrayAdapter <String> aaaa=parent;

                if (!((String) parent.getAdapter().getItem(position)).equals(uafPreferences.getCurrancie())) //  was changed)
                {
                    uafPreferences.setCurrancie((String) parent.getAdapter().getItem(position));


                    startRefreshDatas();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //---------CitySpinner


        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{uafPreferences.getCity()});
        spinnerCity.setAdapter(cityAdapter);


        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (!((String) parent.getAdapter().getItem(position)).equals(uafPreferences.getCity())) {// city was changed
                    uafPreferences.setCity((String) parent.getAdapter().getItem(position));


                    Log.i(TAG, "City was changed");


                    startRefreshDatas();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //--------askBidSpinner---------
        String askBid[] = getResources().getStringArray(R.array.askbid);


        ArrayAdapter<String> askBidAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, askBid);
        spinnerAskBid.setAdapter(askBidAdapter);

        spinnerAskBid.setSelection(askBidAdapter.getPosition(uafPreferences.getAskBid()));

        spinnerAskBid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //ArrayAdapter <String> aaaa=parent;
                if (!((String) parent.getAdapter().getItem(position)).equals(uafPreferences.getAskBid())) //  was changed)
                {
                    uafPreferences.setAskBid((String) parent.getAdapter().getItem(position));


                    startRefreshDatas();
                }


            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //  ---- Setup OrganizationListView(Main ListView in the center of activity)
        organizationListAdapter = new OrganizationListAdapter(this);

        organizationListView.setAdapter(organizationListAdapter);
        organizationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Organization organization = (Organization) organizationListAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, OrganizationActivity.class);


                intent.putExtra("organization", organization);
                intent.putExtra("city", financeUA.getCities().get(organization.getCityId()));
                if (organization.getRegionId().equals(""))
                    intent.putExtra("region", "");
                else
                    intent.putExtra("region", financeUA.getRegions().get(organization.getRegionId()));


                startActivity(intent);

            }
        });

        //------------------------Main Handler of Activity-------------------
        mainActivityHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
//                if (msg!=null)
                switch (msg.what) {
                    case 1://on Read finance datas
                        financeUA = (FinanceUA) msg.obj;
                        Log.i(TAG,"MAIN HANDLER: On UAFinance");


                        if (financeUA != null) {
                            Log.i(TAG,"UAFinance OK");
                            ((BaseAdapter) organizationListView.getAdapter()).notifyDataSetChanged();
                            adapterViewPager.setFinanceUA(financeUA);
                            adapterViewPager.notifyDataSetChanged();
                            setupContentUpHiderSpinners();

                        }

                        // Remove the animation.
                        stopShowRefresh();
                        Log.d(TAG, "OnFinanceUA read");

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


        //--------------- Start Alarm for out Notifiction----------
        if (savedInstanceState == null)
            AlarmBroadcastReciver.setAlarm(this);


        mySwitch.setChecked(uafPreferences.getSortCurrency());

        startRefreshDatas();

        //startRefreshDatas();
        Log.i(TAG, "End OnCreate");
    }


    /*---------------------------------------------------------------------
    --------------------------- On Click----------------------------------
    ---------------------------------------------------------------------- */
    public void onclick(View v) {
        switch (v.getId()) {


            case R.id.showMapButton:

                showLocationMapActivity();// Show BigMAp with all known organizations
                break;


        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        switch (id) {
            case android.R.id.home://start NavigationView
                if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                    mDrawerLayout.closeDrawer(mNavigationView);
                } else {
                    mDrawerLayout.openDrawer(mNavigationView);
                    break;
                }
        }
        //return super.onOptionsItemSelected(item);
        return true;
    }


    @Override
    protected void onPause() {
        removeLocationListener();// for Power economy
        super.onPause();
    }


}
