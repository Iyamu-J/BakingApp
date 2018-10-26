package com.example.android.bakingapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.model.Ingredients;

import java.util.ArrayList;
import java.util.List;

public class IngredientsRecyclerViewAdapter extends RecyclerView.Adapter<IngredientsRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Ingredients> ingredientsList;

    public IngredientsRecyclerViewAdapter(Context context) {
        this.mContext = context;
        ingredientsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ingredient_list_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredients ingredients = ingredientsList.get(position);
        holder.bind(ingredients);
    }

    @Override
    public int getItemCount() {
        if (ingredientsList == null) return 0;
        else return ingredientsList.size();
    }

    public void setIngredientsList(List<Ingredients> ingredientsList) {
        this.ingredientsList = ingredientsList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_ingredient_item);
        }

        void bind(Ingredients ingredients) {
            String quantity = ingredients.getQuantity();
            String measure = ingredients.getMeasure();
            String ingredient = ingredients.getIngredient();

            @SuppressLint("DefaultLocale")
            String formatted = String.format("%s %s(s) of %s", quantity, renameMeasure(measure), ingredient);
            textView.setText(formatted);
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
    }
}
