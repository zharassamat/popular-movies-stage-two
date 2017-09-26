package com.example.android.popularmoviesstagetwo.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by zharas on 9/26/17.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmoviesstagetwo";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITE = "favorite";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE)
                .build();

        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_MOVIE_NAME = "movie_name";

        public static final String COLUMN_MOVIE_POSTER = "movie_poster";

        public static final String COLUMN_MOVIE_THUMBNAIL = "movie_thumbnail";

        public static final String COLUMN_MOVIE_SYNOPSIS = "movie_synopsis";

        public static final String COLUMN_MOVIE_RATING = "movie_rating";

        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";

        public static Uri buildMovieUriWithId(long movieId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(movieId))
                    .build();
        }


        public static String getSqlSelectForTodayOnwards() {
          return MovieEntry.COLUMN_MOVIE_ID;
        }
    }

}
