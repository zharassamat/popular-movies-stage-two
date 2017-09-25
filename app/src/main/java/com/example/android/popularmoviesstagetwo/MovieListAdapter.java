package com.example.android.popularmoviesstagetwo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesstagetwo.model.MovieModel;
import com.example.android.popularmoviesstagetwo.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by zharas on 8/14/17.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>{

    private final ListItemClickListener mOnClickListener;

    private ArrayList<MovieModel> mMovieList;

    interface ListItemClickListener {
        void onListItemClick(int position);
    }

    public MovieListAdapter(ArrayList<MovieModel> movieList, ListItemClickListener listener) {
        mMovieList = movieList;
        mOnClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_movie_poster;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem,parent,shouldAttachToParentImmediately);

        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Picasso.with(holder.moviePoster.getContext()).load(NetworkUtils.MOVIE_IMAGE_URL + mMovieList.get(position).getPosterUrl()).into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        if(mMovieList == null) {
            return 0;
        }
        return mMovieList.size();
    }

    public void setMovieData(ArrayList<MovieModel> movieData) {
        mMovieList = movieData;
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView moviePoster;

        public MovieViewHolder(View itemView) {
            super(itemView);

            moviePoster = (ImageView) itemView.findViewById(R.id.iv_poster);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }
}
