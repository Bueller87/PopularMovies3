package com.example.android.popular_movies.activities;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popular_movies.R;
import com.example.android.popular_movies.adapter.ReviewAdapter;
import com.example.android.popular_movies.adapter.TrailerAdapter;
import com.example.android.popular_movies.callback.RecyclerClickListener;
import com.example.android.popular_movies.database.AppDatabase;
import com.example.android.popular_movies.database.AppExecutors;
import com.example.android.popular_movies.fragments.MovieGridFragment;
import com.example.android.popular_movies.model.Movie;
import com.example.android.popular_movies.model.MovieReview;
import com.example.android.popular_movies.model.MovieReviewsResult;
import com.example.android.popular_movies.model.MovieTrailer;
import com.example.android.popular_movies.model.MovieTrailersResult;
import com.example.android.popular_movies.repository.MovieApi;
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
    @BindView(R.id.rv_trailers)
    RecyclerView trailersRecyclerView;
    @BindView(R.id.tv_no_reviews)
    TextView noReviewsTextView;
    @BindView(R.id.tv_no_trailers)
    TextView noTrailersTextView;
    @BindView(R.id.iv_favorite_fill)
    ImageView favButtonFill;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Snackbar mSnackbar;
    private List<MovieTrailer> mMovieTrailers;
    private AppDatabase mAppDatabase;
    private Movie mMovie;
    private boolean mTrailersLoaded;
    private boolean mReviewsLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details_constraint);
        mAppDatabase = AppDatabase.getInstance(getApplicationContext());
        ButterKnife.bind(this);
        Movie movie = getIntent().getParcelableExtra(MovieGridFragment.MOVIE_OBJECT_TAG);
        boolean b = false;
        mMovie = movie;

        if (movie != null) {
            refreshView(movie);
        }
        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        trailersRecyclerView.setLayoutManager(trailerLayoutManager);

        trailersRecyclerView.addOnItemTouchListener(new RecyclerClickListener(this, new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String url = mMovieTrailers.get(position).getVideoUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        }));


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
    private void loadTrailers(Integer movieId) {
        Call<MovieTrailersResult> getTrailers = null;

        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MovieApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MovieApi movieApi = retrofit.create(MovieApi.class);
            getTrailers = movieApi.getMovieTrailers(movieId, MovieApi.API_KEY);
        } catch (Exception e) {
            Log.e(MainActivity.DEBUG_TAG, String.format("Retrofit getTrailers Exception: %s", e.getMessage()));
            e.printStackTrace();
            hidePB();
        }

        if (getTrailers != null) {
            if (mSnackbar != null) {
                if (mSnackbar.isShown()) {
                    mSnackbar.dismiss();
                }
            }
            getTrailers.enqueue(new Callback<MovieTrailersResult>() {
                @Override
                public void onResponse(@NonNull Call<MovieTrailersResult> call, @NonNull Response<MovieTrailersResult> response) {
                    mTrailersLoaded = true;
                    checkDoneLoading();
                    MovieTrailersResult trailersResult = response.body();
                    Log.i(MainActivity.DEBUG_TAG, "Movie Trailers received?:" + response.isSuccessful());

                    try {
                        if (trailersResult != null && response.isSuccessful() && trailersResult.getMovieTrailers() != null) {
                            mMovieTrailers = trailersResult.getMovieTrailers();
                            createTrailersListAdapter(mMovieTrailers);
                        }
                    } catch (Exception e) {
                        Log.e(MainActivity.DEBUG_TAG, String.format("Retrofit getTrailers:onResponse Exception: %s", e.getMessage()));
                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(@NonNull Call<MovieTrailersResult> call, @NonNull Throwable t) {
                    Log.e(MainActivity.DEBUG_TAG, String.format("Retrofit getTrailers:onFailure : %s", t.getMessage()));
                    showNetworkErrorSnackbar();

                }
            });
        }
    }

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
            Log.e(MainActivity.DEBUG_TAG, String.format("Retrofit getMovieReviews Exception: %s", e.getMessage()));
            e.printStackTrace();
            hidePB();
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
                    mReviewsLoaded = true;
                    checkDoneLoading();
                    MovieReviewsResult reviewsResult = response.body();
                    Log.i(MainActivity.DEBUG_TAG, "Movie Reviews received?:" + response.isSuccessful());


                    try {
                        if (reviewsResult != null && response.isSuccessful() && reviewsResult.getMovieReviews() != null) {
                            List<MovieReview> reviewsList = reviewsResult.getMovieReviews();
                            createReviewListAdapter(reviewsList);
                        }
                    } catch (Exception e) {
                        Log.e(MainActivity.DEBUG_TAG, String.format("Retrofit getMovieReviews onResponse Exception: %s", e.getMessage()));
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MovieReviewsResult> call, @NonNull Throwable t) {
                    Log.e(MainActivity.DEBUG_TAG, String.format("Retrofit getReviews:onFailure : %s", t.getMessage()));
                    showNetworkErrorSnackbar();
                }
            });
        }
    }

    private void checkDoneLoading() {
        if (mReviewsLoaded && mTrailersLoaded) {
            hidePB();
        }
    }

    private void createReviewListAdapter(List<MovieReview> movieReviews) {
        if (movieReviews != null && movieReviews.size() > 0) {
            ReviewAdapter reviewAdapter = new ReviewAdapter(this, movieReviews);
            reviewsRecyclerView.setAdapter(reviewAdapter);
            Utility.setListViewHeightBasedOnChildren(reviewsRecyclerView);
        } else {
            noReviewsTextView.setVisibility(View.VISIBLE);
            reviewsRecyclerView.setVisibility(View.GONE);
        }
    }

    private void createTrailersListAdapter(List<MovieTrailer> movieTrailers) {
        if (movieTrailers != null && movieTrailers.size() > 0) {
            TrailerAdapter trailerAdapter = new TrailerAdapter(this, movieTrailers);
            trailersRecyclerView.setAdapter(trailerAdapter);
        } else {
            noTrailersTextView.setVisibility(View.VISIBLE);
            trailersRecyclerView.setVisibility(View.GONE);
        }
    }



    private void refreshView(Movie movie) {
        showPB();
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

        loadTrailers(movie.getId());
        loadReviews(movie.getId());
        refreshFavoriteButton(movie);

    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onClickFavorite(View view) {
        String favMsg;
        final boolean isFavorite = (favButtonFill.getVisibility() == View.VISIBLE);

        //update ui
        if (isFavorite) {//remove favorite
            favMsg = getString(R.string.favorite_removed);
            favButtonFill.setVisibility(View.INVISIBLE);

        } else { //add favorite
            favMsg = getString(R.string.favorite_added);
            favButtonFill.setVisibility(View.VISIBLE);
        }

        //give user feedback
        mSnackbar = Snackbar.make(view, favMsg,
                Snackbar.LENGTH_SHORT);
        mSnackbar.show();

        //update datebase on a separate thread
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (isFavorite) {
                    mAppDatabase.movieDao().deleteMovie(mMovie);
                }
                else {
                    mAppDatabase.movieDao().insertMovie(mMovie);
                }
            }
        });
    }

    private void refreshFavoriteButton(final Movie currMovie) {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final Movie favoriteMovie = mAppDatabase.movieDao().findFavoriteMovie(currMovie.getId());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isFavorite = (favoriteMovie != null);
                        if (isFavorite) {
                            favButtonFill.setVisibility(View.VISIBLE);
                        }
                        else  {
                            favButtonFill.setVisibility(View.INVISIBLE);
                        }
                    }
                });

            }
        });



    }

    private void showPB() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hidePB() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showNetworkErrorSnackbar() {
        hidePB();
        mSnackbar = Snackbar
                .make(findViewById(R.id.myCoordinatorLayout), R.string.err_no_internet_verbose,
                        Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.got_it, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSnackbar.dismiss();
                    }
                });
        if (!mSnackbar.isShownOrQueued()) {
            mSnackbar.show();
        }
    }
}
