package smikhlevskiy.uafinance.UI.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.model.UAFPreferences;
import smikhlevskiy.uafinance.model.Currencie;
import smikhlevskiy.uafinance.model.FinanceUA;

/**
 * Created by tcont98 on 18-Dec-15.
 */
public class CurrencyCashFragment extends Fragment {
private Activity mainActivity;
private FinanceUA financeUA;
private   View fragmentView;
public static String TAG=CurrencyCashFragment.class.getSimpleName();

    @Override
    public void onAttach(Activity activity) {
        mainActivity=activity;

        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        fragmentView = inflater.inflate(R.layout.fragment_currencie, null);
        draw();
        return fragmentView;
    }




    public void draw() {

        if (financeUA==null) return;
        UAFPreferences UAFPreferences =new UAFPreferences(fragmentView.getContext());

        HashMap<String, Currencie> minMaxCurrencies =financeUA.calckMinMaxCurrencies(
                new String[]{
                        getString(R.string.USD),
                        getString(R.string.EUR),
                        getString(R.string.RUB)},
                UAFPreferences.getCity());

        ((TextView) fragmentView.findViewById(R.id.USD_ask)).setText(minMaxCurrencies.get(getString(R.string.USD)).getAsk());
        ((TextView) fragmentView.findViewById(R.id.USD_bid)).setText(minMaxCurrencies.get(getString(R.string.USD)).getBid());

        ((TextView) fragmentView.findViewById(R.id.EUR_ask)).setText(minMaxCurrencies.get(getString(R.string.EUR)).getAsk());
        ((TextView) fragmentView.findViewById(R.id.EUR_bid)).setText(minMaxCurrencies.get(getString(R.string.EUR)).getBid());

        ((TextView) fragmentView.findViewById(R.id.RUB_ask)).setText(minMaxCurrencies.get(getString(R.string.RUB)).getAsk());
        ((TextView) fragmentView.findViewById(R.id.RUB_bid)).setText(minMaxCurrencies.get(getString(R.string.RUB)).getBid());


    }

    public void setDatas(FinanceUA financeUA){
        this.financeUA=financeUA;
    }
}
