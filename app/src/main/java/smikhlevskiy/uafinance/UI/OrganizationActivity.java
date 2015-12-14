package smikhlevskiy.uafinance.UI;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.Locale;

import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.Utils.CachGeocodingLocation;
import smikhlevskiy.uafinance.Utils.UAFinancePreference;
import smikhlevskiy.uafinance.model.Organization;

public class OrganizationActivity extends AppCompatActivity implements OnMapReadyCallback {
    public String TAG = OrganizationActivity.class.getSimpleName();
    UAFinancePreference uaFinancePreference;
    EditText editTextSum;
    TextView calcResultTextView;
    Organization organization;
    Geocoder geocoder;
    private String city;
    private GoogleMap mMap;
    private UiSettings mUiSettings;

    private String calcResult(String s) {


        try {
            Double d = Double.parseDouble(s) * organization.getSortVal();
            String ss = new DecimalFormat("####.###").format(d);
            if (ss.length() > 7)
                return "######.##";
            else
                return ss;


        } catch (NumberFormatException e) {
            return "0.0";
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization);


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(this, Locale.getDefault());


        uaFinancePreference = new UAFinancePreference(this);

        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_gradient));

        ab.setDisplayHomeAsUpEnabled(true);



        organization = (Organization) getIntent().getExtras().getParcelable("organization");

        Log.i(TAG, "Lat:" + organization.getLatitude() + ", Longi:" + organization.getLongitude());



        ((TextView) findViewById(R.id.organization_title)).setText(organization.getTitle());


        city = getIntent().getExtras().getString("city");
        ((TextView) findViewById(R.id.organization_city)).setText(city);
        String region = getIntent().getExtras().getString("region");
        if (region.equals(city))
            ((TextView) findViewById(R.id.organization_region)).setText("");
        else
            ((TextView) findViewById(R.id.organization_region)).setText(region);

        ((TextView) findViewById(R.id.organization_adress)).setText(organization.getAddress());

        ((TextView) findViewById(R.id.organization_link)).setText(organization.getLink());

        ((TextView) findViewById(R.id.organization_telephone)).setText(organization.getPhone());

        ((Button) findViewById(R.id.buttonCallPhone)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "Your Phone_number"));
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", organization.getPhone(), null));
                startActivity(intent);
            }
        });
        //---Calculator----------
        ((TextView) findViewById(R.id.calckSum)).setText(uaFinancePreference.getAskBid());
        ((TextView) findViewById(R.id.calckCurr)).setText(uaFinancePreference.getCurrancie());
        editTextSum = (EditText) findViewById(R.id.editTextSum);

        calcResultTextView = (TextView) findViewById(R.id.calckResult);
        calcResultTextView.setText(calcResult(editTextSum.getText().toString()));

        editTextSum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calcResultTextView.setText(calcResult(s.toString()).toString());


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);

        mMap.setMyLocationEnabled(true);

        mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(organization.getLatitude(),
                                organization.getLongitude()))
                        .title(organization.getTitle())
        );
            mMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(organization.getLatitude(), organization.getLongitude()), 16));


/*
        CachGeocodingLocation cachGeocodingLocation = new CachGeocodingLocation();
        cachGeocodingLocation.getAddressFromLocation(city + ", " + organization.getAddress(),
                getApplicationContext(), new GeocoderHandler());
*/


        // Keep the UI Settings state in sync with the checkboxes.
        //     mUiSettings.setZoomControlsEnabled(isChecked(R.id.zoom_buttons_toggle));
        //     mUiSettings.setCompassEnabled(isChecked(R.id.compass_toggle));
        //     mUiSettings.setMyLocationButtonEnabled(isChecked(R.id.mylocationbutton_toggle));
        //     mMap.setMyLocationEnabled(isChecked(R.id.mylocationlayer_toggle));
        //     mUiSettings.setScrollGesturesEnabled(isChecked(R.id.scroll_toggle));
        //     mUiSettings.setZoomGesturesEnabled(isChecked(R.id.zoom_gestures_toggle));
        //     mUiSettings.setTiltGesturesEnabled(isChecked(R.id.tilt_toggle));
        //     mUiSettings.setRotateGesturesEnabled(isChecked(R.id.rotate_toggle));

    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            Bundle bundle = message.getData();

            switch (message.what) {
                case 0:

                    locationAddress = bundle.getString("address");
                    Toast.makeText(OrganizationActivity.this, locationAddress, Toast.LENGTH_LONG).show();
                    Log.i(TAG, locationAddress);
                    break;
                case 1:

                    //locationAddress = bundle.getString("address");
                    //Toast.makeText(OrganizationActivity.this,locationAddress,Toast.LENGTH_LONG).show();
                    double latitude = bundle.getDouble("latitude");
                    double longitude = bundle.getDouble("longitude");

                    mMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(new LatLng(latitude, longitude), 16));
                    // mMap.moveCamera(CameraUpdateFactory.zoomBy(17));

                    mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude, longitude))
                                    .title(organization.getTitle())
                    );
                    break;

                default:
                    locationAddress = null;
            }

        }
    }
}
