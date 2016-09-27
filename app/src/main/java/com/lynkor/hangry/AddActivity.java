package com.lynkor.hangry;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lynkor.hangry.sqliteDB.DbContract;
import com.lynkor.hangry.sqliteDB.DbHelper;

import java.util.ArrayList;
import java.util.HashMap;

import static com.lynkor.hangry.R.drawable.check;
import static com.lynkor.hangry.R.id.recipe_name;

public class AddActivity extends AppCompatActivity {
    private final String TAG = "AddActivity: ";
    private final String DEBUG_TAG = "YOOOOOOOOOO: ";

    private ListAdapter ingredientsAdapter;
    private ListAdapter stepsAdapter;
    private ArrayList<String> recipeIngredients;
    private ArrayList<String> stepsList;
    private SQLiteDatabase db;
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private Cursor spinnerCursor;
    private SimpleCursorAdapter spinnerAdapter;
    boolean added;
    boolean nameIsValid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        stepsList = new ArrayList<>();
        recipeIngredients = new ArrayList<>();
        loadCursor();
        setupLists();
        setupUI();
    }

    /*Load Cursor*/
    private void loadCursor(){
        //DbHelper is an SQLiteOpenHelper class connecting to the underlying Database
        DbHelper handler = DbHelper.getInstance(this);
        //Get access to the underlying writable database
        db = handler.getWritableDatabase();
        checkCursor();
    }


    /*Attach List adapters to views*/
    private void setupLists(){
        //Find ListView and set adapter for Ingredients
        ListView ingredientsListView = (ListView) findViewById(R.id.ingredients_list);
        //Add hint to spinner list
        ingredientsAdapter = new ListAdapter(this, recipeIngredients, R.layout.activity_add_listitem, "ingredients");
        ingredientsListView.setAdapter(ingredientsAdapter);

        //Find ListView and set adapter for Steps
        ListView stepsListView = (ListView) findViewById(R.id.steps_list);
        stepsAdapter = new ListAdapter(this, stepsList, R.layout.activity_add_stepitem, "steps");
        stepsListView.setAdapter(stepsAdapter);
    }


    /*Buttons Handlers*/
    private void setupUI(){
        //Check for Recipe Name existence
        EditText recipe_name = (EditText) this.findViewById(R.id.recipe_name);
        recipe_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                EditText recipe_name = (EditText) view.findViewById(R.id.recipe_name);
                //query and check if name exists

               if(!recipe_name.getText().toString().equals("")){
                   Cursor c = db.rawQuery("SELECT name FROM recipes WHERE name = '"+recipe_name.getText().toString()+"'", null);
                   if(c.getCount()>0 && c!=null) {
                       recipe_name.setError("Recipe already exists");
                       nameIsValid = false;
                   }
                   else
                       nameIsValid = true;
               }

            }
        });
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
        final ImageButton addRecipe = (ImageButton) findViewById(R.id.add_recipe);
        addRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                added = postRecipe();

                if(added)
                    finish();
            }
        });
    }


    @Override
    protected void onStop(){
        if(added)
            Toast.makeText(AddActivity.this, "Recipe added!", Toast.LENGTH_SHORT).show();
        super.onStop();
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
        EditText recipeName = (EditText) this.findViewById(recipe_name);
        String name = recipeName.getText().toString();
        Log.v(TAG, "RECIPE NAME: " + name);
        Log.v(TAG, "RECIPE INGREDIENTS: \n");
    }

    public boolean postRecipe(){
        boolean recipeAdded = false;

        String recipe_image = selectedImagePath;
        String recipe_name = ((EditText) this.findViewById(R.id.recipe_name)).getText().toString();

        //ArrayList<ContentValues> groceries = new ArrayList<>();
        ContentValues recipe = new ContentValues();

        //build ingredients qty unit string for recipe. *FORMAT name:qty:unit&..
        String recipe_ingredients = "";

        //build Recipe Steps. *FORMAT step:step:..
        String recipe_steps = "";

        for(int i=0; i<ingredientsAdapter._quantity.size(); i++ ){
            String name = ingredientsAdapter._spinner_value.get(i);
            String quantity = ingredientsAdapter._quantity.get(i);
            String unit = ingredientsAdapter._unit_type.get(i);
            String step = stepsAdapter.savedStepText.get(i);

            if(!(name.equals(DbContract.IngredientsEntry.spinner_hint) || name.equals(DbContract.IngredientsEntry.new_ingredient)) && quantity.length()>0){
                recipe_ingredients += name+":"+quantity+":"+unit+"&";
            }

            if(step != null)
                recipe_steps += step+":";

        }

        if( !(recipe_image == null) && !recipe_name.equals("") && nameIsValid && !recipe_ingredients.equals("") && !recipe_steps.equals("")){
            recipe.put(DbContract.RecipesEntry.COLUMN_NAME, recipe_name);
            recipe.put(DbContract.RecipesEntry.COLUMN_IMAGE, recipe_image);
            recipe.put(DbContract.RecipesEntry.COLUMN_INGREDIENTS, recipe_ingredients.substring(0,recipe_ingredients.length()-1));
            recipe.put(DbContract.RecipesEntry.COLUMN_STEPS, recipe_steps.substring(0,recipe_steps.length()-1));
            recipe.put(DbContract.RecipesEntry.COLUMN_QUANTITY, "0");

            //post to db
            db.insertOrThrow(DbContract.RecipesEntry.TABLE_RECIPES, null, recipe);
            //groceries
            /*for(ContentValues i : groceries){
                db.insertOrThrow(DbContract.GroceriesEntry.TABLE_GROCERIES, null, i);
            }*/
            recipeAdded = true;
            //recipe

        } else {
            Toast.makeText(this, "Incomplete Recipe", Toast.LENGTH_SHORT).show();
        }

        return recipeAdded;
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
        public HashMap<Integer, Integer> _spinner_position = new HashMap<>();
        public HashMap<Integer, String> _spinner_value = new HashMap<>();
        public HashMap<Integer, String>  _unit_type = new HashMap<>();
        public HashMap<Integer, String> _quantity = new HashMap<>();




        public ListAdapter(Context context, ArrayList<String> values, int layout, String type) {
            super(context, layout, values);
            this.context = context;
            this.values = values;
            this.layout = layout;


            if (type.equals("steps"))
                this.isStepList = true;

            // initialize myList
            for (int i = 0; i < 10; i++) {
                savedStepText.put(1, "");
                _spinner_position.put(i, 1);
                _spinner_value.put(i, "");
                _unit_type.put(i, "");
                _quantity.put(i, "");
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
                            _unit_type.put(position, getUnitType((int) id));



                        }

                        _spinner_position.put(position, (int) spinner.getSelectedItemId());
                        _spinner_value.put(position, ((Cursor)spinner.getSelectedItem()).getString(1));


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
                        _quantity.put(position, s.toString().trim());
                    }
                });

                //Set saved stuff


               // spinner.setSelection(current.getSpinnerPosition());
                spinner.setSelection(_spinner_position.get(position) - 1);
                qty.setText(_quantity.get(position));
                unitType.setText(_unit_type.get(position));




            }



            //Is a step list
            if (isStepList) {
                TextView ingredient_position = (TextView) row.findViewById(R.id.position);
                EditText step = (EditText) row.findViewById(R.id.step_description);
                //Numbered bullet points
                ingredient_position.setText(String.valueOf(position + 1) + ".");

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




