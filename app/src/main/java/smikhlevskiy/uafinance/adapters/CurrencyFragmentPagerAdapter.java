package smikhlevskiy.uafinance.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.HashMap;

import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.UI.fragments.CurrencyCashFragment;
import smikhlevskiy.uafinance.UI.fragments.InterBankFragment;
import smikhlevskiy.uafinance.UI.fragments.NBUFragment;
import smikhlevskiy.uafinance.UI.fragments.PreciousMetalsFragment;
import smikhlevskiy.uafinance.Utils.UAFConst;
import smikhlevskiy.uafinance.model.Currencie;
import smikhlevskiy.uafinance.model.FinanceUA;

/**
 * Copyright (C) 2015 Mustafa Ozcan
 * Created on 06 May 2015 (www.mustafaozcan.net)
 * *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * *
 * http://www.apache.org/licenses/LICENSE-2.0
 * *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class CurrencyFragmentPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 1;
    private Context context;


    private FinanceUA financeUA = null;
    private HashMap<String, Currencie> interBank = null;
    private HashMap<String, Currencie> privat = null;


    public CurrencyFragmentPagerAdapter(FragmentManager fragmentManager, Context context, int item_count) {
        super(fragmentManager);
        NUM_ITEMS = item_count;
        this.context = context;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        //return ContentFragment.newInstance(position);


        switch (position) {
            case UAFConst.CURRENCY_CASH:
                CurrencyCashFragment currencyCashFragment= new CurrencyCashFragment();
                currencyCashFragment.setDatas(financeUA);
                return currencyCashFragment;
            case UAFConst.INTERBANK:
                InterBankFragment interBankFragment=new InterBankFragment();
                interBankFragment.setDatas(interBank);
                return interBankFragment;
            case UAFConst.NBU:
                NBUFragment nbuFragment= new NBUFragment();
                nbuFragment.setDatas(privat);
                return nbuFragment;

            case UAFConst.PRECIOUS_METALS:
                PreciousMetalsFragment preciousMetalsFragment= new PreciousMetalsFragment();
                preciousMetalsFragment.setDatas(privat);
                return preciousMetalsFragment;


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
        }
        return "TAB" + " " + String.valueOf(position + 1);

    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof CurrencyCashFragment) {
            ((CurrencyCashFragment) object).setDatas(financeUA);
            ((CurrencyCashFragment) object).draw();
        } else if (object instanceof InterBankFragment) {
            ((InterBankFragment) object).setDatas(interBank);
            ((InterBankFragment) object).draw();
        } else if (object instanceof NBUFragment) {
            if (privat==null)  Log.i("Privat", "Privat is null"); else Log.i("Privat", "Privat is not null");
            ((NBUFragment) object).setDatas(privat);
            ((NBUFragment) object).draw();
        }  else if (object instanceof PreciousMetalsFragment) {
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
