package smikhlevskiy.uafinance.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import java.util.Map;

import smikhlevskiy.uafinance.R;

/**
 * Created by Smikhlevskiy on 13-Jan-16.
 * Constant & Utils static functions for UAFinance application
 */
public class UAFConst {
    //list of Main banks
    public static final int PRIVAT_ID = 0;
    public static final int UKRSOC_ID = 1;
    public static final int PRAVEKS_ID = 2;
    public static final int PUMB_ID = 3;
    public static final int UKRGAZ_ID = 4;
    public static final int ALFA_ID = 5;
    public static final int OTP_ID = 6;
    public static final int AGRICOL_ID = 7;

    public static final int OSCHAD_ID = 8;

    public static final int UKRSIB_ID = 9;
    public static final int TAS_ID = 10;
    public static final int AVAL_ID = 11;
    public static final int SBER_ID = 12;


    public static final String NAME_USD="USD";
    public static final String NAME_EUR="EUR";
    public static final String NAME_RUB="RUB";
    public static final String[] BASE_CURRENCIES={NAME_USD,NAME_EUR,NAME_RUB};



    public static final String[] banks = {
            "Приватбанк",
            "Укрсоцбанк",
            "ПРАВЭКС",
            "ПУМБ",
            "Укргазбанк",
            "Альфа-Банк",
            "ОТП Банк",
            "Креди Агриколь Банк",
            "Ощадбанк",
            "Укрсиббанк",
            "ТАСкомбанк",
            "Райффайзен Банк Аваль",
            "СБЕРБАНК"

    };


    public static final String[] banksLc = {
            banks[0].toLowerCase(),
            banks[1].toLowerCase(),
            banks[2].toLowerCase(),
            banks[3].toLowerCase(),
            banks[4].toLowerCase(),
            banks[5].toLowerCase(),
            banks[6].toLowerCase(),
            banks[7].toLowerCase(),
            banks[8].toLowerCase(),
            banks[9].toLowerCase(),
            banks[10].toLowerCase(),
            banks[11].toLowerCase(),
            banks[12].toLowerCase()
    };

    // number of fragment in PageView
    public static final int CURRENCY_FRAGMENT_COUNT = 4;
    public static final int CURRENCY_CASH = 0;
    public static final int INTERBANK = 1;
    public static final int NBU = 2;
    public static final int PRECIOUS_METALS = 3;


    /**
     * get Image for organization by title
     * @param orgType  type=1 is bank, type=0 is exchange_shop
     * @param title of organization
     * @return
     */
    public static int getBankImage(int orgType, String title) {

        int result;


        int bankID = -1;

        for (int n = 0; n < UAFConst.banksLc.length; n++)
            if (title.toLowerCase().contains(UAFConst.banksLc[n]))
                bankID = n;

        switch (bankID) {

            case PRIVAT_ID:
                result = R.mipmap.bank_privat;
                break;

            case UKRSOC_ID:
                result = R.mipmap.bank_ukrsoc;
                break;
            case PRAVEKS_ID:
                result = R.mipmap.bank_praveks;
                break;
            case PUMB_ID:
                result = R.mipmap.bank_pumb;
                break;
            case UKRGAZ_ID:
                result = R.mipmap.bank_ukrgazbank;
                break;
            case ALFA_ID:
                result = R.mipmap.bank_alfa;
                break;
            case OTP_ID:
                result = R.mipmap.bank_otp;
                break;
            case AGRICOL_ID:
                result = R.mipmap.bank_agricol;
                break;
            case OSCHAD_ID:
                result = R.mipmap.bank_oschad;
                break;
            case UKRSIB_ID:
                result = R.mipmap.bank_ukrsib;
                break;
            case TAS_ID:
                result = R.mipmap.bank_tas;
                break;
            case AVAL_ID:
                result = R.mipmap.bank_aval;
                break;
            case SBER_ID:
                result = R.mipmap.bank_sberbank;
                break;


            default:
                if (orgType == 1)
                    result = R.mipmap.bank;
                else
                    result = R.mipmap.exchange_shop;
        }


        return result;

    }

    /**
     * get Key by Value  from Map<String, String>
     */
    public static String getKeyByValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return "";
    }

    /**
     * Make Address String for Google MAP IPI request
     * @param city        city of object
     * @param address   address of object
     * @return String for Google MAP IPI request
     */
    public static String getAddressbyAdressCity(String city, String address) {
        return ("Украина, " + city + ", " + address).replace(' ', '+');

    }

    public static Spannable getSpanTitle(Context context) {
        String titleString = context.getString(R.string.title_activity_finance);
        Spannable span = new SpannableString(titleString);
        span.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, titleString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        span.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.myYellow)), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        span.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.myBlu)), 1, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return span;

    }

}
