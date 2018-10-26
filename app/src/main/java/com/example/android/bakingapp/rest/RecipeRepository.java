package com.example.android.bakingapp.rest;

import com.example.android.bakingapp.model.Recipes;

import java.util.List;

import io.reactivex.Observable;

public class RecipeRepository {

    private WebService webService;

    public RecipeRepository(WebService webService) {
        this.webService = webService;
    }

    public Observable<List<Recipes>> getRecipeResponse() {
        return webService.getRecipeJson();
    }

}
