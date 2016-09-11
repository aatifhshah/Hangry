package com.lynkor.hangry;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final static String TAG = "Main Activity:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set OnClickListeners for each ImageButton
        ImageButton banner_button = (ImageButton) findViewById(R.id.banner_button);
        banner_button.setOnClickListener(this);

        ImageButton meals_button = (ImageButton) findViewById(R.id.meals_button);
        meals_button.setOnClickListener(this);

        ImageButton recipes_button = (ImageButton) findViewById(R.id.recipes_button);
        recipes_button.setOnClickListener(this);

        ImageButton groceries_button = (ImageButton) findViewById(R.id.groceries_button);
        groceries_button.setOnClickListener(this);

        ImageButton add_button = (ImageButton) findViewById(R.id.add_button);
        add_button.setOnClickListener(this);
    }

    //OnClick function that handles individual button clicks.
    @Override
    public void onClick(View view) {
        //default method for handling click events
        switch (view.getId()){
            case R.id.banner_button:
                Intent intent = new Intent(this, BannerActivity.class);
                startActivity(intent);
                break;
            case R.id.meals_button:
                Intent meals_intent = new Intent(this, MealsActivity.class);
                startActivity(meals_intent);
                break;
            case R.id.recipes_button:
                Intent recipes_intent = new Intent(this, RecipesActivity.class);
                startActivity(recipes_intent);
                break;
            case R.id.groceries_button:
                Intent groceries_intent = new Intent(this, GroceriesActivity.class);
                startActivity(groceries_intent);
                break;
            case R.id.add_button:
                Intent add_intent = new Intent(this, AddActivity.class);
                startActivity(add_intent);
                break;

        }

    }
}
