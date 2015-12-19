package smikhlevskiy.uafinance.UI;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.Utils.UAFinancePreference;
import smikhlevskiy.uafinance.model.Currencie;
import smikhlevskiy.uafinance.model.FinanceUA;

/**
 * Created by tcont98 on 18-Dec-15.
 */
public class FinanceUAFragment extends Fragment implements IShowFragment {
private Activity mainActivity;
private FinanceUA financeUA;
private   View fragmentView;
public static String TAG=FinanceUAFragment.class.getSimpleName();

    @Override
    public void onAttach(Activity activity) {
        mainActivity=activity;

        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_finance_ua, null);
        drawFinanceUA();
        return fragmentView;
    }



    @Override
    public void drawFinanceUA() {

        if (financeUA==null) return;
        UAFinancePreference uaFinancePreference=new UAFinancePreference(fragmentView.getContext());

        HashMap<String, Currencie> minMaxCurrencies =financeUA.calckMinMaxCurrencies(
                new String[]{
                        getString(R.string.USD),
                        getString(R.string.EUR),
                        getString(R.string.RUB)},
                uaFinancePreference.getCity());

        ((TextView) fragmentView.findViewById(R.id.USD_ask)).setText(minMaxCurrencies.get(getString(R.string.USD)).getAsk());
        ((TextView) fragmentView.findViewById(R.id.USD_bid)).setText(minMaxCurrencies.get(getString(R.string.USD)).getBid());

        ((TextView) fragmentView.findViewById(R.id.EUR_ask)).setText(minMaxCurrencies.get(getString(R.string.EUR)).getAsk());
        ((TextView) fragmentView.findViewById(R.id.EUR_bid)).setText(minMaxCurrencies.get(getString(R.string.EUR)).getBid());

        ((TextView) fragmentView.findViewById(R.id.RUB_ask)).setText(minMaxCurrencies.get(getString(R.string.RUB)).getAsk());
        ((TextView) fragmentView.findViewById(R.id.RUB_bid)).setText(minMaxCurrencies.get(getString(R.string.RUB)).getBid());


    }
    @Override
    public void setFinanceUA(FinanceUA financeUA){
        this.financeUA=financeUA;
    }
}
