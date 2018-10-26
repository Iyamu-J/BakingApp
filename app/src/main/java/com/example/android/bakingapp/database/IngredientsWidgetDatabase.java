package com.example.android.bakingapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = IngredientsWidgetEntity.class, exportSchema = false, version = 1)
@TypeConverters(IngredientsWidgetTypeConverters.class)
public abstract class IngredientsWidgetDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "ingredients";
    private static IngredientsWidgetDatabase sInstance;

    public static IngredientsWidgetDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(
                        context,
                        IngredientsWidgetDatabase.class,
                        DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract IngredientsWidgetDao ingredientsWidgetDao();
}
