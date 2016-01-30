package smikhlevskiy.uafinance.Net;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import smikhlevskiy.uafinance.model.FinanceUA;
import smikhlevskiy.uafinance.model.GeoLocationDB;
import smikhlevskiy.uafinance.model.Organization;

/**
 * Created by tcont98 on 11-Nov-15.
 */


public class FinanceUAAsyncTask extends AsyncTask<Void, Void, FinanceUA> {


    WeakReference<Context> context;
    WeakReference<Handler> resultHandler;
    boolean isLowWork;

    static final String TAG = FinanceUAAsyncTask.class.getSimpleName();

    //String tempFile;
    String city;

    String scurrency;
    boolean askBid;
    boolean isSortCurrency;
    Location deviceLocation;


    public FinanceUAAsyncTask(
            Context context,
            String city,
            String scurrency,
            boolean askBid,
            boolean isSortCurrency,
            Boolean isLowWork,
            Location deviceLocation,
            Handler resultHandler

    ) {

        this.resultHandler = new WeakReference<Handler>(resultHandler);

        this.context = new WeakReference<Context>(context);
        this.isLowWork = isLowWork;
        this.city = city;
        this.scurrency = scurrency;
        this.isSortCurrency = isSortCurrency;
        this.askBid = askBid;
        this.deviceLocation = deviceLocation;
        //tempFile = context.getCacheDir().getPath() + "/" + "financeUA.txt";
    }

    //------------calck LatLong for all organization(if sortDistance)-----------------------------------------------------------
    void setOrganizationsLatLon(FinanceUA financeUA, Context context, String city) {
        GeoLocationDB geoLocationDB = new GeoLocationDB(context, GeoLocationDB.DB_NAME, null, GeoLocationDB.DB_VERSION);

        HashMap<String, LatLng> latLngHashMap = geoLocationDB.updteLocationBase(financeUA.getAllAddresses(city));
        for (Organization organization : financeUA.getOrganizations()) {
            String address = financeUA.getURLAddressByOrganization(organization);
            if (latLngHashMap.containsKey(address))
                organization.setLatLong(latLngHashMap.get(address));
            if (organization.getOrganizationBrunches() != null) {
                for (Organization branchOrganization : organization.getOrganizationBrunches()) {
                    String branchAddress = financeUA.getURLAddressByOrganization(branchOrganization);
                    if (latLngHashMap.containsKey(branchAddress))
                        branchOrganization.setLatLong(latLngHashMap.get(branchAddress));

                }

            }

        }

    }


    @Override
    protected FinanceUA doInBackground(Void... params) {

        FinanceUA financeUA=FinanceUA.readFromJSON();//read from Finace.UA


        if (financeUA == null) return null;

        if (!isLowWork) {//LoWork - without sort & optimization(form BigMap)

            PrivatAddresses privatAddresses=new PrivatAddresses();
            privatAddresses.read(city);//read from Privat.UA
            ArrayList<Organization> privatAdressesList = privatAddresses.getPrivatAdressesList();
            //-------------delete other city,move brunches organization to brunch-------
            financeUA.optimizeOrganizationList(city, privatAdressesList);
            //------------calck LatLong for all organization(if sortDistance)---
            if ((!isSortCurrency) && (context.get() != null) && (deviceLocation != null)) {
                setOrganizationsLatLon(financeUA, (Context) context.get(), city);
            }
            //----Sort------
            financeUA.sort(askBid,
                    isSortCurrency,
                    city, scurrency, deviceLocation);
        }
        return financeUA;
    }


    @Override
    protected void onPostExecute(FinanceUA financeUA) {
        if (financeUA == null) {
            Log.i(TAG, "datas not read");
            return;
        }


        if (context.get() == null) {
            Log.i(TAG, "activity is destroy");
            return;//
        } else
            Log.i(TAG, "onPostExecute");


        if (resultHandler.get() != null) {

            Message message = new Message();
            message.what = 1;
            message.obj = financeUA;
            ((Handler) resultHandler.get()).handleMessage(message);
            Log.i(TAG, "datas sucsessuful reads");
        }

        if ((!isLowWork) && (isSortCurrency)) {//Start GeoChaching Thread
            GeoLocationDB geoLocationDB = new GeoLocationDB((Context) context.get(), GeoLocationDB.DB_NAME, null, GeoLocationDB.DB_VERSION);

            geoLocationDB.startUpdteLocationBase(financeUA.getAllAddresses(city));
        }

        super.onPostExecute(financeUA);
    }
}

