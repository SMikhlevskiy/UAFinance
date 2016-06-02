package smikhlevskiy.uafinance.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by SMikhlevskiy on 05-Dec-15.
 * Utils for work with Geocoding Locations
 */
public class GeoLocationUtils {

    private static final String TAG = GeoLocationUtils.class.getSimpleName();
    //private Geocoder geocoder;


    /**
     * Get Latitude&Longtitude from Google Map by request
     * @param locationAddress  - adress in request format
     * @return
     */
    public LatLng getAddressFromLocationByURL(final String locationAddress) {

        //LatLng address=new LatLng();
        StringBuilder bulder = new StringBuilder("");
        try {
            InputStreamReader isr;
            String text="https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(locationAddress, "utf-8") + "&key=AIzaSyCN-atwyMdbfyAW3FKeHlCGb7aSn8sY4Ew&language=ru";
            //Log.i(TAG, text);
            URL url = new URL(text);

            //Log.i(TAG, url.toString());

//https://maps.googleapis.com/maps/api/geocode/json?address=Украина,+Берегово,+пл.+Л.+Кошута,+2&key=AIzaSyCN-atwyMdbfyAW3FKeHlCGb7aSn8sY4Ew
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();



            con.setReadTimeout(15000);
            con.setConnectTimeout(15000);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            con.setDoOutput(true);


            if (con.getResponseCode() != HttpsURLConnection.HTTP_OK) return null;

            isr = new InputStreamReader(con.getInputStream());


            //***

            BufferedReader reader = new BufferedReader(isr);
            String str = null;

            do {
                str = reader.readLine();
                if (str != null)
                    bulder.append(str);
            } while (str != null);

            isr.close();
            con.disconnect();

            Thread.sleep(100,0);

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return null;
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Log.i(TAG,bulder.toString());
            JSONObject obj = new JSONObject(bulder.toString());
            if (obj==null) return null;

            JSONArray array = obj.getJSONArray("results");
            if (array.length()<=0) return null;

            JSONObject item = array.getJSONObject(0);
            if (item==null) return null;
            JSONObject geoJson=item.getJSONObject("geometry");
            if (geoJson==null) return null;
            JSONObject locJson=geoJson.getJSONObject("location");
            if (locJson==null) return null;
            LatLng latLng=new LatLng(Double.parseDouble(locJson.getString("lat")), Double.parseDouble(locJson.getString("lng")));

            return latLng;

        } catch (JSONException e) {
            return null;
        }

    }

    public void getHandleAddressFromLocation(final String locationAddress
            , final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {


                //Address address = getAddressFromLocation(locationAddress);
                LatLng latLng=getAddressFromLocationByURL(locationAddress);


                Message message = Message.obtain();
                message.setTarget(handler);


                if (latLng != null) {

                    message.what = 1;
                    Bundle bundle = new Bundle();

                    bundle.putDouble("latitude", latLng.latitude);
                    bundle.putDouble("longitude", latLng.longitude);
                    message.setData(bundle);
                } else {
                    message.what = 0;
                    Bundle bundle = new Bundle();

                    bundle.putString("address", locationAddress +
                            ":null");
                    message.setData(bundle);
                }
                message.sendToTarget();


            }
        };
        thread.start();
    }
}
