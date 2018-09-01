package com.example.android.popular_movies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

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

        //TODO inflate list view layout
        /*if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.flavor_item, parent, false);
        }*/

       /* Sandwich sandwich = mSandwichList.get(position);

        /TODO load imageview with poster file
        ImageView thumbImageView = (ImageView) listItem.findViewById(R.id.iv_thumbnail);
        Picasso.with(mContext)
                .load(sandwich.getImage())
                .placeholder(R.drawable.placeholder_banner)
                .into(thumbImageView);*/
        //TODO load imageview with poster file
        return super.getView(position, convertView, parent);
    }
}
