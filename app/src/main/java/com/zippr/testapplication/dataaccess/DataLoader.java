package com.zippr.testapplication.dataaccess;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.zippr.testapplication.common.AppPreference;
import com.zippr.testapplication.datalayers.LocationDL;
import com.zippr.testapplication.models.SelLocDO;

import static com.zippr.testapplication.common.AppConstant.BUNDLE_LOC;
import static com.zippr.testapplication.dataaccess.DbCall.DbCallPref.FETCHALLLOCS;
import static com.zippr.testapplication.dataaccess.DbCall.DbCallPref.SAVELOC;
import static com.zippr.testapplication.dataaccess.CPConstants.TYPE_DEFAULT;

/**
 * Created by aritrapal on 26/07/17.
 */

public class DataLoader extends AsyncTaskLoader {

    private Context context;
    private Bundle bundle;
    private int call = TYPE_DEFAULT;
    private AppPreference preference;

    public DataLoader(Context context, Bundle bundle, @DbCall.DbCallPref int call){
        super(context);
        this.context = context;
        this.bundle = bundle;
        this.call = call;

        preference = new AppPreference(context);
    }

    @Override
    public Object loadInBackground() {
        switch (call) {
            case FETCHALLLOCS:
                return new LocationDL().fetchAllLocs(context, bundle);
            case SAVELOC:
                SelLocDO objSelLocDO = (SelLocDO) bundle.get(BUNDLE_LOC);
                return new LocationDL().insertLocation(context, objSelLocDO);
            default:
                return null;
        }
    }
}
