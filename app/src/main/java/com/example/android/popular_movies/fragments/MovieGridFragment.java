package com.example.android.popular_movies.fragments;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.example.android.popular_movies.activities.MovieDetailsActivity;
import com.example.android.popular_movies.R;
import com.example.android.popular_movies.activities.MainActivity;
import com.example.android.popular_movies.adapter.MovieAdapter;
import com.example.android.popular_movies.database.AppDatabase;
import com.example.android.popular_movies.model.DataWrapper;
import com.example.android.popular_movies.viewmodel.MainViewModel;
import com.example.android.popular_movies.model.DiscoverMoviesResult;
import com.example.android.popular_movies.model.Movie;
import com.example.android.popular_movies.repository.MovieApi;

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
public class MovieGridFragment extends Fragment {
    public final static String MOVIE_OBJECT_TAG = "MovieParcel";
    public final static String SAVED_SORT_OPTIONS = "SavedSortOptions";
    public final static String SAVED_SCROLL_POSITION = "SavedScrollPosition";
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private View mFragmentView;
    private Snackbar mSnackbar;
    private MenuItem mPopularMenuItem;
    private MenuItem mHighestRatedMenuItem;
    private MenuItem mFavoriteMenuItem;
    private List<Movie> mMoviesList;
    private int mSortOptions = MainViewModel.FILTER_BY_MOST_POPULAR;
    private int mRestoredSearchOptions = MainViewModel.FILTER_BY_UNDEFINED;
    private int mScrollPosition = 0;
    private MainViewModel mMainViewModel;


