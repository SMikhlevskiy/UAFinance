package smikhlevskiy.uafinance.Threadas;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import smikhlevskiy.uafinance.model.Currencie;

/**
 * Created by tcont98 on 22-Dec-15.
 */
public class InterBankAsyncTask extends AsyncTask<Void,Void,HashMap<String,Currencie>> {
 public static final String TAG=InterBankAsyncTask.class.getSimpleName();
    private static final String openratesUrl="http://openrates.in.ua/rates";
 WeakReference<Handler> handler;



    public InterBankAsyncTask(Handler handler){
        this.handler=new WeakReference<Handler>(handler);
    }

    @Override
    protected HashMap<String, Currencie> doInBackground(Void... params) {
        HashMap<String,Currencie> hashMap=new HashMap<String,Currencie>();
        try {
            URL url= new URL(openratesUrl);
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
            InputStreamReader isr=new InputStreamReader(con.getInputStream());
            BufferedReader bufferedReader=new BufferedReader(isr);
            String str=null;
            do {
                str=bufferedReader.readLine();
                Log.i(TAG,str);

            } while (str==null);


        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return hashMap;
    }

    @Override
    protected void onPostExecute(HashMap<String, Currencie> hashMap) {
        if ((handler.get() != null) && (hashMap!=null)) {

            Message message = new Message();
            message.what = 3;
            message.obj=hashMap;
            ((Handler) handler.get()).handleMessage(message);
            Log.i(TAG, "datas sucsessuful reads");
        }

        super.onPostExecute(hashMap);
    }
}
