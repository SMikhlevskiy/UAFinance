package smikhlevskiy.uafinance.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import smikhlevskiy.uafinance.R;

/**
 * Created by tcont98 on 16-Nov-15.
 */
public class UAFPreferences {
    static final String ASK_BID_KEY = "ask_bid_pref";
    static final String CURRENCIE_KEY = "currencie_pref";
    static final String CITY_KEY = "city_pref";
    static final String SORTCUR_KEY = "sortcur_pref";





    static final String EVERY_DAY_NOTIFICATION = "every_day_notification";
    static final String JUMP_NOTIFICATION = "jump_exchange_rates";

    SharedPreferences sharedPreferences;
    Context context;

    public UAFPreferences(Context context) {
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        this.context=context;
    }


    public String getAskBid() {
        return sharedPreferences.getString(ASK_BID_KEY, (context.getResources().getStringArray(R.array.askbid))[0]);
    }

    public void setAskBid(String askBid) {


        SharedPreferences.Editor ed=sharedPreferences.edit();
        ed.putString(ASK_BID_KEY, askBid);
        ed.commit();

    }

    public String getCurrancie() {
        return sharedPreferences.getString(CURRENCIE_KEY, context.getResources().getString(R.string.default_currancie));
    }

    public void setCurrancie(String currancie) {
        SharedPreferences.Editor ed=sharedPreferences.edit();
        ed.putString(CURRENCIE_KEY, currancie);
        ed.commit();

    }

    public String getCity() {
        return sharedPreferences.getString(CITY_KEY, context.getResources().getString(R.string.default_city));
    }

    public void setCity(String city) {
        SharedPreferences.Editor ed=sharedPreferences.edit();
        ed.putString(CITY_KEY, city);
        ed.commit();

    }


    public boolean getSortCurrency() {
        return sharedPreferences.getBoolean(SORTCUR_KEY, true);
    }

    public boolean isJumpNotificatiom() {
        return sharedPreferences.getBoolean(JUMP_NOTIFICATION, true);
    }

    public boolean isEveryDayNotification() {
        return sharedPreferences.getBoolean(EVERY_DAY_NOTIFICATION, true);
    }


    public void setSortCurrency(boolean sortCurrency) {
        SharedPreferences.Editor ed=sharedPreferences.edit();
        ed.putBoolean(SORTCUR_KEY, sortCurrency);
        ed.commit();

    }



}
