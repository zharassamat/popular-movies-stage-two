package com.example.android.popularmoviesstagetwo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviesstagetwo.data.MovieContract;
import com.example.android.popularmoviesstagetwo.model.MovieModel;
import com.example.android.popularmoviesstagetwo.model.VideoModel;
import com.example.android.popularmoviesstagetwo.utilities.MovieJsonUtils;
import com.example.android.popularmoviesstagetwo.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private TextView mOriginalTitleTextView;
    private TextView mOverviewTextView;
    private TextView mRatingTextView;
    private TextView mReleaseDateTextView;
    private TextView mVideoErrorMessageTextView;

    private ImageView mPosterImageView;

    private ImageButton mFavoriteButton;

    private MovieModel mMovie;

    private ProgressBar mLoadingIndicator;

    private LinearLayout dynamicLayout;

    private LayoutInflater vi;

    private View customVideoView;

    private Button btnShowReviews;

    private Uri mUri;

    private boolean cursorHasValidData = false;

    // id of movie row in database
    private int indexId = -1;

    private String movieTrailerUrl = null;

    private ArrayList<VideoModel> mVideoModels = new ArrayList<>();

    private static final String BUNDLE_EXTRA_MOVIE_KEY = "movie_key";
    private static final String BUNDLE_EXTRA_VIDEO_LIST_KEY = "video_list_key";

    public static final String[] MOVIE_DETAIL_PROJECTION = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_NAME,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MovieContract.MovieEntry.COLUMN_MOVIE_RATING,
            MovieContract.MovieEntry.COLUMN_MOVIE_THUMBNAIL,
            MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE
    };

    public static final int INDEX_ID = 0;
    public static final int INDEX_MOVIE_ID = 1;
    public static final int INDEX_MOVIE_NAME = 2;
    public static final int INDEX_MOVIE_POSTER = 3;
    public static final int INDEX_MOVIE_RATING = 4;
    public static final int INDEX_MOVIE_THUMBNAIL = 5;
    public static final int INDEX_MOVIE_SYNOPSIS = 6;
    public static final int INDEX_MOVIE_RELEASE_DATE = 7;

    private static final int ID_DETAIL_LOADER = 353;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setTitle(getString(R.string.details));

        mOriginalTitleTextView = (TextView) findViewById(R.id.tv_movie_name);
        mOverviewTextView = (TextView) findViewById(R.id.tv_description);
        mRatingTextView = (TextView) findViewById(R.id.tv_rating);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_movie_date);
        mVideoErrorMessageTextView = (TextView) findViewById(R.id.tv_video_error);

        mFavoriteButton = (ImageButton) findViewById(R.id.iv_favorite);

        btnShowReviews = (Button) findViewById(R.id.btn_load_reviews);

        mPosterImageView = (ImageView) findViewById(R.id.iv_movie_poster);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        dynamicLayout = (LinearLayout) findViewById(R.id.dynamic_video_layout);

        if (savedInstanceState == null || !savedInstanceState.containsKey(BUNDLE_EXTRA_MOVIE_KEY)) {
            Intent intent = getIntent();

            if (intent != null) {
                if (intent.getExtras() != null) {
                    mMovie = intent.getExtras().getParcelable("movie");
                }
            }

        } else {
            mMovie = savedInstanceState.getParcelable(BUNDLE_EXTRA_MOVIE_KEY);
        }

        if (mMovie != null) {
            mOriginalTitleTextView.setText(mMovie.getOriginalTitle());
            mOverviewTextView.setText(mMovie.getOverview());
            mRatingTextView.setText(mMovie.getRating());
            mReleaseDateTextView.setText(mMovie.getReleaseDate());


            Picasso.with(this).load(NetworkUtils.MOVIE_IMAGE_URL + mMovie.getThumbnailUrl()).into(mPosterImageView);

            if (savedInstanceState == null || !savedInstanceState.containsKey(BUNDLE_EXTRA_VIDEO_LIST_KEY)) {
                loadVideosData(mMovie.getMovieId() + "");
            } else {
                mVideoModels = savedInstanceState.getParcelableArrayList(BUNDLE_EXTRA_VIDEO_LIST_KEY);
                showVideoDataView(mVideoModels);
            }

            btnShowReviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailsActivity.this, ReviewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("movieId", mMovie.getMovieId() + "");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cursorHasValidData) {
                    deleteFromFavorites(indexId);
                } else {
                    addToFavorites();
                }

            }
        });

        mUri = MovieContract.MovieEntry.buildMovieUriWithId(mMovie.getMovieId());

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BUNDLE_EXTRA_MOVIE_KEY, mMovie);
        outState.putParcelableArrayList(BUNDLE_EXTRA_VIDEO_LIST_KEY,mVideoModels);
        super.onSaveInstanceState(outState);
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(movieTrailerUrl)
                .getIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }
        return shareIntent;
    }

    private void addToFavorites() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getMovieId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME, mMovie.getOriginalTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, mMovie.getPosterUrl());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, mMovie.getRating());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, mMovie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, mMovie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_THUMBNAIL, mMovie.getThumbnailUrl());

        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        if (uri != null) {
            mFavoriteButton.setImageResource(R.mipmap.ic_is_favorite);
        }
    }

    private void deleteFromFavorites(int id) {

        String stringId = Integer.toString(id);

        Uri uri = MovieContract.MovieEntry.CONTENT_URI;

        uri = uri.buildUpon().appendPath(stringId).build();

        int deleted = getContentResolver().delete(uri, null, null);

        if (deleted != 0) {
            mFavoriteButton.setImageResource(R.mipmap.ic_is_not_favorite);
        }
        //getSupportLoaderManager().restartLoader(ID_DETAIL_LOADER, null, DetailsActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;

            case R.id.action_share:
                Intent shareIntent = createShareMovieIntent();
                startActivity(shareIntent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void loadVideosData(String videoId) {

        MoviesApp.getAPI().loadMovieTrailers(videoId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body() != null) {
                    try {
                        mVideoModels.clear();
                        ArrayList<VideoModel> jsonVideoData = MovieJsonUtils
                                .getVideoListFromJson(response.body());
                        if (jsonVideoData != null && !jsonVideoData.isEmpty()) {
                            mVideoModels.addAll(jsonVideoData);
                            showVideoDataView(mVideoModels);
                        } else {
                            showErrorMessage();
                        }
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

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {

            case ID_DETAIL_LOADER:

                return new CursorLoader(this,
                        mUri,
                        MOVIE_DETAIL_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            mFavoriteButton.setImageResource(R.mipmap.ic_is_not_favorite);
            return;
        }

        indexId = data.getInt(INDEX_ID);

        mFavoriteButton.setImageResource(R.mipmap.ic_is_favorite);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void showVideoDataView(ArrayList<VideoModel> videoModels) {

        vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView dLabel;
        for (final VideoModel mVideo : videoModels) {
            customVideoView = vi.inflate(R.layout.item_video, null);
            dLabel = (TextView) customVideoView.findViewById(R.id.tv_video_name);
            dLabel.setText(mVideo.getName());

            if(movieTrailerUrl == null) {
                movieTrailerUrl = "http://www.youtube.com/watch?v=" + mVideo.getKey();
            }

            customVideoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    watchYoutubeVideo(mVideo.getKey());
                }
            });

            dynamicLayout.addView(customVideoView);
        }

        mVideoErrorMessageTextView.setVisibility(View.GONE);
    }

    private void showErrorMessage() {
        mVideoErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    public void watchYoutubeVideo(String id) {
        Uri webpage = Uri.parse("http://www.youtube.com/watch?v=" + id);

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
