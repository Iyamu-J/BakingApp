package com.example.android.bakingapp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.model.Recipes;

import butterknife.ButterKnife;

public class StepsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        ButterKnife.bind(this);

        if (getIntent() != null) {
            Recipes recipes = getIntent().getParcelableExtra(getString(R.string.extra_recipe));
            int stepId = getIntent().getIntExtra(getString(R.string.extra_position), 0);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit()
                    .putInt(getString(R.string.key_step_id), stepId)
                    .apply();

            if (recipes != null) {

                setTitle(recipes.getName());

                StepsFragment fragment = StepsFragment.newInstance(recipes);

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.activity_steps_container, fragment)
                        .commit();
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
}
