package com.example.android.popularmoviesstagetwo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesstagetwo.model.MovieModel;
import com.example.android.popularmoviesstagetwo.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    private TextView mOriginalTitleTextView;
    private TextView mOverviewTextView;
    private TextView mRatingTextView;
    private TextView mReleaseDateTextView;

    private ImageView mPosterImageView;

    private MovieModel mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mOriginalTitleTextView = (TextView) findViewById(R.id.tv_movie_name);
        mOverviewTextView = (TextView) findViewById(R.id.tv_description);
        mRatingTextView = (TextView) findViewById(R.id.tv_rating);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_movie_date);

        mPosterImageView = (ImageView) findViewById(R.id.iv_movie_poster);

        Intent intent = getIntent();

        if(intent != null) {
            if(intent.getExtras() != null) {
               mMovie = intent.getExtras().getParcelable("movie");
            }
        }

        if(mMovie != null) {
            mOriginalTitleTextView.setText(mMovie.getOriginalTitle());
            mOverviewTextView.setText(mMovie.getOverview());
            mRatingTextView.setText(mMovie.getRating());
            mReleaseDateTextView.setText(mMovie.getReleaseDate());


            Picasso.with(this).load(NetworkUtils.MOVIE_IMAGE_URL + mMovie.getThumbnailUrl()).into(mPosterImageView);
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
}
