package com.lynkor.hangry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.lynkor.hangry.sqliteDB.DbContract;
import com.lynkor.hangry.sqliteDB.DbHelper;
import com.lynkor.hangry.sqliteDB.InventoryProvider;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
}

/*
package com.lynkor.hangry;

        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.support.v4.app.LoaderManager;
        import android.support.v4.content.CursorLoader;
        import android.support.v4.content.Loader;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.CursorAdapter;
        import android.widget.ListView;
        import android.widget.Spinner;

        import com.lynkor.hangry.sqliteDB.DbContract;
        import com.lynkor.hangry.sqliteDB.DbHelper;
        import com.lynkor.hangry.sqliteDB.InventoryProvider;

        import java.util.ArrayList;

public class AddActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView ingredientsList;
    private Spinner ingredientsSpinner;
    ListAdapter listAdapter;
    SpinnerAdapter spinnerAdapter;
    private SQLiteDatabase db;
    private String[] ingredients = new String[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

       */
/*Retrieving Cursor*//*


        //ProductDbHelper is a SQLiteOpenHelper class connecting to SQLite
        DbHelper handler = new DbHelper(this);
        //Get access to the underlying writeable database
        db = handler.getWritableDatabase();
        //Query for items from the database and get a cursor back
        Cursor ingredientCursor = db.rawQuery(DbContract.SELECT_ALL_FROM_INGREDIENTS,null);

         */
/*Attach adapters to views*//*


        //Find ListView and set adapter
        ingredientsList = (ListView) findViewById(R.id.ingredients_list);
        listAdapter = new ListAdapter(this, ingredients);
        ingredientsList.setAdapter(listAdapter);


        //Find Spinner and set adapter
        ingredientsSpinner = (Spinner) findViewById(R.id.choose_ingredient);
        spinnerAdapter = new SpinnerAdapter(this, ingredientCursor);
        ingredientsSpinner.setAdapter(spinnerAdapter);



    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {DbContract.IngredientsEntry.ID, DbContract.IngredientsEntry.COLUMN_NAME};
        CursorLoader cursorLoader = new CursorLoader(this, InventoryProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        spinnerAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        spinnerAdapter.swapCursor(null);
    }
}

class ListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public ListAdapter(Context context, String[] values){
        super(context, R.layout.activity_add_listitem, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.activity_add_listitem, parent, false);

        return row;
    }

}

class SpinnerAdapter extends CursorAdapter {

    public SpinnerAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.activity_add_spinneritem, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}*/
