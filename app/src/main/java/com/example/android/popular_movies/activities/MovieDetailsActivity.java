package com.example.android.popular_movies.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Constraints;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.Toast;

import com.example.android.popular_movies.R;
import com.example.android.popular_movies.adapter.ReviewAdapter;
import com.example.android.popular_movies.adapter.TrailerAdapter;
import com.example.android.popular_movies.callback.RecyclerClickListener;
import com.example.android.popular_movies.fragments.MovieGridFragment;
import com.example.android.popular_movies.model.DataWrapper;
import com.example.android.popular_movies.model.Movie;
import com.example.android.popular_movies.model.MovieReview;
import com.example.android.popular_movies.model.MovieTrailer;
import com.example.android.popular_movies.utilities.Utility;
import com.example.android.popular_movies.viewmodel.MainViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


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
    private MainViewModel mMainViewModel;
    private Movie mMovie;
    private boolean mTrailersLoaded;
    private boolean mReviewsLoaded;
    private boolean mIsFavoriteMovie;
    public static final String TAG = MovieDetailsActivity.class.getSimpleName();
    public ConstraintLayout mConstraintLayout;
    private String mNetworkErrorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details_constraint);
        ButterKnife.bind(this);
        mMovie = getIntent().getParcelableExtra(MovieGridFragment.MOVIE_OBJECT_TAG);
        setupViewModel();

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

        //setup transparent actionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        if (mMovie != null) {
            refreshView(mMovie);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (mMovie != null) {
            refreshView(mMovie);
        }*/
    }

    private void setupViewModel() {
        mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    }

    private void setFavButtonState(Integer movieId) {
        mMainViewModel.isFavoriteMovie(movieId).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer isFav) {
                if (isFav == null) {  //this should never happen but just in case
                    mIsFavoriteMovie = false;

                } else {
                    mIsFavoriteMovie = isFav.equals(1);
                }
                refreshFavoriteButton();
            }
        });
    }

    private void loadTrailers(Integer movieId) {
        mMainViewModel.getMovieTrailerList(movieId).observe(this, new Observer<DataWrapper<List<MovieTrailer>>>() {
            @Override
            public void onChanged(DataWrapper<List<MovieTrailer>> trailerCallData) {
                mTrailersLoaded = true;
                checkDoneLoading();  //clear progress bar if done
                if (trailerCallData.getDeviceNoConnectivity()) {
                    onFailedToLoadTrailers();
                    showNetworkErrorSnackbar();
                } else if (trailerCallData.getData() != null) {
                    mMovieTrailers = trailerCallData.getData();
                    createTrailersListAdapter(mMovieTrailers);
                } else if (trailerCallData.getErrMessage() != null) {
                    onFailedToLoadTrailers();
                    Toast.makeText(MovieDetailsActivity.this,
                            trailerCallData.getErrMessage(),
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private void loadReviews(Integer movieId) {
        mMainViewModel.getMovieReviewList(movieId).observe(this, new Observer<DataWrapper<List<MovieReview>>>() {
            @Override
            public void onChanged(DataWrapper<List<MovieReview>> reviewCallData) {
                mReviewsLoaded = true;
                checkDoneLoading();  //clear progress bar if done
                if (reviewCallData.getDeviceNoConnectivity()) {
                    onFailedToLoadReviews();
                    showNetworkErrorSnackbar();
                } else if (reviewCallData.getData() != null) {
                    createReviewListAdapter(reviewCallData.getData());
                } else if (reviewCallData.getErrMessage() != null) {
                    onFailedToLoadReviews();
                    Toast.makeText(MovieDetailsActivity.this,
                            reviewCallData.getErrMessage(),
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private void checkDoneLoading() {
        if (mReviewsLoaded && mTrailersLoaded) {
            hidePB();
            dismissNetworkErrorSnackbar();
        }
    }

    private void createReviewListAdapter(List<MovieReview> movieReviews) {
        if (movieReviews != null && movieReviews.size() > 0) {
            ReviewAdapter reviewAdapter = new ReviewAdapter(this, movieReviews);
            reviewsRecyclerView.setAdapter(reviewAdapter);
            Utility.setListViewHeightBasedOnChildren(reviewsRecyclerView);
            noReviewsTextView.setVisibility(View.GONE);
            reviewsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noReviewsTextView.setVisibility(View.VISIBLE);
            reviewsRecyclerView.setVisibility(View.GONE);
        }
    }

    private void createTrailersListAdapter(List<MovieTrailer> movieTrailers) {
        if (movieTrailers != null && movieTrailers.size() > 0) {
            TrailerAdapter trailerAdapter = new TrailerAdapter(this, movieTrailers);
            trailersRecyclerView.setAdapter(trailerAdapter);
            noTrailersTextView.setVisibility(View.GONE);
            trailersRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noTrailersTextView.setVisibility(View.VISIBLE);
            trailersRecyclerView.setVisibility(View.GONE);
        }
    }

    private void onFailedToLoadTrailers() {
        noTrailersTextView.setVisibility(View.VISIBLE);
        trailersRecyclerView.setVisibility(View.GONE);
    }

    private void onFailedToLoadReviews() {
        noReviewsTextView.setVisibility(View.VISIBLE);
        reviewsRecyclerView.setVisibility(View.GONE);
    }

    private void refreshView(Movie movie) {
        showPB();
        dismissNetworkErrorSnackbar();
        this.setTitle("");
        titleTextView.setText(movie.getOriginalTitle());
        releaseDateTextView.setText(movie.getReleaseDate());
        ratingTextView.setText(String.valueOf(movie.getVoteAverage()));
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
        setFavButtonState(movie.getId());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onClickFavorite(View view) {
        String favMsg;
        //create message
        if (mIsFavoriteMovie) {//remove favorite
            favMsg = getString(R.string.favorite_removed);
        } else { //add favorite
            favMsg = getString(R.string.favorite_added);
        }

        //give user feedback
        mSnackbar = Snackbar.make(view, favMsg,
                Snackbar.LENGTH_SHORT);
        mSnackbar.show();
        //update database
        mMainViewModel.toggleFavorite(mIsFavoriteMovie, mMovie);
    }

    private void refreshFavoriteButton() {
        if (mIsFavoriteMovie) {
            favButtonFill.setVisibility(View.VISIBLE);
        } else {
            favButtonFill.setVisibility(View.INVISIBLE);
        }
    }

    private void showPB() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hidePB() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void dismissNetworkErrorSnackbar() {
        if (mSnackbar != null) {
            if (mSnackbar.isShown()) {
                mSnackbar.dismiss();
            }
        }
    }

    private void showNetworkErrorSnackbar() {

        try {   //catch exception if snackbar is created outside of lifecycle
            mSnackbar = Snackbar
                    .make( findViewById(R.id.activity_main_inference),
                            R.string.err_no_internet_verbose,
                            Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSnackbar.dismiss();
                            refreshView(mMovie);
                        }
                    });
            if (!mSnackbar.isShownOrQueued()) {
                mSnackbar.show();
            }
        } catch (Exception ex) {
            Log.e(TAG, "showNetworkErrorSnackbar: " + ex.getMessage());
        }
    }
}
