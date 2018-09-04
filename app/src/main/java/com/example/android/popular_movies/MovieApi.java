package com.example.android.popular_movies;

import com.example.android.popular_movies.model.DiscoverMoviesResult;

import retrofit2.Call;
import retrofit2.http.GET;


public interface MovieApi {
    int SORTBY_UNDEFINED = 0;
    int SORTBY_POPULAR = 1;
    int SORTBY_HIGHEST_RATED = 2;

    String BASE_URL = "https://api.themoviedb.org/3/";

    @GET("movie/popular?api_key=")
    Call<DiscoverMoviesResult> getPopularMovies();

    @GET("movie/top_rated?api_key=")
    Call<DiscoverMoviesResult> getTopRatedMovies();
}