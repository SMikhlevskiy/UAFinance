package smikhlevskiy.uafinance.UI.fragments;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * Out rates of PerciousMetals
 */
public class PreciousMetalsFragment extends Fragment {

    public static String TAG=PreciousMetalsFragment.class.getSimpleName();
    private View fragmentView;
    private HashMap<String, Currencie> privatHashMap=null;


    public void setDatas(HashMap<String, Currencie> privatHashMap){
        this.privatHashMap=privatHashMap;
    }

    public void draw(){
        if (privatHashMap!=null) {
            ((TextView) fragmentView.findViewById(R.id.Gold_ask)).setText(privatHashMap.get(getString(R.string.Gold_key)).getAsk());
            ((TextView) fragmentView.findViewById(R.id.Silver_ask)).setText(privatHashMap.get(getString(R.string.Silver_key)).getAsk());
            ((TextView) fragmentView.findViewById(R.id.Platina_ask)).setText(privatHashMap.get(getString(R.string.Platina_key)).getAsk());
            ((TextView) fragmentView.findViewById(R.id.Palladiy_ask)).setText(privatHashMap.get(getString(R.string.Palladiy_key)).getAsk());

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView= inflater.inflate(R.layout.fragment_precious_metals,null);
        draw();
        return fragmentView;
    }



}
