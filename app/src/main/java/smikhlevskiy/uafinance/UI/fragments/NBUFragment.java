package smikhlevskiy.uafinance.UI.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * Out NBU rates
 */
public class NBUFragment extends Fragment {
    public static String TAG = NBUFragment.class.getSimpleName();
    private View fragmentView;
    private HashMap<String, Currencie> privatHashMap=null;


    public void setDatas(HashMap<String, Currencie> privatHashMap){
        this.privatHashMap=privatHashMap;
    }

    public void draw(){
if (privatHashMap!=null) {
    ((TextView) fragmentView.findViewById(R.id.USD_ask)).setText(privatHashMap.get(getString(R.string.USD)).getAsk());
    ((TextView) fragmentView.findViewById(R.id.USD_bid)).setVisibility(View.INVISIBLE);

        ((TextView) fragmentView.findViewById(R.id.EUR_ask)).setText(privatHashMap.get(getString(R.string.EUR)).getAsk());
        ((TextView) fragmentView.findViewById(R.id.EUR_bid)).setVisibility(View.INVISIBLE);

        ((TextView) fragmentView.findViewById(R.id.RUB_ask)).setText(privatHashMap.get(getString(R.string.RUR)).getAsk());
        ((TextView) fragmentView.findViewById(R.id.RUB_bid)).setVisibility(View.INVISIBLE);

    ((TextView) fragmentView.findViewById(R.id.ask)).setVisibility(View.INVISIBLE);
    ((TextView) fragmentView.findViewById(R.id.bid)).setVisibility(View.INVISIBLE);
} else Log.i(TAG,"PRIVAT hash is null");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_currencie, null);
        draw();
        return fragmentView;
    }
}
