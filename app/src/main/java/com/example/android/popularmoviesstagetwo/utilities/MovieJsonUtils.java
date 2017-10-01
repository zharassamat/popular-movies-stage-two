package com.example.android.popularmoviesstagetwo.utilities;

import android.database.Cursor;

import com.example.android.popularmoviesstagetwo.data.MovieContract;
import com.example.android.popularmoviesstagetwo.model.MovieModel;
import com.example.android.popularmoviesstagetwo.model.ReviewModel;
import com.example.android.popularmoviesstagetwo.model.VideoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zharas on 8/14/17.
 */

public class MovieJsonUtils {

    public static ArrayList<MovieModel> getMovieListFromJson(String movieJsonStr)
            throws JSONException {

        JSONObject jsonObject = new JSONObject(movieJsonStr);

        JSONArray jsonArray = jsonObject.getJSONArray("results");

        ArrayList<MovieModel> mMovieList = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject movieObject = jsonArray.getJSONObject(i);

            MovieModel mMovie = new MovieModel();

            mMovie.setMovieId(movieObject.getInt("id"));
            mMovie.setTitle(movieObject.getString("title"));
            mMovie.setPosterUrl(movieObject.getString("poster_path"));
            mMovie.setOriginalTitle(movieObject.getString("original_title"));
            mMovie.setOverview(movieObject.getString("overview"));
            mMovie.setRating(movieObject.getString("vote_average"));
            mMovie.setReleaseDate(movieObject.getString("release_date"));
            mMovie.setThumbnailUrl(movieObject.getString("backdrop_path"));

            mMovieList.add(mMovie);
        }

        return mMovieList;
    }

    public static ArrayList<VideoModel> getVideoListFromJson(String videoJsonStr)
            throws JSONException {

        JSONObject jsonObject = new JSONObject(videoJsonStr);

        JSONArray jsonArray = jsonObject.getJSONArray("results");

        ArrayList<VideoModel> mVideoList = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject videoObject = jsonArray.getJSONObject(i);

            VideoModel mVideo = new VideoModel();

            mVideo.setId(videoObject.getString("id"));
            mVideo.setKey(videoObject.getString("key"));
            mVideo.setName(videoObject.getString("name"));
            mVideo.setSite(videoObject.getString("site"));
            mVideoList.add(mVideo);
        }

        return mVideoList;
    }

    public static ArrayList<ReviewModel> getReviewListFromJson(String reviewJsonStr)
            throws JSONException {

        JSONObject jsonObject = new JSONObject(reviewJsonStr);

        JSONArray jsonArray = jsonObject.getJSONArray("results");

        ArrayList<ReviewModel> mReviewList = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject reviewObject = jsonArray.getJSONObject(i);

            ReviewModel mReview = new ReviewModel();

            mReview.setId(reviewObject.getString("id"));
            mReview.setAuthor(reviewObject.getString("author"));
            mReview.setContent(reviewObject.getString("content"));
            mReview.setUrl(reviewObject.getString("url"));
            mReviewList.add(mReview);
        }

        return mReviewList;
    }

    public static ArrayList<MovieModel> parseCursorToMovieArray(ArrayList<MovieModel> movieList, Cursor data){
        movieList.clear();
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

                movieList.add(movie);
            }
        } finally {
            data.close();
        }

        return movieList;
    }

}
