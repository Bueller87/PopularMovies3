package com.example.android.popular_movies;

//import android.app.ActionBar;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.popular_movies.adapter.ReviewAdapter;
import com.example.android.popular_movies.model.Movie;
import com.example.android.popular_movies.model.MovieReview;
import com.example.android.popular_movies.model.MovieReviewsResult;
import com.example.android.popular_movies.model.MovieTrailer;
import com.example.android.popular_movies.model.MovieTrailersResult;
import com.example.android.popular_movies.utilities.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import android.support.v7.widget.Toolbar;

public class MovieDetailsActivity extends AppCompatActivity {

    @BindView(R.id.iv_backdrop)
    ImageView backdropImageView;
    @BindView(R.id.iv_details_poster)
    ImageView moviePosterImageView;
    @BindView(R.id.tv_title)
    TextView titleTextView;
    @BindView(R.id.tv_release_date)
    TextView releaseDateTextView;
    @BindView(R.id.tv_rating)
    TextView ratingTextView;
    @BindView(R.id.tv_overview)
    TextView overviewTextView;
    @BindView(R.id.rv_reviews)
    ListView reviewsRecyclerView;
    private Snackbar mSnackbar;

    private void loadReviews(Integer movieId) {
        Call<MovieReviewsResult> getMovieReviews = null;
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MovieApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MovieApi movieApi = retrofit.create(MovieApi.class);
            getMovieReviews = movieApi.getMovieReviews(movieId, MovieApi.API_KEY);
        } catch (Exception e) {
            Log.e(MainActivity.DEBUG_TAG, String.format("Retrofit getMovieReviews Error: %s", e.getMessage()));
        }

        if (getMovieReviews != null) {
            if (mSnackbar != null) {
                if (mSnackbar.isShown()) {
                    mSnackbar.dismiss();
                }
            }
            getMovieReviews.enqueue(new Callback<MovieReviewsResult>() {
                @Override
                public void onResponse(@NonNull Call<MovieReviewsResult> call, @NonNull Response<MovieReviewsResult> response) {
                    MovieReviewsResult reviewsResult = response.body();
                    Log.i(MainActivity.DEBUG_TAG, "Movie Trailers received?:" + response.isSuccessful());


                    try {
                        if (reviewsResult != null && response.isSuccessful() && reviewsResult.getMovieReviews() != null) {
                            List<MovieReview> reviewsList =  reviewsResult.getMovieReviews();
                            createReviewListAdapter(reviewsList);
                            for (MovieReview review : reviewsList) {
                                Log.i(MainActivity.DEBUG_TAG, "Review Author: " + review.getAuthor());
                                Log.i(MainActivity.DEBUG_TAG, "Review Content:\n" + review.getContent());
                                Log.i(MainActivity.DEBUG_TAG, "---------------------------");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MovieReviewsResult> call, @NonNull  Throwable t) {

                }
            });
        }
    }

    private void createReviewListAdapter(List<MovieReview> movieReviews){
        ReviewAdapter reviewAdapter = new ReviewAdapter(this, movieReviews);
        reviewsRecyclerView.setAdapter(reviewAdapter);
        Utility.setListViewHeightBasedOnChildren(reviewsRecyclerView);
    }

    private void refreshView(Movie movie) {

        this.setTitle("");
        titleTextView.setText(movie.getOriginalTitle());
        releaseDateTextView.setText(movie.getReleaseDate());
        ratingTextView.setText(String.valueOf(movie.getVoteAverage()));
        String overView = movie.getOverview();
        Log.d("adf", overView);
        overviewTextView.setText(movie.getOverview());
        String posterPath = movie.getPosterFullPath();
        Picasso.with(this)
                .load(posterPath)
                .placeholder(R.drawable.film_poster_placeholder)
                .into(moviePosterImageView);

        String backdropPath = movie.getBackdropFullPath();
        Picasso.with(this)
                .load(backdropPath)
                .placeholder(R.drawable.backdrop_placeholder)
                .into(backdropImageView);

        loadReviews(movie.getId());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details_constraint);
        ButterKnife.bind(this);
        Movie movie = getIntent().getParcelableExtra(MovieFragment.MOVIE_OBJECT_TAG);
        boolean b = false;


        if (movie != null) {
            refreshView(movie);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
