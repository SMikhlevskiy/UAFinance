package smikhlevskiy.uafinance.resivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import smikhlevskiy.uafinance.UI.MainActivity;
import smikhlevskiy.uafinance.services.NotificationService;

/**
 * Created by tcont98 on 30-Jan-16.
 */
public class BootBroadcastResiver extends BroadcastReceiver {
    public static final String TAG=BootBroadcastResiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "boot");

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
        }
        Intent sintent=new Intent(context,NotificationService.class);
        context.startService(sintent);

    }
}
