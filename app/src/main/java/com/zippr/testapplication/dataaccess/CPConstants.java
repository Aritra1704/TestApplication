package com.zippr.testapplication.dataaccess;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by ARPaul on 07-01-2016.
 */
public class CPConstants {
    public static final String CONTENT_AUTHORITY                = "com.zippr.testapplication.dataaccess.CPHelper";

    public static final String DATABASE_NAME                    = "TestApp.sqlite";

    public static final String TN_SELECTLOC                     = "tblSelLoc";

    public static final String AS_SELECTLOC                     = " tSL";

    public static final int DATABASE_VERSION                   = 1;

    public static final String PATH_RELATIONSHIP_JOIN          = "relationship_join";

    public final static int TYPE_DEFAULT                = 0;

    public static final String DELIMITER = "/";
    public static final String T_ID    = "_id";
    public static final String TAG_ID = "/#";
    public static final String TAG_ID_ALL = "/*";

    public static final String T_INNER_JOIN = " INNER JOIN ";
    public static final String T_LEFT_OUTER_JOIN = " LEFT OUTER JOIN ";
    public static final String T_ON = " ON ";
    public static final String T_DOT = ".";
    public static final String T_COMMA = ",";
    public static final String T_EQUAL = " = ";
    public static final String T_NOT_EQUAL = "!=";
    public static final String T_WHERE = " WHERE ";
    public static final String T_FROM = " FROM ";
    public static final String T_AND = " AND ";
    public static final String T_OR = " OR ";
    public static final String T_IN = " IN ";
    public static final String T_NOT_IN = " NOT IN ";
    public static final String T_DISTINCT = " DISTINCT ";
    public static final String T_QUES  = " = ? ";
    public static final String T_NOT_QUES  = " != ? ";
    public static final String T_IN_BRACKET  = " ( ? ) ";
    public static final String T_MAX  = " MAX( ";
    public static final String T_MAX_AS  = " ) AS ";
    public static final String T_NESTED_START  = " ( ";
    public static final String T_SELECT  = " SELECT ";
    public static final String T_COUNT_ALL = " COUNT(*) AS ";
    public static final String T_COUNT = " COUNT( ";
    public static final String T_LIMIT  = " LIMIT ";
    public static final String T_LIKE  = " LIKE ";
    public static final String T_LIKE_PREFIX  = " '%";
    public static final String T_LIKE_SUFFIX  = "%' ";
    public static final String T_ASC  = " ASC ";
    public static final String T_GROUP_BY  = " GROUP BY ";
    public static final String T_NESTED_ALIAS  = " Q";

    public static final String CONTENT = "content://";
    public static final Uri BASE_CONTENT_URI = Uri.parse(CONTENT + CONTENT_AUTHORITY);

    public static final Uri CU_SELLOC       = Uri.parse(CONTENT + CONTENT_AUTHORITY + DELIMITER + TN_SELECTLOC);

    public static final Uri CU_JOIN         = Uri.parse(CONTENT + CONTENT_AUTHORITY + DELIMITER + PATH_RELATIONSHIP_JOIN);

    public static final String PROVIDER_NAME = CONTENT_AUTHORITY;

    // create cursor of base type directory for multiple entries
    public static final String CONTENT_MULTIPLE_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + DELIMITER + CONTENT_AUTHORITY + DELIMITER + DATABASE_NAME;
    // create cursor of base type item for single entry
    public static final String CONTENT_BASE_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + DELIMITER + CONTENT_AUTHORITY + DELIMITER + DATABASE_NAME;

    public static Uri buildLocationUri(long id){
        return ContentUris.withAppendedId(CU_SELLOC, id);
    }

    public static String sqlDateDesc(String column) {
        return String.format("DATETIME(%s) DESC", column);
    }

    public static String sqlDateAsc(String column) {
        return String.format("DATE(%s) ASC", column);
    }

    public static class T_SelLoc {
        public static final String LOC_ID                   = "loc_id";
        public static final String NAME                     = "name";
        public static final String PARCELCOUNT              = "parcelCount";
        public static final String LOCLAT                   = "locLat";
        public static final String LOCLNG                   = "locLng";
    }
}
