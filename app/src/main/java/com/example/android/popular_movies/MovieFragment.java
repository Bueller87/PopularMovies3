package com.example.android.popular_movies;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popular_movies.model.DiscoverMoviesResult;
import com.example.android.popular_movies.model.Movie;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {
    public final static String MOVIE_OBJECT_TAG = "MovieParcel";
    public final static String SAVED_SORT_OPTIONS = "SavedSortOptions";
    public final static String SAVED_SCROLL_POSITION = "SavedScrollPosition";
    /*@BindView(R.id.movies_grid)*/
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private View mFragmentView;
    private Snackbar mSnackbar;
    private MenuItem mPopularMenuItem;
    private MenuItem mHighestRatedMenuItem;

    private List<Movie> mMoviesList;
    private int mSortOptions = MovieApi.SORTBY_POPULAR;
    private int mRestoredSearchOptions = MovieApi.SORTBY_UNDEFINED;
    private int mScrollPosition = 0;


    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sort_menu, menu);
        mPopularMenuItem = menu.findItem(R.id.action_popular);
        mHighestRatedMenuItem = menu.findItem(R.id.action_highest_rated);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int sortOptions;
        switch (item.getItemId()) {
            case R.id.action_popular:
                sortOptions = MovieApi.SORTBY_POPULAR;
                break;
            case R.id.action_highest_rated:
                sortOptions = MovieApi.SORTBY_HIGHEST_RATED;
                break;
            case R.id.action_sort:
                try {
                    if (mSortOptions == MovieApi.SORTBY_POPULAR) {
                        mPopularMenuItem.setVisible(false);
                        mHighestRatedMenuItem.setVisible(true);
                    } else if (mSortOptions == MovieApi.SORTBY_HIGHEST_RATED) {
                        mPopularMenuItem.setVisible(true);
                        mHighestRatedMenuItem.setVisible(false);
                    } else if (mSortOptions == MovieApi.SORTBY_UNDEFINED) { //this should could happen if no internet on startup
                        mPopularMenuItem.setVisible(true);
                        mHighestRatedMenuItem.setVisible(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }

        if (sortOptions != mSortOptions) {
            mRestoredSearchOptions = mSortOptions;  //restore sort options in case of error
            mSortOptions = sortOptions;

            mScrollPosition = 0;
            refreshData(mSortOptions);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SAVED_SORT_OPTIONS, mSortOptions);
        outState.putInt(SAVED_SCROLL_POSITION, mGridView.getFirstVisiblePosition());
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_movie, container, false);
        mFragmentView = fragmentView;
        mGridView = fragmentView.findViewById(R.id.movies_grid);
        mProgressBar = fragmentView.findViewById(R.id.progressBar);


        if (savedInstanceState != null) {
            mSortOptions = savedInstanceState.getInt(SAVED_SORT_OPTIONS);
            mScrollPosition = savedInstanceState.getInt(SAVED_SCROLL_POSITION);
        }
        refreshData(mSortOptions);
        return fragmentView;
    }

    private void createListAdapter(List<Movie> moviesList) {
        MovieAdapter movieAdapter = new MovieAdapter(Objects.requireNonNull(getActivity()), moviesList);
        mMoviesList = moviesList;
        mGridView.setAdapter(movieAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO launch Details View Activity
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra(MovieFragment.MOVIE_OBJECT_TAG, mMoviesList.get(i));
                startActivity(intent);
            }
        });

    }

    public void setActionBarTitle(String title) {
        try {
            ((MainActivity) getActivity())
                    .setActionBarTitle(title);
        } catch (NullPointerException e) {
            Log.e(MainActivity.DEBUG_TAG, "setActionBarTitle: NullPointer Exception");
        }
    }

    private void refreshData(int sortOptions) {

        Call<DiscoverMoviesResult> popMoviesCall = null;
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MovieApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MovieApi movieApi = retrofit.create(MovieApi.class);


            if (sortOptions == MovieApi.SORTBY_POPULAR) {
                popMoviesCall = movieApi.getPopularMovies();
            } else {
                popMoviesCall = movieApi.getTopRatedMovies();
            }
        } catch (Exception e) {
            Log.e(MainActivity.DEBUG_TAG, String.format("Retrofit Error: %s", e.getMessage()));
            e.printStackTrace();
        }


        if (popMoviesCall != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            if (mSnackbar != null) {
                if (mSnackbar.isShown()) {
                    mSnackbar.dismiss();
                }
            }
            popMoviesCall.enqueue(new Callback<DiscoverMoviesResult>() {
                                      @Override
                                      public void onResponse(Call<DiscoverMoviesResult> call, Response<DiscoverMoviesResult> response) {
                                          DiscoverMoviesResult moviesResult = response.body();
                                          Log.i(MainActivity.DEBUG_TAG, "response.isSuccessful():" + response.isSuccessful());
                                          mProgressBar.setVisibility(View.INVISIBLE);


                                          if (response.isSuccessful() && moviesResult.getMovies() != null) {

                                              createListAdapter(moviesResult.getMovies());
                                              mGridView.smoothScrollToPosition(mScrollPosition);
                                              if (mSortOptions == MovieApi.SORTBY_POPULAR) {
                                                  setActionBarTitle(getResources().getString(R.string.popular_movies));
                                              } else {
                                                  setActionBarTitle(getResources().getString(R.string.highest_rated_movies));
                                              }
                                          } else {
                                              Log.i(MainActivity.DEBUG_TAG, "response.NOTSuccessful():" + response.code());
                                              Toast.makeText(getContext(), R.string.err_service_unavailable, Toast.LENGTH_LONG).show();
                                              mSortOptions = mRestoredSearchOptions;
                                          }

                                      }

                                      @Override
                                      public void onFailure(Call<DiscoverMoviesResult> call, Throwable t) {
                                          mProgressBar.setVisibility(View.INVISIBLE);
                                          Log.e(MainActivity.DEBUG_TAG, String.format("popMoviesCall.onFailure: %s", t.getMessage()));
                                          mSortOptions = mRestoredSearchOptions;
                                          CoordinatorLayout cl = (CoordinatorLayout) mFragmentView;
                                          if (cl != null) {
                                              mSnackbar = Snackbar.make(cl, R.string.err_no_internet_verbose,
                                                      Snackbar.LENGTH_INDEFINITE);

                                              mSnackbar.show();
                                          } else {
                                              Toast.makeText(getContext(), R.string.err_no_internet, Toast.LENGTH_LONG).show();
                                          }

                                      }
                                  }
            );
        } else {
            Log.e(MainActivity.DEBUG_TAG, "refreshData: Null Pointer Exception");
            mSortOptions = mRestoredSearchOptions;
        }
    }

}
