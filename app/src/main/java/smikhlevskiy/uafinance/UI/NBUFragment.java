package smikhlevskiy.uafinance.UI;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.model.FinanceUA;

/**
 * Created by tcont98 on 19-Dec-15.
 */
public class NBUFragment extends Fragment implements IShowFragment {
    public static String TAG = NBUFragment.class.getSimpleName();
    private View fragmentView;

    @Override
    public void drawFinanceUA() {

    }

    @Override
    public void setFinanceUA(FinanceUA financeUA) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_currencie, null);
        return fragmentView;
    }
}
