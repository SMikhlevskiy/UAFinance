package smikhlevskiy.uafinance.UI;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;


import java.util.HashMap;

import smikhlevskiy.uafinance.R;
import smikhlevskiy.uafinance.Threadas.FinanceUAAsyncTask;
import smikhlevskiy.uafinance.Threadas.InterBankAsyncTask;
import smikhlevskiy.uafinance.Threadas.PrivatAsyncTask;
import smikhlevskiy.uafinance.Utils.UAFinancePreference;
import smikhlevskiy.uafinance.adapters.OrganizationListAdapter;
import smikhlevskiy.uafinance.model.Currencie;
import smikhlevskiy.uafinance.model.FinanceUA;
import smikhlevskiy.uafinance.model.Organization;


public class MainActivity extends AppCompatActivity {
    //private EditText resultTextEdit;
    final static String TAG = MainActivity.class.getSimpleName();
    OrganizationListAdapter organizationListAdapter;
    UAFinancePreference uaFinancePreference;
    ListView organizationListView;
    Handler mainActivityReDrawHandler;
    boolean startRefresh = true;

    private HashMap<String, Currencie> privatHashMap=null;

    /*-----------*/
    public void startRefreshDatas() {
        (new FinanceUAAsyncTask(
                this,
                mainActivityReDrawHandler,
                organizationListAdapter,
                (Spinner) findViewById(R.id.spinerCurrency),
                (Spinner) findViewById(R.id.spinerCity)
        )).execute(getString(R.string.financeua_json_path));

        (new PrivatAsyncTask(mainActivityReDrawHandler)).execute();
        (new InterBankAsyncTask(mainActivityReDrawHandler)).execute();

    }

    /*---*/
    public void showCurrencyCashFragment(FinanceUA financeUA) {
        Fragment fragment = getFragmentManager().findFragmentByTag(CurrencyCashFragment.TAG);
        if (fragment == null) {

            fragment = new CurrencyCashFragment();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.CurencieFrame, fragment, CurrencyCashFragment.TAG);
            ft.commit();

        }


