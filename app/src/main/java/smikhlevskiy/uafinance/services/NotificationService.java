package smikhlevskiy.uafinance.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import java.util.HashMap;

import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.UI.MainActivity;
import smikhlevskiy.uafinance.Utils.UAFConst;
import smikhlevskiy.uafinance.model.Currencie;
import smikhlevskiy.uafinance.model.FinanceUA;


/**
 * Created by tcont98 on 25-Jan-16.
 */

public class NotificationService extends IntentService {
    public static final String TAG = NotificationService.class.getSimpleName();

    public NotificationService() {
        super(TAG);
    }

    public void sendNotification(String title, String text, Bitmap bigBitmap) {

        FinanceUA financeUA = FinanceUA.readFromJSON();

        String resultString = "";
        if (financeUA == null) return;
        HashMap<String, Currencie> minMaxCurrencies = financeUA.calckMinMaxCurrencies(new String[]{
                        getString(R.string.USD),
                        getString(R.string.EUR),
                        getString(R.string.RUB)},
                getString(R.string.default_city));
        if (minMaxCurrencies == null) return;
        resultString = "Покупка USD:" + minMaxCurrencies.get(getString(R.string.USD)).getBid();


        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//for one activity work
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification noti = new Notification.Builder(this)
                .setContentTitle(UAFConst.getSpanTitle(this))
                .setContentText(resultString)
                .setSmallIcon(R.mipmap.currency_exchange)
                .setContentIntent(pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);




    }

    @Override
    protected void onHandleIntent(Intent intent) {

        sendNotification("", "", null);
    }
}
