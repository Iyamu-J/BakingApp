package com.example.android.bakingapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.model.Recipes;
import com.example.android.bakingapp.model.Steps;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsActivity extends AppCompatActivity {

    Recipes recipes;
    int position;
    int stepsSize;

    @BindView(R.id.btn_prev)
    FloatingActionButton prevButton;
    @BindView(R.id.btn_next)
    FloatingActionButton nextButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        ButterKnife.bind(this);

        if (getIntent() != null) {
            recipes = getIntent().getParcelableExtra(getString(R.string.extra_recipe));
            position = getIntent().getIntExtra(getString(R.string.extra_position), 0);

            if (recipes != null) {
                stepsSize = recipes.getSteps().size();

                setTitle(recipes.getName());

                Steps clickedStep = recipes.getSteps().get(position);

                StepsFragment fragment = StepsFragment.newInstance(
                        clickedStep.getShortDescription(),
                        clickedStep.getDescription(),
                        clickedStep.getVideoURL());

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.activity_steps_container, fragment)
                        .commit();
                makeButtonVisible();
            }
        }

        // handle clicks on the previous button
        prevButton.setOnClickListener(v -> {
            if (position > 0) {
                position--;
                Steps clickedStep = recipes.getSteps().get(position);
                StepsFragment fragment = StepsFragment.newInstance(
                        clickedStep.getShortDescription(),
                        clickedStep.getDescription(),
                        clickedStep.getVideoURL());
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_steps_container, fragment)
                        .commit();
            }
            makeButtonVisible();
        });

        // handle clicks on the next button
        nextButton.setOnClickListener(v -> {
            if (position < stepsSize - 1) {
                position++;
                Steps clickedStep = recipes.getSteps().get(position);
                StepsFragment fragment = StepsFragment.newInstance(
                        clickedStep.getShortDescription(),
                        clickedStep.getDescription(),
                        clickedStep.getVideoURL());
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_steps_container, fragment)
                        .commit();
            }
            makeButtonVisible();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows the either of the Buttons depending on the position
     */
    private void makeButtonVisible() {
        if (position == 0) {
            prevButton.setVisibility(View.INVISIBLE);
        } else {
            prevButton.setVisibility(View.VISIBLE);
        }
        if (position == recipes.getSteps().size() - 1) {
            nextButton.setVisibility(View.INVISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
        }
    }
}
