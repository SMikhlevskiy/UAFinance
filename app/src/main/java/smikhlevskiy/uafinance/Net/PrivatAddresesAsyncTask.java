package smikhlevskiy.uafinance.Net;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


import smikhlevskiy.uafinance.model.FinanceUA;
import smikhlevskiy.uafinance.model.Organization;

/** To DO
 * Sinhronize Address from Priavat
 * Created by tcont98 on 15-Jan-16.
 */
public class PrivatAddresesAsyncTask  extends AsyncTask<Void, Void, ArrayList<Organization>>{


    private WeakReference<Handler> resultHandler;
    private WeakReference<Context> context;

    public PrivatAddresesAsyncTask(Handler resultHandler,Context context) {
        this.resultHandler = new WeakReference<Handler>(resultHandler);
        this.context = new WeakReference<Context>(context);
    }

    @Override
    protected ArrayList<Organization> doInBackground(Void... params) {
        PrivatAddresses privatAddresses=new PrivatAddresses();

        privatAddresses.read("");
        if (context.get()==null) return null;
        privatAddresses.setLatLon((Context)context.get());
        ArrayList<Organization> privatAdressesList=privatAddresses.getPrivatAdressesList();
        return privatAdressesList;
    }

    @Override
    protected void onPostExecute(ArrayList<Organization> organizations) {
        if ((resultHandler.get()!=null) && (organizations!=null))
        {
            Message message=new Message();
            message.what=10;
            message.obj=organizations;
           ((Handler) resultHandler.get()).handleMessage(message);
        }
        super.onPostExecute(organizations);
    }


}
