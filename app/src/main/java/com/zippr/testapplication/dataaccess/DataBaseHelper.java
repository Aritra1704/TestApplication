package com.zippr.testapplication.dataaccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.zippr.testapplication.BuildConfig.DEBUG;
import static com.zippr.testapplication.dataaccess.CPConstants.TN_SELECTLOC;


/**
 * Created by ARPaul on 15-01-2018.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;
    private static final String TAG = DataBaseHelper.class.getSimpleName();

    static final String CREATE_T_LOCATION =
            " CREATE TABLE IF NOT EXISTS "                      + TN_SELECTLOC +
                    " (" + CPConstants.T_ID                    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CPConstants.T_SelLoc.LOC_ID                + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, " +
                    CPConstants.T_SelLoc.NAME                  + " VARCHAR , " +
                    CPConstants.T_SelLoc.PARCELCOUNT           + " INTEGER , " +
                    CPConstants.T_SelLoc.LOCLAT                + " DOUBLE , " +
                    CPConstants.T_SelLoc.LOCLNG                + " DOUBLE " +
                    ");";

    DataBaseHelper(Context context){
        super(context, CPConstants.DATABASE_NAME, null, CPConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_T_LOCATION);

        if(DEBUG) Log.d(TAG, String.format("tables created: " +
                        "LOCATION%n%s ",
                CREATE_T_LOCATION
        ));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TN_SELECTLOC);
        onCreate(db);
    }
}
