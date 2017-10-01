package com.example.android.popularmoviesstagetwo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviesstagetwo.data.MovieContract;
import com.example.android.popularmoviesstagetwo.model.MovieModel;
import com.example.android.popularmoviesstagetwo.utilities.MovieJsonUtils;

import org.json.JSONException;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieListAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String POPULAR_MOVIES = "popular";
    private static final String TOP_RATED_MOVIES = "top_rated";

    private static final int MOVIE_LOADER_ID = 0;

    private boolean isFavoritesSelected = false;

    private static final String BUNDLE_EXTRA_MOVIELIST_KEY = "movies";

    private RecyclerView mMovieListView;

    private MovieListAdapter mAdapter;

    private ArrayList<MovieModel> mMovieList = new ArrayList<>();

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovieListView = (RecyclerView) findViewById(R.id.rv_movie_list);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns());

        mMovieListView.setLayoutManager(layoutManager);

        mMovieListView.setHasFixedSize(true);

        mAdapter = new MovieListAdapter(mMovieList, this);

        mMovieListView.setAdapter(mAdapter);

        if (savedInstanceState == null || !savedInstanceState.containsKey(BUNDLE_EXTRA_MOVIELIST_KEY)) {

            if (isFavoritesSelected) {
                getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
            } else {
                loadMoviesData(POPULAR_MOVIES);
            }
        } else {
            mMovieList = savedInstanceState.getParcelableArrayList(BUNDLE_EXTRA_MOVIELIST_KEY);
            mAdapter.setMovieData(mMovieList);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(BUNDLE_EXTRA_MOVIELIST_KEY, mMovieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isFavoritesSelected) {
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_popular:
                loadMoviesData(POPULAR_MOVIES);
                isFavoritesSelected = false;

                return true;

            case R.id.action_top:
                loadMoviesData(TOP_RATED_MOVIES);
                isFavoritesSelected = false;
                return true;

            case R.id.action_favorites:
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
                isFavoritesSelected = true;
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMoviesData(String movieType) {
        showMovieDataView();

        mLoadingIndicator.setVisibility(View.VISIBLE);
        mAdapter.setMovieData(null);

        MoviesApp.getAPI().loadMovie(movieType).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body() != null) {
                    try {
                        ArrayList<MovieModel> jsonMovieData = MovieJsonUtils
                                .getMovieListFromJson(response.body());
                        mMovieList.clear();
                        mMovieList.addAll(jsonMovieData);
                        mAdapter.setMovieData(mMovieList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showErrorMessage();
                    }
                } else {
                    showErrorMessage();
                }

                mLoadingIndicator.setVisibility(View.INVISIBLE);

            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
               showErrorMessage();
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }
        });

    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    @Override
    public void onListItemClick(int position) {

        Intent intent = new Intent(this, DetailsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable("movie", mMovieList.get(position));
        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMovieListView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mMovieListView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mMovieData = null;

            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID);

                } catch (Exception e) {

                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        showMovieDataView();

        mAdapter.setMovieData(MovieJsonUtils.parseCursorToMovieArray(mMovieList,data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setMovieData(null);
    }

}
