package smikhlevskiy.uafinance.Net;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.Utils.UAFinancePreference;
import smikhlevskiy.uafinance.Adapters.OrganizationListAdapter;
import smikhlevskiy.uafinance.Model.FinanceUA;
import smikhlevskiy.uafinance.Model.GeoLocationDB;

/**
 * Created by tcont98 on 11-Nov-15.
 */


public class FinanceUAAsyncTask extends AsyncTask<String, Void, FinanceUA> {

    WeakReference<OrganizationListAdapter> organizationListAdapter;

    WeakReference<Spinner> spinnerCurrency;
    WeakReference<Spinner> spinnerCity;
    WeakReference<Context> context;
    WeakReference<Handler> reDrawHandler;

    static final String TAG = FinanceUAAsyncTask.class.getSimpleName();

    String tempFile;

    public FinanceUAAsyncTask(
            Context context,
            Handler reDrawHandler,
            OrganizationListAdapter organizationListAdapter,
            Spinner spinnerCurrency,
            Spinner spinnerCity) {
        this.organizationListAdapter = new WeakReference<OrganizationListAdapter>(organizationListAdapter);
        this.reDrawHandler = new WeakReference<Handler>(reDrawHandler);
        this.spinnerCurrency = new WeakReference<Spinner>(spinnerCurrency);
        this.spinnerCity = new WeakReference<Spinner>(spinnerCity);
        this.context = new WeakReference<Context>(context);
        tempFile = context.getCacheDir().getPath() + "/" + "financeUA.txt";
    }

    @Override
    protected FinanceUA doInBackground(String... params) {
        StringBuilder bulder = new StringBuilder("");
        try {

            // Thread.sleep(10000);//simulate lon read
            //  from URL
            InputStreamReader isr;
            if (true) {
                URL url = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                isr = new InputStreamReader(con.getInputStream());

            } else {
                //***  Test from assets

                if (context.get() != null) {

                    isr = new InputStreamReader(((Context) context.get()).getAssets().open("currency-cash.json"));
                } else return null;
            }
            //***

            BufferedReader reader = new BufferedReader(isr);
            String str = null;

            do {
                str = reader.readLine();
                if (str != null)
                    bulder.append(str);
            } while (str != null);

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return null;
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;

        }   /* catch (InterruptedException e) {

            e.printStackTrace();
            return null;
        }
        */

        Gson gson = new Gson();
        //saveToCache();
        FinanceUA financeUA = (FinanceUA) gson.fromJson(bulder.toString(), FinanceUA.class);

        financeUA.optimizeOrganizationList();

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


        UAFinancePreference uaFinancePreference = new UAFinancePreference((Context) context.get());

        GeoLocationDB geoLocationDB = new GeoLocationDB((Context) context.get(), GeoLocationDB.DB_NAME, null, GeoLocationDB.DB_VERSION);

        geoLocationDB.UpdteLocationBase(financeUA.getAllAddresses(uaFinancePreference.getCity()));


        ArrayList cityArrayList = new ArrayList<String>();
        cityArrayList.add(uaFinancePreference.getCity());
        String[] citiesArray = (String[]) financeUA.getCities().values().toArray(new String[0]);
        for (int i = 0; i < citiesArray.length; i++)
            cityArrayList.add(citiesArray[i]);
        String[] cities = (String[]) cityArrayList.toArray(new String[0]);


        if ((spinnerCity.get() != null) && (context.get() != null)) {
            ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(((Context) context.get()), android.R.layout.simple_spinner_item, cities);
            ((Spinner) spinnerCity.get()).setAdapter(cityAdapter);
            ((Spinner) spinnerCity.get()).setSelection(cityAdapter.getPosition(uaFinancePreference.getCity()));
        }

        //Arrays.allCurrancies
        ArrayList currencyArrayList = new ArrayList<String>();// (HashSet)financeUA.getCurrancies().keySet();

        currencyArrayList.add(((Context) context.get()).getResources().getString(R.string.USD));
        currencyArrayList.add(((Context) context.get()).getResources().getString(R.string.RUB));
        currencyArrayList.add(((Context) context.get()).getResources().getString(R.string.EUR));

        String[] currencyArray = (String[]) financeUA.getCurrancies().keySet().toArray(new String[0]);
        for (int i = 0; i < currencyArray.length; i++)
            currencyArrayList.add(currencyArray[i]);
        String allCurrancies[] = (String[]) currencyArrayList.toArray(new String[0]);

        if ((spinnerCurrency.get() != null) && (context.get() != null)) {
            ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(((Context) context.get()), android.R.layout.simple_spinner_item, allCurrancies);
            ((Spinner) spinnerCurrency.get()).setAdapter(currencyAdapter);
            ((Spinner) spinnerCurrency.get()).setSelection(currencyAdapter.getPosition(uaFinancePreference.getCurrancie()));
        }


        ((OrganizationListAdapter) organizationListAdapter.get()).setFinanceUA(financeUA);
        ((OrganizationListAdapter) organizationListAdapter.get()).notifyDataSetChanged();

        if (reDrawHandler.get() != null) {

            Message message = new Message();
            message.what = 1;
            ((Handler) reDrawHandler.get()).handleMessage(message);
            Log.i(TAG, "datas sucsessuful reads");
        }
        super.onPostExecute(financeUA);
    }
}

