package com.example.android.popularmoviesstagetwo;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviesstagetwo.model.ReviewModel;
import com.example.android.popularmoviesstagetwo.utilities.MovieJsonUtils;
import com.example.android.popularmoviesstagetwo.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity implements ReviewListAdapter.ListItemClickListener {

    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;

    private RecyclerView mReviewListView;

    private ReviewListAdapter mAdapter;

    private String movieId = "-1";

    private ArrayList<ReviewModel> mReviewList = new ArrayList<>();

    private static final String BUNDLE_EXTRA_REVIEWLIST_KEY = "review";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        setTitle(getString(R.string.reviews));

        mReviewListView = (RecyclerView) findViewById(R.id.rv_review_list);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        Intent intent = getIntent();

        if (intent != null) {
            if (intent.getExtras() != null) {
                movieId = intent.getExtras().getString("movieId");

            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mReviewListView.setLayoutManager(layoutManager);

        mReviewListView.setHasFixedSize(true);

        mAdapter = new ReviewListAdapter(mReviewList, this);

        mReviewListView.setAdapter(mAdapter);

        if (savedInstanceState == null || !savedInstanceState.containsKey(BUNDLE_EXTRA_REVIEWLIST_KEY)) {
            loadReviewData("movie/" + movieId + "/reviews");
        } else {
            mReviewList = savedInstanceState.getParcelableArrayList(BUNDLE_EXTRA_REVIEWLIST_KEY);
            mAdapter.setReviewData(mReviewList);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(BUNDLE_EXTRA_REVIEWLIST_KEY, mReviewList);
        super.onSaveInstanceState(outState);
    }

    private void loadReviewData(String reviewUrl) {
        //showMovieDataView();

        new FetchReviewsTask().execute(reviewUrl);
    }

    private void showReviewDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mReviewListView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mReviewListView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListItemClick(int position) {

        Uri webpage = Uri.parse(mReviewList.get(position).getUrl());

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class FetchReviewsTask extends AsyncTask<String, Void, ArrayList<ReviewModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);

        }

        @Override
        protected ArrayList<ReviewModel> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String location = params[0];
            URL reviewRequestUrl = NetworkUtils.buildUrl(location);

            try {
                String jsonReviewResponse = NetworkUtils
                        .getResponseFromHttpUrl(reviewRequestUrl);

                ArrayList<ReviewModel> jsonReviewData = MovieJsonUtils
                        .getReviewListFromJson(jsonReviewResponse);

                return jsonReviewData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<ReviewModel> reviewData) {
            mLoadingIndicator.setVisibility(View.GONE);
            if (reviewData != null && !reviewData.isEmpty()) {
                showReviewDataView();

                mReviewList.clear();
                mReviewList.addAll(reviewData);
                mAdapter.setReviewData(mReviewList);
            } else {
                showErrorMessage();
            }


        }
    }

}
