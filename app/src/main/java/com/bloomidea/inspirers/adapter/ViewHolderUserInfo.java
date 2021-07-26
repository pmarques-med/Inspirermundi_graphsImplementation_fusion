package com.bloomidea.inspirers.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.UserProfileActivity;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.utils.Utils;
import com.github.lzyzsd.circleprogress.DonutProgress;

/**
 * Created by michellobato on 06/04/17.
 */

public class ViewHolderUserInfo extends RecyclerView.ViewHolder {
    private ImageView profile_image;
    private DonutProgress donut_progress;
    private ProgressBar profile_image_progress;
    private TextView user_name_textView;
    private ImageView country_flag_imageView;
    private TextView country_name_textView;
    private TextView points_textView;
    private TextView level_textView;

    private User user;
    private Activity context;

    public ViewHolderUserInfo(View v) {
        super(v);

        profile_image = (ImageView) v.findViewById(R.id.profile_image);
        donut_progress = (DonutProgress) v.findViewById(R.id.donut_progress);
        profile_image_progress = (ProgressBar) v.findViewById(R.id.profile_image_progress);
        user_name_textView = (TextView) v.findViewById(R.id.user_name_textView);
        country_flag_imageView = (ImageView) v.findViewById(R.id.country_flag_imageView);
        country_name_textView = (TextView) v.findViewById(R.id.country_name_textView);
        points_textView = (TextView) v.findViewById(R.id.points_textView);
        level_textView = (TextView) v.findViewById(R.id.level_textView);
    }

    public void setUserInfo(Activity context, User user){
        this.user = user;
        this.context = context;

        Utils.loadImageView(context, this.profile_image, this.profile_image_progress, user.getPictureUrl(), user.getPicture(), R.drawable.default_avatar, null);

        this.donut_progress.setFinishedStrokeColor(Color.parseColor(user.getStatsEverColor()));
        this.donut_progress.setProgress(user.getStatsEver().floatValue());

        this.user_name_textView.setText(Html.fromHtml(user.getUserName()));

        Utils.loadImageView(context, this.country_flag_imageView, null, user.getCountryFlagUrl(), user.getCountryFlag(), R.drawable.default_country, null);

        if(user.getCountryName()!=null && !user.getCountryName().isEmpty()) {
            this.country_name_textView.setText(user.getCountryName());
        }else{
            this.country_name_textView.setText(R.string.default_country_name);
        }
        this.points_textView.setText(""+user.getUserPoints());
        this.level_textView.setText(context.getResources().getString(R.string.level_text, user.getLevel()));

        if(user.getUid().equals(AppController.getmInstance().getActiveUser().getUid())){
            this.profile_image.setOnClickListener(null);
        }else {
            this.profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ViewHolderUserInfo.this.context, UserProfileActivity.class);
                    i.putExtra(UserProfileActivity.EXTRA_USER_PROFILE, ViewHolderUserInfo.this.user);

                    Utils.openIntent(ViewHolderUserInfo.this.context, i, R.anim.slide_in_left, R.anim.slide_out_left);
                }
            });
        }
    }
}