package smikhlevskiy.uafinance.Net;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import smikhlevskiy.uafinance.Model.Currencie;
//https://privat24.privatbank.ua/p24/accountorder?oper=prp&JSON&apicour&country=ua&full

/**
 * Created by tcont98 on 19-Dec-15.
 */
public class PrivatAsyncTask extends AsyncTask<Void, Void, HashMap<String, Currencie>> {
    public static final String TAG = PrivatAsyncTask.class.getSimpleName();
    private static final String PrivatURL = "https://privat24.privatbank.ua/p24/accountorder?oper=prp&JSON&apicour&country=ua&full";
    private WeakReference<Handler> reDrawHandler;

    public PrivatAsyncTask(Handler reDrawHandler) {
        this.reDrawHandler = new WeakReference<Handler>(reDrawHandler);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected HashMap<String, Currencie> doInBackground(Void... params) {
        HashMap<String, Currencie> hashMap = new HashMap<String, Currencie>();
        StringBuilder stringBuilder = new StringBuilder("");
        try {
            URL url = new URL(PrivatURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStreamReader isr = new InputStreamReader(con.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(isr);

            String str = null;
            do {
                str = bufferedReader.readLine();
                stringBuilder.append(str);
                Log.i(TAG, str);

            } while (str == null);


        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware(true);
            XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
            xmlPullParser.setInput(new StringReader(stringBuilder.toString()));
            int event = xmlPullParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        String name = xmlPullParser.getName();

                        if (name.equals("exchangerate")) {
                            //xmlPullParser.require(XmlPullParser.START_TAG,null, "ccy");
                            //String curName = xmlPullParser.getText();
                            //Log.i(TAG, "Name:" + curName);
                            //xmlPullParser.require(XmlPullParser.END_TAG, null, "title");
                            /*
                            for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
                                Log.i(TAG,xmlPullParser.getAttributeName(i) + " = "
                                        + xmlPullParser.getAttributeValue(i) + ", ");
                                        }
                            */
                            String shortName = xmlPullParser.getAttributeValue(null, "ccy");
                            if (shortName != null) {
                                Currencie currencie = new Currencie();
                                Double val=new Double(xmlPullParser.getAttributeValue(null, "buy"));
                                Double unit=new Double(xmlPullParser.getAttributeValue(null, "unit"));
                                val=1.0*val/(10000.0*unit);
                                currencie.setAsk(val.toString());
                                hashMap.put(shortName, currencie);

                                Log.i(TAG, shortName + ":" + hashMap.get(shortName).getAsk());
                            }

                        }

                        break;
                    case XmlPullParser.END_TAG:

                        break;
                    case XmlPullParser.TEXT:

                        break;

                }
                event = xmlPullParser.next();
            }


        } catch (
                XmlPullParserException e
                )

        {
            e.printStackTrace();
            return null;
        } catch (
                IOException e
                )

        {
            e.printStackTrace();
            return null;
        }

        return hashMap;
    }

    @Override
    protected void onPostExecute(HashMap<String, Currencie> hashMap) {


        if (reDrawHandler.get() != null) {

            Message message = new Message();
            message.what = 2;
            message.obj=hashMap;
            ((Handler) reDrawHandler.get()).handleMessage(message);
            Log.i(TAG, "datas sucsessuful reads");
        }

        super.onPostExecute(hashMap);
    }


}
