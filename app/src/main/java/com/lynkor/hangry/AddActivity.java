package com.lynkor.hangry;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lynkor.hangry.sqliteDB.DbContract;
import com.lynkor.hangry.sqliteDB.DbHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class AddActivity extends AppCompatActivity {
    private final String TAG = "AddActivity: ";
    private final String DEBUG_TAG = "YOOOOOOOOOO: ";

    private ListView ingredientsListView;
    private ListView stepsListView;
    ListAdapter ingredientsAdapter;
    ListAdapter stepsAdapter;
    private ArrayList<String> recipeIngredients;
    private ArrayList<String> stepsList;
    private SQLiteDatabase db;
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private Cursor ingredientsCursor;
    Cursor spinnerCursor;
    SimpleCursorAdapter spinnerAdapter;
    private Spinner[] ingredients;
    private EditText[] quantity;
    private EditText[] steps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        stepsList = new ArrayList<>();
        recipeIngredients = new ArrayList<>();
        ingredients = new Spinner[10];
        steps = new EditText[10];
        quantity = new EditText[10];

        /*Load Cursor*/

        //DbHelper is an SQLiteOpenHelper class connecting to the underlying Database
        DbHelper handler = DbHelper.getInstance(this);
        //Get access to the underlying writable database
        db = handler.getWritableDatabase();



         /*Attach List adapters to views*/

        //Find ListView and set adapter for Ingredients
        ingredientsListView = (ListView) findViewById(R.id.ingredients_list);
        //Add hint to spinner list
        ingredientsAdapter = new ListAdapter(this, recipeIngredients, R.layout.activity_add_listitem, "ingredients");
        ingredientsListView.setAdapter(ingredientsAdapter);

        //Find ListView and set adapter for Steps
        stepsListView = (ListView) findViewById(R.id.steps_list);
        stepsAdapter = new ListAdapter(this, stepsList, R.layout.activity_add_stepitem, "steps");
        stepsListView.setAdapter(stepsAdapter);

        /*Buttons Handlers*/

        //ImageButton for choosing from file
        ImageButton recipeImage = (ImageButton) findViewById(R.id.recipe_image);
        recipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.activity_add_image_popover, null);
                //Button for file storage
                ImageButton files = (ImageButton) dialogLayout.findViewById(R.id.local_file_button);
                files.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent selectIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        selectIntent.setType("image/*");

                        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("image/*");

                        Intent chooserIntent = Intent.createChooser(selectIntent, "Select Image");
                        selectIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                        startActivityForResult(chooserIntent, SELECT_PICTURE);
                    }


                });

                //show custom popover
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
                if (recipeIngredients.size() < 10) {
                    recipeIngredients.add("New Ingredient");
                    ingredientsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AddActivity.this, "Sorry, only 10 ingredients allowed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button addStep = (Button) findViewById(R.id.add_step);
        addStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stepsList.size() < 10) {
                    stepsList.add("New Step");
                    stepsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AddActivity.this, "Sorry, a maximum of 10 steps is ideal!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //add recipe
        ImageButton addRecipe = (ImageButton) findViewById(R.id.add_recipe);
        addRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logRecipe();
            }
        });

    }

    private void addNew(String name, String unit) {
        ContentValues entry = new ContentValues();
        entry.put(DbContract.IngredientsEntry.COLUMN_NAME, name);
        entry.put(DbContract.IngredientsEntry.COLUMN_UNIT, unit);

        db.insert(DbContract.IngredientsEntry.TABLE_INGREDIENTS, null, entry);
    }


    private void checkCursor() {
        final String check = DbContract.IngredientsEntry.spinner_hint;
        //Setup Cursor
        String[] queryCols = new String[]{DbContract.IngredientsEntry._ID, DbContract.IngredientsEntry.COLUMN_NAME, DbContract.IngredientsEntry.COLUMN_UNIT};
        spinnerCursor = db.query(true, DbContract.IngredientsEntry.TABLE_INGREDIENTS, queryCols, null, null, null, null, null, null);
        if (spinnerCursor != null && spinnerCursor.getCount() > 0) {
            spinnerCursor.moveToFirst();
            String name = spinnerCursor.getString(spinnerCursor.getColumnIndexOrThrow(DbContract.IngredientsEntry.COLUMN_NAME));
            if (name.equals(check)) {
                //Cursor is set up properly
            } else {
                Log.v(DEBUG_TAG, "HEREEEEEE333333");

                //setup Cursor
                setupCursor();
            }
        } else {
            Log.v(DEBUG_TAG, "HEREEEEEE");
            setupCursor();
        }
    }

    private void setupCursor() {
        //Add Hint and New Ingredient
        ContentValues spinnerHint = new ContentValues();
        ContentValues addNew = new ContentValues();
        spinnerHint.put(DbContract.IngredientsEntry.COLUMN_NAME, DbContract.IngredientsEntry.spinner_hint);
        spinnerHint.put(DbContract.IngredientsEntry.COLUMN_UNIT, "");
        addNew.put(DbContract.IngredientsEntry.COLUMN_NAME, DbContract.IngredientsEntry.new_ingredient);
        addNew.put(DbContract.IngredientsEntry.COLUMN_UNIT, "");

        db.insertOrThrow(DbContract.IngredientsEntry.TABLE_INGREDIENTS, null, spinnerHint);
        db.insertOrThrow(DbContract.IngredientsEntry.TABLE_INGREDIENTS, null, addNew);


    }


    public String getUnitType(int id) {

        Cursor cursor = db.query(DbContract.IngredientsEntry.TABLE_INGREDIENTS, new String[]{DbContract.IngredientsEntry.COLUMN_UNIT}, DbContract.IngredientsEntry.ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        String unit = cursor.getString(0);
        // return contact
        return unit;
    }

    public void logRecipe() {

        Log.v(TAG, "RECIPE IMAGE PATH: " + selectedImagePath);
        EditText recipeName = (EditText) this.findViewById(R.id.recipe_name);
        String name = recipeName.getText().toString();
        Log.v(TAG, "RECIPE NAME: " + name);
        Log.v(TAG, "RECIPE INGREDIENTS: \n");
        for (int i = 0; i < ingredients.length; i++) {
            if (ingredients[i] != null) {
                //String testCursro = ;
                Log.v("      ingredient: ", ((Cursor) ingredients[i].getSelectedItem()).getString(1) + ":" + quantity[i].getText().toString() + ":" + ((Cursor) ingredients[i].getSelectedItem()).getString(2));
            }
        }
        Log.v(TAG, "RECIPE STEPS: \n");
        for (EditText i : steps) {
            if (i != null)
                Log.v("      step:       ", i.getText().toString() + "");
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = selectedImageUri.getPath();
                Log.v(DEBUG_TAG, selectedImagePath);
                ImageButton recipeImage = (ImageButton) findViewById(R.id.recipe_image);
                recipeImage.setImageURI(selectedImageUri);
            }
        }
    }


    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }


    class ListAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> values;
        private final int layout;
        private boolean isStepList;
        private String type = "";
        public HashMap<Integer, String> savedStepText = new HashMap<>();
        public HashMap<Integer, Integer> save_spinner_position = new HashMap<>();
        public HashMap<Integer, String>  save_unit_type = new HashMap<>();
        public HashMap<Integer, String> save_quantity = new HashMap<>();




        public ListAdapter(Context context, ArrayList<String> values, int layout, String type) {
            super(context, layout, values);
            this.context = context;
            this.values = values;
            this.layout = layout;


            if (type.equals("steps"))
                this.isStepList = true;

            // initialize myList
            for (int i = 0; i < 10; i++) {
                savedStepText.put(i, "");
                save_spinner_position.put(i, 1);
                save_unit_type.put(i, "");
                save_quantity.put(i, "");
            }

        }

        private void addNewIngredient(String name, String unit) {
            ContentValues entry = new ContentValues();
            entry.put(DbContract.IngredientsEntry.COLUMN_NAME, name);
            entry.put(DbContract.IngredientsEntry.COLUMN_UNIT, unit);

            db.insert(DbContract.IngredientsEntry.TABLE_INGREDIENTS, null, entry);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View row = inflater.inflate(layout, parent, false);

            // Cancel Button
            ImageButton cancelButton = (ImageButton) row.findViewById(R.id.cancel_button);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isStepList) {
                        stepsList.remove(position);
                        stepsAdapter.notifyDataSetChanged();
                    } else {
                        recipeIngredients.remove(position);
                        ingredientsAdapter.notifyDataSetChanged();
                    }
                }
            });


            //Ingredients List
            if (!isStepList) {
                final EditText qty = (EditText) row.findViewById(R.id.enter_quantity);
                final TextView unitType = (TextView) row.findViewById(R.id.ingredient_unit);
                final Spinner spinner = (Spinner) row.findViewById(R.id.choose_ingredient);


                checkCursor();
                String[] adapterCols = new String[]{"name"};
                int[] viewsAttached = new int[]{android.R.id.text1};
                spinnerAdapter = new SimpleCursorAdapter(row.getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerCursor, adapterCols, viewsAttached, 0);
                SimpleCursorAdapter.CursorToStringConverter converter = new SimpleCursorAdapter.CursorToStringConverter() {

                    @Override
                    public CharSequence convertToString(Cursor cursor) {
                        int desiredColumn = 1;
                        return cursor.getString(desiredColumn);
                    }
                };

                spinnerAdapter.setCursorToStringConverter(converter);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);


                /*Set onClickListener for spinner*/
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        //Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString() + ", id: " + String.valueOf(id), Toast.LENGTH_SHORT).show();

                        if (id == 2) {
                            View dialogLayout = getLayoutInflater().inflate(R.layout.activity_add_new_ingredient_popover, null);
                            final AlertDialog builder = new AlertDialog.Builder(AddActivity.this).create();
                            builder.setTitle("New Ingredient");
                            builder.setView(dialogLayout);
                            builder.show();
                            //get Input
                            Button add = (Button) dialogLayout.findViewById(R.id.add_ingredient);
                            add.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    EditText name = (EditText) builder.findViewById(R.id.new_ingredient_name);
                                    EditText unit = (EditText) builder.findViewById(R.id.new_ingredient_unit);
                                    final String newName = name.getText().toString();
                                    final String newUnit = unit.getText().toString();

                                    if (newName.trim().toLowerCase().matches("") || newUnit.trim().toLowerCase().matches("")) {
                                        Toast.makeText(AddActivity.this, "Fill out all fields", Toast.LENGTH_SHORT).show();
                                    } else {
                                        addNew(newName, newUnit);
                                        if (spinnerAdapter != null)
                                            spinnerAdapter.notifyDataSetChanged();
                                        ingredientsAdapter.notifyDataSetChanged();
                                        builder.dismiss();
                                    }
                                }
                            });
                        } else if (id != 1) {

                            Spinner objSpinner = (Spinner) row.findViewById(R.id.choose_ingredient);
                            unitType.setText(getUnitType((int) id));
                            save_unit_type.put(position, getUnitType((int) id));
                            ingredients[position] = objSpinner;
                            quantity[position] = (EditText) row.findViewById(R.id.enter_quantity);


                        }

                        Log.v(DEBUG_TAG, "selected spinner"+String.valueOf(spinner.getSelectedItemId()));
                        save_spinner_position.put(position, (int) spinner.getSelectedItemId());


                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }


                });

                //Set onClickListener for quatity edittext
                qty.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        save_quantity.put(position, s.toString().trim());
                    }
                });

                //Set saved stuff


               // spinner.setSelection(current.getSpinnerPosition());
                spinner.setSelection(save_spinner_position.get(position) - 1);
                qty.setText(save_quantity.get(position));
                unitType.setText(save_unit_type.get(position));




            }



            //Is a step list
            if (isStepList) {
                TextView ingredient_position = (TextView) row.findViewById(R.id.position);
                EditText step = (EditText) row.findViewById(R.id.step_description);
                //Numbered bullet points
                ingredient_position.setText(String.valueOf(position + 1) + ".");
                //Add Edittext ? maybe should remove xD
                steps[position] = step;
                //Set Character limit
                step.setFilters(new InputFilter[]{new InputFilter.LengthFilter(250 / values.size())});

                step.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        savedStepText.put(position, editable.toString().trim());
                    }
                });

                step.setText(savedStepText.get(position));
            }

            return row;
        }




    }




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


