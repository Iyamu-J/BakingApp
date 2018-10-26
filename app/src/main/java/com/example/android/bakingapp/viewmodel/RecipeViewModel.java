package com.example.android.bakingapp.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.bakingapp.rest.RecipeRepository;
import com.example.android.bakingapp.utils.RecipeResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RecipeViewModel extends ViewModel {

    private RecipeRepository mRecipeRepository;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<RecipeResponse> responseLiveData;

    RecipeViewModel(RecipeRepository recipeRepository) {
        this.mRecipeRepository = recipeRepository;
        compositeDisposable = new CompositeDisposable();
        responseLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<RecipeResponse> getResponseLiveData() {
        return responseLiveData;
    }

    public void loadRecipeResponse() {
        compositeDisposable.add(mRecipeRepository.getRecipeResponse()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    responseLiveData.setValue(RecipeResponse.loading());
                    Timber.d("Loading: %s", disposable);
                })
                .subscribe(
                        result -> {
                            responseLiveData.setValue(RecipeResponse.success(result));
                            Timber.d("Result: %s", result);
                        },
                        error -> {
                            responseLiveData.setValue(RecipeResponse.error(error));
                            Timber.d(error, "Error: ");
                        }
                ));
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }
}
