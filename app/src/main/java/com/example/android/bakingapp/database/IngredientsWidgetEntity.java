package com.example.android.bakingapp.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.example.android.bakingapp.model.Ingredients;

import java.util.List;

@Entity
public class IngredientsWidgetEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private List<Ingredients> ingredientsList;

    IngredientsWidgetEntity(int id, List<Ingredients> ingredientsList) {
        this.id = id;
        this.ingredientsList = ingredientsList;
    }

    @Ignore
    public IngredientsWidgetEntity(List<Ingredients> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Ingredients> getIngredientsList() {
        return ingredientsList;
    }

    public void setIngredientsList(List<Ingredients> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }
}
