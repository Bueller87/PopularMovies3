package com.example.android.popular_movies.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.android.popular_movies.model.DiscoverMoviesResult;
import com.example.android.popular_movies.model.Movie;
import com.example.android.popular_movies.viewmodel.MainViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieRepository {
    private MovieApi mMovieApi;
    private static MovieRepository sMovieRepository;
    final MutableLiveData<List<Movie>> data = new MutableLiveData<>();
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

    public LiveData<List<Movie>> getMovieList(int sortOptions) {

        Call<DiscoverMoviesResult> movieListCall = mMovieApi.getPopularMovies(MovieApi.API_KEY);
        if (sortOptions == MainViewModel.FILTER_BY_HIGHEST_RATED) {
            movieListCall = mMovieApi.getTopRatedMovies(MovieApi.API_KEY);
        }
        movieListCall.enqueue(new Callback<DiscoverMoviesResult>() {
            @Override
            public void onResponse(Call<DiscoverMoviesResult> call, Response<DiscoverMoviesResult> response) {
                DiscoverMoviesResult moviesResult = response.body();
                if (response.isSuccessful() && moviesResult.getMovies() != null) {
                    data.setValue(moviesResult.getMovies());
                } else {
                    //TODO: Set error Livedata errmsg to R.string.err_service_unavailable
                }

            }

            @Override
            public void onFailure(Call<DiscoverMoviesResult> call, Throwable t) {
                //TODO: //TODO: Set error Livedata errmsg to  R.string.err_no_internet_verbose
                data.setValue(null);
            }
        });

        return data;
    }
}
