package com.bloomidea.inspirers.adapter;

import android.app.Activity;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.UserProfileActivity;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.model.Review;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.utils.Utils;

import java.util.ArrayList;

/**
 * Created by michellobato on 04/04/17.
 */

public class LeaderboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_MY_RANKING = 101;
    private static final int TYPE_USER = 102;

    private Activity context;
    private ArrayList<User> leaderBoard;
    private int myPosition;

    public LeaderboardAdapter(Activity context, ArrayList<User> leaderBoard, int myPosition) {
        this.context = context;
        this.leaderBoard = leaderBoard;
        this.myPosition = myPosition;
    }

    @Override
    public int getItemViewType(int position) {
        return position==0?TYPE_MY_RANKING:TYPE_USER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        RecyclerView.ViewHolder viewHolder;

        if (viewType == TYPE_MY_RANKING) {
            view = LayoutInflater.from(context).inflate(R.layout.item_my_position, parent, false);
            viewHolder = new ViewHolderMyRating(view);
        } else{
            //TYPE_USER
            view = LayoutInflater.from(context).inflate(R.layout.item_user_leaderboard, parent, false);
            viewHolder = new ViewHolderUserInfoLeaderBoard(view);
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderMyRating){
            ((ViewHolderMyRating) holder).my_position_textView.setText(myPosition+".º");
        }else{
            //ViewHolderUserInfo
            ViewHolderUserInfoLeaderBoard auxHolder = (ViewHolderUserInfoLeaderBoard) holder;

            User auxUser = leaderBoard.get(position-1);

            if(auxUser.isConnected() || auxUser.getUid().equals(AppController.getmInstance().getActiveUser().getUid())) {
                auxHolder.item_box.setBackgroundColor(context.getResources().getColor(R.color.connected_item_background));
            }else{
                auxHolder.item_box.setBackgroundColor(context.getResources().getColor(R.color.normal_item_background));
            }

            auxHolder.position_textView.setText(position+".");

            auxHolder.setUserInfo(context, auxUser);

            auxHolder.item_box.setTag(R.id.tag_position, position - 1);
            if(auxUser.getUid().equals(AppController.getmInstance().getActiveUser().getUid())){
                auxHolder.item_box.setOnClickListener(null);
            }else {
                auxHolder.item_box.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        User auxUserOpen = leaderBoard.get((int) view.getTag(R.id.tag_position));

                        Intent i = new Intent(context, UserProfileActivity.class);
                        i.putExtra(UserProfileActivity.EXTRA_USER_PROFILE, auxUserOpen);

                        Utils.openIntent(context, i, R.anim.slide_in_left, R.anim.slide_out_left);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return leaderBoard.size() + 1;
    }

    public void updateReviewsInfo(String uid, Review newReview) {
        for(User aux : leaderBoard){
            if(aux.getUid().equals(uid)){
                aux.updateReviewsInfo(newReview);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void addUnreadMessage(String uid, int value) {
        for(User aux : leaderBoard){
            if(aux.getUid().equals(uid)){
                aux.setUnreadMessages(aux.getUnreadMessages() + value);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void resetCounter(String uid) {
        for(User aux : leaderBoard){
            if(aux.getUid().equals(uid)){
                aux.setUnreadMessages(0);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public static class ViewHolderMyRating extends RecyclerView.ViewHolder {
        public TextView my_position_textView;

        public ViewHolderMyRating(View v) {
            super(v);

            my_position_textView = (TextView) v.findViewById(R.id.my_position_textView);
        }
    }

    public static class ViewHolderUserInfoLeaderBoard extends ViewHolderUserInfo {
        public View item_box;
        public TextView position_textView;

        public ViewHolderUserInfoLeaderBoard(View v) {
            super(v);

            item_box = v.findViewById(R.id.item_box);
            position_textView = (TextView) v.findViewById(R.id.position_textView);
        }
    }
}
