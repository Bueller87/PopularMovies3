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
import com.example.android.popular_movies.model.MovieTrailer;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<MovieTrailer> mTrailers;
    private final Context mContext;

    public TrailerAdapter(Context context, List<MovieTrailer> trailers) {
        mContext = context;
        mTrailers = trailers;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder trailerViewHolder;
        View viewFromGroup;

        viewFromGroup = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_trailer,
                parent, false);
        trailerViewHolder = new TrailerItemHolder(viewFromGroup);

        return trailerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String url = mTrailers.get(position).getThumbnailUrl();
        Picasso.with(mContext)
                .load(url)
                .placeholder(R.drawable.movie_trailer_placeholder)
                .into(((TrailerItemHolder) holder).mImageViewTrailerThumbnail);
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public static class TrailerItemHolder extends RecyclerView.ViewHolder {
        ImageView mImageViewTrailerThumbnail;

        public TrailerItemHolder(View view) {
            super(view);
            mImageViewTrailerThumbnail = view.findViewById(R.id.iv_movie_thumb);
        }
    }
}