    public MovieGridFragment() {
        // Required empty public constructor
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
        setupViewModel();
        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sort, menu);
        mPopularMenuItem = menu.findItem(R.id.action_popular);
        mHighestRatedMenuItem = menu.findItem(R.id.action_highest_rated);
        mFavoriteMenuItem = menu.findItem(R.id.action_favorite);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int sortOptions;
        switch (item.getItemId()) {
            case R.id.action_popular:
                sortOptions = MainViewModel.FILTER_BY_MOST_POPULAR;
                break;
            case R.id.action_highest_rated:
                sortOptions = MainViewModel.FILTER_BY_HIGHEST_RATED;
                break;
            case R.id.action_favorite:
                sortOptions = MainViewModel.FILTER_BY_FAVORITE;
                break;
            case R.id.action_sort:
                try {
                    if (mSortOptions == MainViewModel.FILTER_BY_MOST_POPULAR) {
                        mPopularMenuItem.setVisible(false);
                        mHighestRatedMenuItem.setVisible(true);
                        mFavoriteMenuItem.setVisible(true);
                    } else if (mSortOptions == MainViewModel.FILTER_BY_HIGHEST_RATED) {
                        mPopularMenuItem.setVisible(true);
                        mFavoriteMenuItem.setVisible(true);
                        mHighestRatedMenuItem.setVisible(false);

                    } else if (mSortOptions == MainViewModel.FILTER_BY_FAVORITE) {
                        mPopularMenuItem.setVisible(true);
                        mFavoriteMenuItem.setVisible(false);
                        mHighestRatedMenuItem.setVisible(true);
                    } else if (mSortOptions == MainViewModel.FILTER_BY_UNDEFINED) { //this should could happen if no internet on startup
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.e(MainActivity.DEBUG_TAG, "MovieGridFragment.onSaveInstanceState()");
        outState.putInt(SAVED_SORT_OPTIONS, mSortOptions);
        outState.putInt(SAVED_SCROLL_POSITION, mGridView.getFirstVisiblePosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.e(MainActivity.DEBUG_TAG, "MovieGridFragment.onViewStateRestored()");
        if (savedInstanceState != null) {
            mSortOptions = savedInstanceState.getInt(SAVED_SORT_OPTIONS);
            mScrollPosition = savedInstanceState.getInt(SAVED_SCROLL_POSITION);
            Log.d(MainActivity.DEBUG_TAG, "MovieGridFragment.onViewStateRestored(), RESUME_CONFIG_CHANGE");
        } else {
            Log.d(MainActivity.DEBUG_TAG, "MovieGridFragment.onViewStateRestored(), RESUME_FIRST_TIME");
        }
    }

    private void createListAdapter(List<Movie> moviesList) {
        MovieAdapter movieAdapter = new MovieAdapter(Objects.requireNonNull(getActivity()), moviesList);
        mMoviesList = moviesList;
        mGridView.setAdapter(movieAdapter);
        mGridView.smoothScrollToPosition(mScrollPosition);
        mProgressBar.setVisibility(View.INVISIBLE);
        if (mSortOptions == MainViewModel.FILTER_BY_MOST_POPULAR) {
            setActionBarTitle(getResources().getString(R.string.popular_movies));
        } else if (mSortOptions == MainViewModel.FILTER_BY_HIGHEST_RATED) {
            setActionBarTitle(getResources().getString(R.string.highest_rated_movies));
        } else if (mSortOptions == MainViewModel.FILTER_BY_FAVORITE) {
            setActionBarTitle(getResources().getString(R.string.favorite_movies));
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO launch Details View Activity
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra(MovieGridFragment.MOVIE_OBJECT_TAG, mMoviesList.get(i));
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

    private void setupViewModel() {
        Activity activity = getActivity();
        if (activity != null) {
            mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
            mMainViewModel.getFavoriteMovies().observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    onLoadFavorites();
                }
            });

            //movieListChanged
            mMainViewModel.getMovieList().observe(this, new Observer<DataWrapper<List<Movie>>>() {
                @Override
                public void onChanged(@Nullable DataWrapper<List<Movie>> movieCallData) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Log.d(MainActivity.DEBUG_TAG, "Observer:MovieList onChanged Event");
                    if (mSortOptions == MainViewModel.FILTER_BY_MOST_POPULAR ||
                            mSortOptions == MainViewModel.FILTER_BY_HIGHEST_RATED) {
                        onLoadMovies(movieCallData);
                    }
                }
            });
        }
    }

    private void onLoadFavorites() {
        if (mSortOptions == MainViewModel.FILTER_BY_FAVORITE) {
            mProgressBar.setVisibility(View.VISIBLE);
            createListAdapter(mMainViewModel.getFavoriteMovies().getValue());
        }
    }

    private void onLoadMovies(DataWrapper<List<Movie>> movieCallData) {
        if (mSortOptions == MainViewModel.FILTER_BY_MOST_POPULAR ||
                mSortOptions == MainViewModel.FILTER_BY_HIGHEST_RATED) {
            if (movieCallData.getDeviceNoConnectivity()) {
                showNetworkErrorSnackbar();
            } else if (movieCallData.getData() != null) {
                createListAdapter(movieCallData.getData());
            } else if (movieCallData.getErrMessage() != null) {
                Toast.makeText(getContext(), movieCallData.getErrMessage(), Toast.LENGTH_LONG).show();
                mSortOptions = mRestoredSearchOptions;
            }
        }
    }

    private void refreshData(int sortOptions) {
        dismissNetworkErrorSnackbar();
        if (sortOptions == MainViewModel.FILTER_BY_FAVORITE) {
            onLoadFavorites();
            Log.e(MainActivity.DEBUG_TAG, "Sort by Favorites");
        } else if (sortOptions == MainViewModel.FILTER_BY_MOST_POPULAR ||
                sortOptions == MainViewModel.FILTER_BY_HIGHEST_RATED) {
            mProgressBar.setVisibility(View.VISIBLE);
            mMainViewModel.onMoviesRefreshNeeded(sortOptions);
        }
    }
    private void dismissNetworkErrorSnackbar() {
        if (mSnackbar != null) {
            if (mSnackbar.isShown()) {
                mSnackbar.dismiss();
            }
        }
    }
    private void showNetworkErrorSnackbar() {

        mSnackbar = Snackbar
                .make( mFragmentView, R.string.err_no_internet_verbose,
                        Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSnackbar.dismiss();
                        refreshData(mSortOptions);
                    }
                });
        if (!mSnackbar.isShownOrQueued()) {
            mSnackbar.show();
        }
    }
}
