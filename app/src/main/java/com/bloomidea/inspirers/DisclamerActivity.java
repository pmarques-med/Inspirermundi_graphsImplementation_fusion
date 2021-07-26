package com.bloomidea.inspirers;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.utils.Utils;

public class DisclamerActivity extends AppCompatActivity {
    public static final String EXTRA_IS_ABOUT = "EXTRA_IS_ABOUT";
    public static final String EXTRA_FROM_SETTINGS = "EXTRA_FROM_SETTINGS";

    private boolean isAbout = false;
    private boolean fromSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclamer);

        isAbout = getIntent().getBooleanExtra(EXTRA_IS_ABOUT,false);
        fromSettings = getIntent().getBooleanExtra(EXTRA_FROM_SETTINGS,false);

        int textId;

        if(!isAbout) {
            textId = R.string.terms_and_conditions_text;

            if(!AppController.getmInstance().getActiveUser().isAcceptedTerms()) {
                findViewById(R.id.agree_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AppController.getmInstance().updateUserAcceptedTerms()) {
                            goToMain();
                        }
                    }
                });

                findViewById(R.id.disagree_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //AppController.getmInstance().resetDatabaseUser(AppController.getmInstance().getActiveUser().getId());
                        finish();
                    }
                });
            }else{
                if(!fromSettings) {
                    findViewById(R.id.btnsView).setVisibility(View.GONE);

                    findViewById(R.id.noMoreTermsView).setVisibility(View.VISIBLE);

                    findViewById(R.id.close_btn).setVisibility(View.VISIBLE);
                    findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (((CheckBox) findViewById(R.id.noMoreCheckBox)).isChecked()) {
                                AppController.getmInstance().updateUserTermsOff();
                            }

                            goToMain();
                        }
                    });
                }
            }

        }else{
            textId = R.string.about_text;

            findViewById(R.id.btnsView).setVisibility(View.GONE);

            ((TextView) findViewById(R.id.titleTextView)).setText(R.string.about_title);

            findViewById(R.id.close_btn).setVisibility(View.VISIBLE);
            findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        if(fromSettings){
            findViewById(R.id.btnsView).setVisibility(View.GONE);

            findViewById(R.id.close_btn).setVisibility(View.VISIBLE);
            findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        ((TextView) findViewById(R.id.textTextView)).setText(Html.fromHtml(getResources().getString(textId)));
        ((TextView) findViewById(R.id.textTextView)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);

        Utils.openIntent(this, i, -1, -1);

        this.finish();
    }
}
