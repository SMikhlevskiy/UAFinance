package smikhlevskiy.uafinance.UI.fragments;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.model.Currencie;

/**
 * Created by SMikhlevskiy on 19-Dec-15.
 * Fragment for ViewPager MainActivty in Bottom header
 * Out InterBank rates
 */
public class InterBankFragment extends Fragment {
    public static String TAG = InterBankFragment.class.getSimpleName();
    private View fragmentView;

    private HashMap<String, Currencie> ibHashMap = null;


    public void draw()

    {
if (ibHashMap==null) return;
        ((TextView) fragmentView.findViewById(R.id.USD_ask)).setText(ibHashMap.get(getString(R.string.USD)).getAsk());
        ((TextView) fragmentView.findViewById(R.id.USD_bid)).setText(ibHashMap.get(getString(R.string.USD)).getBid());

        ((TextView) fragmentView.findViewById(R.id.EUR_ask)).setText(ibHashMap.get(getString(R.string.EUR)).getAsk());
        ((TextView) fragmentView.findViewById(R.id.EUR_bid)).setText(ibHashMap.get(getString(R.string.EUR)).getBid());

        ((TextView) fragmentView.findViewById(R.id.RUB_ask)).setText(ibHashMap.get(getString(R.string.RUB)).getAsk());
        ((TextView) fragmentView.findViewById(R.id.RUB_bid)).setText(ibHashMap.get(getString(R.string.RUB)).getBid());

    }

    public void setDatas(HashMap<String, Currencie> ibHashMap) {
        this.ibHashMap = ibHashMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_currencie, null);
        draw();

        return fragmentView;
    }
}
