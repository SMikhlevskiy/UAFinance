package smikhlevskiy.uafinance.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
public class BlackMarketFragment extends Fragment {
    public static String TAG = BlackMarketFragment.class.getSimpleName();
    private View fragmentView;

    private HashMap<String, Currencie> ibHashMap = null;


    public void draw()

    {
        try {
            if (ibHashMap == null) return;
            ((TextView) fragmentView.findViewById(R.id.USD_ask)).setText(ibHashMap.get(getString(R.string.USD)).getAsk());
            ((TextView) fragmentView.findViewById(R.id.USD_bid)).setText(ibHashMap.get(getString(R.string.USD)).getBid());

            ((TextView) fragmentView.findViewById(R.id.EUR_ask)).setText(ibHashMap.get(getString(R.string.EUR)).getAsk());
            ((TextView) fragmentView.findViewById(R.id.EUR_bid)).setText(ibHashMap.get(getString(R.string.EUR)).getBid());

            ((TextView) fragmentView.findViewById(R.id.RUB_ask)).setText(ibHashMap.get(getString(R.string.RUB)).getAsk());
            ((TextView) fragmentView.findViewById(R.id.RUB_bid)).setText(ibHashMap.get(getString(R.string.RUB)).getBid());
        } catch (IllegalStateException e) {
            //IllegalStateException: Fragment  not attached to Activity
            // I do not find another solution
            Log.i(TAG, "Fragment not attached to Activity");

            return;
        }
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
