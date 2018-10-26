package com.example.android.bakingapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface IngredientsWidgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addIngredients(IngredientsWidgetEntity ingredientsWidgetEntityList);

    @Query("SELECT * FROM ingredientswidgetentity")
    List<IngredientsWidgetEntity> loadAllIngredients();
}
