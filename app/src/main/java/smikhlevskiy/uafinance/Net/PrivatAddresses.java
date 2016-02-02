package smikhlevskiy.uafinance.Net;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import smikhlevskiy.uafinance.Utils.UAFConst;
import smikhlevskiy.uafinance.model.GeoLocationDB;
import smikhlevskiy.uafinance.model.Organization;

/**
 * Created by tcont98 on 27-Jan-16.
 */
public class PrivatAddresses {
    public static final String TAG = PrivatAddresses.class.getSimpleName();

    private ArrayList<Organization> privatAdressesList;

    //---------------------------------------
    public ArrayList<Organization> getPrivatAdressesList() {
        return privatAdressesList;

    }

    //---------------------------------------------------------
    public void setLatLon(Context context) {
        if (privatAdressesList==null) return;
        GeoLocationDB geoLocationDB = new GeoLocationDB(context, GeoLocationDB.DB_NAME, null, GeoLocationDB.DB_VERSION);

        List<String> textAddress = new ArrayList<String>();
        for (Organization organization : privatAdressesList) {
            textAddress.add(UAFConst.getAddressbyAdressCity(organization.getCityId(), organization.getAddress()));
            //Log.i(TAG,UAFConst.getAddressbyAdressCity(organization.getCityId(), organization.getAddress()));
        }

        HashMap<String, LatLng> latLngHashMap = geoLocationDB.updteLocationBase(textAddress);
        for (Organization organization : privatAdressesList) {
            String address = UAFConst.getAddressbyAdressCity(organization.getCityId(), organization.getAddress());
            if (latLngHashMap.containsKey(address))

                organization.setLatLong(latLngHashMap.get(address));


        }
    }

    //----------------------------------------------------------------------------------------------

    public void read(String city) {

        Log.i(TAG, "getPrivatAdressesList");
        StringBuilder bulder = new StringBuilder("");
        try {
            //  from URL
            InputStreamReader isr;
            URL url;
            if (city.length() != 0)
                url = new URL("https://api.privatbank.ua/p24api/pboffice?json&city=" + URLEncoder.encode(city, "utf-8"));

            else
              url = new URL("https://api.privatbank.ua/p24api/pboffice?json");


            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            isr = new InputStreamReader(con.getInputStream());


            BufferedReader reader = new BufferedReader(isr);
            String str = null;

            do {
                str = reader.readLine();
                if (str != null) {
                    bulder.append(str);
                    //Log.i(TAG, str);
                }
            } while (str != null);

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            privatAdressesList = null;
            return;
        } catch (IOException e1) {
            e1.printStackTrace();
            privatAdressesList = null;
            return;

        }


        privatAdressesList = new ArrayList<Organization>();
        try {
            Log.i(TAG, "getPrivatAdressesList");


            JSONArray array = new JSONArray(bulder.toString());

            if (array.length() <= 0) {
                privatAdressesList = null;
                return;

            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                String pcity = item.getString("city");
                if ((city.length() == 0) || (pcity.contains(city))) {
                    Organization organization = new Organization();
                    organization.setTitle(UAFConst.banks[UAFConst.PRIVAT_ID] + " :" + item.getString("name"));
                    organization.setAddress(item.getString("address"));
                    organization.setPhone(item.getString("phone"));
                    //!!!!! if (city.length() == 0)
                        organization.setCityId(pcity);

                    privatAdressesList.add(organization);

                    if (city.length() == 0) {

                    }


                }


            }

        } catch (JSONException e) {


            privatAdressesList = null;
            return;


        }


        Log.i(TAG, "PrivatAddressesLenght=" + privatAdressesList.size());

    }

}
