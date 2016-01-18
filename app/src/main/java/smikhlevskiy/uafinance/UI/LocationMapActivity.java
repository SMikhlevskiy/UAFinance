package smikhlevskiy.uafinance.UI;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;


import smikhlevskiy.uafinance.model.FinanceUA;

import smikhlevskiy.uafinance.model.GeoLocationDB;
import smikhlevskiy.uafinance.model.Organization;
import smikhlevskiy.uafinance.Net.FinanceUAAsyncTask;
import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.Utils.UAFinancePreference;


/**
 * Created by tcont98 on 10-Jan-16.
 */
public class LocationMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = LocationMapActivity.class.getSimpleName();
    private FinanceUA financeUA;
    private Location deviceLocation=null;
    UAFinancePreference uaFinancePreference;
    Handler mapHandler;

    Geocoder geocoder;

    private GoogleMap mMap;
    private UiSettings mUiSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        deviceLocation=(Location) getIntent().getExtras().getParcelable(Location.class.getSimpleName());
        setContentView(R.layout.activity_locationmap);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(this, Locale.getDefault());


        uaFinancePreference = new UAFinancePreference(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbargradient));

        ab.setDisplayHomeAsUpEnabled(true);

        // financeUA = (FinanceUA) getIntent().getExtras().getParcelable(FinanceUA.class.getSimpleName());


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);

        mMap.setMyLocationEnabled(true);



        if (deviceLocation!=null)
            mMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(deviceLocation.getLatitude(),deviceLocation.getLongitude()), 17)); else
            Log.i(TAG,"deviceLocation is null");


        mapHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Log.i(TAG, "On handleMessage");

                financeUA = (FinanceUA) msg.obj;
                GeoLocationDB geoLocationDB = new GeoLocationDB(LocationMapActivity.this, GeoLocationDB.DB_NAME, null, GeoLocationDB.DB_VERSION);
                if (geoLocationDB == null) return;
                for (Organization organization : financeUA.getOrganizations()) {
                    LatLng latLng = geoLocationDB.getLocation(FinanceUA.getAddressbyAdressCity(financeUA.getCities().get(organization.getCityId()), organization.getAddress()));
                    if (latLng != null) {
                        mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(organization.getTitle())
                        );


                    } else Log.i(TAG, "Do not find Location");

                    //-------------other Brunches----
                    if (organization.getOrganizationBrunches() != null)
                        for (Organization organizationBrunch : organization.getOrganizationBrunches()) {
                            latLng = geoLocationDB.getLocation(FinanceUA.getAddressbyAdressCity(financeUA.getCities().get(organizationBrunch.getCityId()), organizationBrunch.getAddress()));
                            if (latLng != null)
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(organizationBrunch.getTitle()));
                        }

                }
            }
        };
        (new FinanceUAAsyncTask(
                this,
                "",
                true,
                mapHandler,
                null,
                null,
                null
        )).execute(getString(R.string.financeua_json_path));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_organization, menu);
        return true;
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