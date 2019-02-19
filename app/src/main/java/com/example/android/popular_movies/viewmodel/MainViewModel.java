package com.example.android.popular_movies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.android.popular_movies.database.AppDatabase;
import com.example.android.popular_movies.model.Movie;
import com.example.android.popular_movies.repository.MovieRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    public final static int FILTER_BY_UNDEFINED = 0;
    public final static int FILTER_BY_MOST_POPULAR = 1;
    public final static int FILTER_BY_HIGHEST_RATED = 2;
    public final static int FILTER_BY_FAVORITE = 3;
    private LiveData<List<Movie>> mFavoriteMovies;
    private List<Movie> mMovies;
    private LiveData<List<Movie>> mMovieList;
    private final MovieRepository mMovieRepository;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        mFavoriteMovies = database.movieDao().loadAllFavoriteMovies();
        mMovieRepository = MovieRepository.getInstance();
        mMovieList = mMovieRepository.getMovieList(FILTER_BY_MOST_POPULAR);
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return mFavoriteMovies;
    }

    public LiveData<List<Movie>> getMovieList() {
        return mMovieList;
    }

    public void onMoviesRefreshNeeded(int sortOptions) {
        mMovieList = mMovieRepository.getMovieList(sortOptions);
    }

    /*public List<Movie> getMovies() {
        return mMovies;
    }

    public void setMovies(List<Movie> movies) {
        mMovies = movies;
    }*/
}
