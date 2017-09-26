package com.example.android.popularmoviesstagetwo.utilities;

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

}
