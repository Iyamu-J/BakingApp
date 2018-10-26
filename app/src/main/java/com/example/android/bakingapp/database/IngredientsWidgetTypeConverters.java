package com.example.android.bakingapp.database;

import android.arch.persistence.room.TypeConverter;

import com.example.android.bakingapp.model.Ingredients;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class IngredientsWidgetTypeConverters {

    @TypeConverter
    public static List<Ingredients> stringToIngredientsList(String data) {
        if (data == null) return Collections.emptyList();
        Type listType = new TypeToken<List<Ingredients>>(){}.getType();
        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String IngredientsListToString(List<Ingredients> ingredientsList) {
        return new Gson().toJson(ingredientsList);
    }
}
