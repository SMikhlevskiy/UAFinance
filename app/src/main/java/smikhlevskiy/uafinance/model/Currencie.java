package smikhlevskiy.uafinance.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tcont98 on 08-Nov-15.
 */
public class Currencie implements Parcelable {

    private String ask;
    private String bid;




    public String getAsk() {
        return ask;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ask);
        dest.writeString(bid);
    }
    protected Currencie(Parcel in) {
        ask = in.readString();
        bid = in.readString();
    }

    public static final Creator<Currencie> CREATOR = new Creator<Currencie>() {
        @Override
        public Currencie createFromParcel(Parcel in) {
            return new Currencie(in);
        }

        @Override
        public Currencie[] newArray(int size) {
            return new Currencie[size];
        }
    };

    public Currencie() {
    }
}
