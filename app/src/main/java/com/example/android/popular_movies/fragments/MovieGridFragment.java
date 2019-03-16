package com.example.android.popular_movies.fragments;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.android.popular_movies.adapter.MovieGridAdapter;
import com.example.android.popular_movies.callback.RecyclerClickListener;
import com.example.android.popular_movies.model.DataWrapper;
import com.example.android.popular_movies.viewmodel.MainViewModel;
import com.example.android.popular_movies.model.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static com.example.android.popular_movies.activities.MovieDetailsActivity.TAG;

public class MovieGridFragment extends Fragment implements MovieGridAdapter.ItemClickListener{
    public final static String MOVIE_OBJECT_TAG = "MovieParcel";
    public final static String SAVED_SORT_OPTIONS = "SavedSortOptions";
    private RecyclerView mGridRecyclerView;
    private ProgressBar mProgressBar;
    private View mFragmentView;
    private Snackbar mSnackbar;
    private MenuItem mPopularMenuItem;
    private MenuItem mHighestRatedMenuItem;
    private MenuItem mFavoriteMenuItem;
    private List<Movie> mMoviesList;
    private int mSortOptions = MainViewModel.FILTER_BY_MOST_POPULAR;
    private int mRestoredSearchOptions = MainViewModel.FILTER_BY_UNDEFINED;
    private Parcelable mRecyclerViewState;
    //private int mScrollPosition = 0;
    private MainViewModel mMainViewModel;
    private  MovieGridAdapter movieAdapter;
// initialise loading state
    private boolean  mIsLoading = false;
    private boolean  mIsLastPage = false;
    public static final int PAGE_SIZE = 20;
    public MovieGridFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_movie, container, false);
        mFragmentView = fragmentView;
        mProgressBar = fragmentView.findViewById(R.id.progressBar);
        setupRecyclerView();
        setupViewModel();
        return fragmentView;
    }

    private void setupRecyclerView() {
        mGridRecyclerView = mFragmentView.findViewById(R.id.movies_grid);
        mGridRecyclerView.addOnItemTouchListener(new RecyclerClickListener(getActivity(), new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra(MovieGridFragment.MOVIE_OBJECT_TAG, mMoviesList.get(position));
                startActivity(intent);

                FragmentActivity fa = getActivity();
                if (fa != null) {
                   /* fa.overridePendingTransition(
                            android.R.anim.fade_in, android.R.anim.fade_out);*/
                    fa.overridePendingTransition(
                            R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        }));

        RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = (GridLayoutManager)mGridRecyclerView.getLayoutManager();
                int visibleItemCount =  layoutManager.getChildCount();
                int totalItemCount =  layoutManager.getItemCount();
                int firstVisibleItemPosition =  layoutManager.findFirstVisibleItemPosition();

                if (!mIsLoading && !mIsLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        loadMoreItems();
                    }
                }
            }
        };



        mMoviesList = new ArrayList<>();
        movieAdapter = new MovieGridAdapter(Objects.requireNonNull(getActivity()), mMoviesList);
        //movieAdapter.setClickListener(this);
        mGridRecyclerView.setItemAnimator(new SlideInUpAnimator());
        mGridRecyclerView.setAdapter(movieAdapter);
        // Pagination
        mGridRecyclerView.addOnScrollListener(recyclerViewOnScrollListener);
    }

    private void loadMoreItems() {
        Log.d(TAG, "loadMoreItems: ");
        mIsLoading = true;
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
        outState.putInt(SAVED_SORT_OPTIONS, mSortOptions);
        mRecyclerViewState = mGridRecyclerView.getLayoutManager().onSaveInstanceState();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mSortOptions = savedInstanceState.getInt(SAVED_SORT_OPTIONS);
            mGridRecyclerView.getLayoutManager().onRestoreInstanceState(mRecyclerViewState);
        }
    }
    @Override
    public void onItemClick(View view, int position) {
    /*    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra(MovieGridFragment.MOVIE_OBJECT_TAG, mMoviesList.get(position));
        startActivity(intent);*/
    }

    private void updateMovieList(List<Movie> moviesList) {
        mMoviesList = moviesList;
        mIsLoading = false;
        movieAdapter.updateMovieList(mMoviesList);

        mProgressBar.setVisibility(View.INVISIBLE);
        if (mSortOptions == MainViewModel.FILTER_BY_MOST_POPULAR) {
            setActionBarTitle(getResources().getString(R.string.popular_movies));
        } else if (mSortOptions == MainViewModel.FILTER_BY_HIGHEST_RATED) {
            setActionBarTitle(getResources().getString(R.string.highest_rated_movies));
        } else if (mSortOptions == MainViewModel.FILTER_BY_FAVORITE) {
            setActionBarTitle(getResources().getString(R.string.favorite_movies));
        }
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

            //Observe changes in favorites db table
            mMainViewModel.getFavoriteMovies().observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    onLoadFavorites();
                }
            });

            //observe changes from Movie API calls
            mMainViewModel.getMovieList().observe(this, new Observer<DataWrapper<List<Movie>>>() {
                @Override
                public void onChanged(@Nullable DataWrapper<List<Movie>> movieCallData) {
                    mProgressBar.setVisibility(View.INVISIBLE);
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
            updateMovieList(mMainViewModel.getFavoriteMovies().getValue());
        }
    }

    private void onLoadMovies(DataWrapper<List<Movie>> movieCallData) {
        if (mSortOptions == MainViewModel.FILTER_BY_MOST_POPULAR ||
                mSortOptions == MainViewModel.FILTER_BY_HIGHEST_RATED) {
            if (movieCallData.getDeviceNoConnectivity()) {
                showNetworkErrorSnackbar();
            } else if (movieCallData.getData() != null) {
                updateMovieList(movieCallData.getData());
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
