package com.bloomidea.inspirers;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.model.User;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import static com.bloomidea.inspirers.application.AppController.getmInstance;

public class StatisticsActivity extends MyActiveActivity {
    public static final String EXTRA_USER = "EXTRA_USER";

    private boolean isActiveUser;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        View auxLayout = findViewById(R.id.linearLayout_stats);
        auxLayout.setPadding(auxLayout.getPaddingLeft(),auxLayout.getPaddingTop(),auxLayout.getPaddingRight(),getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin));

        Object aux = getIntent().getSerializableExtra(EXTRA_USER);

        if(aux==null){
            setActiveUserToLoad();
        }else{
            isActiveUser = false;
            user = (User) aux;

            if(user.getUid().equals(getmInstance().getActiveUser().getUid())){
                setActiveUserToLoad();
            }
        }

        configTopMenu();

        //loadInfo();
    }

    private void configTopMenu() {
        findViewById(R.id.back_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ((TextView) findViewById(R.id.title_textView)).setText(R.string.statistics);
    }

//    private void loadInfo(){
//        if(isActiveUser) {
//            GregorianCalendar firstMedDate = AppController.getmInstance().getTimelineDataSource().getFirstMedicineDate(user.getId());
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//
//            if (firstMedDate != null) {
//                ((TextView) findViewById(R.id.init_date_textView)).setText(getResources().getString(R.string.stats_started_in, dateFormat.format(firstMedDate.getTime())));
//            } else {
//                findViewById(R.id.init_date_textView).setVisibility(View.GONE);
//            }
//        }else{
//            findViewById(R.id.init_date_textView).setVisibility(View.GONE);
//        }
//
//        ((DonutProgress) findViewById(R.id.stats_begining_donut_progress)).setUnfinishedStrokeColor(Color.parseColor(user.getStatsEverColor()));
//        ((DonutProgress) findViewById(R.id.stats_begining_donut_progress)).setProgress(100 - user.getStatsEver().intValue());
//        ((TextView) findViewById(R.id.percent_beginning_textview)).setText(user.getStatsEver().setScale(0, RoundingMode.HALF_UP).toPlainString());
//
//        ((DonutProgress) findViewById(R.id.stats_month_donut_progress)).setUnfinishedStrokeColor(Color.parseColor(user.getStatsMonthColor()));
//        ((DonutProgress) findViewById(R.id.stats_month_donut_progress)).setProgress(100 - user.getStatsMonth().intValue());
//        ((TextView) findViewById(R.id.percent_month_textview)).setText(user.getStatsMonth().setScale(0, RoundingMode.HALF_UP).toPlainString());
//
//        ((DonutProgress) findViewById(R.id.stats_7_days_donut_progress)).setUnfinishedStrokeColor(Color.parseColor(user.getStatsWeekColor()));
//        ((DonutProgress) findViewById(R.id.stats_7_days_donut_progress)).setProgress(100 - user.getStatsWeek().intValue());
//        ((TextView) findViewById(R.id.percent_7_days_textview)).setText(user.getStatsWeek().setScale(0, RoundingMode.HALF_UP).toPlainString());
//    }

    private void setActiveUserToLoad() {
        isActiveUser = true;
        user = getmInstance().getActiveUser();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
