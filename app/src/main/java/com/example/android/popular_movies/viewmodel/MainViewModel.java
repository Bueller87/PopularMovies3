package com.example.android.popular_movies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import com.example.android.popular_movies.database.AppDatabase;
import com.example.android.popular_movies.database.AppExecutors;
import com.example.android.popular_movies.model.DataWrapper;
import com.example.android.popular_movies.model.Movie;
import com.example.android.popular_movies.model.MovieReview;
import com.example.android.popular_movies.model.MovieTrailer;
import com.example.android.popular_movies.repository.MovieRepository;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    public final static int FILTER_BY_UNDEFINED = 0;
    public final static int FILTER_BY_MOST_POPULAR = 1;
    public final static int FILTER_BY_HIGHEST_RATED = 2;
    public final static int FILTER_BY_FAVORITE = 3;
    private LiveData<List<Movie>> mFavoriteMovieList;
    private LiveData<DataWrapper<List<Movie>>> mMovieList;
    private final MovieRepository mMovieRepository;
    private final AppDatabase mAppDatabase;

    public MainViewModel(Application application) {
        super(application);
        mAppDatabase = AppDatabase.getInstance(this.getApplication());
        mFavoriteMovieList = mAppDatabase.movieDao().loadAllFavoriteMovies();
        mMovieRepository = MovieRepository.getInstance();
        mMovieList = mMovieRepository.getMovieList(FILTER_BY_MOST_POPULAR);
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return mFavoriteMovieList;
    }

    public LiveData<DataWrapper<List<Movie>>> getMovieList() {
        return mMovieList;
    }

    public LiveData<DataWrapper<List<MovieTrailer>>> getMovieTrailerList(Integer movieId) {
        return mMovieRepository.getTrailerList(movieId);
    }

    public LiveData<DataWrapper<List<MovieReview>>> getMovieReviewList(Integer movieId) {
        return mMovieRepository.getReviewList(movieId);
    }

    public void onMoviesRefreshNeeded(int sortOptions) {
        mMovieList = mMovieRepository.getMovieList(sortOptions);
    }

    public void toggleFavorite(final boolean isFavorite, final Movie movie) {
        //update database on a separate thread
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (isFavorite) {
                    mAppDatabase.movieDao().deleteMovie(movie);
                }
                else {
                    mAppDatabase.movieDao().insertMovie(movie);
                }
            }
        });
    }

    public LiveData<Integer> isFavoriteMovie(final Integer movieId) {
        return mAppDatabase.movieDao().isFavoriteMovie(movieId);
    }

}
