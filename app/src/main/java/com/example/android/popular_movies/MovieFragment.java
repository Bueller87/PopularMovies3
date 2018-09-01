package com.example.android.popular_movies;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.example.android.popular_movies.model.DiscoverMoviesResult;
import com.example.android.popular_movies.model.Movie;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {

    @BindView(R.id.movies_grid)
    GridView mGridView;
    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView =  inflater.inflate(R.layout.fragment_movie, container, false);

        refreshData();
        return  fragmentView;
    }

    private void createListAdapter(List<Movie> moviesList) {
        MovieAdapter movieAdapter = new MovieAdapter(getContext(), moviesList);
        mGridView.setAdapter(movieAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO launch Details View Activity
            }
        });

    }
    private void refreshData() {

        Call<DiscoverMoviesResult> popMoviesCall = null;
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MovieApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MovieApi movieApi = retrofit.create(MovieApi.class);

            popMoviesCall = movieApi.getPopularMovies();
        } catch (Exception e) {
            Log.d(MainActivity.DEBUG_TAG, String.format("Retrofit Error: %s", e.getMessage()));
            e.printStackTrace();
        }



        popMoviesCall.enqueue(new Callback<DiscoverMoviesResult>() {
                                  @Override
                                  public void onResponse(Call<DiscoverMoviesResult> call, Response<DiscoverMoviesResult> response) {
                                      DiscoverMoviesResult moviesResult = response.body();
                                      Log.d(MainActivity.DEBUG_TAG, "response.isSuccessful():" + response.isSuccessful());

                                      if (response.isSuccessful() && moviesResult.getMovies() != null) {
                                          for (Movie movie : moviesResult.getMovies()) {
                                              createListAdapter(moviesResult.getMovies());
                                              Log.d(MainActivity.DEBUG_TAG, movie.getTitle());
                                          }
                                      } else {
                                          Log.d(MainActivity.DEBUG_TAG, "response.NOTSuccessful():" + response.code());
                                      }

                                  }

                                  @Override
                                  public void onFailure(Call<DiscoverMoviesResult> call, Throwable t) {
                                      //Toast failToast = Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG);
                                      //failToast.show();
                                      Log.d(MainActivity.DEBUG_TAG, String.format("popMoviesCall.onFailure: %s", t.getMessage()));
                                  }
                              }
        );
    }

}
