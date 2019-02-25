package com.example.android.popular_movies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.popular_movies.R;
import com.example.android.popular_movies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {
    private Context mContext;
    private List<Movie> mMovies;

    public MovieAdapter(@NonNull Context context,  @NonNull List<Movie> movies) {
        super(context, 0, movies);
        mMovies = movies;
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Context infContext = getContext();
        if (convertView == null) {
            convertView = LayoutInflater.from(infContext).inflate(
                    R.layout.list_item_movie, parent, false);
        }
        Movie movie = mMovies.get(position);
        ImageView gridImageView = convertView.findViewById(R.id.iv_movie_poster);
        String posterPath = movie.getPosterFullPath();
        Picasso.with(mContext)
                .load(posterPath)
                .placeholder(R.drawable.film_poster_placeholder)
                .into(gridImageView);

        return convertView;
    }
}
