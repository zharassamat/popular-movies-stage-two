package com.example.android.popularmoviesstagetwo;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviesstagetwo.data.MovieContract;
import com.example.android.popularmoviesstagetwo.model.MovieModel;
import com.example.android.popularmoviesstagetwo.utilities.MovieJsonUtils;
import com.example.android.popularmoviesstagetwo.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieListAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String POPULAR_MOVIES = "movie/popular";
    private static final String TOP_RATED_MOVIES = "movie/top_rated";

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

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

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

        new FetchMovieTask().execute(movieType);
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

        mAdapter.setMovieData(convertCursorToList(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setMovieData(null);
    }

    private ArrayList<MovieModel> convertCursorToList(Cursor data) {
        mMovieList.clear();
        MovieModel movie;

        try {
            while (data.moveToNext()) {
                int idIndex = data.getColumnIndex(MovieContract.MovieEntry._ID);
                int movieIdIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                int movieNameIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_NAME);
                int moviePosterIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
                int movieRatingIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RATING);
                int movieReleaseDateIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE);
                int movieSynopsisIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS);
                int movieThumbnailIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_THUMBNAIL);

                int id = data.getInt(idIndex);
                int movieId = data.getInt(movieIdIndex);
                String movieName = data.getString(movieNameIndex);
                String moviePoster = data.getString(moviePosterIndex);
                String movieRating = data.getString(movieRatingIndex);
                String movieReleaseDate = data.getString(movieReleaseDateIndex);
                String movieSysopsis = data.getString(movieSynopsisIndex);
                String movieThumbnail = data.getString(movieThumbnailIndex);

                movie = new MovieModel();

                movie.setMovieId(movieId);
                movie.setOriginalTitle(movieName);
                movie.setPosterUrl(moviePoster);
                movie.setRating(movieRating);
                movie.setReleaseDate(movieReleaseDate);
                movie.setOverview(movieSysopsis);
                movie.setThumbnailUrl(movieThumbnail);

                mMovieList.add(movie);
            }
        } finally {
            data.close();
        }

        return mMovieList;
    }

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MovieModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mAdapter.setMovieData(null);
        }

        @Override
        protected ArrayList<MovieModel> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String location = params[0];
            URL moviesRequestUrl = NetworkUtils.buildUrl(location);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                ArrayList<MovieModel> jsonMovieData = MovieJsonUtils
                        .getMovieListFromJson(jsonMovieResponse);

                return jsonMovieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MovieModel> moviesData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (moviesData != null) {
                showMovieDataView();

                mMovieList.clear();
                mMovieList.addAll(moviesData);
                mAdapter.setMovieData(mMovieList);

            } else {
                showErrorMessage();
            }
        }
    }
}
