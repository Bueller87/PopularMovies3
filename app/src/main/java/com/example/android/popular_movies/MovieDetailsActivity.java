package com.example.android.popular_movies;

//import android.app.ActionBar;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popular_movies.model.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

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
