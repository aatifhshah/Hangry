package com.lynkor.hangry;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

public class RecipesActivity extends FragmentActivity implements RecipeListFragment.DataPassListener{
    private SQLiteDatabase db;
    private Cursor recipeCursor;
    private ListAdapter cursorAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        ImageButton close = (ImageButton) findViewById(R.id.activity_recipes_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void passData(Bundle args) {

        RecipeDescFragment descFrag = (RecipeDescFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_recipedesc);
        if(descFrag != null){
            //then we are in two-pane layout
            descFrag.getData(args);
        }
    }









        /*Fragment 1*/
    // need to add a viewholder list
    // need to get all recipes from db
    // need custom adapter
    // function to increment quantity
    // parse recipe ingredients and update or insert into groceries if it exists..

        /*Fragment 2*/
    //image
    //name
    //description
    //ingredients
    //steps
}




