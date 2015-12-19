package smikhlevskiy.uafinance.UI;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.zip.Inflater;

import smikhlevskiy.uafinance.R;

/**
 * Created by tcont98 on 19-Dec-15.
 */
public class PreciousMetalsFragment extends Fragment {

    public static String TAG=PreciousMetalsFragment.class.getSimpleName();
    private View fragmentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView= inflater.inflate(R.layout.fragment_precious_metals,null);
        return fragmentView;
    }



}
