package com.lynkor.hangry;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lynkor.hangry.sqliteDB.DbContract;



public class RecipeDescFragment extends Fragment {
    private LinearLayout details;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_desc, container, false);
        details = (LinearLayout) view.findViewById(R.id.fragment_recipedesc_details);

        return view;
    }

    public void getData(Bundle args){
        // Bundle has all arguments. need to parse through and add child views
        Log.v("Debuggin", args.getString(DbContract.RecipesEntry.COLUMN_INGREDIENTS));


        ImageView recipeImage = (ImageView) getActivity().findViewById(R.id.fragment_recipedesc_image);
//        Uri image = Uri.fromFile(new File(args.getString(DbContract.RecipesEntry.COLUMN_IMAGE)));
//        recipeImage.setImageURI(image);

        recipeImage.setImageDrawable(getResources().getDrawable(R.drawable.default_recipe_img));

        TextView name = (TextView) getActivity().findViewById(R.id.fragment_recipedesc_name);
        name.setText(args.getString(DbContract.RecipesEntry.COLUMN_NAME));

        showIngredients(args.getString(DbContract.RecipesEntry.COLUMN_INGREDIENTS), args.getString(DbContract.RecipesEntry.COLUMN_STEPS));
    }

    private void showIngredients(String rawIngredients, String rawSteps){
        Log.v("inside show: ", rawIngredients);
        String[] ingredients = rawIngredients.substring(0,rawIngredients.length()-1).split("&");
        String[] ingredient;
        details.removeViews(2,details.getChildCount()-2);
        for(int i = 0; i < ingredients.length; i++){
            View child = getActivity().getLayoutInflater().inflate(R.layout.fragment_recipe_descingredient, details, false);
            TextView name = (TextView) child.findViewById(R.id.fragment_recipedesc_ingredientname);
            TextView qty = (TextView) child.findViewById(R.id.fragment_recipedesc_ingredientqty);
            TextView unit = (TextView) child.findViewById(R.id.fragment_recipedesc_ingredientunit);

            ingredient = ingredients[i].split(":");

            name.setText(ingredient[0]);
            qty.setText(ingredient[1]);
            unit.setText(ingredient[2]);

            details.addView(child);

        }

        String[] steps = rawSteps.substring(0,rawSteps.length()-1).split(":");
        for(int i = 0; i < steps.length; i++){
            View child = getActivity().getLayoutInflater().inflate(R.layout.fragment_recipe_descstep, details, false);
            TextView description = (TextView) child.findViewById(R.id.fragment_recipedesc_step);
            TextView position = (TextView) child.findViewById(R.id.fragment_recipedesc_position);
            position.setText(String.valueOf(i + 1) + ".");
            description.setText(steps[i]);

            details.addView(child);

        }


    }

}
