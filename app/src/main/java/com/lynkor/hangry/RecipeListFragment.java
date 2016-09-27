package com.lynkor.hangry;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lynkor.hangry.sqliteDB.DbContract;
import com.lynkor.hangry.sqliteDB.DbHelper;

import static android.R.attr.name;
import static com.lynkor.hangry.R.drawable.groceries;

public class RecipeListFragment extends Fragment {
    private final static String TAG = RecipeListFragment.class.getSimpleName();
    private SQLiteDatabase db;
    private Cursor recipeCursor;
    DataPassListener fragCallback;

    public interface DataPassListener{
        void passData(Bundle args);

        //need to pass 
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            fragCallback = (DataPassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DataPassListener");
        }
    }

    @Override
    public void onCreate(Bundle saved){
        super.onCreate(saved);
        getRecipes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupList();

    }

    private void getRecipes(){
        //Get db instance
        DbHelper handler = DbHelper.getInstance(this.getContext());
        db = handler.getWritableDatabase();
        //Get cursor
        recipeCursor = db.rawQuery(DbContract.SELECT_ALL_FROM_RECIPES, null);
    }

    private void setupList(){
        ListView listView = (ListView) getView().findViewById(R.id.recipe_list);
        ListAdapter cursorAdapter = new ListAdapter(this, recipeCursor, 0, db, fragCallback);
        listView.setAdapter(cursorAdapter);
    }
}





class ListAdapter extends CursorAdapter {
    private Cursor cf;
    private LayoutInflater cursorInflater;
    private SQLiteDatabase db;
    private RecipeListFragment fragment;
    private RecipeListFragment.DataPassListener fragCallback;


    public ListAdapter(RecipeListFragment fragment, Cursor c, int flags, SQLiteDatabase db, RecipeListFragment.DataPassListener fragCallback) {
        super(fragment.getContext(), c, flags);
        this.cf = c;
        this.db = db;
        this.fragment = fragment;
        this.fragCallback = fragCallback;
        cursorInflater = (LayoutInflater) fragment.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return cursorInflater.inflate(R.layout.activity_recipes_listitem, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView recipe = (TextView) view.findViewById(R.id.recipe_listitem);
        Button addToMeals = (Button) view.findViewById(R.id.fragment_recipelist_addbutton);

        recipe.setText(cf.getString(cf.getColumnIndexOrThrow(DbContract.RecipesEntry.COLUMN_NAME)));
        recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("hallo", "HALFDKJLSJFIODSFJOISJF");
                Bundle args = new Bundle();
                args.putString(DbContract.RecipesEntry.COLUMN_NAME, cf.getString(cf.getColumnIndexOrThrow(DbContract.RecipesEntry.COLUMN_NAME)));
                args.putString(DbContract.RecipesEntry.COLUMN_IMAGE, cf.getString(cf.getColumnIndexOrThrow(DbContract.RecipesEntry.COLUMN_IMAGE)));
                args.putString(DbContract.RecipesEntry.COLUMN_INGREDIENTS, cf.getString(cf.getColumnIndexOrThrow(DbContract.RecipesEntry.COLUMN_INGREDIENTS)));
                args.putString(DbContract.RecipesEntry.COLUMN_STEPS, cf.getString(cf.getColumnIndexOrThrow(DbContract.RecipesEntry.COLUMN_STEPS)));
                fragCallback.passData(args);

            }

        });

        addToMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //nned to add to groceries here
                //need to add to meals here
                String rawIngredients = cf.getString(cf.getColumnIndexOrThrow(DbContract.RecipesEntry.COLUMN_INGREDIENTS));
                String [] ingredients = rawIngredients.substring(0,rawIngredients.length()-1).split("&");


                //put recipe ingredients inside groceries)
                for (int i = 0; i < ingredients.length; i++) {
                    ContentValues grocery = new ContentValues();
                    String[] ingredient = ingredients[i].split(":");
                    grocery.put(DbContract.GroceriesEntry.COLUMN_NAME, ingredient[0]);
                    grocery.put(DbContract.GroceriesEntry.COLUMN_QUANTITY, ingredient[1]);
                    grocery.put(DbContract.GroceriesEntry.COLUMN_UNIT, ingredient[2]);

                    db.execSQL("UPDATE groceries SET quantity = quantity + "+ingredient[1]+" WHERE name = '"+ingredient[0]+"'");
                    SQLiteStatement check = db.compileStatement("SELECT changes()");
                    if(check.simpleQueryForLong() == 0)
                        db.insert(DbContract.GroceriesEntry.TABLE_GROCERIES, null, grocery);


                }

                //insert recipe name into meal planner
                ContentValues meal = new ContentValues();
                meal.put(DbContract.MealsEntry.COLUMN_NAME, cf.getString(cf.getColumnIndexOrThrow(DbContract.RecipesEntry.COLUMN_NAME)));
                db.insert(DbContract.MealsEntry.TABLE_MEALS, null, meal);


                Toast.makeText(view.getContext(), "Added to Meal Planner and Groceries", Toast.LENGTH_SHORT).show();
            }
        });


    }


}




















/*package com.lynkor.hangry;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lynkor.hangry.sqliteDB.DbContract;
import com.lynkor.hangry.sqliteDB.DbHelper;

public class RecipeListFragment extends Fragment {
    private final static String TAG = RecipeListFragment.class.getSimpleName();
    private ListView listView;
    private ListAdapter cursorAdapter;
    private SQLiteDatabase db;
    private Cursor recipeCursor;
    DataPassListener fragCallback;

    public interface DataPassListener{
        public void passData(String data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            fragCallback = (DataPassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DataPassListener");
        }
    }

    @Override
    public void onCreate(Bundle saved){
        super.onCreate(saved);
        getRecipes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupList();

    }

    private void getRecipes(){
        //Get db instance
        DbHelper handler = DbHelper.getInstance(this.getContext());
        db = handler.getWritableDatabase();
        //Get cursor
        recipeCursor = db.rawQuery(DbContract.SELECT_ALL_FROM_RECIPES, null);
    }

    private void setupList(){
        listView = (ListView) getView().findViewById(R.id.recipe_list);
        cursorAdapter = new ListAdapter(this, recipeCursor, 0, db, fragCallback);
        listView.setAdapter(cursorAdapter);
    }
}





class ListAdapter extends CursorAdapter {
    private Cursor cf;
    private LayoutInflater cursorInflater;
    private SQLiteDatabase db;
    private RecipeListFragment fragment;
    private RecipeListFragment.DataPassListener fragCallback;

    public ListAdapter(RecipeListFragment fragment, Cursor c, int flags, SQLiteDatabase db, RecipeListFragment.DataPassListener fragCallback) {
        super(fragment.getContext(), c, flags);
        this.cf = c;
        this.db = db;
        this.fragment = fragment;
        this.fragCallback = fragCallback;
        cursorInflater = (LayoutInflater) fragment.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return cursorInflater.inflate(R.layout.activity_recipes_listitem, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView recipe = (TextView) view.findViewById(R.id.recipe_listitem);
        recipe.setText(cf.getString(cf.getColumnIndexOrThrow(DbContract.RecipesEntry.COLUMN_NAME)));
        recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragCallback.passData("HALLO!");
            }
        });
    }
}*/