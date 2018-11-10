package com.example.android.popular_movies;

import com.example.android.popular_movies.model.DiscoverMoviesResult;
import com.example.android.popular_movies.model.MovieReviewsResult;
import com.example.android.popular_movies.model.MovieTrailersResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface MovieApi {
    int SORTBY_UNDEFINED = 0;
    int SORTBY_POPULAR = 1;
    int SORTBY_HIGHEST_RATED = 2;

    String BASE_URL = "https://api.themoviedb.org/3/movie/";
    String API_KEY = "";

    @GET("popular")
    Call<DiscoverMoviesResult>getPopularMovies(@Query("api_key") String apiKey);

    @GET("top_rated")
    Call<DiscoverMoviesResult>getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("{movieId}/videos")
    Call<MovieTrailersResult>getMovieTrailers(@Path("movieId") Integer movieId, @Query("api_key") String apiKey);
    ///movie/335983/videos?api_key=8137e80aa355d0aa982c215c3c009bdc
    @GET("{movieId}/reviews")
    Call<MovieReviewsResult>getMovieReviews(@Path("movieId") Integer movieId, @Query("api_key") String apiKey);
}