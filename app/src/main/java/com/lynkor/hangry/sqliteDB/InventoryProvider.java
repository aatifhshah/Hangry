package com.lynkor.hangry.sqliteDB;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by LAViATHoR on 9/15/2016.
 */
public class InventoryProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.laviathor.hangry.sqliteDB.InventoryProvider";
    public static final int INGREDIENTS = 100;
    public static final int INGREDIENTS_ID = 110;
    public static final int INGREDIENTS_NAME = 120;
    public static final int INGREDIENTS_UNIT = 130;
    public static final int RECIPES = 200;
    public static final int RECIPES_ID = 210;
    public static final int RECIPES_NAME = 220;
    public static final int RECIPES_IMAGE = 230;
    public static final int RECIPES_INGREDIENTS = 240;
    public static final int RECIPES_STEPS = 250;
    public static final int RECIPES_QUANTITY = 260;
    private static final String INGREDIENTS_BASE_PATH = "ingredients";
    private static final String RECIPES_BASE_PATH = "recipes";
    private DbHelper mDB;

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/*");

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
