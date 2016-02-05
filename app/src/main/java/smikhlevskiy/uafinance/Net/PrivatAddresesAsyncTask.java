package smikhlevskiy.uafinance.Net;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


import smikhlevskiy.uafinance.model.FinanceUA;
import smikhlevskiy.uafinance.model.Organization;
import smikhlevskiy.uafinance.model.UAFPreferences;

/**
 * Created by SMikhlevskiy on 15-Jan-16.
 * Get addres of privat organizations for BigMap of organizations
 * now datas gets for Current city becouse speed of update is slovly(TO-do gets All datas with cached)
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


        if (context.get()==null) return null;
        UAFPreferences uafPreferences=new UAFPreferences((Context)context.get());
        privatAddresses.read(uafPreferences.getCity());////privatAddresses.read("");/* comment before optimization work with privat addresses */



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
