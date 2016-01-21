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
import smikhlevskiy.uafinance.Utils.UAFConstansts;
import smikhlevskiy.uafinance.Utils.UAFinancePreference;
import smikhlevskiy.uafinance.model.FinanceUA;
import smikhlevskiy.uafinance.model.GeoLocationDB;
import smikhlevskiy.uafinance.model.Organization;

/**
 * Created by tcont98 on 09-Nov-15.
 */
public class OrganizationListAdapter extends BaseAdapter {
    private FinanceUA financeUA = null;
    private Context context;
    private UAFinancePreference uaFinancePreference;
    private Location deviceLocation = null;
    private GeoLocationDB geoLocationDB = null;

    public OrganizationListAdapter(Context context) {
        this.context = context;
        uaFinancePreference = new UAFinancePreference((context));
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

        if (organization.getOrgType() == 1)
            imageView.setImageResource(R.mipmap.bank);
        else
            imageView.setImageResource(R.mipmap.exchange_shop);
        String lowertitle = organization.getTitle().toLowerCase();
        if (lowertitle.contains(UAFConstansts.PRIVAT_LC))
            imageView.setImageResource(R.mipmap.bank_privat);
        else if (lowertitle.contains("правэкс"))
            imageView.setImageResource(R.mipmap.bank_praveks);
        else if (lowertitle.contains("пумб"))
            imageView.setImageResource(R.mipmap.bank_pumb);
        else if (lowertitle.contains("укргазбанк"))
            imageView.setImageResource(R.mipmap.bank_ukrgazbank);
        else if (lowertitle.contains("альфа"))
            imageView.setImageResource(R.mipmap.bank_alfa);
        else if (lowertitle.contains(UAFConstansts.UKRSOC_LC))
            imageView.setImageResource(R.mipmap.bank_ukrsoc);
        else if (lowertitle.contains("отп банк"))
            imageView.setImageResource(R.mipmap.bank_otp);
        else if (lowertitle.contains("агриколь"))
            imageView.setImageResource(R.mipmap.bank_agricol);
        else if (lowertitle.contains("укрсиб"))
            imageView.setImageResource(R.mipmap.bank_ukrsib);
        else if (lowertitle.contains("ощад"))
            imageView.setImageResource(R.mipmap.bank_oschad);
        else if (lowertitle.contains("таскомбанк"))
            imageView.setImageResource(R.mipmap.bank_tas);
        else if (lowertitle.contains("укрсиб"))
            imageView.setImageResource(R.mipmap.bank_ukrsib);
        else if (lowertitle.contains("авал"))
            imageView.setImageResource(R.mipmap.bank_aval);
        else if (lowertitle.contains("сбербанк"))
            imageView.setImageResource(R.mipmap.bank_sberbank);


        textName.setText(organization.getTitle());

        TextView textDistance = (TextView) convertView.findViewById(R.id.itemDistance);
        textDistance.setVisibility(View.INVISIBLE);
        if (deviceLocation != null) {
            Location locationOrganization = new Location(organization.getTitle());

            LatLng latLng = geoLocationDB.getLocation(FinanceUA.getAddressbyAdressCity(financeUA.getCities().get(organization.getCityId()), organization.getAddress()));
            if (latLng != null) {
                locationOrganization.setLongitude(latLng.longitude);
                locationOrganization.setLatitude(latLng.latitude);


                float distance = locationOrganization.distanceTo(deviceLocation) / 1000;
                textDistance.setText(new DecimalFormat("####.#").format(distance) + " " + context.getString(R.string.km));
                textDistance.setVisibility(View.VISIBLE);
            }
        }


        //uaFinancePreference.getAskBid(), uaFinancePreference.getCity(), uaFinancePreference.getCurrancie()


        TextView textCurrancie = (TextView) convertView.findViewById(R.id.itemCurrencie);
        textCurrancie.setText(new Double(organization.getCurrentCurrencyVal()).toString());


        return convertView;
    }
}
