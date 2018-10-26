package com.example.android.bakingapp.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.bakingapp.rest.RecipeRepository;

import javax.inject.Inject;

public class RecipeViewModelFactory implements ViewModelProvider.Factory {

    private RecipeRepository recipeRepository;

    @Inject
    public RecipeViewModelFactory(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new RecipeViewModel(recipeRepository);

    }
}
