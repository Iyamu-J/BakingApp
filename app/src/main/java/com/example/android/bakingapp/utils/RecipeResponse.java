package com.example.android.bakingapp.utils;

import com.example.android.bakingapp.model.Recipes;

import java.util.List;

import static com.example.android.bakingapp.utils.Status.ERROR;
import static com.example.android.bakingapp.utils.Status.LOADING;
import static com.example.android.bakingapp.utils.Status.SUCCESS;

public class RecipeResponse {

    public Status status;
    public List<Recipes> recipeJsonList;
    public Throwable error;

    private RecipeResponse(Status status, List<Recipes> recipeJsonList, Throwable error) {
        this.status = status;
        this.recipeJsonList = recipeJsonList;
        this.error = error;
    }

    public static RecipeResponse loading() {
        return new RecipeResponse(LOADING, null, null);
    }

    public static RecipeResponse success(List<Recipes> recipeJson) {
        return new RecipeResponse(SUCCESS, recipeJson, null);
    }

    public static RecipeResponse error(Throwable error) {
        return new RecipeResponse(ERROR, null, error);
    }
}
