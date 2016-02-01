package smikhlevskiy.uafinance.resivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import smikhlevskiy.uafinance.model.UAFPreferences;
import smikhlevskiy.uafinance.services.NotificationService;

/**
 * Created by tcont98 on 31-Jan-16.
 */
public class AlarmBroadcastReciver extends BroadcastReceiver {
    public static int ALARM_CYCLE = 120;//minutes
    public static final String TAG = AlarmBroadcastReciver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar c = Calendar.getInstance();
        int hour=c.get(Calendar.HOUR_OF_DAY);
        int day=c.get(Calendar.DAY_OF_WEEK);

        if ((hour<10) || (hour>18) || (day==Calendar.SUNDAY) || (day==Calendar.SATURDAY)) //do not disturb
            return;



        Intent sintent = new Intent(context, NotificationService.class);
        context.startService(sintent);//Start notification currencies service

    }

    public static void setAlarm(Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReciver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        UAFPreferences uafPreferences = new UAFPreferences(context);
        if ((uafPreferences.isJumpNotificatiom()) || (uafPreferences.isEveryDayNotification()))
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * ALARM_CYCLE, pendingIntent); else // Millisec * Second * Minute
            alarmManager.cancel(pendingIntent);

        //Log.i(TAG, "Start Alarm");
    }

    public static void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmBroadcastReciver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

}
