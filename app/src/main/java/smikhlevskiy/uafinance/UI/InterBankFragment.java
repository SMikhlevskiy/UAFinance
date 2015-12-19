package smikhlevskiy.uafinance.UI;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.model.FinanceUA;

/**
 * Created by tcont98 on 19-Dec-15.
 */
public class InterBankFragment extends Fragment implements IShowFragment {
    public static String TAG=InterBankFragment.class.getSimpleName();
    private   View fragmentView;
    private  FinanceUA financeUA;
    @Override
    public void drawFinanceUA() {

        ((TextView) fragmentView.findViewById(R.id.USD_ask)).setText("0.0");
        ((TextView) fragmentView.findViewById(R.id.USD_bid)).setText("0.0");

        ((TextView) fragmentView.findViewById(R.id.EUR_ask)).setText("0.0");
        ((TextView) fragmentView.findViewById(R.id.EUR_bid)).setText("0.0");

        ((TextView) fragmentView.findViewById(R.id.RUB_ask)).setText("0.0");
        ((TextView) fragmentView.findViewById(R.id.RUB_bid)).setText("0.0");

    }
    @Override
    public void setFinanceUA(FinanceUA financeUA){
        this.financeUA=financeUA;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_currencie, null);

        return fragmentView;
    }
}
