package smikhlevskiy.uafinance.adapters;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.Utils.UAFConst;
import smikhlevskiy.uafinance.model.UAFPreferences;
import smikhlevskiy.uafinance.model.FinanceUA;
import smikhlevskiy.uafinance.model.GeoLocationDB;
import smikhlevskiy.uafinance.model.Organization;

/**
 * Created by SMikhlevskiy on 09-Nov-15.
 * Adapter for work with OrganizationsListView in MainActivity
 * Now There are 4 Fragmets: CurencyCashFragment,NBUFragment,InterbankFragment,PreciosMetalsFragment
 */
public class OrganizationListAdapter extends BaseAdapter {
    private FinanceUA financeUA = null;
    private Context context;
    private UAFPreferences UAFPreferences;
    private Location deviceLocation = null;
    private GeoLocationDB geoLocationDB = null;

    public OrganizationListAdapter(Context context) {
        this.context = context;
        UAFPreferences = new UAFPreferences((context));
        geoLocationDB = new GeoLocationDB(context, GeoLocationDB.DB_NAME, null, GeoLocationDB.DB_VERSION);
    }


    public FinanceUA getFinanceUA() {
        return financeUA;
    }

    public void setDeviceLocation(Location deviceLocation) {
        this.deviceLocation = deviceLocation;
    }

    public void setFinanceUA(FinanceUA financeUA) {
        this.financeUA = financeUA;
    }

    @Override
    public int getCount() {
        if (financeUA == null)
            return 0;
        else
            return financeUA.getFu_index().size();
    }

    @Override
    public Object getItem(int position) {
        if (financeUA == null)
            return null;
        else
            return financeUA.getOrganizations()[(financeUA.getFu_index().get(position))];

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater lInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = lInflater.inflate(R.layout.menu_organization_item, parent, false);


        }

        Organization organization = (Organization) getItem(position);

        TextView textName = (TextView) convertView.findViewById(R.id.itemName);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        imageView.setImageResource(UAFConst.getBankImage(organization.getOrgType(),organization.getTitle()));


        String title=organization.getTitle();
        for (int n = 0; n < UAFConst.banksLc.length; n++)
            if (title.toLowerCase().contains(UAFConst.banksLc[n]))
                title = UAFConst.banks[n];

        textName.setText(title);

        TextView textDistance = (TextView) convertView.findViewById(R.id.itemDistance);
        textDistance.setVisibility(View.INVISIBLE);
        if (deviceLocation != null) {
            Location locationOrganization = new Location(organization.getTitle());

            LatLng latLng = geoLocationDB.getLocation(UAFConst.getAddressbyAdressCity(financeUA.getCities().get(organization.getCityId()), organization.getAddress()));
            if (latLng != null) {
                locationOrganization.setLongitude(latLng.longitude);
                locationOrganization.setLatitude(latLng.latitude);


                float distance = locationOrganization.distanceTo(deviceLocation) / 1000;
                textDistance.setText(new DecimalFormat("####.#").format(distance) + " " + context.getString(R.string.km));
                textDistance.setVisibility(View.VISIBLE);
            }
        }


        //UAFPreferences.getAskBid(), UAFPreferences.getCity(), UAFPreferences.getCurrancie()


        TextView textCurrancie = (TextView) convertView.findViewById(R.id.itemCurrencie);
        textCurrancie.setText(new Double(organization.getCurrentCurrencyVal()).toString());


        return convertView;
    }
}
