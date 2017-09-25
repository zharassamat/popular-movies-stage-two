package com.example.android.popularmoviesstagetwo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zharas on 8/14/17.
 */

public class MovieModel implements Parcelable {

    private int movieId;

    private String title;
    private String originalTitle;
    private String posterUrl;
    private String thumbnailUrl;
    private String overview;
    private String rating;
    private String releaseDate;
    private String duration;

    public MovieModel(Parcel in) {
        movieId = in.readInt();
        title = in.readString();
        originalTitle = in.readString();
        posterUrl = in.readString();
        thumbnailUrl = in.readString();
        overview = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
        duration = in.readString();
    }

    public MovieModel() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(title);
        dest.writeString(originalTitle);
        dest.writeString(posterUrl);
        dest.writeString(thumbnailUrl);
        dest.writeString(overview);
        dest.writeString(rating);
        dest.writeString(releaseDate);
        dest.writeString(duration);
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

}
