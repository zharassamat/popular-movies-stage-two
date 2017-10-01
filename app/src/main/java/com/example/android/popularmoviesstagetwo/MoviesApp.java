package com.example.android.popularmoviesstagetwo;

import android.app.Application;
import android.content.Context;

import com.example.android.popularmoviesstagetwo.utilities.MoviesAPI;
import com.example.android.popularmoviesstagetwo.utilities.NetworkUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by zharas on 10/1/17.
 */

public class MoviesApp extends Application{

    private static MoviesAPI api;

    public static MoviesApp get(Context context) {
        return (MoviesApp) context.getApplicationContext();
    }

    public static MoviesAPI getAPI() {
        if (api == null) {
            // We need client for debugging
             //HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
             //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
             //OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            OkHttpClient.Builder httpClient =
                    new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    HttpUrl originalHttpUrl = original.url();

                    HttpUrl url = originalHttpUrl.newBuilder()
                            .addQueryParameter(NetworkUtils.API_PARAM, NetworkUtils.API_KEY)
                            .build();

                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .url(url);

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });



            api = new Retrofit.Builder()
                    .baseUrl(NetworkUtils.MOVIE_BASE_URL)
                           //.client(client)
                    .client(httpClient.build())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build().create(MoviesAPI.class);
        }
        return api;
    }

}
