package com.lynkor.hangry.sqliteDB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by LAViATHoR on 9/15/2016.
 */
public class DbHelper extends SQLiteOpenHelper{
    private static DbHelper sInstance;

    private static final String DEBUG_TAG = "DbHelper";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "inventory_data";
    private final String[] DB_SCHEMA = DbContract.SQL_CREATE_ENTRIES;

    private DbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized DbHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for(String statement : DB_SCHEMA){
            sqLiteDatabase.execSQL(statement);
        }
        Log.v(DEBUG_TAG, sqLiteDatabase.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(DEBUG_TAG, "Upgrading database. Existing contents will be lost. [" + oldVersion + "]->[" + newVersion + "]");
        sqLiteDatabase.execSQL(DbContract.SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }



}
