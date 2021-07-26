package com.bloomidea.inspirers;

import android.os.Bundle;


import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bloomidea.inspirers.adapter.SlideAdapter;
import com.rd.PageIndicatorView;

import java.util.ArrayList;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ArrayList<String> auxText= new ArrayList<>();
        auxText.add(getString(R.string.slide_text1));
        auxText.add(getString(R.string.slide_text2));
        auxText.add(getString(R.string.slide_text3));
        auxText.add(getString(R.string.slide_text4));
        auxText.add(getString(R.string.slide_text5));

        SlideAdapter pagerAdapter = new SlideAdapter(this, auxText);
        ((ViewPager) findViewById(R.id.viewPager)).setAdapter(pagerAdapter);

        PageIndicatorView pageIndicatorView = findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setViewPager( ((ViewPager) findViewById(R.id.viewPager)));


        findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
