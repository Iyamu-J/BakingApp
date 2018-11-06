package com.example.android.bakingapp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.model.Recipes;

import butterknife.ButterKnife;
import timber.log.Timber;

public class StepsActivity extends AppCompatActivity {

    private int stepId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        ButterKnife.bind(this);

        if (getIntent() != null) {
            Recipes recipes = getIntent().getParcelableExtra(getString(R.string.extra_recipe));
            if (savedInstanceState == null) {
                stepId = getIntent().getIntExtra(getString(R.string.extra_position), 0);
            } else {
                stepId = savedInstanceState.getInt(getString(R.string.extra_step_id));
            }

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit()
                    .putInt(getString(R.string.key_step_id), stepId)
                    .apply();

            if (recipes != null) {

                setTitle(recipes.getName());

                FragmentManager fm = getSupportFragmentManager();
                StepsFragment stepsFragment = (StepsFragment) fm.findFragmentByTag(getString(R.string.tag_steps_fragment));
                if (stepsFragment == null) {
                    stepsFragment = StepsFragment.newInstance(recipes);
                    fm.beginTransaction()
                            .add(R.id.activity_steps_container, stepsFragment, getString(R.string.tag_steps_fragment))
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        stepId = sharedPreferences.getInt(getString(R.string.key_step_id), 0);
        outState.putInt(getString(R.string.extra_step_id), stepId);
    }
}
