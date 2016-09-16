package com.lynkor.hangry;

import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity{
    private final String DEBUG_TAG = "AddActivity: ";
    private ListView ingredientsList;
    private ListView stepsList;
    ListAdapter ingredientsAdapter;
    ListAdapter stepsAdapter;
    private ArrayList<String> ingredients;
    private ArrayList<String> steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ingredients = new ArrayList<>();
        steps = new ArrayList<>();

         /*Attach adapters to views*/

        //Find ListView and set adapter for Ingredients
        ingredientsList = (ListView) findViewById(R.id.ingredients_list);
        ingredientsAdapter = new ListAdapter(this, ingredients, R.layout.activity_add_listitem, true);
        ingredientsList.setAdapter(ingredientsAdapter);

        //Find ListView and set adapter for Steps
        stepsList = (ListView) findViewById(R.id.steps_list);
        stepsAdapter = new ListAdapter(this, steps, R.layout.activity_add_stepitem, false);
        stepsList.setAdapter(stepsAdapter);

        //Find Spinner and set adapter for stored ingredients


        /*Add Buttons*/

        //ImageButton for choosing from file
        ImageButton recipeImage = (ImageButton) findViewById(R.id.recipe_image);
        recipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show custom popover
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.activity_add_image_popover, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                builder.setTitle("Recipe Image");
                builder.setView(dialogLayout);
                builder.show();
            }
        });

        //Find and assign onClickListeners for "Add New" Buttons
        Button addIngredient = (Button) findViewById(R.id.add_ingredient);
        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientsAdapter.add("New Ingredient");
            }
        });

        Button addStep = (Button) findViewById(R.id.add_step);
        addStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepsAdapter.add("New Step");
            }
        });


    }
}

class ListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;
    private final int layout;
    private final boolean hasSpinner;
    private boolean selected;
    ArrayAdapter<String> dataAdapter;
    private TextView text;

    public ListAdapter(Context context, ArrayList<String> values, int layout, boolean hasSpinner){
        super(context, layout, values);
        this.context = context;
        this.values = values;
        this.layout = layout;
        this.hasSpinner = hasSpinner;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(layout, parent, false);



        if(hasSpinner){
            Spinner spinner = (Spinner) row.findViewById(R.id.choose_ingredient);
            List<String> categories = new ArrayList<String>();
            categories.add("This is a Hint");
            categories.add("Business Services");
            categories.add("Computers");
            categories.add("Education");
            categories.add("Personal");
            categories.add("Travel");
            dataAdapter = new ArrayAdapter<String>(row.getContext(), android.R.layout.simple_spinner_item, categories);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
        }


        return row;
    }

    private View.OnTouchListener typeSpinnerTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            selected = true;
            ((BaseAdapter) dataAdapter).notifyDataSetChanged();
            return false;
        }
    };

}

// TODO Use later with RelativeLayout
/*class NonScrollListView extends ListView {

    public NonScrollListView(Context context) {
        super(context);
    }
    public NonScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public NonScrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}*/







