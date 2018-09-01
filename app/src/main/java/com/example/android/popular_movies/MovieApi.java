package com.example.android.popular_movies;

import com.example.android.popular_movies.model.DiscoverMoviesResult;

import retrofit2.Call;
import retrofit2.http.GET;


public interface MovieApi {

    String BASE_URL = "https://api.themoviedb.org/3/";

    @GET("discover/movie?api_key=8137e80aa355d0aa982c215c3c009bdc")
    Call<DiscoverMoviesResult> getPopularMovies();
}