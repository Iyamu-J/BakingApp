package com.example.android.bakingapp.di;

import com.example.android.bakingapp.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {RestModule.class})
@Singleton
public interface AppComponent {

    void inject(MainActivity mainActivity);
}
