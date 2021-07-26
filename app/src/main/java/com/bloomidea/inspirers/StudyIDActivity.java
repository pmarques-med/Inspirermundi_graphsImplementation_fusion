package com.bloomidea.inspirers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.utils.Utils;

public class StudyIDActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_id);

        findViewById(R.id.back_btn_imageView).setVisibility(View.INVISIBLE);

        ((TextView) findViewById(R.id.title_textView)).setText(R.string.study_id);

        ((EditText) findViewById(R.id.study_id_textview)).setText(AppController.getmInstance().getActiveUser().getUserProvidedId());

        findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userStudyId = ((EditText) findViewById(R.id.study_id_textview)).getText().toString();

                AppController.getmInstance().updateUserStudyID(userStudyId);
                Intent i = new Intent(StudyIDActivity.this, EditProfileActivity.class);
                i.putExtra(EditProfileActivity.EXTRA_HIDE_BACK_BTN, true);

                Utils.openIntent(StudyIDActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);

                finish();
            }
        });

        if(AppController.getmInstance().getActiveUser().isFirstLogin()){
            Intent i = new Intent(this, AboutActivity.class);

            Utils.openIntent(this, i, -1, -1);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
