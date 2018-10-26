package com.example.android.bakingapp.rest;

import com.example.android.bakingapp.model.Recipes;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface WebService {

    @GET("baking.json")
    Observable<List<Recipes>> getRecipeJson();
}
