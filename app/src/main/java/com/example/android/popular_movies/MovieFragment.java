package com.example.android.popular_movies;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

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
    View mFragmentView;
    GridView mGridView;
    MovieApi.SortOptions mSortOptions = MovieApi.SortOptions.POPULAR;

    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sort_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        MovieApi.SortOptions sortOptions;
        switch (item.getItemId()) {
            case R.id.action_popular:
                sortOptions = MovieApi.SortOptions.POPULAR;
                Toast.makeText(getActivity(), "action_popular", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_highest_rated:
                sortOptions = MovieApi.SortOptions.HIGHEST_RATED;
                Toast.makeText(getActivity(), "action_highest_rated", Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        if (sortOptions != mSortOptions) {
            mSortOptions = sortOptions;
            refreshData(mSortOptions);
        }
        return true;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View fragmentView =  inflater.inflate(R.layout.fragment_movie, container, false);
        mFragmentView = fragmentView;
        mGridView = (GridView) mFragmentView.findViewById(R.id.movies_grid);
        refreshData(mSortOptions);
        return  fragmentView;
    }

    private void createListAdapter(List<Movie> moviesList) {
        MovieAdapter movieAdapter = new MovieAdapter(getActivity(), moviesList);

        mGridView.setAdapter(movieAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO launch Details View Activity
            }
        });

    }

    public void setActionBarTitle(String title) {
        ((MainActivity) getActivity())
                .setActionBarTitle(title);
    }
    private void refreshData(MovieApi.SortOptions sortOptions) {

        Call<DiscoverMoviesResult> popMoviesCall = null;
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MovieApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MovieApi movieApi = retrofit.create(MovieApi.class);

            if (sortOptions == MovieApi.SortOptions.POPULAR) {
                popMoviesCall = movieApi.getPopularMovies();
                setActionBarTitle(getResources().getString(R.string.popular_movies));
            }
            else {
                popMoviesCall = movieApi.getTopRatedMovies();
                setActionBarTitle(getResources().getString(R.string.highest_rated_movies));
            }
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

                                              Log.d(MainActivity.DEBUG_TAG, movie.getTitle());
                                          }
                                          createListAdapter(moviesResult.getMovies());
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
