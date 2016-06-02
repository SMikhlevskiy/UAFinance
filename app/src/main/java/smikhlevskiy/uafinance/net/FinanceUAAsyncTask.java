package smikhlevskiy.uafinance.net;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import smikhlevskiy.uafinance.model.FinanceUA;
import smikhlevskiy.uafinance.model.GeoLocationDB;
import smikhlevskiy.uafinance.model.Organization;

/**
 * Created by SMikhlevskiy on 11-Nov-15.
 * Getting Main finance datas from finance.ua & Privat.ua &  processing(Sort,Move to Brunch..) this datas
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

    /**
     * @param context
     * @param city           current city
     * @param scurrency      id current Currency(USD,EUR...)
     * @param askBid         Ask or Bid
     * @param isSortCurrency true - SortBuy exchange rate,  false - sort by distance
     * @param isLowWork      true - no Privat datas, no sort, no further processing  used for LocationMap&NitificationService,
     *                       false used for ListView in MAinActivity
     * @param deviceLocation current deviceLocation used for Sort by Distance
     * @param resultHandler  Handler for out result
     */
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

    /**
     * calck LatLong for all organization(if sortDistance)
     */
    void setOrganizationsLatLon(FinanceUA financeUA, Context context, String city,boolean isUpdate) {
        GeoLocationDB geoLocationDB = new GeoLocationDB(context, GeoLocationDB.DB_NAME, null, GeoLocationDB.DB_VERSION);

        HashMap<String, LatLng> latLngHashMap = geoLocationDB.updteLocationBase(financeUA.getAllAddresses(city),isUpdate);
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

        FinanceUA financeUA = FinanceUA.readFromJSON(city);//read from Finace.UA


        if (financeUA == null) return null;

        if (!isLowWork) {//LoWork - without sort & optimization(BigMap)

            PrivatAddresses privatAddresses = new PrivatAddresses();
            privatAddresses.read(city);//read from Privat.UA
            ArrayList<Organization> privatAdressesList = privatAddresses.getPrivatAdressesList();
            //-------------delete other city,move brunches organization to brunch-------
            financeUA.processingOrganizations(city, privatAdressesList);
        }

        //------------calck LatLong for all organization(if sortDistance)---
        if ((!isSortCurrency) && (context.get() != null)) {
            setOrganizationsLatLon(financeUA, (Context) context.get(), city,!isLowWork);
        }


        if (!isLowWork) {//LoWork - without sort & optimization(BigMap)
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
            //return;

        }


        if (context.get() == null) {

            Log.i(TAG, "activity is destroy");
            return;

        } else
            Log.i(TAG, "onPostExecute");




            Message message = new Message();
            message.what = 1;
            message.obj = financeUA;
            ((Handler) resultHandler.get()).handleMessage(message);
            Log.i(TAG, "datas sucsessuful reads");


        if ((!isLowWork) && (isSortCurrency) && (financeUA!=null)) {//Start GeoChaching Thread
            GeoLocationDB geoLocationDB = new GeoLocationDB((Context) context.get(), GeoLocationDB.DB_NAME, null, GeoLocationDB.DB_VERSION);

            geoLocationDB.startUpdteLocationBase(financeUA.getAllAddresses(city));
        }

        super.onPostExecute(financeUA);
    }
}

