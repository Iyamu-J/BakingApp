package com.example.android.bakingapp.di;

import android.arch.lifecycle.ViewModelProvider;

import com.example.android.bakingapp.BuildConfig;
import com.example.android.bakingapp.rest.RecipeRepository;
import com.example.android.bakingapp.rest.WebService;
import com.example.android.bakingapp.viewmodel.RecipeViewModelFactory;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RestModule {

    @Provides
    @Singleton
    WebService provideWebService(Retrofit retrofit) {
        return retrofit.create(WebService.class);
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    OkHttpClient provideClient(HttpLoggingInterceptor interceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
    }

    @Provides
    @Singleton
    HttpLoggingInterceptor provideInterceptor() {
        return new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Provides
    @Singleton
    RecipeRepository provideRecipeRepository(WebService webService) {
        return new RecipeRepository(webService);
    }

    @Provides
    @Singleton
    ViewModelProvider.Factory provideRecipeViewModelFactory(RecipeRepository recipeRepository) {
        return new RecipeViewModelFactory(recipeRepository);
    }
}
