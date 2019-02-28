package com.example.android.popular_movies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popular_movies.R;
import com.example.android.popular_movies.activities.MainActivity;
import com.example.android.popular_movies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MovieItemHolder> {
    private Context mContext;
    private List<Movie> mMovies;
    private ItemClickListener mClickListener;

    public MovieGridAdapter(@NonNull Context context, List<Movie> movieList) {
        mContext = context;
        mMovies = movieList;
    }

    public void updateMovieList(@NonNull List<Movie> movieList) {
        mMovies.clear();
        mMovies.addAll(movieList);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MovieGridAdapter.MovieItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MovieItemHolder movieViewHolder;
        View viewFromGroup;
        viewFromGroup = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_item_movie, parent, false);
        movieViewHolder = new MovieItemHolder(viewFromGroup);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieGridAdapter.MovieItemHolder holder, int position) {
        holder.bind(mMovies.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public class MovieItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mPosterImageView;

        public MovieItemHolder(View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.iv_movie_poster);
        }

        public void bind(Movie movie) {
            String posterPath = movie.getPosterFullPath();
            Log.d(MainActivity.DEBUG_TAG, "bind: " + posterPath);
            Picasso.with(mContext)
                    .load(posterPath)
                    .placeholder(R.drawable.film_poster_placeholder)
                    .into(this.mPosterImageView);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    // convenience method for getting data at click position
    Movie getItem(int id) {
        return mMovies.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

