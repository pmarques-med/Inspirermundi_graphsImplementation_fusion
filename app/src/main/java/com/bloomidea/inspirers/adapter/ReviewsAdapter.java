package com.bloomidea.inspirers.adapter;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.customViews.ProgressBar;
import com.bloomidea.inspirers.model.Review;
import com.bloomidea.inspirers.utils.Utils;
import com.github.ornolfr.ratingview.RatingView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by michellobato on 06/04/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_REVIEW_CREATE = 101;
    private static final int TYPE_REVIEW = 102;

    private Activity context;
    private ArrayList<Review> reviewsList;
    private boolean isActiveUser;
    private int totalReviews;

    private ReviewAdapterListener listener;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");

    private boolean clearInfo = false;

    public ReviewsAdapter(Activity context, ArrayList<Review> reviewsList, boolean isActiveUser, int totalReviews, ReviewAdapterListener listener) {
        this.context = context;
        this.reviewsList = reviewsList;
        this.isActiveUser = isActiveUser;
        this.totalReviews = totalReviews;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        RecyclerView.ViewHolder viewHolder;

        if (viewType == TYPE_REVIEW_CREATE) {
            view = LayoutInflater.from(context).inflate(R.layout.item_create_review, parent, false);
            viewHolder = new ViewHolderCreateReview(view);
        } else{
            //TYPE_REVIEW
            view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
            viewHolder = new ViewHolderReview(view);
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderCreateReview){
            ViewHolderCreateReview aux = (ViewHolderCreateReview) holder;

            if(isActiveUser){
                aux.review_box.setVisibility(View.GONE);
            }else{
                aux.review_box.setVisibility(View.VISIBLE);
            }

            aux.total_eval_textView.setText(context.getString(R.string.evaluations_with_total,""+totalReviews));

            if((reviewsList == null || reviewsList.isEmpty()) && totalReviews==0){
                aux.no_info_layout.setVisibility(View.VISIBLE);
                aux.no_info_textView.setText(R.string.no_reviews);
            }else{
                aux.no_info_layout.setVisibility(View.GONE);
            }

            aux.send_btn.setTag(R.id.tag_viewholder,aux);
            aux.send_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewHolderCreateReview auxC = (ViewHolderCreateReview) view.getTag(R.id.tag_viewholder);

                    float rating = auxC.ratingBar.getRating();
                    String review = auxC.review_editText.getText().toString();

                    listener.createReview(rating,review);
                }
            });

            if(clearInfo){
                clearInfo=false;
                aux.ratingBar.setRating(0);
                aux.review_editText.setText("");
            }
        }else{
            ViewHolderReview aux = (ViewHolderReview) holder;
            Review review = reviewsList.get(position-1);

            Utils.loadImageView(context, aux.profile_image, aux.profile_image_progress, review.getPictureUrl(), review.getPictureImg(),R.drawable.default_avatar, null);

            aux.user_name_textView.setText(review.getUserName());
            aux.evaluation_ratingBar.setRating(review.getReviewTreated5Stars().floatValue());
            aux.rating_text_textView.setText(review.getReviewText());
            aux.date_textView.setText(dateFormat.format(review.getDate().getTime()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position==0)?TYPE_REVIEW_CREATE:TYPE_REVIEW;
    }

    @Override
    public int getItemCount() {
        return reviewsList.size() + 1;
    }

    public void addAll(ArrayList<Review> results) {
        reviewsList.addAll(results);
        notifyDataSetChanged();
    }

    public void reviewCreated(Review newReview) {
        reviewsList.add(0,newReview);
        clearInfo = true;
        totalReviews++;
        notifyDataSetChanged();
    }

    public static class ViewHolderCreateReview extends RecyclerView.ViewHolder {
        public RatingBar ratingBar;
        public EditText review_editText;
        public View send_btn;
        public View review_box;
        public TextView total_eval_textView;
        public View no_info_layout;
        public TextView no_info_textView;

        public ViewHolderCreateReview(View v) {
            super(v);

            ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
            review_editText = (EditText)v.findViewById(R.id.review_editText);
            send_btn = v.findViewById(R.id.send_btn);
            review_box = v.findViewById(R.id.review_box);
            total_eval_textView = (TextView) v.findViewById(R.id.total_eval_textView);
            no_info_layout = v.findViewById(R.id.no_info_layout);
            no_info_textView = (TextView) v.findViewById(R.id.no_info_textView);
        }
    }

    public static class ViewHolderReview extends RecyclerView.ViewHolder {
        public ImageView profile_image;
        public ProgressBar profile_image_progress;
        public TextView user_name_textView;
        public RatingView evaluation_ratingBar;
        public TextView rating_text_textView;
        public TextView date_textView;

        public ViewHolderReview(View v) {
            super(v);

            profile_image = (ImageView) v.findViewById(R.id.profile_image);
            profile_image_progress = (ProgressBar) v.findViewById(R.id.profile_image_progress);
            user_name_textView = (TextView) v.findViewById(R.id.user_name_textView);
            evaluation_ratingBar = (RatingView)v.findViewById(R.id.evaluation_ratingBar);
            rating_text_textView = (TextView) v.findViewById(R.id.rating_text_textView);
            date_textView = (TextView) v.findViewById(R.id.date_textView);
        }
    }

    public static interface ReviewAdapterListener{
        void createReview(float rating, String review);
    }
}