//TODO maybe
/*// Adapter for Ingredients
class customAdapter extends CursorAdapter{

    public customAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.activity_add_listitem, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        Spinner spinner = (Spinner) view.findViewById(R.id.choose_ingredient);
        ArrayList<String> spinnerValues = cursorToArrayListString(ingredientsCursor);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, spinnerValues);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

            *//*Set onClickListener for spinner*//*
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString() + ", id: " + String.valueOf(id), Toast.LENGTH_SHORT).show();

                //New Ingredient
                if(id == 1){
                    View dialogLayout = LayoutInflater.from(context).inflate(R.layout.activity_add_new_ingredient_popover, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                    builder.setTitle("New Ingredient");
                    builder.setView(dialogLayout);
                    builder.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    private ArrayList<String> cursorToArrayListString(Cursor c){
        ArrayList<String> a = new ArrayList<>();
        if(c != null && c.getCount() > 0){
            do {
                String id  = c.getString(c.getColumnIndexOrThrow(DbContract.IngredientsEntry.ID));
                String name  = c.getString(c.getColumnIndexOrThrow(DbContract.IngredientsEntry.COLUMN_NAME));
                String unit  = c.getString(c.getColumnIndexOrThrow(DbContract.IngredientsEntry.COLUMN_UNIT));

                String row = id + ":" + name + ":" +unit;
                a.add(row);
            } while (c.moveToNext());
        }
        return a;
    }
}*/



