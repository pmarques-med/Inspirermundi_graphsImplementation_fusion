package com.bloomidea.inspirers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.adapter.TimelineAdapter;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.LinearLayoutManagerWithSmoothScroller;
import com.bloomidea.inspirers.customViews.MyActiveActivity;
import com.bloomidea.inspirers.customViews.MyRegFragment;
import com.bloomidea.inspirers.customViews.SpacesItemDecoration;
import com.bloomidea.inspirers.events.MedicineCreatedEvent;
import com.bloomidea.inspirers.events.MedicineTakenEvent;
import com.bloomidea.inspirers.events.MedicinesChangedEvent;
import com.bloomidea.inspirers.events.PollAnswered;
import com.bloomidea.inspirers.events.PollsUpdated;
import com.bloomidea.inspirers.events.UserBadgeWon;
import com.bloomidea.inspirers.listener.OkErrorListener;
import com.bloomidea.inspirers.model.Days;
import com.bloomidea.inspirers.model.MedicineDays;
import com.bloomidea.inspirers.model.TimelineItem;
import com.bloomidea.inspirers.model.TimelineWeek;
import com.bloomidea.inspirers.utils.MedicineTypeAux;

import org.medida.inhalerdetection.InhalerDetectionActivity;

import java.util.ArrayList;

