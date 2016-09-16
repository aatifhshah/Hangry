package com.lynkor.hangry.sqliteDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by LAViATHoR on 9/15/2016.
 */
public class DbHelper extends SQLiteOpenHelper{

    private static final String DEBUG_TAG = "DbHelper";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "inventory_data";
    private final String DB_SCHEMA = DbContract.SQL_CREATE_ENTRIES;

    public DbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DB_SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(DEBUG_TAG, "Upgrading database. Existing contents will be lost. [" + oldVersion + "]->[" + newVersion + "]");
        sqLiteDatabase.execSQL(DbContract.SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

}
