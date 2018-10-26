package com.example.android.bakingapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.model.Recipes;

import java.util.ArrayList;
import java.util.List;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecipeRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private ListItemClickListener mListener;
    private List<Recipes> recipesList;

    public RecipeRecyclerAdapter(Context context, ListItemClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
        recipesList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recipe_list_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipes recipes = recipesList.get(position);
        String recipeName = recipes.getName();
        holder.textView.setText(recipeName);
    }

    @Override
    public int getItemCount() {
        if (recipesList == null) return 0;
        else return recipesList.size();
    }

    public void setRecipesList(List<Recipes> recipesList) {
        this.recipesList = recipesList;
        notifyDataSetChanged();
    }

    public interface ListItemClickListener {
        void onListItemClick(Recipes recipes);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;

        ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tv_recipe);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mListener.onListItemClick(recipesList.get(position));
        }
    }
}
