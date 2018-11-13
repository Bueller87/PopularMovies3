package com.example.android.popular_movies.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.android.popular_movies.model.Movie;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> mFavoriteMovies;
    private List<Movie> mMovies;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        mFavoriteMovies = database.movieDao().loadAllFavoriteMovies();
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return mFavoriteMovies;
    }


    public List<Movie> getMovies() {
        return mMovies;
    }

    public void setMovies(List<Movie> movies) {
        mMovies = movies;
    }
}
