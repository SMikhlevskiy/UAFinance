package smikhlevskiy.uafinance.UI;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.model.Currencie;
import smikhlevskiy.uafinance.model.FinanceUA;

/**
 * Created by "SMikhlevskiy" on 14-Feb-16.
 */
public class UAFWidget extends AppWidgetProvider{
    private static SimpleDateFormat formatter = new SimpleDateFormat(
            "dd MMM yyyy  hh:mm:ss a");
    static String strWidgetText = "";

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // TODO Auto-generated method stub
        // super.onDeleted(context, appWidgetIds);
        Toast.makeText(context, "onDeleted()", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisabled(Context context) {
        // TODO Auto-generated method stub
        // super.onDisabled(context);
        Toast.makeText(context, "onDisabled()", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEnabled(Context context) {
        // TODO Auto-generated method stub
        // super.onEnabled(context);
        Toast.makeText(context, "onEnabled()", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // TODO Auto-generated method stub
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId);

            Toast.makeText(
                    context,
                    "onUpdate(): " + String.valueOf(i) + " : "
                            + String.valueOf(appWidgetId), Toast.LENGTH_LONG)
                    .show();
        }

    }

    public static void updateAppWidget(Context context,
                                       AppWidgetManager appWidgetManager, int appWidgetId) {
        String currentTime = formatter.format(new Date());
        strWidgetText = currentTime;

        /*
        RemoteViews updateViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_uaf);
        updateViews.setTextViewText(R.id.widgettext,
                "[" + String.valueOf(appWidgetId) + "]" + strWidgetText);
        appWidgetManager.updateAppWidget(appWidgetId, updateViews);
        */
        (new AsyncTask<Void,Void,FinanceUA>(){
            private WeakReference<Context> context;
            AppWidgetManager appWidgetManager;
            int appWidgetId;

            public AsyncTask<Void,Void,FinanceUA>  initialise(Context context,AppWidgetManager appWidgetManager,int appWidgetId){
                this.context=new WeakReference<Context>(context);
                this.appWidgetManager=appWidgetManager;
                this.appWidgetId=appWidgetId;
                return this;

                //this.context = context;
            }
            @Override
            protected FinanceUA doInBackground(Void... params) {


                return FinanceUA.readFromJSON();
            }

            @Override
            protected void onPostExecute(FinanceUA financeUA) {
                if ((financeUA == null) || (context.get()==null)) return;

                HashMap<String, Currencie> minMaxCurrencies = financeUA.getMinMaxCurrencies(new String[]{
                                ((Context) context.get()).getString(R.string.USD),
                                ((Context) context.get()).getString(R.string.EUR),
                                ((Context) context.get()).getString(R.string.RUB)},
                        ((Context) context.get()).getString(R.string.default_city));

                if (minMaxCurrencies == null) return;


                RemoteViews updateViews = new RemoteViews( ((Context) context.get()).getPackageName(),
                        R.layout.widget_uaf);

                updateViews.setTextViewText(R.id.USD_ask,
                        minMaxCurrencies.get(((Context) context.get()).getString(R.string.USD)).getAsk());
                updateViews.setTextViewText(R.id.USD_bid,
                        minMaxCurrencies.get(((Context) context.get()).getString(R.string.USD)).getBid());


                updateViews.setTextViewText(R.id.EUR_ask,
                        minMaxCurrencies.get(((Context) context.get()).getString(R.string.EUR)).getAsk());
                updateViews.setTextViewText(R.id.EUR_bid,
                        minMaxCurrencies.get(((Context) context.get()).getString(R.string.EUR)).getBid());


                updateViews.setTextViewText(R.id.RUB_ask,
                        minMaxCurrencies.get(((Context) context.get()).getString(R.string.RUB)).getAsk());
                updateViews.setTextViewText(R.id.RUB_bid,
                        minMaxCurrencies.get(((Context) context.get()).getString(R.string.RUB)).getBid());



                appWidgetManager.updateAppWidget(appWidgetId, updateViews);




                super.onPostExecute(financeUA);
            }


        }).initialise(context,appWidgetManager,appWidgetId).execute();





        Toast.makeText(
                context,
                "updateAppWidget(): " + String.valueOf(appWidgetId) + " "
                        + strWidgetText, Toast.LENGTH_LONG).show();

    }

}
