package smikhlevskiy.uafinance.UI;

import android.app.Fragment;
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
 * Created by tcont98 on 19-Dec-15.
 */
public class NBUFragment extends Fragment {
    public static String TAG = NBUFragment.class.getSimpleName();
    private View fragmentView;
    private HashMap<String, Currencie> privatHashMap;


    public void setPrivatHashMap(HashMap<String, Currencie> privatHashMap){
        this.privatHashMap=privatHashMap;
    }

    public void drawNBU(){
if (privatHashMap!=null) {
    ((TextView) fragmentView.findViewById(R.id.USD_ask)).setText(privatHashMap.get(getString(R.string.USD)).getAsk());
    ((TextView) fragmentView.findViewById(R.id.USD_bid)).setVisibility(View.INVISIBLE);

        ((TextView) fragmentView.findViewById(R.id.EUR_ask)).setText(privatHashMap.get(getString(R.string.EUR)).getAsk());
        ((TextView) fragmentView.findViewById(R.id.EUR_bid)).setVisibility(View.INVISIBLE);

        ((TextView) fragmentView.findViewById(R.id.RUB_ask)).setText(privatHashMap.get(getString(R.string.RUR)).getAsk());
        ((TextView) fragmentView.findViewById(R.id.RUB_bid)).setVisibility(View.INVISIBLE);

    ((TextView) fragmentView.findViewById(R.id.ask)).setVisibility(View.INVISIBLE);
    ((TextView) fragmentView.findViewById(R.id.bid)).setVisibility(View.INVISIBLE);
}
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_currencie, null);
        drawNBU();
        return fragmentView;
    }
}