        if (financeUA != null)
            ((IShowFragment) fragment).setFinanceUA(financeUA);

    }

    /*---*/
    public void showInterBankFragment(FinanceUA financeUA) {
        Fragment fragment = getFragmentManager().findFragmentByTag(InterBankFragment.TAG);
        if (fragment == null) {

            fragment = new InterBankFragment();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.CurencieFrame, fragment, InterBankFragment.TAG);
            ft.commit();

        }


        if (financeUA != null)
            ((IShowFragment) fragment).setFinanceUA(financeUA);

    }

    /*---*/
    public void showNBUFragment(FinanceUA financeUA) {
        Fragment fragment = getFragmentManager().findFragmentByTag(NBUFragment.TAG);
        if (fragment == null) {

            fragment = new NBUFragment();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.CurencieFrame, fragment, NBUFragment.TAG);
            ft.commit();

        }


        if (privatHashMap != null)
            ((NBUFragment) fragment).setPrivatHashMap(privatHashMap);

    }

    /*---*/
    public void showPreciousMetalsFragment() {
        Fragment fragment = getFragmentManager().findFragmentByTag(PreciousMetalsFragment.TAG);
        if (fragment == null) {

            fragment = new PreciousMetalsFragment();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.CurencieFrame, fragment, PreciousMetalsFragment.TAG);
            ft.commit();

        }

        if (privatHashMap != null)
            ((PreciousMetalsFragment) fragment).setPrivatHashMap(privatHashMap);


    }
    /*-----*/
    public void reDrawNBU(){
        Fragment fragment = getFragmentManager().findFragmentByTag(NBUFragment.TAG);
        if (fragment != null) {
            ((NBUFragment) fragment).setPrivatHashMap(privatHashMap);
            ((NBUFragment) fragment).drawNBU();
        }
        fragment = getFragmentManager().findFragmentByTag(PreciousMetalsFragment.TAG);
        if (fragment != null) {
            ((PreciousMetalsFragment) fragment).setPrivatHashMap(privatHashMap);
            ((PreciousMetalsFragment) fragment).drawNBU();
        }
    }

    /* ------------*/
    public void reDrawMainActivity() {
        FinanceUA financeUA = ((OrganizationListAdapter) organizationListView.getAdapter()).getFinanceUA();
        if (financeUA == null) return;
        financeUA.sort((uaFinancePreference.getAskBid().equals(MainActivity.this.getResources().getStringArray(R.array.askbid)[0])),
                uaFinancePreference.getCity(), uaFinancePreference.getCurrancie());


        ((BaseAdapter) organizationListView.getAdapter()).notifyDataSetChanged();
        Fragment fragment;
        fragment = getFragmentManager().findFragmentByTag(CurrencyCashFragment.TAG);
        if (fragment != null) {
            ((IShowFragment) fragment).setFinanceUA(financeUA);
            ((IShowFragment) fragment).drawFinanceUA();
        }

        fragment = getFragmentManager().findFragmentByTag(InterBankFragment.TAG);
        if (fragment != null) {
            ((IShowFragment) fragment).setFinanceUA(financeUA);
            ((IShowFragment) fragment).drawFinanceUA();
        }


//

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.i(TAG, "Begin OnCreate");
        startRefresh = true;
        setContentView(R.layout.activity_main);


        //CurrencieFragment

        if (savedInstanceState == null)
            showCurrencyCashFragment(null);

        //ActionBar
        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_gradient));
        String titleString = getResources().getString(R.string.title_activity_finance);
        Spannable span = new SpannableString(titleString);
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.myYellow)), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.myBlu)), 1, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ab.setTitle(span);
        ab.setDisplayHomeAsUpEnabled(true);

        uaFinancePreference = new UAFinancePreference(this);


        //---------CurrencieSpinner

        Spinner spinnerCurrencie = ((Spinner) findViewById(R.id.spinerCurrency));

        ArrayAdapter<String> CurrencieAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{uaFinancePreference.getCurrancie()});
        spinnerCurrencie.setAdapter(CurrencieAdapter);


        spinnerCurrencie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //ArrayAdapter <String> a=parent;

                uaFinancePreference.setCurrancie((String) parent.getAdapter().getItem(position));

                reDrawMainActivity();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //---------CitySpinner

        Spinner spinnerCity = ((Spinner) findViewById(R.id.spinerCity));

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{uaFinancePreference.getCity()});
        spinnerCity.setAdapter(cityAdapter);


        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //ArrayAdapter <String> a=parent;


                uaFinancePreference.setCity((String) parent.getAdapter().getItem(position));

                reDrawMainActivity();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //--------askBidSpinner---------
        String askBid[] = getResources().getStringArray(R.array.askbid);
        Spinner spinnerAskBid = ((Spinner) findViewById(R.id.spinerAskBid));

        ArrayAdapter<String> askBidAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, askBid);
        spinnerAskBid.setAdapter(askBidAdapter);

        spinnerAskBid.setSelection(askBidAdapter.getPosition(uaFinancePreference.getAskBid()));

        spinnerAskBid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //ArrayAdapter <String> a=parent;

                uaFinancePreference.setAskBid((String) parent.getAdapter().getItem(position));

                reDrawMainActivity();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinnerAskBid.setON`


        organizationListAdapter = new OrganizationListAdapter(this);
        organizationListView = (ListView) findViewById(R.id.listViewBanks);
        organizationListView.setAdapter(organizationListAdapter);
        organizationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Organization organization = (Organization) organizationListAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, OrganizationActivity.class);


                intent.putExtra("organization", organization);
                intent.putExtra("city", organizationListAdapter.getFinanceUA().getCities().get(organization.getCityId()));
                intent.putExtra("region", organizationListAdapter.getFinanceUA().getRegions().get(organization.getRegionId()));


                startActivity(intent);

            }
        });


        //ab.setIcon(R.mipmap.currency_exchange);
        //ab.setDisplayOptions(ab.DISPLAY_SHOW_HOME | ab.DISPLAY_SHOW_TITLE);

        //resultTextEdit = (EditText) findViewById(R.id.editTextResult);

        mainActivityReDrawHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1://on Read finance datas
                        reDrawMainActivity();
                        break;
                    case 2:
                        reDrawNBU();
                        privatHashMap=(HashMap<String, Currencie>)msg.obj;
                        break;

                }
            }
        };

        //startRefreshDatas();
        Log.i(TAG, "End OnCreate");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (item.getItemId()) {
            case R.id.refreshmenuitem:
                startRefreshDatas();
                break;
            case android.R.id.home:
                finish();
                /*
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                */
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (startRefresh)
            startRefreshDatas();
        startRefresh = false;
    }


    public void onclick(View v) {
        switch (v.getId()) {
            case R.id.currencyCashButton:

                showCurrencyCashFragment(((OrganizationListAdapter) organizationListView.getAdapter()).getFinanceUA());

                break;
            case R.id.InterBankButton:
                showInterBankFragment(((OrganizationListAdapter) organizationListView.getAdapter()).getFinanceUA());

                break;
            case R.id.NBUButton:
                showNBUFragment(((OrganizationListAdapter) organizationListView.getAdapter()).getFinanceUA());

                break;
            case R.id.PreciousMetalsButton:
                showPreciousMetalsFragment();

                break;


        }

    }
}
