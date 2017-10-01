package com.example.android.popularmoviesstagetwo.utilities;

import android.database.Observable;

import com.example.android.popularmoviesstagetwo.model.MovieModel;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by zharas on 10/1/17.
 */

public interface MoviesAPI {

    @GET("movie/{type}")
    Call<String> loadMovie(@Path("type") String type);

    @GET("movie/{id}/videos")
    Call<String> loadMovieTrailers(@Path("id") String id);

}
