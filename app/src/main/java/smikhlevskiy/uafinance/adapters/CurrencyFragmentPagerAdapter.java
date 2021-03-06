package smikhlevskiy.uafinance.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.HashMap;

import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.ui.fragments.BlackMarketFragment;
import smikhlevskiy.uafinance.ui.fragments.CurrencyCashFragment;
import smikhlevskiy.uafinance.ui.fragments.InterBankFragment;
import smikhlevskiy.uafinance.ui.fragments.NBUFragment;
import smikhlevskiy.uafinance.ui.fragments.PreciousMetalsFragment;
import smikhlevskiy.uafinance.utils.UAFConst;
import smikhlevskiy.uafinance.model.Currencie;
import smikhlevskiy.uafinance.model.FinanceUA;

/**
 * Created by SMikhlevskiy
 * Adapter for work with Fragment ViewPager in bottom Header of MainActivity
 */


public class CurrencyFragmentPagerAdapter extends FragmentPagerAdapter {
    public static final String TAG = CurrencyFragmentPagerAdapter.class.getSimpleName();
    private static int NUM_ITEMS = 1;
    private Context context;
    private FragmentManager fragmentManager;


    private FinanceUA financeUA = null;
    private HashMap<String, Currencie> interBank = null;
    private HashMap<String, Currencie> privat = null;


    public CurrencyFragmentPagerAdapter(FragmentManager fragmentManager, Context context, int item_count) {
        super(fragmentManager);
        this.fragmentManager=fragmentManager;
        NUM_ITEMS = item_count;
        this.context = context;


    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    public Fragment getFragmentByPosition(int position,ViewPager viewPager){

        return fragmentManager.findFragmentByTag(
                "android:switcher:" + viewPager.getId() + ":"
                        + getItemId(position));
    }
    @Override
    public Fragment getItem(int position) {
        //return ContentFragment.newInstance(position);
        Log.i(TAG, "Build Position:" + position);

        switch (position) {
            case UAFConst.CURRENCY_CASH:
                CurrencyCashFragment currencyCashFragment = new CurrencyCashFragment();
                currencyCashFragment.setDatas(financeUA);
                return currencyCashFragment;
            case UAFConst.INTERBANK:
                InterBankFragment interBankFragment = new InterBankFragment();
                interBankFragment.setDatas(interBank);
                return interBankFragment;
            case UAFConst.NBU:
                NBUFragment nbuFragment = new NBUFragment();
                nbuFragment.setDatas(privat);
                return nbuFragment;

            case UAFConst.PRECIOUS_METALS:
                PreciousMetalsFragment preciousMetalsFragment = new PreciousMetalsFragment();
                preciousMetalsFragment.setDatas(privat);
                return preciousMetalsFragment;

            case UAFConst.BLACK_MARKET:
                BlackMarketFragment blackMarketFragment = new BlackMarketFragment();
                blackMarketFragment.setDatas(interBank);
                return blackMarketFragment;
        }
        return null;
    }


    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case UAFConst.CURRENCY_CASH:
                return context.getString(R.string.CurrencyCash);
            case UAFConst.NBU:
                return context.getString(R.string.NBU);
            case UAFConst.INTERBANK:
                return context.getString(R.string.InterBank);
            case UAFConst.PRECIOUS_METALS:
                return context.getString(R.string.PreciousMetals);
            case UAFConst.BLACK_MARKET:
                return context.getString(R.string.blackMarket);
        }
        return "TAB" + " " + String.valueOf(position + 1);

    }

    @Override
    public int getItemPosition(Object object) {
        Log.i(TAG, object.getClass().getSimpleName());

        if (object instanceof CurrencyCashFragment) {
            ((CurrencyCashFragment) object).setDatas(financeUA);
            ((CurrencyCashFragment) object).draw();
        } else if (object instanceof InterBankFragment) {
            ((InterBankFragment) object).setDatas(interBank);
            ((InterBankFragment) object).draw();
        } else if (object instanceof BlackMarketFragment) {
            ((BlackMarketFragment) object).setDatas(interBank);
            ((BlackMarketFragment) object).draw();
        } else if (object instanceof NBUFragment) {
            if (privat == null) Log.i("Privat", "Privat is null");
            else Log.i("Privat", "Privat is not null");
            ((NBUFragment) object).setDatas(privat);
            ((NBUFragment) object).draw();
        } else if (object instanceof PreciousMetalsFragment) {
            ((PreciousMetalsFragment) object).setDatas(privat);
            ((PreciousMetalsFragment) object).draw();
        }
        //don't return POSITION_NONE, avoid fragment recreation.
        return super.getItemPosition(object);
    }

    public void setFinanceUA(FinanceUA financeUA) {
        this.financeUA = financeUA;
    }

    public void setInterBank(HashMap<String, Currencie> interBank) {
        this.interBank = interBank;
    }

    public void setPrivat(HashMap<String, Currencie> privat) {
        this.privat = privat;
    }

}
