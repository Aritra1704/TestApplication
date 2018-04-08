package com.zippr.testapplication.dataaccess;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by aritrapal on 19/07/17.
 */

public class DbCall {
    @IntDef({DbCallPref.FETCHALLLOCS, DbCallPref.SAVELOC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DbCallPref {
        int FETCHALLLOCS            = 601;
        int SAVELOC                 = 602;
    };
}
