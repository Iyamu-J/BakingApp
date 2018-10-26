package com.example.android.bakingapp.di;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    AppComponent component;
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        component = DaggerAppComponent.builder()
                .restModule(new RestModule())
                .build();
    }

    public AppComponent getComponent() {
        return component;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }
}
