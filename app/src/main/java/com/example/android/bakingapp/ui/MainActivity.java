package com.example.android.bakingapp.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapter.RecipeRecyclerAdapter;
import com.example.android.bakingapp.database.IngredientsWidgetDatabase;
import com.example.android.bakingapp.database.IngredientsWidgetEntity;
import com.example.android.bakingapp.di.MyApplication;
import com.example.android.bakingapp.model.Recipes;
import com.example.android.bakingapp.utils.AppExecutors;
import com.example.android.bakingapp.utils.RecipeResponse;
import com.example.android.bakingapp.viewmodel.RecipeViewModel;
import com.example.android.bakingapp.viewmodel.RecipeViewModelFactory;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipeRecyclerAdapter.ListItemClickListener {

    public static final String KEY_ENTITY_ID = "ENTITY_ID";

    @Inject
    RecipeViewModelFactory factory;

    @BindView(R.id.recipe_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private RecipeRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isConnected()) {
            init();
        } else {
            setContentView(R.layout.activity_main_no_internet);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            if (isConnected()) {
                init();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialises the view of the activity and calls the required methods
     */
    private void init() {
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Timber.plant(new Timber.DebugTree());

        ((MyApplication) getApplication()).getComponent().inject(this);

        populateUI();

        setupViewModels();
    }

    /**
     * Populates the UI by setting the RecyclerView, its layout and adapter
     */
    private void populateUI() {
        mAdapter = new RecipeRecyclerAdapter(this, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Instantiates the {@link RecipeViewModel} object.
     * This method also starts the observe method of the {@link RecipeViewModel} object
     */
    private void setupViewModels() {
        RecipeViewModel viewModel = ViewModelProviders.of(this, factory).get(RecipeViewModel.class);
        viewModel.loadRecipeResponse();
        viewModel.getResponseLiveData().observe(this,
                recipeResponse -> {
                    if (recipeResponse != null) {
                        loadRecipe(recipeResponse);
                    }
                });
    }

    /**
     * This method uses the output of the RecipeResponse Status to carry out further operations
     * if the output is successful, The RecipeJsonList is used to populate the RecyclerAdapter.
     * Other outputs are handled properly
     *
     * @param recipeResponse the required {@link RecipeResponse} object
     */
    private void loadRecipe(RecipeResponse recipeResponse) {
        switch (recipeResponse.status) {
            case LOADING:
                loadProgressBar(true);
                break;
            case SUCCESS:
                loadProgressBar(false);
                if (recipeResponse.recipeJsonList != null) {
                    mAdapter.setRecipesList(recipeResponse.recipeJsonList);
                    addIngredientsForWidget(recipeResponse.recipeJsonList);
                } else {
                    Timber.d("RecipeResponse is null");
                }
                break;
            case ERROR:
                Timber.d(recipeResponse.error);
                loadProgressBar(false);
                Toast.makeText(this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onListItemClick(Recipes recipes) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        sharedPreferences.edit()
                .putInt(KEY_ENTITY_ID, recipes.getId())
                .apply();

        Intent intent = new Intent(this, RecipeActivity.class);
        Bundle extras = new Bundle();
        extras.putParcelable(getString(R.string.extra_recipe), recipes);
        intent.putExtras(extras);
        startActivity(intent);
    }

    private void addIngredientsForWidget(List<Recipes> recipesList) {

        AppExecutors.getInstance().getDiskIO().execute(() -> {

            for (Recipes recipes : recipesList) {
                IngredientsWidgetEntity ingredientsWidgetEntity = new IngredientsWidgetEntity(recipes.getIngredients());
                IngredientsWidgetDatabase
                        .getInstance(this)
                        .ingredientsWidgetDao()
                        .addIngredients(ingredientsWidgetEntity);
                Timber.d("Added to Database");
            }
        });
    }

    /**
     * Shows the progressBar if required
     *
     * @param load tells whether or not to load the ProgressBar
     */
    private void loadProgressBar(boolean load) {
        if (load) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Checks if there is an active internet connection
     *
     * @return true if there is and false if there isn't
     */
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
