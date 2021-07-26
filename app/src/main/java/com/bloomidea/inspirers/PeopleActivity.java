package com.bloomidea.inspirers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.utils.Utils;

import java.util.GregorianCalendar;


public class PeopleActivity extends MyActiveActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);


        ((TextView) findViewById(R.id.title_textView)).setText(R.string.my_social);

        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.share_with_doc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PeopleActivity.this, InviteDoctorActivity.class);

                Utils.openIntent(PeopleActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        findViewById(R.id.add_warrior_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User auxuser = AppController.getmInstance().getActiveUser();
                if(Utils.canOpenGodchild(auxuser.getLevel(),auxuser.getNumGodChilds())){

                    Intent i = new Intent(PeopleActivity.this, WarriorsActivity.class);

                    Utils.openIntent(PeopleActivity.this, i, R.anim.slide_in_left, R.anim.slide_out_left);

                } else {
                    Toast.makeText(PeopleActivity.this, Utils.cantOpenGodchildReason(PeopleActivity.this, auxuser.getLevel()), Toast.LENGTH_SHORT).show();

                }
            }
        });

        setFragment(PeopleFragment.newInstance());

    }

    private void setFragment(final Fragment newFragment){


        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        String tagAux = ""+(new GregorianCalendar()).getTimeInMillis();
        transaction.replace(R.id.fragment_container, newFragment,tagAux);

        transaction.commit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }


}
