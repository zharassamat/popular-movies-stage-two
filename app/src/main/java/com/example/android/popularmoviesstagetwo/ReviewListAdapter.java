package com.example.android.popularmoviesstagetwo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesstagetwo.model.ReviewModel;

import java.util.ArrayList;

/**
 * Created by zharas on 8/14/17.
 */

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder>{

    private final ListItemClickListener mOnClickListener;

    private ArrayList<ReviewModel> mReviewList;

    interface ListItemClickListener {
        void onListItemClick(int position);
    }

    public ReviewListAdapter(ArrayList<ReviewModel> reviewList, ListItemClickListener listener) {
        mReviewList = reviewList;
        mOnClickListener = listener;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.item_review;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem,parent,shouldAttachToParentImmediately);

        ReviewViewHolder viewHolder = new ReviewViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

        holder.reviewContent.setText(mReviewList.get(position).getContent());
        holder.reviewAuthor.setText(mReviewList.get(position).getAuthor());
    }

    @Override
    public int getItemCount() {
        if(mReviewList == null) {
            return 0;
        }
        return mReviewList.size();
    }

    public void setReviewData(ArrayList<ReviewModel> reviewData) {
        mReviewList = reviewData;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView reviewAuthor;
        TextView reviewContent;

        ImageView reviewUrl;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            reviewAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
            reviewContent = (TextView) itemView.findViewById(R.id.tv_review_content);

            reviewUrl = (ImageView) itemView.findViewById(R.id.iv_review_url);

            reviewUrl.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }
}
