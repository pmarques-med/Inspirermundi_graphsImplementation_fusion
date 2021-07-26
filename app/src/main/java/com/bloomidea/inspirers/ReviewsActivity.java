package com.bloomidea.inspirers;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.bloomidea.inspirers.adapter.ReviewsAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.customViews.ProgressBar;
import com.bloomidea.inspirers.listener.JSONArrayListener;
import com.bloomidea.inspirers.listener.OkErrorListener;
import com.bloomidea.inspirers.model.Review;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.utils.APIInspirers;
import com.bloomidea.inspirers.utils.InspirersJSONParser;
import com.bloomidea.inspirers.utils.Utils;

import org.json.JSONArray;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ReviewsActivity extends MyActiveActivity {
    public static final String EXTRA_ACTIVE_USER = "EXTRA_ACTIVE_USER";
    public static final String EXTRA_USER_NAME = "EXTRA_USER_NAME";
    public static final String EXTRA_TOTAL_REVIEWS = "EXTRA_TOTAL_REVIEWS";
    public static final String EXTRA_REVIEWS_NID = "EXTRA_REVIEWS_NID";
    public static final String EXTRA_USER_UID = "EXTRA_USER_UID";

    private boolean isActiveUser;
    private String userName;
    private int totalReviews;
    private String userUid;
    private String reviewsNid;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ReviewsAdapter reviewsAdapter;


    private ProgressBar progressBar;
    private boolean hasMore = true;
    private boolean loading = false;
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        isActiveUser = getIntent().getBooleanExtra(EXTRA_ACTIVE_USER,false);
        userName = getIntent().getStringExtra(EXTRA_USER_NAME);
        totalReviews = getIntent().getIntExtra(EXTRA_TOTAL_REVIEWS,0);
        reviewsNid = getIntent().getStringExtra(EXTRA_REVIEWS_NID);
        userUid = getIntent().getStringExtra(EXTRA_USER_UID);

        configTopMenu();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        reviewsAdapter = new ReviewsAdapter(this, new ArrayList<Review>(), isActiveUser, totalReviews, new ReviewsAdapter.ReviewAdapterListener() {
            @Override
            public void createReview(float rating, String review) {
                if(Utils.isOnline(ReviewsActivity.this,true,getSupportFragmentManager())) {
                    if (rating <= 0) {
                        Toast.makeText(ReviewsActivity.this, R.string.rating_empty, Toast.LENGTH_SHORT).show();
                    } else if (review.isEmpty()) {
                        Toast.makeText(ReviewsActivity.this, R.string.review_empty, Toast.LENGTH_SHORT).show();
                    } else {
                        User activeUser = AppController.getmInstance().getActiveUser();

                        final Review auxR = new Review(activeUser.getPicture(), "",
                                activeUser.getUserName(),
                                activeUser.getUid(),
                                review,
                                new BigDecimal((rating*100)/5).setScale(2,BigDecimal.ROUND_HALF_UP),
                                new GregorianCalendar());

                        AppController.getmInstance().createReview(userUid, reviewsNid, auxR, ReviewsActivity.this, getSupportFragmentManager(), new OkErrorListener() {
                            @Override
                            public void ok() {
                                Utils.createNavigationAction(getString(R.string.send_evaluation));

                                totalReviews++;
                                reviewsAdapter.reviewCreated(auxR);
                            }

                            @Override
                            public void error() {
                                Toast.makeText(ReviewsActivity.this, R.string.problem_send_to_server, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(reviewsAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!loading) {
                    if (linearLayoutManager.findLastVisibleItemPosition() == reviewsAdapter.getItemCount() - 1) {
                        Log.d("LoadMore", "Scroll");
                        loadInfo(true);
                    }
                }
            }
        });

        loadInfo(false);
    }

    private void configTopMenu() {
        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ((TextView) findViewById(R.id.title_textView)).setText(R.string.review);
        ((TextView) findViewById(R.id.bottom_textView)).setText(userName);
    }

    private void loadInfo(boolean loadMore) {
        if(!loading && hasMore) {
            loading=true;

            progressBar.setVisibility(View.VISIBLE);

            if(loadMore){
                page+=1;
            }

            APIInspirers.getReviews(reviewsNid, page, new JSONArrayListener() {
                @Override
                public void onResponse(JSONArray response) {
                    treatResponse(response);

                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    treatError(error);
                }
            });
        }
    }

    private void treatResponse(JSONArray response) {
        Log.d("RESPOSTA",response.toString());

        progressBar.setVisibility(View.GONE);
        ArrayList<Review> results = InspirersJSONParser.parseListReviews(response);

        hasMore = !results.isEmpty();

        reviewsAdapter.addAll(results);

        loading = false;
    }

    private void treatError(VolleyError error) {
        progressBar.setVisibility(View.GONE);

        Log.d("loadInfo(ERROR)", error.toString());

        page--;
        loading = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
