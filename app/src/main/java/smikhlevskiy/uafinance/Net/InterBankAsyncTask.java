package smikhlevskiy.uafinance.Net;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
 * Created by SMikhlevskiy on 22-Dec-15.
 * Getting InterBank Rates of Main currancies (USD,EUR,RUB)
 */
public class InterBankAsyncTask extends AsyncTask<Void, Void, HashMap<String, Currencie>> {
    public static final String TAG = InterBankAsyncTask.class.getSimpleName();
    private static final String openratesUrl = "http://openrates.in.ua/rates";
    WeakReference<Handler> handler;
    String[] mainCurrencies;

    /**
     *
     * @param handler  Handler for out result
     * @param mainCurrencies  list of getting Currenciees USD,EUR,RUB)
     */

    public InterBankAsyncTask(Handler handler, String[] mainCurrencies) {
        this.handler = new WeakReference<Handler>(handler);
        this.mainCurrencies = mainCurrencies;
    }

    @Override
    protected HashMap<String, Currencie> doInBackground(Void... params) {
        HashMap<String, Currencie> hashMap = new HashMap<String, Currencie>();
        try {
            URL url = new URL(openratesUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStreamReader isr = new InputStreamReader(con.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(isr);
            String str = null;
            StringBuilder sb = new StringBuilder("");
            do {
                str = bufferedReader.readLine();
                if (str != null) {
                    sb.append(str);
                    Log.i(TAG, str);
                }

            } while (str == null);

            //Gson gson = new Gson();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(sb.toString());
            for (int i = 0; i < mainCurrencies.length; i++) {
                JsonNode USDNode = rootNode.get(mainCurrencies[i]);
                JsonNode InterbankNode = USDNode.get("interbank");
                Log.i(TAG, mainCurrencies[i] + ":" + InterbankNode.get("buy").asText() + "/" + InterbankNode.get("sell").asText());
                Currencie currencie = new Currencie();
                currencie.setAsk(InterbankNode.get("sell").asText());
                currencie.setBid(InterbankNode.get("buy").asText());
                hashMap.put(mainCurrencies[i], currencie);
            }


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
        if ((handler.get() != null) && (hashMap != null)) {

            Message message = new Message();
            message.what = 3;
            message.obj = hashMap;
            ((Handler) handler.get()).handleMessage(message);
            Log.i(TAG, "datas sucsessuful reads");
        }

        super.onPostExecute(hashMap);
    }
}
