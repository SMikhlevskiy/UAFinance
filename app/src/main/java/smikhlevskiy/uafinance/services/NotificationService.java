package smikhlevskiy.uafinance.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.ui.MainActivity;
import smikhlevskiy.uafinance.utils.UAFConst;
import smikhlevskiy.uafinance.model.Currencie;
import smikhlevskiy.uafinance.model.FinanceUA;
import smikhlevskiy.uafinance.model.UAFPreferences;


/**
 * Created by SMikhlevskiy on 25-Jan-16.
 * Service for out Notification about exchange rates(once aaaa day) or Notification about exchange rates jump(control every two hours)
 * Notification do not out in  rest day&time
 */

public class NotificationService extends IntentService {
    public static final double D_ALARM = 0.3;
    public static final String TAG = NotificationService.class.getSimpleName();

    public NotificationService() {
        super(TAG);
    }

    public String GetDTVal(double val, double lastVal) {

        double dv = val - lastVal;

        if ((val == 0) || (lastVal == 0) || (Math.abs(dv)<0.00001)) return "";





        String result;
        DecimalFormat df =new DecimalFormat("#.##");
        if (dv > 0)
            result ="(+"+df.format(dv)+")"; else
            result ="("+df.format(dv)+")";



        return result;
    }

    public void sendNotification() {



        UAFPreferences uafPreferences=new UAFPreferences(this);
        FinanceUA financeUA = FinanceUA.readFromJSON(uafPreferences.getCity());

        String resultString = "";
        if (financeUA == null) return;
        HashMap<String, Currencie> minMaxCurrencies = financeUA.getMinMaxCurrencies();
        if (minMaxCurrencies == null) return;



        double valUSD;
        double lastValUSD;
        try {
            valUSD = Double.parseDouble(minMaxCurrencies.get(getString(R.string.USD)).getBid());
            lastValUSD = uafPreferences.getLastCurrancieVal(getString(R.string.USD));
        } catch (Exception e) {
            return;
        }

        double valEUR;
        double lastValEUR;
        try {
            valEUR = Double.parseDouble(minMaxCurrencies.get(getString(R.string.EUR)).getBid());
            lastValEUR = uafPreferences.getLastCurrancieVal(getString(R.string.EUR));
        } catch (Exception e) {
            return;
        }


        resultString = "Покупка: " +
                getString(R.string.USD) + " " + minMaxCurrencies.get(getString(R.string.USD)).getBid()+GetDTVal(valUSD,lastValUSD) + ", " +
                getString(R.string.EUR) + " " + minMaxCurrencies.get(getString(R.string.EUR)).getBid()+GetDTVal(valEUR,lastValEUR);


        Calendar c = Calendar.getInstance();


        int day_of_year = c.get(Calendar.DAY_OF_YEAR);
        int last_day_notify = uafPreferences.getLastDayNotification();


        if ((day_of_year == last_day_notify) &&
                (Math.abs(valUSD - lastValUSD) < D_ALARM) &&
                (Math.abs(valEUR - lastValEUR) < D_ALARM)) return;



        // once aaaa day || currency jump


        uafPreferences.setLastDayNotification(day_of_year);
        uafPreferences.setLastCurrancieVal(getString(R.string.USD), valUSD);
        uafPreferences.setLastCurrancieVal(getString(R.string.EUR), valEUR);


        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//one activity work
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(UAFConst.getSpanTitle(this))
                .setContentText(resultString)
                .setSmallIcon(R.mipmap.currency_exchange)
                .setContentIntent(pIntent);


        Notification noti = mBuilder.build();


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        noti.flags |= Notification.FLAG_AUTO_CANCEL;// hide the notification after its selected


        notificationManager.notify(0, noti);


    }

    @Override
    protected void onHandleIntent(Intent intent) {

        sendNotification();
    }
}
