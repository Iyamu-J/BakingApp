package com.example.android.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.database.IngredientsWidgetDatabase;
import com.example.android.bakingapp.database.IngredientsWidgetEntity;
import com.example.android.bakingapp.model.Ingredients;
import com.example.android.bakingapp.utils.AppExecutors;

import java.util.List;

import static com.example.android.bakingapp.ui.MainActivity.KEY_ENTITY_ID;

public class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }

    class ListRemoteViewsFactory implements RemoteViewsFactory {


        private Context mContext;
        private int entityId;
        private List<IngredientsWidgetEntity> ingredientsWidgetEntityList;

        ListRemoteViewsFactory(Context context) {
            mContext = context;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            // request for the Id of the last clicked Recipe
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            entityId = sharedPreferences.getInt(KEY_ENTITY_ID, 0);

            // Make call to Database to load recent IngredientsWidgetEntity
            AppExecutors.getInstance().getDiskIO().execute(() ->
                    ingredientsWidgetEntityList = IngredientsWidgetDatabase
                            .getInstance(mContext)
                            .ingredientsWidgetDao()
                            .loadAllIngredients());
        }

        @Override
        public void onDestroy() {
            if (ingredientsWidgetEntityList != null) {
                ingredientsWidgetEntityList.clear();
            }
        }

        @Override
        public int getCount() {
            if (ingredientsWidgetEntityList == null) return 0;
            else return ingredientsWidgetEntityList.get(entityId).getIngredientsList().size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

            // save the IngredientsList to an IngredientsWidgetEntity
            IngredientsWidgetEntity ingredientsWidgetEntity = ingredientsWidgetEntityList.get(entityId);
            // use individual Ingredients to populate text view
            Ingredients ingredients = ingredientsWidgetEntity.getIngredientsList().get(position);
            rv.setTextViewText(R.id.widget_item, formatIngredientWidgetText(ingredients));

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        /**
         * Renames the Measure of the Ingredient based on its abbreviation
         *
         * @param measure the required measure
         * @return the renamed measure
         */
        private String renameMeasure(String measure) {
            switch (measure) {
                case "TBLSP":
                    return "Tablespoon";
                case "CUP":
                    return "Cup";
                case "TSP":
                    return "Teaspoon";
                case "K":
                    return "Kilo";
                case "G":
                    return "Grams";
                case "OZ":
                    return "Ounce";
                case "UNIT":
                    return "Unit";
                default:
                    return measure;
            }
        }

        /**
         * Uses the Quantity, Measure and Ingredient to create a single
         * formatted Ingredients String
         *
         * @param ingredients required {@link Ingredients} Object
         * @return the formatted Ingredients String
         */
        private String formatIngredientWidgetText(Ingredients ingredients) {
            String quantity = ingredients.getQuantity();
            String measure = ingredients.getMeasure();
            String ingredient = ingredients.getIngredient();

            return String.format("%s %s(s) of %s", quantity, renameMeasure(measure), ingredient);
        }
    }
}

