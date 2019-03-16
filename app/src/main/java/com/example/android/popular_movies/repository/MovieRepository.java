package com.example.android.popular_movies.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popular_movies.BuildConfig;
import com.example.android.popular_movies.activities.MainActivity;
import com.example.android.popular_movies.model.DataWrapper;
import com.example.android.popular_movies.model.DiscoverMoviesResult;
import com.example.android.popular_movies.model.Movie;
import com.example.android.popular_movies.model.MovieReview;
import com.example.android.popular_movies.model.MovieReviewsResult;
import com.example.android.popular_movies.model.MovieTrailer;
import com.example.android.popular_movies.model.MovieTrailersResult;
import com.example.android.popular_movies.viewmodel.MainViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.android.popular_movies.R;

public class MovieRepository {
    private MovieApi mMovieApi;
    private static MovieRepository sMovieRepository;
    private final MutableLiveData<DataWrapper<List<Movie>>> mMovieListLiveData = new MutableLiveData<>();
    private final MutableLiveData<DataWrapper<List<MovieTrailer>>> mMovieTrailerListLiveData = new MutableLiveData<>();
    private final MutableLiveData<DataWrapper<List<MovieReview>>> mMovieReviewListLiveData = new MutableLiveData<>();

    private MovieRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMovieApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mMovieApi = retrofit.create(MovieApi.class);
    }

    public synchronized static MovieRepository getInstance() {
        if (sMovieRepository == null) {
            sMovieRepository = new MovieRepository();
        }
        return sMovieRepository;
    }

    public LiveData<DataWrapper<List<Movie>>> getMovieList(int sortOptions) {
        Call<DiscoverMoviesResult> movieListCall = mMovieApi.getPopularMovies(BuildConfig.MOVIE_API_KEY);
        if (sortOptions == MainViewModel.FILTER_BY_HIGHEST_RATED) {
            movieListCall = mMovieApi.getTopRatedMovies(BuildConfig.MOVIE_API_KEY);
        }
        movieListCall.enqueue(new Callback<DiscoverMoviesResult>() {
            @Override
            public void onResponse(Call<DiscoverMoviesResult> call, Response<DiscoverMoviesResult> response) {
                DiscoverMoviesResult moviesResult = response.body();
                if (moviesResult != null && response.isSuccessful() && moviesResult.getMovies() != null) {
                    mMovieListLiveData.setValue(new DataWrapper<>(moviesResult.getMovies(), null) );
                } else {
                    mMovieListLiveData.setValue(new DataWrapper<List<Movie>>(null, R.string.err_service_unavailable));
                }
            }
            @Override
            public void onFailure(Call<DiscoverMoviesResult> call, Throwable t) {
                //no internet
                mMovieListLiveData.setValue(new DataWrapper<List<Movie>>(null, null));
            }
        });
        return mMovieListLiveData;
    }

    public LiveData<DataWrapper<List<MovieTrailer>>> getTrailerList(Integer movieId) {
        Call<MovieTrailersResult> getTrailers  = mMovieApi.getMovieTrailers(movieId, BuildConfig.MOVIE_API_KEY);
        getTrailers.enqueue(new Callback<MovieTrailersResult>() {
            @Override
            public void onResponse(@NonNull Call<MovieTrailersResult> call, @NonNull Response<MovieTrailersResult> response) {
                MovieTrailersResult trailersResult = response.body();
                if (trailersResult != null && response.isSuccessful() && trailersResult.getMovieTrailers() != null) {
                    mMovieTrailerListLiveData.setValue(
                            new DataWrapper<>(trailersResult.getMovieTrailers(),null));
                } else {
                    mMovieTrailerListLiveData.setValue(new DataWrapper<List<MovieTrailer>>(null, null));
                }
            }
            @Override
            public void onFailure(@NonNull Call<MovieTrailersResult> call, @NonNull Throwable t) {
                mMovieTrailerListLiveData.setValue(new DataWrapper<List<MovieTrailer>>(null, null));
            }
        });
        return mMovieTrailerListLiveData;
    }

    public LiveData<DataWrapper<List<MovieReview>>> getReviewList(Integer movieId) {
        Call<MovieReviewsResult> getReviews  = mMovieApi.getMovieReviews(movieId, BuildConfig.MOVIE_API_KEY);
        getReviews.enqueue(new Callback<MovieReviewsResult>() {
            @Override
            public void onResponse(@NonNull Call<MovieReviewsResult> call, @NonNull Response<MovieReviewsResult> response) {
                MovieReviewsResult reviewsResult = response.body();
                if (reviewsResult != null && response.isSuccessful() && reviewsResult.getMovieReviews() != null) {
                    mMovieReviewListLiveData.setValue(
                            new DataWrapper<>(reviewsResult.getMovieReviews() ,null));
                } else {
                    mMovieReviewListLiveData.setValue(new DataWrapper<List<MovieReview>>(null, null));
                }
            }

            @Override
            public void onFailure(Call<MovieReviewsResult> call, Throwable t) {
                mMovieReviewListLiveData.setValue(new DataWrapper<List<MovieReview>>(null, null));
            }
        });
        return mMovieReviewListLiveData;
    }
}
