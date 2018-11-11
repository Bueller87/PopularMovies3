package com.example.android.popular_movies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.popular_movies.R;
import com.example.android.popular_movies.model.MovieReview;

import java.util.List;

public class ReviewAdapter extends ArrayAdapter<MovieReview> {
    private Context mContext;
    private List<MovieReview> mMovieReviews;


    public ReviewAdapter(@NonNull Context context, @NonNull List<MovieReview> movieReviews) {
        super(context, 0, movieReviews);
        mMovieReviews = movieReviews;
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Context infContext = getContext();
        if (convertView == null) {
            convertView = LayoutInflater.from(infContext).inflate(
                    R.layout.item_review, parent, false);
        }

        MovieReview movieReview = mMovieReviews.get(position);

        TextView authorTextView = convertView.findViewById(R.id.tv_review_author);
        authorTextView.setText(movieReview.getAuthor());

        TextView contentTextView = convertView.findViewById(R.id.tv_review_content);
        contentTextView.setText(movieReview.getContent());

        return convertView;
    }
}