import rx.functions.Action1;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class TimelineFragment extends MyRegFragment {
    private static final int INHALER_DETECTION_REQUEST = 103;

    private View rootView;
    private ArrayList<TimelineWeek> timeline;

    private RecyclerView timelineRecyclerView;
    private LinearLayoutManagerWithSmoothScroller timelineLayoutManager;
    private TimelineAdapter timelineAdapter;

    private boolean autoScroll = true;
    private Point size;


    public TimelineItem medicineToTakeAux;
    public View medicineToTakeViewAux;
    public int medicineToTakePosAux;

    private AlertDialog dialog;

    private TimelineItem medicineToTakeAuxLocation;

    private OnTimelineFragmentInteractionListener mListener;

    private CountDownTimer timerScrollToActual = new CountDownTimer(5000,1000) {
        @Override
        public void onTick(long l) {
            Log.d("CountDownTimer","onTick");
        }

        @Override
        public void onFinish() {
            Log.d("CountDownTimer","onFinish");
            scrollToActualMed();
        }
    };

    public TimelineFragment() {
        // Required empty public constructor
    }

    public static TimelineFragment newInstance() {
        TimelineFragment fragment = new TimelineFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_timeline, container, false);

        initTimeline();

        AppController.getmInstance().getMyBus().toObserverable()
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {

                        if(event instanceof MedicineTakenEvent) {
                            final MedicineTakenEvent eventAux = (MedicineTakenEvent) event;

                            int pos = eventAux.getPositionAdapter();
                            int points = eventAux.getPoints();

                            if(eventAux.isOnTime()){
                                pos = timelineAdapter.getTodayPos();
                                points = eventAux.getMedicinetaken().getTimePoints();
                            }

                            timelineAdapter.updateInfoTakenTimelineItem(eventAux.getTimelineItemUpdated());


                            timelineAdapter.animateTakeMedicine(timelineRecyclerView.findViewHolderForAdapterPosition(pos),
                                    eventAux.isOnTime(), points, eventAux.getMultiplierUsed(), eventAux.getTimelineItemUpdated().getDateTaken(), new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            if(eventAux.isOnTime()){
                                                timelineAdapter.updateTodayPos();
                                                timelineAdapter.notifyDataSetChanged();
                                                scrollToActualMed();
                                            }else{
                                                reloadTimeline();
                                            }
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });
                        }else if(event instanceof MedicineCreatedEvent || event instanceof MedicinesChangedEvent){
                            reloadTimeline();
                        }else if(event instanceof UserBadgeWon){
                            reloadTimeline();
                        }else if(event instanceof PollAnswered){
                            reloadTimeline();
                        }else if(event instanceof PollsUpdated){
                            reloadTimeline();
                        }
                    }
                });

        return rootView;
    }

    public void reloadTimeline() {
        //if(timelineAdapter==null) {
        //    configureTimeLine();
        //}else{
            timeline = AppController.getmInstance().getTimelineDataSource().getTimeLine(AppController.getmInstance().getActiveUser().getId());
            timelineAdapter.reloadTimeline(timeline);

        configureTimeLineVisibility();

         //   scrollToActualMed();
        //}
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void initTimeline(){
        timeline = AppController.getmInstance().getTimelineDataSource().getTimeLine(AppController.getmInstance().getActiveUser().getId());

        int remove = getResources().getDimensionPixelSize(R.dimen.main_top_bar_h) + getResources().getDimensionPixelSize(R.dimen.main_menu_h);

        timelineRecyclerView = (RecyclerView) rootView.findViewById(R.id.timeline_recyclerView);

        int statusBarHeight = getStatusBarHeight();

        timelineLayoutManager = new LinearLayoutManagerWithSmoothScroller(getActivity());
        timelineAdapter = new TimelineAdapter(getActivity(),getActivity().getSupportFragmentManager(), timeline, statusBarHeight + getResources().getDimensionPixelSize(R.dimen.main_top_bar_h), new TimelineAdapter.TimelineAdapterInterface() {
            @Override
            public void updateActiveMedicinePos() {
                //timerScrollToActual.start();
            }

            @Override
            public void notesPopUpClose() {
                if(timerScrollToActual!=null){
                    timerScrollToActual.start();
                }
            }

            @Override
            public void takeMedicine(final View view, final int position, final TimelineItem item) {

                medicineToTakeAux = item;
                medicineToTakePosAux = position;
                medicineToTakeViewAux = view;

                mListener.onMedTakePressed();

                /*View auxView = LayoutInflater.from(getActivity()).inflate(R.layout.overlay_take_med, null);//criei esta view porque nao estava a conseguir ir buscar as refs com o dialog.findbyID tava a dar null
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyleNoWindow);
                builder.setView(auxView);

                auxView.findViewById(R.id.btn_take_med).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(MedicineTypeAux.needsOpenRecognitionToTake(item.getMedicine().getMedicineName(),item.getMedicine().getMedicineType()) &&
                                medicineToTakeAux.getRecognitionFailedTimes() < 2){
                            Intent intent = InhalerDetectionActivity.PrepareIntentInput(getActivity(),Long.parseLong(AppController.getmInstance().getActiveUser().getUid()), MedicineTypeAux.getRecognitionCodeToTake(item.getMedicine().getMedicineName()),  AppController.INHALER_DETECTION_TOTAL, AppController.INHALER_DETECTION_MAX_TIME_SECONDS);
                            startActivityForResult(intent, INHALER_DETECTION_REQUEST);
                        }else {
                            TimelineFragment.this.takeMedicineAux();
                        }
                    }
                });

                dialog = builder.create();
                dialog.show();*/

            }

            @Override
            public void notesPopUpOpen() {
                if(timerScrollToActual!=null){
                    timerScrollToActual.cancel();
                }
            }
        });

        timelineRecyclerView.setLayoutManager(timelineLayoutManager);

        timelineRecyclerView.setAdapter(timelineAdapter);

        //View first = timelineLayoutManager.findViewByPosition(0);
        //Log.d("FIRST",""+first.getHeight());

        View child = getActivity().getLayoutInflater().inflate(R.layout.item_medicine, null);
        child.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Log.d("Height",""+child.getMeasuredHeight());

        int spaceTop = (size.y-remove) - (child.getMeasuredHeight()*2);
        int spaceBottom = size.x/5;

        timelineRecyclerView.addItemDecoration(new SpacesItemDecoration(spaceTop, spaceBottom));
        timelineRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    if(autoScroll){
                        autoScroll = false;
                    }else {
                        timerScrollToActual.start();
                    }
                }else{
                    timerScrollToActual.cancel();
                }
            }
        });

        rootView.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollToActualMed();
            }
        });

        //final Handler handler = new Handler();
        //handler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
        //int mainPos = timelineAdapter.getTodayPos();
        //autoScroll = true;
        //timelineRecyclerView.smoothScrollToPosition(mainPos);
        //    }
        //}, 100);

       // scrollToActualMed();

            /*timelineRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    timelineRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    Log.d("teste","teste");


                }
            });*/


        configureTimeLineVisibility();
    }

    public void takeMedicineAux(){
        if(medicineToTakeAux!=null) {
            medicineToTakeViewAux.clearAnimation();

            medicineToTakeAuxLocation = medicineToTakeAux;

            AppController.getmInstance().takeMedicine(medicineToTakeAux, false, medicineToTakePosAux, getActivity().getSupportFragmentManager(), new OkErrorListener() {
                @Override
                public void ok() {
                    AppController.getmInstance().addTimelineItemNeedLocation(medicineToTakeAuxLocation);

                    mListener.onLastLocationAsk();

                    medicineToTakeAuxLocation = null;
                    //    animateTakeMedicineLate((ViewHolderMedicine)view.getTag(R.id.tag_viewholder), true, 15, auxMult);
                }

                @Override
                public void error() {
                    medicineToTakeAuxLocation = null;
                    Toast.makeText(getActivity(), R.string.problem_takin_med, Toast.LENGTH_SHORT).show();
                }
            });

            medicineToTakeAux = null;
            medicineToTakePosAux = -1;
            medicineToTakeViewAux = null;
        }
    }

    private void configureTimeLineVisibility() {
        if(timeline.isEmpty() || (AppController.getmInstance().getMedicineDataSource().getUserNormalMedicines(AppController.getmInstance().getActiveUser().getId()).isEmpty() && AppController.getmInstance().getMedicineDataSource().getUserNormalMedicinesDisable(AppController.getmInstance().getActiveUser().getId()).isEmpty())){
            rootView.findViewById(R.id.empty_timeline).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.timeline_view).setVisibility(View.GONE);
        }else{
            rootView.findViewById(R.id.empty_timeline).setVisibility(View.GONE);
            rootView.findViewById(R.id.timeline_view).setVisibility(View.VISIBLE);

            scrollToActualMed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void scrollToActualMed() {
        int mainPos = timelineAdapter.getTodayPos();


        autoScroll = true;
        timelineRecyclerView.smoothScrollToPosition(mainPos);
    }

    public void refreshTimeline() {
        if(timelineAdapter!=null){
            refreshTimeline();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == INHALER_DETECTION_REQUEST) {
            switch(resultCode) {
                case RESULT_OK:
                    int detectionCount = data.getIntExtra(InhalerDetectionActivity.EXTRA_RESULT_DETECTION_COUNT_INT, 0);
                    int dosageCount = data.getIntExtra(InhalerDetectionActivity.EXTRA_RESULT_DOSAGE_COUNT_INT, 0);
                    //Toast.makeText(this, "Result is " + result, Toast.LENGTH_LONG);
                    Log.d("MAIN CLASS", "Inhaler detected " + detectionCount + " times, with dosage count " + dosageCount);

                    if (AppController.getmInstance().getInhalerDataSource().changeDosage(dosageCount, medicineToTakeAux.getMedicine().getInhalers().get(0).getId())){

                    }


                    //if(detectionCount >= AppController.INHALER_DETECTION_TOTAL){
                        takeMedicineAux();
                    //}else{
                    //    if(medicineToTakeAux!=null){
                    //        int failedTimes = medicineToTakeAux.getRecognitionFailedTimes()+1;
                    //        if(AppController.getmInstance().getTimelineDataSource().updateRecognitionFailedTimes(medicineToTakeAux.getId(),failedTimes)){
                    //            medicineToTakeAux.setRecognitionFailedTimes(failedTimes);
                    //        }
                    //    }
                    //}

                    break;
                case RESULT_CANCELED:
                    //todo implementar contador do inalador quando for enviado pelo reconhecimento
                    /*if(data != null && data.hasExtra(InhalerDetectionActivity.EXTRA_RESULT_DETECTION_COUNT_INT)) {
                        //do stuff
                    } else {
                        //do stuff
                    }*/
                    break;
                default:
                    break;
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTimelineFragmentInteractionListener) {
            mListener = (OnTimelineFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnTimelineFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnTimelineFragmentInteractionListener {
        void onLastLocationAsk();
        void onMedTakePressed();
    }
}
