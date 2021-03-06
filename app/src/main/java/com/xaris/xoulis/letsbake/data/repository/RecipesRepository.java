package com.xaris.xoulis.letsbake.data.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xaris.xoulis.letsbake.R;
import com.xaris.xoulis.letsbake.data.api.ApiResponse;
import com.xaris.xoulis.letsbake.data.api.UdactiyService;
import com.xaris.xoulis.letsbake.data.db.RecipeDao;
import com.xaris.xoulis.letsbake.data.db.RecipesDatabase;
import com.xaris.xoulis.letsbake.data.model.Recipe;
import com.xaris.xoulis.letsbake.data.model.Step;
import com.xaris.xoulis.letsbake.utils.AppExecutors;

import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RecipesRepository {

    private AppExecutors appExecutors;
    private UdactiyService udactiyService;
    private RecipesDatabase recipesDatabase;
    private RecipeDao recipeDao;

    @Inject
    public RecipesRepository(AppExecutors appExecutors, UdactiyService udactiyService,
                             RecipesDatabase recipesDatabase, RecipeDao recipeDao) {
        this.appExecutors = appExecutors;
        this.udactiyService = udactiyService;
        this.recipesDatabase = recipesDatabase;
        this.recipeDao = recipeDao;
    }

    public LiveData<List<Recipe>> getRecipeList() {
        return new NetworkBoundResource<List<Recipe>, List<Recipe>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<Recipe> recipeList) {
                recipeDao.insertRecipes(handleEmptyImageUrl(recipeList));
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Recipe> data) {
                return data == null || data.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<List<Recipe>> loadFromDb() {
                return recipeDao.getRecipes();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Recipe>>> createCall() {
                return udactiyService.getRecipeList();
            }
        }.asLiveData();
    }

    public LiveData<Recipe> getRecipe(int recipeId) {
        return recipeDao.getRecipeById(recipeId);
    }

    public LiveData<List<Step>> getSteps(int recipeId) {
        return null;
    }

    private List<Recipe> handleEmptyImageUrl(List<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            if (TextUtils.isEmpty(recipe.getImage())) {
                recipe.setImageSrcId(chooseRandomPlaceholder());
            }
        }
        return recipes;
    }

    private int chooseRandomPlaceholder() {
        int randomNumber = new Random().nextInt(4) + 1;
        int imageResourceId;
        switch (randomNumber) {
            case 1:
                imageResourceId = R.drawable.recipe_default_1;
                break;
            case 2:
                imageResourceId = R.drawable.recipe_default_2;
                break;
            case 3:
                imageResourceId = R.drawable.recipe_default_3;
                break;
            default:
                imageResourceId = R.drawable.recipe_default_4;
                break;
        }
        return imageResourceId;
    }
}
