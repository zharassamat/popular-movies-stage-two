package com.example.android.popularmoviesstagetwo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviesstagetwo.model.MovieModel;
import com.example.android.popularmoviesstagetwo.utilities.MovieJsonUtils;
import com.example.android.popularmoviesstagetwo.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieListAdapter.ListItemClickListener {

    private static final String POPULAR_MOVIES = "movie/popular";
    private static final String TOP_RATED_MOVIES = "movie/top_rated";

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

        GridLayoutManager layoutManager = new GridLayoutManager(this,2);

        mMovieListView.setLayoutManager(layoutManager);

        mMovieListView.setHasFixedSize(true);

        mAdapter = new MovieListAdapter(mMovieList, this);

        mMovieListView.setAdapter(mAdapter);

        if (savedInstanceState == null || !savedInstanceState.containsKey(BUNDLE_EXTRA_MOVIELIST_KEY)) {
            loadMoviesData(POPULAR_MOVIES);
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
                return true;

            case R.id.action_top:
                loadMoviesData(TOP_RATED_MOVIES);
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
