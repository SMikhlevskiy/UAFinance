package smikhlevskiy.uafinance.Net;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import smikhlevskiy.uafinance.model.FinanceUA;
import smikhlevskiy.uafinance.model.GeoLocationDB;

/**
 * Created by tcont98 on 11-Nov-15.
 */


public class FinanceUAAsyncTask extends AsyncTask<String, Void, FinanceUA> {



    WeakReference<Context> context;
    WeakReference<Handler> resultHandler;
    boolean isLowWork;

    static final String TAG = FinanceUAAsyncTask.class.getSimpleName();

    //String tempFile;
    String city;

    String scurrency;
    boolean askBid;
    boolean sortCurrency;


    public FinanceUAAsyncTask(
            Context context,
            String city,
            String scurrency,
            boolean askBid,
            boolean sortCurrency,
            Boolean isLowWork,
            Handler resultHandler
            ) {

        this.resultHandler = new WeakReference<Handler>(resultHandler);

        this.context = new WeakReference<Context>(context);
        this.isLowWork = isLowWork;
        this.city = city;
        this.scurrency=scurrency;
        this.sortCurrency=sortCurrency;
        this.askBid=askBid;
        //tempFile = context.getCacheDir().getPath() + "/" + "financeUA.txt";
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

        if (!isLowWork) {
            financeUA.optimizeOrganizationList(city);


            financeUA.sort(askBid,
                    sortCurrency,
                    city, scurrency,/*deviceLocation*/null);
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

        if (!isLowWork) {//Start GeoChaching Thread
            GeoLocationDB geoLocationDB = new GeoLocationDB((Context) context.get(), GeoLocationDB.DB_NAME, null, GeoLocationDB.DB_VERSION);

            geoLocationDB.UpdteLocationBase(financeUA.getAllAddresses(city));
        }

        super.onPostExecute(financeUA);
    }
}

