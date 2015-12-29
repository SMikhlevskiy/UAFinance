package smikhlevskiy.uafinance.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import smikhlevskiy.uafinance.Utils.GeoLocationUtils;

/**
 This class allows to speed up gets Latitude & Longitude for address of all organization used in application
1) When application starting  Latitude & Longitude reads from assets/latlon_start.json
2) if Latitude & Longitude is null it get from Google Maps
3)  Latitude & Longitude saves in DataBase(Caching)
4) When application works Latitude & Longitude gets from DataBase


 */
public class GeoLocationDB extends SQLiteOpenHelper {
    public static String TAG = GeoLocationDB.class.getSimpleName();
    private static final String LATLON_START_JSON = "latlon_start.json";

    public static String DB_NAME = "GEOCACHDB";
    public static int DB_VERSION = 4;

    public static String TABLE_NAME = "GEOCACH";
    public static String KEY_ID = "_ID";
    public static String KEY_ADDRESS = "ADDRESS";
    public static String KEY_LATITUDE = "LATITUDE";
    public static String KEY_LONGITUDE = "LONGITUDE";
    private Context context;


    public GeoLocationDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }


    public LatLng getLocation(final String locationAddress) {
        SQLiteDatabase db = getReadableDatabase();
        Log.i(TAG, locationAddress);
        if (db == null) {
            Log.i(TAG, "Do not open DB");
            return null;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ADDRESS + "=?",
        /*new String[]{locationAddress}*/new String[]{locationAddress});

        if ((cursor != null) && cursor.moveToFirst()) {
            {

                return new LatLng(cursor.getDouble(2), cursor.getDouble(3));
            }


        } else {
            Log.i(TAG, "DO not get Cursor");
            return null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "ONCreateDB");
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_ADDRESS + " TEXT, " +
                KEY_LATITUDE + " FLOAT, " +
                KEY_LONGITUDE + " FLOAT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "ONUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
/*------------Read Start form JSON in assets LatLon whean first app start--------------*/
    private HashMap<String, LatLng> readStartLatLonFromJSON() {
        Log.i(TAG, "First start Application||new version BD");
        try {
            InputStreamReader isr = new InputStreamReader(context.getAssets().open(LATLON_START_JSON));
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(isr);
            String str = null;

            do {
                str = reader.readLine();
                if (str != null) {
                    sb.append(str);
                    //Log.i(TAG, str);
                }
            } while (str != null);


            isr.close();

            Gson gson = new Gson();

            Type type = new TypeToken<HashMap<String, LatLng>>(){}.getType();//!!! good idea

            return (HashMap<String, LatLng>) gson.fromJson(sb.toString(), type);


        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }


    }

    public void UpdteLocationBase(final List<String> textAddress) {
        //Log.i(TAG, text);
        new Thread(new Runnable() {
            @Override
            public void run() {
                GeoLocationUtils geoLocationUtils = new GeoLocationUtils();

                SQLiteDatabase db = getWritableDatabase();


                HashMap<String, LatLng> latLonMap = new HashMap<String, LatLng>();

                Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
                if (cursor.moveToFirst()) {
                    do {


                        latLonMap.put(cursor.getString(1), new LatLng(cursor.getDouble(2), cursor.getDouble(3)));


                    } while (cursor.moveToNext());
                }
                HashMap<String, LatLng> startAppLatLon = null;
                if (latLonMap.size() <= 0) //firstStart Application
                {
                    startAppLatLon = readStartLatLonFromJSON();
                }


                for (String text : textAddress)
                    if (!latLonMap.containsKey(text)) {
                        LatLng latLng = null;
                        if ((startAppLatLon != null) && (startAppLatLon.containsKey(text))) {//first App start or new version DB
                            latLng = startAppLatLon.get(text);
                            //Log.i(TAG, text + ":read from start JSON:" + latLng.latitude + "," + latLng.longitude);
                        } else
                            latLng = geoLocationUtils.getAddressFromLocationByURL(text);//get LatLon from GoogleMap

                        if (latLng != null) {
                            Log.i(TAG, text + ":" + latLng.toString());
                            ContentValues cv = new ContentValues();

                            cv.put(KEY_ADDRESS, text);
                            cv.put(KEY_LATITUDE, latLng.latitude);
                            cv.put(KEY_LONGITUDE, latLng.longitude);


                            db.insert(TABLE_NAME, null, cv);


                        } else Log.i(TAG, text + ": do not find address");
                    } else {
                        LatLng latLng = latLonMap.get(text);//Get Lat Long from Cash
                        //Log.i(TAG, text + ":read from DBCach:" + latLng.latitude + "," + latLng.longitude);
                    }

                db.close();
                /*
// result to GSON for first start app
                Gson gson = new Gson();
                File path = context.getExternalFilesDir(null);


                File file = new File(path, "my-file-name.txt");

                FileOutputStream stream = null;
                try {
                    stream = new FileOutputStream(file);
                    Log.i(TAG,file.getPath());
                    stream.write(gson.toJson(latLonMap).getBytes());
                    stream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

*/
            }
        }).start();


    }
}
