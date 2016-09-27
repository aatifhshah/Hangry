package com.lynkor.hangry.sqliteDB;

import android.provider.BaseColumns;

/**
 * Created by LAViATHoR on 9/15/2016.
 */
public final class DbContract {


    public static final String INTEGER_PRIMARY_KEY = " INTEGER PRIMARY KEY,";
    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String DOUBLE_TYPE = " REAL";
    public static final String COMMA_SEP = ",";
    public static final String SEMICOLON_SEP = ";";
    public static final String[] SQL_CREATE_ENTRIES = new String[]{
            "CREATE TABLE " + IngredientsEntry.TABLE_INGREDIENTS + " (" +
                    IngredientsEntry._ID + INTEGER_PRIMARY_KEY +
                    IngredientsEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    IngredientsEntry.COLUMN_UNIT + TEXT_TYPE +
                    " ); "
            ,
            "CREATE TABLE " + RecipesEntry.TABLE_RECIPES + " (" +
                    RecipesEntry._ID + INTEGER_PRIMARY_KEY +
                    RecipesEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    RecipesEntry.COLUMN_IMAGE + TEXT_TYPE + COMMA_SEP +
                    RecipesEntry.COLUMN_INGREDIENTS + TEXT_TYPE + COMMA_SEP +
                    RecipesEntry.COLUMN_STEPS + TEXT_TYPE + COMMA_SEP +
                    RecipesEntry.COLUMN_QUANTITY + INTEGER_TYPE +
                    " )"
            ,
            "CREATE TABLE " + GroceriesEntry.TABLE_GROCERIES + " (" +
                    GroceriesEntry._ID + INTEGER_PRIMARY_KEY +
                    GroceriesEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    GroceriesEntry.COLUMN_UNIT + TEXT_TYPE + COMMA_SEP +
                    GroceriesEntry.COLUMN_QUANTITY + INTEGER_TYPE +
                    " )"
            ,
            "CREATE TABLE " + MealsEntry.TABLE_MEALS + " (" +
                    MealsEntry.MEALS_ID + INTEGER_PRIMARY_KEY +
                    MealsEntry.COLUMN_NAME + TEXT_TYPE +
                    " )"
//            ,
//            "CREATE TABLE " + MealsEntry.TABLE_PLANNER + " (" +
//                    MealsEntry.PLANNER_ID + INTEGER_PRIMARY_KEY +
//                    MealsEntry.COLUMN_MONDAY + TEXT_TYPE +
//                    MealsEntry.COLUMN_TUESDAY + TEXT_TYPE +
//                    MealsEntry.COLUMN_WEDNESDAY + TEXT_TYPE +
//                    MealsEntry.COLUMN_THURSDAY + TEXT_TYPE +
//                    MealsEntry.COLUMN_FRIDAY + TEXT_TYPE +
//                    MealsEntry.COLUMN_SATURDAY + TEXT_TYPE +
//                    MealsEntry.COLUMN_SUNDAY + TEXT_TYPE +
//                    " )"

    };

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + IngredientsEntry.TABLE_INGREDIENTS + "; " +
                    "DROP TABLE IF EXISTS " + RecipesEntry.TABLE_RECIPES;

    public static final String SQL_DELETE_MEALS =
            "DROP TABLE IF EXISTS " + MealsEntry.TABLE_MEALS;

    public static final String SELECT_COLUMN_NAME_FROM_INGREDIENTS =
            "SELECT "
                    + IngredientsEntry.COLUMN_NAME
                    + " FROM "
                    + IngredientsEntry.TABLE_INGREDIENTS;

    public static final String SELECT_COLUMN_NAME_FROM_RECIPES =
            "SELECT "
                    + RecipesEntry.COLUMN_NAME
                    + " FROM "
                    + RecipesEntry.TABLE_RECIPES;

    public static final String SELECT_COLUMN_NAME_FROM_GROCERIES =
            "SELECT "
                    + RecipesEntry.COLUMN_NAME
                    + " FROM "
                    + GroceriesEntry.TABLE_GROCERIES;

    public static final String SELECT_ALL_FROM_INGREDIENTS =
            "SELECT"
                    + " * "
                    + "FROM "
                    + IngredientsEntry.TABLE_INGREDIENTS;

    public static final String SELECT_ALL_FROM_RECIPES =
            "SELECT"
                    + " * "
                    + "FROM "
                    + RecipesEntry.TABLE_RECIPES;


    public static final String SELECT_ALL_FROM_GROCERIES =
            "SELECT"
                    + " * "
                    + "FROM "
                    + GroceriesEntry.TABLE_GROCERIES;


    public static final String SELECT_ALL_FROM_MEALS =
            "SELECT"
                    + " * "
                    + "FROM "
                    + MealsEntry.TABLE_MEALS;




    public static final String SELECT_ROW_FROM_INGREDIENTS =
            "SELECT "
                    + IngredientsEntry._ID
                    + " FROM  "
                    + IngredientsEntry.TABLE_INGREDIENTS
                    + " WHERE _id=?";


    public static final String INCREMENT_GROCERY_QUANTITY =
            "UPDATE "
                    + GroceriesEntry.TABLE_GROCERIES
                    + " SET quantity = quantity + 1 WHERE _id = ";


    public static final String DECREMENT_GROCERY_QUANTITY =
            "UPDATE "
                    + GroceriesEntry.TABLE_GROCERIES
                    + " SET quantity = quantity - 1 WHERE _id = ";


    public DbContract() {
    }

    public static abstract class IngredientsEntry implements BaseColumns {
        public static final String TABLE_INGREDIENTS = "ingredients";
        public static final String ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_UNIT = "unit";
        public static final String spinner_hint = "Ingredient";
        public static final String new_ingredient = "Add New Ingredient";
    }

    public static abstract class RecipesEntry implements BaseColumns {
        public static final String TABLE_RECIPES = "recipes";
        public static final String ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_INGREDIENTS = "ingredients";
        public static final String COLUMN_STEPS = "steps";
        public static final String COLUMN_QUANTITY = "quantity";
    }

    public static abstract class GroceriesEntry implements BaseColumns {
        public static final String TABLE_GROCERIES = "groceries";
        public static final String ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_UNIT = "unit";
        public static final String COLUMN_QUANTITY = "quantity";
    }

    public static abstract class MealsEntry implements BaseColumns {
        public static final String TABLE_MEALS = "meals";
        public static final String MEALS_ID = "_id";
        public static final String COLUMN_NAME = "name";


        static final String TABLE_PLANNER = "planner";
        static final String PLANNER_ID  = "_id";
        static final String COLUMN_MONDAY = "monday";
        static final String COLUMN_TUESDAY= "tuesday";
        static final String COLUMN_WEDNESDAY= "wednesday";
        static final String COLUMN_THURSDAY = "thursday";
        static final String COLUMN_FRIDAY = "friday";
        static final String COLUMN_SATURDAY = "saturday";
        static final String COLUMN_SUNDAY= "sunday";


    }

}