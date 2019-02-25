package com.example.android.popular_movies.repository;

import com.example.android.popular_movies.model.DiscoverMoviesResult;
import com.example.android.popular_movies.model.MovieReviewsResult;
import com.example.android.popular_movies.model.MovieTrailersResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface MovieApi {


    String BASE_URL = "https://api.themoviedb.org/3/movie/";
    String API_KEY = "";  //TODO: insert your API Key here

    @GET("popular")
    Call<DiscoverMoviesResult>getPopularMovies(@Query("api_key") String apiKey);

    @GET("top_rated")
    Call<DiscoverMoviesResult>getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("{movieId}/videos")
    Call<MovieTrailersResult>getMovieTrailers(@Path("movieId") Integer movieId, @Query("api_key") String apiKey);

    @GET("{movieId}/reviews")
    Call<MovieReviewsResult>getMovieReviews(@Path("movieId") Integer movieId, @Query("api_key") String apiKey);
}