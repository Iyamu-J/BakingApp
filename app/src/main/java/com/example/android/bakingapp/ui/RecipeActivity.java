package com.example.android.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.model.Recipes;
import com.example.android.bakingapp.model.Steps;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity implements RecipeDetailsFragment.OnStepClickListener {

    private boolean mTwoPane;
    private int selectedStepPosition;

    private Recipes recipes;

    @BindView(R.id.btn_prev)
    FloatingActionButton prevButton;
    @BindView(R.id.btn_next)
    FloatingActionButton nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        View stepsContainer = findViewById(R.id.steps_container);
        mTwoPane = stepsContainer != null && stepsContainer.getVisibility() == View.VISIBLE;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recipes = extras.getParcelable(getString(R.string.extra_recipe));
            if (recipes != null) {
                setTitle(recipes.getName());
                if (mTwoPane) {
                    ButterKnife.bind(this);

                    int stepsSize = recipes.getSteps().size();

                    Steps clickedStep = recipes.getSteps().get(selectedStepPosition);
                    StepsFragment fragment = StepsFragment.newInstance(
                            clickedStep.getShortDescription(),
                            clickedStep.getDescription(),
                            clickedStep.getVideoURL());
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.fragment_steps_container, fragment)
                            .commit();
                    makeButtonVisible();

                    prevButton.setOnClickListener(v -> {
                        if (selectedStepPosition > 0) {
                            selectedStepPosition--;
                            Steps prevClickedStep = recipes.getSteps().get(selectedStepPosition);
                            StepsFragment prevFragment = StepsFragment.newInstance(
                                    prevClickedStep.getShortDescription(),
                                    prevClickedStep.getDescription(),
                                    prevClickedStep.getVideoURL());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_steps_container, prevFragment)
                                    .commit();
                        }
                        makeButtonVisible();
                    });

                    nextButton.setOnClickListener(v -> {
                        if (selectedStepPosition < stepsSize - 1) {
                            selectedStepPosition++;
                            Steps nextClickedStep = recipes.getSteps().get(selectedStepPosition);
                            StepsFragment nextFragment = StepsFragment.newInstance(
                                    nextClickedStep.getShortDescription(),
                                    nextClickedStep.getDescription(),
                                    nextClickedStep.getVideoURL());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_steps_container, nextFragment)
                                    .commit();
                        }
                        makeButtonVisible();
                    });

                } else {
                    RecipeDetailsFragment fragment = RecipeDetailsFragment.newInstance(recipes);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.recipe_details_container, fragment)
                            .commit();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStepSelected(int position) {
        selectedStepPosition = position;
        if (mTwoPane) {
            Steps clickedStep = recipes.getSteps().get(selectedStepPosition);
            StepsFragment fragment = StepsFragment.newInstance(
                    clickedStep.getShortDescription(),
                    clickedStep.getDescription(),
                    clickedStep.getVideoURL());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_steps_container, fragment)
                    .commit();
            makeButtonVisible();
        } else {
            Intent intent = new Intent(this, StepsActivity.class);
            intent.putExtra(getString(R.string.extra_position), position);
            intent.putExtra(getString(R.string.extra_recipe), recipes);
            startActivity(intent);
        }
    }

    private void makeButtonVisible() {
        if (selectedStepPosition == 0) {
            prevButton.setVisibility(View.INVISIBLE);
        } else {
            prevButton.setVisibility(View.VISIBLE);
        }
        if (selectedStepPosition == recipes.getSteps().size() - 1) {
            nextButton.setVisibility(View.INVISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
        }
    }
}
