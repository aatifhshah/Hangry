package com.lynkor.hangry;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final static String TAG = "Main Activity:";
    FrameLayout layout_main;

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

        //Set foreground alpha to 0
        layout_main = (FrameLayout) findViewById(R.id.main);
        layout_main.getForeground().setAlpha(0);

    }

    //OnClick function that handles individual button clicks.
    @Override
    public void onClick(View view) {
        //default method for handling click events
        switch (view.getId()){
            case R.id.banner_button:
                //TODO open pop-up window with the app information, such as author’s name, software version number, URL link for help, copyright information, will be shown.
                LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.activity_banner, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
                //dim main activity
                layout_main.getForeground().setAlpha(200);
                btnDismiss.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        //Undim main activity
                        layout_main.getForeground().setAlpha(0);
                        popupWindow.dismiss();
                    }});
                popupWindow.showAtLocation(view, Gravity.CENTER, 50, -30);
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
