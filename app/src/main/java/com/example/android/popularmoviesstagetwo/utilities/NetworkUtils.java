package com.example.android.popularmoviesstagetwo.utilities;

/**
 * Created by zharas on 8/14/17.
 */

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    public static final String API_KEY = "YOUR API KEY";
    
    private static final String STATIC_MOVIE_URL =
            "http://api.themoviedb.org/3/";

    public static final String MOVIE_BASE_URL = STATIC_MOVIE_URL;

    public static final String MOVIE_IMAGE_URL = "http://image.tmdb.org/t/p/w342//";

    public final static String API_PARAM = "api_key";

    public static URL buildUrl(String locationQuery) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL + locationQuery).buildUpon()
                .appendQueryParameter(API_PARAM, API_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}