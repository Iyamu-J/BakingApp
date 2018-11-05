package com.example.android.bakingapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.model.Recipes;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity implements RecipeDetailsFragment.OnStepClickListener {

    private static final String TAG_STEPS_FRAGMENT = "TAG_STEPS_FRAGMENT";
    private boolean mTwoPane;
    private Recipes recipes;

    private StepsFragment stepsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        View stepsContainer = findViewById(R.id.fragment_steps_container);
        mTwoPane = stepsContainer != null && stepsContainer.getVisibility() == View.VISIBLE;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit()
                .putBoolean(getString(R.string.key_two_pane), mTwoPane)
                .apply();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recipes = extras.getParcelable(getString(R.string.extra_recipe));
            if (recipes != null) {
                setTitle(recipes.getName());
                if (mTwoPane) {
                    ButterKnife.bind(this);

                    FragmentManager fm = getSupportFragmentManager();

                    RecipeDetailsFragment recipeDetailsFragment = RecipeDetailsFragment.newInstance(recipes);
                    fm.beginTransaction()
                            .replace(R.id.recipe_details_container, recipeDetailsFragment)
                            .commit();

                    stepsFragment = (StepsFragment) fm.findFragmentByTag(TAG_STEPS_FRAGMENT);
                    if (stepsFragment == null) {
                        stepsFragment = StepsFragment.newInstance(recipes);
                        if (!stepsFragment.isInLayout()) {
                            fm.beginTransaction()
                                    .add(R.id.fragment_steps_container, stepsFragment, TAG_STEPS_FRAGMENT)
                                    .commit();
                        }
                    }

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
        if (mTwoPane) {
            StepsFragment fragment = StepsFragment.newInstance(recipes);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_steps_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, StepsActivity.class);
            intent.putExtra(getString(R.string.extra_position), position);
            intent.putExtra(getString(R.string.extra_recipe), recipes);
            startActivity(intent);
        }
    }
}
