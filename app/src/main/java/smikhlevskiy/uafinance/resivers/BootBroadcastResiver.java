package smikhlevskiy.uafinance.resivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by tcont98 on 30-Jan-16.
 */
public class BootBroadcastResiver extends BroadcastReceiver {
    public static final String TAG = BootBroadcastResiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") ||
                intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON")) {
            AlarmBroadcastReciver.setAlarm(context);//start AlarmClock
        }


    }
}
