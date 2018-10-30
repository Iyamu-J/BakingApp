package com.example.android.bakingapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapter.IngredientsRecyclerViewAdapter;
import com.example.android.bakingapp.adapter.StepsRecyclerViewAdapter;
import com.example.android.bakingapp.model.Ingredients;
import com.example.android.bakingapp.model.Recipes;
import com.example.android.bakingapp.model.Steps;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailsFragment extends Fragment {

    private static final String EXTRA_RECIPE = "EXTRA_RECIPE";

    @BindView(R.id.recipe_image)
    ImageView recipeImageView;
    @BindView(R.id.ingredients_recycler_view)
    RecyclerView ingredientsRecyclerView;
    @BindView(R.id.steps_recycler_view)
    RecyclerView stepsRecyclerView;

    OnStepClickListener mCallback;

    public RecipeDetailsFragment() {
        // Required empty public constructor
    }

    public interface OnStepClickListener {
        void onStepSelected(int position);
    }

    public static RecipeDetailsFragment newInstance(Recipes recipes) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_RECIPE, recipes);
        fragment.setArguments(args);
        return fragment;
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStepClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);

        ButterKnife.bind(this, view);

        if (getArguments() != null) {

            Recipes recipes = getArguments().getParcelable(EXTRA_RECIPE);

            if (recipes != null) {
                if (!recipes.getImage().isEmpty()) {
                    recipeImageView.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.divider).setVisibility(View.VISIBLE);

                    Picasso.with(getContext())
                            .load(recipes.getImage())
                            .placeholder(R.drawable.ic_placeholder_image)
                            .error(R.drawable.ic_placeholder_image)
                            .into(recipeImageView);
                } else {
                    recipeImageView.setVisibility(View.GONE);
                    view.findViewById(R.id.divider).setVisibility(View.GONE);
                }

                setupIngredientsList(recipes.getIngredients());
                setupStepsList(recipes.getSteps());
            }
        }

        return view;
    }

    /**
     * This method setups the RecyclerView intended for the List of Ingredients
     *
     * @param ingredientsList required IngredientsList
     */
    private void setupIngredientsList(List<Ingredients> ingredientsList) {
        IngredientsRecyclerViewAdapter mIngredientsAdapter = new IngredientsRecyclerViewAdapter(getActivity());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mIngredientsAdapter.setIngredientsList(ingredientsList);
        ingredientsRecyclerView.setAdapter(mIngredientsAdapter);
        ingredientsRecyclerView.setLayoutManager(mLayoutManager);
        ingredientsRecyclerView.setHasFixedSize(true);
    }

    /**
     * This method setups the RecyclerView intended for the List of Steps
     *
     * @param stepsList required StepsList
     */
    private void setupStepsList(List<Steps> stepsList) {
        StepsRecyclerViewAdapter mStepsAdapter = new StepsRecyclerViewAdapter(getActivity(),
                position -> mCallback.onStepSelected(position));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mStepsAdapter.setStepsList(stepsList);
        stepsRecyclerView.setAdapter(mStepsAdapter);
        stepsRecyclerView.setLayoutManager(mLayoutManager);
        stepsRecyclerView.setHasFixedSize(true);
    }
}
