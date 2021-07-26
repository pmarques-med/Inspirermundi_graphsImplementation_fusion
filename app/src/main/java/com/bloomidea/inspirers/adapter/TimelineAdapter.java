package com.bloomidea.inspirers.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.CARATQuizActivity;
import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.SymptomsPollActivity;
import com.bloomidea.inspirers.application.AppController;
import com.bloomidea.inspirers.customViews.MorebiRoundedMediumTimerTextView;
import com.bloomidea.inspirers.model.Poll;
import com.bloomidea.inspirers.model.TimelineItem;
import com.bloomidea.inspirers.model.TimelineWeek;
import com.bloomidea.inspirers.model.UserBadge;
import com.bloomidea.inspirers.model.UserMedicine;
import com.bloomidea.inspirers.utils.MedicineTypeAux;
import com.bloomidea.inspirers.utils.Utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * Created by michellobato on 23/03/17.
 */

public class TimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_WEEK_RESUME = 101;
    private static final int TYPE_MEDICINE = 102;
    private static final int TYPE_BADGE = 103;
    private static final int TYPE_POLL = 104;

    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

    private Activity context;
    private FragmentManager fragmentManager;

    private ArrayList<TimelineWeek> timeline;

    private ArrayList<InnerTimelineItem> innerTimeline;

    private int todayPos = -1;

    private TimelineAdapterInterface listener;

    private int topLimit = 0;


    //popup notas auxiliares
    private PopupWindow mpopup;
    private TimelineItem popUpAuxTimelineItem;

    public TimelineAdapter(Activity context,FragmentManager fragmentManager,  ArrayList<TimelineWeek> timeline, int topLimit, TimelineAdapterInterface listener) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.timeline = timeline;
        this.listener = listener;
        this.topLimit = topLimit;

        initInnerAdapter();
    }

    private void initInnerAdapter() {
        this.innerTimeline = new ArrayList<>();

        for(TimelineWeek timelineWeek : timeline){
            if(timelineWeek.isOpen()){
                boolean showDate;
                GregorianCalendar lastDateVisible = null;

                for(TimelineItem timelineItem : timelineWeek.getItems()) {
                    if(lastDateVisible == null || !Utils.sameDate(lastDateVisible,timelineItem.getDate())){
                        showDate = true;
                        lastDateVisible = timelineItem.getDate();
                    }else{
                        showDate = false;
                    }

                    if(timelineItem.getBadge()!=null){
                        innerTimeline.add(new InnerTimelineItem(TYPE_BADGE, showDate, null, timelineItem));
                    }else if(timelineItem.getPoll()!=null){
                            innerTimeline.add(new InnerTimelineItem(TYPE_POLL, showDate, null, timelineItem));
                    }else {
                        innerTimeline.add(new InnerTimelineItem(TYPE_MEDICINE, showDate, null, timelineItem));
                    }
                }
            }else{
                innerTimeline.add(new InnerTimelineItem(TYPE_WEEK_RESUME,false,timelineWeek,null));
            }
        }

        updateTodayPos();

        listener.updateActiveMedicinePos();
    }

    private boolean hasMoreThanOnePoolAnimating(){
        int total = 0;
        boolean moreThanOne = false;

        if(innerTimeline!=null) {
            for (InnerTimelineItem item : innerTimeline) {
                if(item.getType() == TYPE_POLL){
                    if(item.getTimelineItem().getState() == null || !item.getTimelineItem().getState().equals(TimelineItem.STATE_DONE)){
                        total+=1;
                    }

                    if(total>=2){
                        moreThanOne=true;
                        break;
                    }
                }
            }
        }

        return moreThanOne;
    }

    public void reloadTimeline(ArrayList<TimelineWeek> newTimeline){
        this.timeline = newTimeline;
        initInnerAdapter();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return innerTimeline.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        RecyclerView.ViewHolder viewHolder;

        if (viewType == TYPE_WEEK_RESUME) {
            view = LayoutInflater.from(context).inflate(R.layout.item_week_resume, parent, false);
            viewHolder = new ViewHolderWeekResume(view);
        } else if(viewType == TYPE_MEDICINE){
            view = LayoutInflater.from(context).inflate(R.layout.item_medicine, parent, false);
            viewHolder = new ViewHolderMedicine(view);
        } else if(viewType == TYPE_POLL) {
            view = LayoutInflater.from(context).inflate(R.layout.item_poll, parent, false);
            viewHolder = new ViewHolderPoll(view);
        }else {
            //TYPE_BADGE
            view = LayoutInflater.from(context).inflate(R.layout.item_badge, parent, false);
            viewHolder = new ViewHolderBadge(view);
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        InnerTimelineItem item = innerTimeline.get(position);

        holder.itemView.setTag(R.id.tag_is_active,false);

        if(holder instanceof ViewHolderWeekResume){
            ViewHolderWeekResume auxViewHolder = (ViewHolderWeekResume) holder;

            TimelineWeek auxItem = item.getTimelineWeek();

            if(position==0){
                auxViewHolder.top_line_view.setVisibility(View.GONE);
            }else{
                auxViewHolder.top_line_view.setVisibility(View.VISIBLE);
            }

            auxViewHolder.week_textView.setText(Utils.getStartEndOFWeek(context, auxItem.getItems().get(0).getWeekNumber()));

            int totalMeds = 0;
            int totalOkMeds = 0;
            int totalLateMeds = 0;
            int totalFailedMeds = 0;
            int totalPoints = 0;

            for(TimelineItem timelineItem : auxItem.getItems()){
                if(timelineItem.getMedicine() != null && !timelineItem.isSOS()){
                    totalMeds++;

                    totalPoints+=timelineItem.getPointWon();

                    if(timelineItem.getState()!=null && timelineItem.isMedicine() && !timelineItem.isSOS()) {
                        if (timelineItem.getState().equals(TimelineItem.STATE_DONE)) {
                            totalOkMeds++;
                        } else if (timelineItem.getState().equals(TimelineItem.STATE_LATE)) {
                            totalLateMeds++;
                        } else if (timelineItem.getState().equals(TimelineItem.STATE_MISSED)) {
                            totalFailedMeds++;
                        }
                    }
                }
            }

            BigDecimal totalMedsBig = new BigDecimal(totalMeds);
            BigDecimal totalOkMedsPerc = new BigDecimal(0).setScale(1);
            BigDecimal totalLateMedsPerc = new BigDecimal(0).setScale(1);
            BigDecimal totalFailedMedsPerc = new BigDecimal(0).setScale(1);

            if(totalMeds>0){
                totalOkMedsPerc = (new BigDecimal(totalOkMeds).multiply(new BigDecimal(100))).divide(totalMedsBig,BigDecimal.ROUND_DOWN, 1);
                totalLateMedsPerc = (new BigDecimal(totalLateMeds).multiply(new BigDecimal(100))).divide(totalMedsBig,BigDecimal.ROUND_DOWN, 1);
                totalFailedMedsPerc = (new BigDecimal(totalFailedMeds).multiply(new BigDecimal(100))).divide(totalMedsBig,BigDecimal.ROUND_DOWN, 1);
            }


            auxViewHolder.total_points_textView.setText(""+totalPoints);
            auxViewHolder.total_meds_textView.setText(""+totalMeds);

            auxViewHolder.total_ok_meds_textView.setText(""+totalOkMeds);
            auxViewHolder.total_ok_meds_percent_textView.setText(context.getResources().getString(R.string.total_ok_meds,(totalOkMedsPerc.toPlainString())+"%"));

            auxViewHolder.total_late_meds_textView.setText(""+totalLateMeds);
            auxViewHolder.total_late_meds_percent_textView.setText(context.getResources().getString(R.string.total_late_meds,(totalLateMedsPerc.toPlainString())+"%"));

            auxViewHolder.total_failed_meds_textView.setText(""+totalFailedMeds);
            auxViewHolder.total_failed_meds_percent_textView.setText(context.getResources().getString(R.string.total_failed_meds,(totalFailedMedsPerc.toPlainString())+"%"));

            auxViewHolder.view_all_btn.setTag(R.id.tag_position, position);
            auxViewHolder.view_all_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int positionAux = (int) view.getTag(R.id.tag_position);

                    TimelineWeek auxItem = innerTimeline.get(positionAux).getTimelineWeek();
                    auxItem.setOpen(true);

                    initInnerAdapter();
                    notifyDataSetChanged();
                }
            });
        }else if(holder instanceof ViewHolderBadge){
            ViewHolderBadge auxViewHolder = (ViewHolderBadge) holder;

            UserBadge auxItem = item.getTimelineItem().getBadge();

            if(position==0){
                auxViewHolder.top_line_view.setVisibility(View.GONE);
            }else{
                auxViewHolder.top_line_view.setVisibility(View.VISIBLE);
            }

            auxViewHolder.badge_icon.setImageResource(auxItem.getBadge().getImgResourceId());

            if(item.isShowDate()){
                auxViewHolder.layout_date_box.setVisibility(View.VISIBLE);
                auxViewHolder.date_textView.setText(Utils.getTimelineStingDate(context, auxItem.getDate()));
            }else{
                auxViewHolder.layout_date_box.setVisibility(View.GONE);
            }
        }else if (holder instanceof ViewHolderPoll){
            ViewHolderPoll auxViewHolder = (ViewHolderPoll) holder;
            TimelineItem auxItem = item.getTimelineItem();

            int paddingExtra = context.getResources().getDimensionPixelSize(R.dimen.points_padding_top_extra);

            boolean animate = false;

            auxViewHolder.multiplier_textView.setText("x"+AppController.getmInstance().getActiveUser().getActualBonus().toPlainString());

            if(position==0){
                auxViewHolder.top_line_view.setVisibility(View.GONE);
            }else{
                auxViewHolder.top_line_view.setVisibility(View.VISIBLE);
            }

            if(item.isShowDate()){
                auxViewHolder.layout_date_box.setVisibility(View.VISIBLE);
                auxViewHolder.date_textView.setText(Utils.getTimelineStingDate(context, auxItem.getDate()));
            }else{
                auxViewHolder.layout_date_box.setVisibility(View.GONE);
            }

            Poll poll = item.getTimelineItem().getPoll();

            auxViewHolder.time_textView.setTextColor(context.getResources().getColor(R.color.text_color_333333));

            if(poll.getPoolType().equals(Poll.POLL_TYPE_CARAT)){
                auxViewHolder.poll_type_textView.setText("");
            }else{
                auxViewHolder.poll_type_textView.setText(poll.getPoolType().equals(Poll.POLL_TYPE_DAILY)?R.string.daily:R.string.weekly);
            }

            auxViewHolder.poll_textView.setText(poll.getPoolType().equals(Poll.POLL_TYPE_CARAT)?R.string.carat_poll:R.string.simptoms_poll);
            //POll icon
            auxViewHolder.medicine_btn_imageView.setImageResource(poll.getPoolType().equals(Poll.POLL_TYPE_CARAT)?R.drawable.carat_timline_icon:R.drawable.poll_timeline_icon);

            if(auxItem.getState() != null && auxItem.getState().equals(TimelineItem.STATE_DONE)){
                //auxViewHolder.back_btn_view.setBackgroundResource(R.drawable.item_medicine_normal_btn_back);
                Utils.changeBtnBackgroundMedicine(auxViewHolder.medicine_btn_imageView, R.drawable.item_medicine_normal_btn_back_green);

                auxViewHolder.time_textView.setText(context.getString(R.string.at_time,timeFormatter.format(auxItem.getDateTaken().getTime())));

                auxViewHolder.points_without_background_box.setVisibility(View.VISIBLE);
                auxViewHolder.points_without_background_textView.setText(""+auxItem.getPointWon());
                auxViewHolder.points_without_background_textView.setTextColor(context.getResources().getColor(R.color.medicine_normal_text_colro));

                auxViewHolder.poll_type_textView.setTextColor(context.getResources().getColor(R.color.text_color_333333));
                auxViewHolder.poll_textView.setTextColor(context.getResources().getColor(R.color.text_color_333333));
                auxViewHolder.time_textView.setTextColor(context.getResources().getColor(R.color.text_color_333333));


                //auxViewHolder.points_with_backgorund_box.setVisibility(View.INVISIBLE);
            }else{
                //NO STATE == NOT ANSWERED

                if(auxItem.getStartTime().before(new GregorianCalendar())){
                    animate = true;
                }

                if(position==getItemCount()-1 && !hasMoreThanOnePoolAnimating()){
                    auxViewHolder.itemView.setTag(R.id.tag_is_active,true);
                }else{
                    auxViewHolder.itemView.setTag(R.id.tag_is_active,false);
                }

//                auxViewHolder.time_textView.setText(context.getResources().getString(R.string.time_interval,timeFormatter.format(auxItem.getStartTime().getTime()), timeFormatter.format(auxItem.getEndTime().getTime())));
                if(poll.getPoolType().equals(Poll.POLL_TYPE_DAILY)) {
                    auxViewHolder.time_textView.setText(AppController.POLL_DAILY_INIT_TIME+":00 - 23:59");
                }else{
                    auxViewHolder.time_textView.setText(R.string.today);
                }

                auxViewHolder.points_without_background_box.setVisibility(View.INVISIBLE);

                auxViewHolder.back_btn_view.setBackgroundResource(R.drawable.item_medicine_normal_btn_back);

                //auxViewHolder.points_with_background_textView.setText(""+auxItem.getTimePoints());

                //GregorianCalendar auxTimeStart = new GregorianCalendar();
                //auxTimeStart.setTimeInMillis(auxItem.getStartTime().getTimeInMillis());
                //auxTimeStart.add(Calendar.HOUR_OF_DAY,-1);

                if(animate) {
                    auxViewHolder.poll_type_textView.setTextColor(context.getResources().getColor(R.color.text_color_333333));
                    auxViewHolder.poll_textView.setTextColor(context.getResources().getColor(R.color.text_color_333333));

                    if (poll.getPoolType().equals(Poll.POLL_TYPE_CARAT)) {
                        Utils.changeBtnBackgroundMedicine(auxViewHolder.medicine_btn_imageView, R.drawable.item_poll_carat_btn_back);
                    } else {
                        Utils.changeBtnBackgroundMedicine(auxViewHolder.medicine_btn_imageView, R.drawable.item_poll_other_btn_back);
                    }

                    auxViewHolder.time_textView.setTextColor(context.getResources().getColor(R.color.text_color_333333));
                }else{
                    auxViewHolder.poll_type_textView.setTextColor(context.getResources().getColor(R.color.medicine_future));
                    auxViewHolder.poll_textView.setTextColor(context.getResources().getColor(R.color.medicine_future));
                    Utils.changeBtnBackgroundMedicine(auxViewHolder.medicine_btn_imageView, R.drawable.item_medicine_future_btn_back);
                    auxViewHolder.time_textView.setTextColor(context.getResources().getColor(R.color.medicine_future));
                }

                //auxViewHolder.points_with_background_textView.setBackgroundResource(R.drawable.actual_circle);

                //int padding = context.getResources().getDimensionPixelSize(R.dimen.points_padding);
                //int paddingTopWithExtra = context.getResources().getDimensionPixelSize(R.dimen.points_padding_top);

                //auxViewHolder.points_with_background_textView.setPadding(padding,paddingTopWithExtra,padding,padding);
                //auxViewHolder.points_with_background_textView.setTextColor(context.getResources().getColor(R.color.white));
                //auxViewHolder.points_with_backgorund_box.setVisibility(View.VISIBLE);
            }

            auxViewHolder.medicine_btn_imageView.setTag(R.id.tag_animating, animate);

            if(animate){
                Utils.animateBtnToTakeMedicine(context, auxViewHolder.medicine_btn_imageView);
                auxViewHolder.medicine_btn_imageView.setTag(R.id.tag_position,position);
                auxViewHolder.medicine_btn_imageView.setTag(R.id.tag_viewholder, auxViewHolder);
                auxViewHolder.medicine_btn_imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = (int) view.getTag(R.id.tag_position);

                        TimelineItem item = innerTimeline.get(position).getTimelineItem();

                        if(item.getPoll().getPoolType().equals(Poll.POLL_TYPE_CARAT)){
                            Intent i = new Intent(context, CARATQuizActivity.class);
                            i.putExtra(CARATQuizActivity.EXTRA_POLL, item.getPoll());
                            i.putExtra(CARATQuizActivity.EXTRA_POLL_TIMELINE_ID, item.getId());

                            Utils.openIntent(context, i, R.anim.slide_in_left, R.anim.slide_out_left);
                        } else{
                            Intent i = new Intent(context, SymptomsPollActivity.class);
                            i.putExtra(SymptomsPollActivity.EXTRA_POLL, item.getPoll());
                            i.putExtra(SymptomsPollActivity.EXTRA_POLL_TIMELINE_ID, item.getId());

                            Utils.openIntent(context, i, R.anim.slide_in_left, R.anim.slide_out_left);
                        }
                    }
                });
            }else{
                auxViewHolder.medicine_btn_imageView.clearAnimation();
                auxViewHolder.medicine_btn_imageView.setOnClickListener(null);
            }

        }else if(holder instanceof ViewHolderMedicine){
            int paddingExtra = context.getResources().getDimensionPixelSize(R.dimen.points_padding_top_extra);

            boolean animate = false;

            ViewHolderMedicine auxViewHolder = (ViewHolderMedicine) holder;
            TimelineItem auxItem = item.getTimelineItem();

            auxViewHolder.multiplier_textView.setText("x"+AppController.getmInstance().getActiveUser().getActualBonus().toPlainString());

            if(position==0){
                auxViewHolder.top_line_view.setVisibility(View.GONE);
            }else{
                auxViewHolder.top_line_view.setVisibility(View.VISIBLE);
            }

            auxViewHolder.taken_text_textView.setVisibility(View.VISIBLE);
            auxViewHolder.timer_text_textView.setVisibility(View.GONE);

            if(item.isShowDate()){
                auxViewHolder.layout_date_box.setVisibility(View.VISIBLE);
                auxViewHolder.date_textView.setText(Utils.getTimelineStingDate(context, auxItem.getDate()));
            }else{
                auxViewHolder.layout_date_box.setVisibility(View.GONE);
            }

            UserMedicine userMedicine = item.getTimelineItem().getMedicine();

            auxViewHolder.medicine_name_textView.setText(userMedicine.getMedicineName());
            auxViewHolder.medicine_name_textView.setTextColor(context.getResources().getColor(R.color.text_color_333333));

            int dosage = item.getTimelineItem().getDosage();
            auxViewHolder.medicine_podology_textView.setText(context.getResources().getQuantityString(MedicineTypeAux.getTextResourceForTotal(userMedicine.getMedicineType().getCode()), dosage, dosage));
            auxViewHolder.medicine_podology_textView.setTextColor(context.getResources().getColor(R.color.text_color_333333));


            auxViewHolder.time_textView.setTextColor(context.getResources().getColor(R.color.text_color_333333));

            //Medicnie icon
            auxViewHolder.medicine_btn_imageView.setImageResource(MedicineTypeAux.getMedicineTypeIcon(userMedicine.getMedicineType().getCode()));


            if((auxItem.getTimeLineNote() !=null && !auxItem.getTimeLineNote().isEmpty()) || (auxItem.getMedicine().getNote() != null && !auxItem.getMedicine().getNote().isEmpty())){
                auxViewHolder.note_btn.setImageResource(R.drawable.note_icon_green);
            }else{
                auxViewHolder.note_btn.setImageResource(R.drawable.note_icon);
            }


            auxViewHolder.note_btn.setTag(R.id.tag_position,position);
            auxViewHolder.note_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupNotes((int) view.getTag(R.id.tag_position), view);
                }
            });

            if(auxItem.isSOS()) {
                //SOS
                auxViewHolder.taken_text_textView.setText(R.string.emergency);
                auxViewHolder.taken_text_textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.morebiRounded_7pt));
                auxViewHolder.taken_text_textView.setTextColor(context.getResources().getColor(R.color.sos_text_color));

                auxViewHolder.medicine_back_linearLayout.setBackgroundResource(R.drawable.item_medicine_normal);
                auxViewHolder.back_btn_view.setBackgroundResource(R.drawable.item_medicine_normal_btn_back);

                Utils.changeBtnBackgroundMedicine(auxViewHolder.medicine_btn_imageView, R.drawable.item_medicine_sos_btn_back);

                auxViewHolder.time_textView.setText(context.getString(R.string.at_time,timeFormatter.format(auxItem.getStartTime().getTime())));

                auxViewHolder.done_late_img.setImageResource(R.drawable.btn_add_sos);
                auxViewHolder.points_without_background_box.setVisibility(View.VISIBLE);
                auxViewHolder.points_without_background_textView.setText("");
                auxViewHolder.points_with_backgorund_box.setVisibility(View.INVISIBLE);
            }else{
                //Medicine

                if((auxItem.getState() == null || auxItem.getState().isEmpty())){
                    verifyMissedState(auxItem);
                }

                auxViewHolder.taken_text_textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.morebiRounded_6pt));
                auxViewHolder.taken_text_textView.setTextColor(context.getResources().getColor(R.color.text_color_333333));

                if(auxItem.getState() != null && auxItem.getState().equals(TimelineItem.STATE_DONE)){
                    auxViewHolder.taken_text_textView.setText("");

                    auxViewHolder.medicine_back_linearLayout.setBackgroundResource(R.drawable.item_medicine_normal);
                    auxViewHolder.back_btn_view.setBackgroundResource(R.drawable.item_medicine_normal_btn_back);
                    Utils.changeBtnBackgroundMedicine(auxViewHolder.medicine_btn_imageView, R.drawable.item_medicine_normal_btn_back_green);

                    auxViewHolder.time_textView.setText(context.getString(R.string.at_time,timeFormatter.format(auxItem.getDateTaken().getTime())));

                    auxViewHolder.done_late_img.setImageResource(R.drawable.check_green_icon);

                    auxViewHolder.points_without_background_box.setVisibility(View.VISIBLE);
                    auxViewHolder.points_without_background_textView.setText(""+auxItem.getPointWon());
                    auxViewHolder.points_without_background_textView.setTextColor(context.getResources().getColor(R.color.medicine_normal_text_colro));

                    auxViewHolder.points_with_backgorund_box.setVisibility(View.INVISIBLE);
                }else if(auxItem.getState() != null && auxItem.getState().equals(TimelineItem.STATE_MISSED)){
                    auxViewHolder.taken_text_textView.setText(R.string.failed_medicine_text);

                    auxViewHolder.medicine_back_linearLayout.setBackgroundResource(R.drawable.item_medicine_failed);
                    auxViewHolder.back_btn_view.setBackgroundResource(R.drawable.item_medicine_failed_btn_back);
                    Utils.changeBtnBackgroundMedicine(auxViewHolder.medicine_btn_imageView, R.drawable.item_medicine_failed_btn_back);


                    auxViewHolder.time_textView.setText("");

                    auxViewHolder.done_late_img.setImageBitmap(null);

                    auxViewHolder.points_without_background_box.setVisibility(View.VISIBLE);
                    auxViewHolder.points_without_background_textView.setText(""+auxItem.getPointWon());
                    auxViewHolder.points_without_background_textView.setTextColor(context.getResources().getColor(R.color.medicine_failed));

                    auxViewHolder.points_with_backgorund_box.setVisibility(View.INVISIBLE);
                }else if(auxItem.getState() != null && auxItem.getState().equals(TimelineItem.STATE_LATE)){
                    auxViewHolder.taken_text_textView.setText(R.string.medicine_late_msg);

                    auxViewHolder.medicine_back_linearLayout.setBackgroundResource(R.drawable.item_medicine_late);
                    auxViewHolder.back_btn_view.setBackgroundResource(R.drawable.item_medicine_late_btn_back);
                    Utils.changeBtnBackgroundMedicine(auxViewHolder.medicine_btn_imageView, R.drawable.item_medicine_late_btn_back);

                    auxViewHolder.time_textView.setText(context.getString(R.string.at_time,timeFormatter.format(auxItem.getDateTaken().getTime())));

                    auxViewHolder.points_without_background_box.setVisibility(View.VISIBLE);

                    auxViewHolder.done_late_img.setImageResource(R.drawable.check_yellow_icon);
                    auxViewHolder.points_without_background_textView.setText(""+auxItem.getPointWon());
                    auxViewHolder.points_without_background_textView.setTextColor(context.getResources().getColor(R.color.medicine_late));

                    auxViewHolder.points_with_backgorund_box.setVisibility(View.INVISIBLE);
                }else{
                    //Por Tomar

                    auxViewHolder.itemView.setTag(R.id.tag_is_active,true);

                    auxViewHolder.time_textView.setText(context.getResources().getString(R.string.time_interval,timeFormatter.format(auxItem.getStartTime().getTime()), timeFormatter.format(auxItem.getEndTime().getTime())));

                    auxViewHolder.points_without_background_box.setVisibility(View.INVISIBLE);

                    if(auxItem.getEndTime().before(new GregorianCalendar())){
                        //LATE
                        auxViewHolder.taken_text_textView.setText(R.string.take_late_medicine);

                        auxViewHolder.medicine_back_linearLayout.setBackgroundResource(R.drawable.item_medicine_late);
                        auxViewHolder.back_btn_view.setBackgroundResource(R.drawable.item_medicine_late_btn_back);
                        Utils.changeBtnBackgroundMedicine(auxViewHolder.medicine_btn_imageView, R.drawable.item_medicine_late_btn_back);

                        auxViewHolder.points_with_background_textView.setBackgroundResource(R.drawable.yellow_circle);

                        auxViewHolder.points_with_background_textView.setText(""+(auxItem.getTimePoints() >= 15?15:auxItem.getTimePoints()));

                        animate = true;

                        auxViewHolder.itemView.setTag(R.id.tag_is_active,false);
                    }else{

                        animate = true;

                        auxViewHolder.taken_text_textView.setText("");
                        auxViewHolder.medicine_back_linearLayout.setBackgroundResource(R.drawable.item_medicine_normal);
                        auxViewHolder.back_btn_view.setBackgroundResource(R.drawable.item_medicine_normal_btn_back);

                        auxViewHolder.points_with_background_textView.setText(""+auxItem.getTimePoints());

                        //ON TIME
                        GregorianCalendar auxTimeStartCounter = new GregorianCalendar();
                        auxTimeStartCounter.setTimeInMillis(auxItem.getStartTime().getTimeInMillis());
                        auxTimeStartCounter.add(Calendar.HOUR_OF_DAY,-2);

                        GregorianCalendar auxTimeValidTake = new GregorianCalendar();
                        auxTimeValidTake.setTimeInMillis(auxItem.getStartTime().getTimeInMillis());
                        auxTimeValidTake.add(Calendar.HOUR_OF_DAY,-1);

                        GregorianCalendar now = new GregorianCalendar();

                        if(now.compareTo(auxTimeStartCounter) >= 0 && now.compareTo(auxItem.getEndTime()) <= 0){
                            // actual
                            if(now.compareTo(auxTimeStartCounter) >= 0 && now.compareTo(auxTimeValidTake) < 0){
                                //1 hora antes nao pode tomar;
                                Log.d("ACTUAL","ACTUAL 2H ANTES");

                                auxViewHolder.taken_text_textView.setVisibility(View.GONE);
                                auxViewHolder.timer_text_textView.setVisibility(View.VISIBLE);
                                auxViewHolder.timer_text_textView.setTimerDate(auxTimeValidTake, auxItem, new MorebiRoundedMediumTimerTextView.TimerTextViewListener() {
                                    @Override
                                    public void onTimeEnd(Object obj) {
                                        notifyDataSetChanged();
                                    }
                                });

                                auxViewHolder.medicine_podology_textView.setTextColor(context.getResources().getColor(R.color.medicine_actual));
                                auxViewHolder.medicine_name_textView.setTextColor(context.getResources().getColor(R.color.medicine_actual));

                                Utils.changeBtnBackgroundMedicine(auxViewHolder.medicine_btn_imageView, R.drawable.item_medicine_counting_btn_back);

                                auxViewHolder.time_textView.setTextColor(context.getResources().getColor(R.color.medicine_actual));
                                auxViewHolder.points_with_background_textView.setBackgroundResource(R.drawable.actual_circle);
                            }else{
                                //ja pode tomar
                                Log.d("ACTUAL","ACTUAL PODE TOMAR");
                                Utils.changeBtnBackgroundMedicine(auxViewHolder.medicine_btn_imageView, R.drawable.item_medicine_normal_btn_back_grey);
                                auxViewHolder.time_textView.setTextColor(context.getResources().getColor(R.color.medicine_actual));
                                auxViewHolder.points_with_background_textView.setBackgroundResource(R.drawable.actual_circle);

                                auxViewHolder.taken_text_textView.setVisibility(View.GONE);
                                auxViewHolder.timer_text_textView.setVisibility(View.VISIBLE);
                                auxViewHolder.timer_text_textView.setTimerDate(auxItem.getEndTime(), auxItem, new MorebiRoundedMediumTimerTextView.TimerTextViewListener() {
                                    @Override
                                    public void onTimeEnd(Object obj) {
                                        notifyDataSetChanged();
                                        updateTodayPos();
                                        listener.updateActiveMedicinePos();
                                    }
                                });
                            }
                        }else{
                            //futuros
                            //Log.d("FUTURO","FUTURO");
                            auxViewHolder.medicine_podology_textView.setTextColor(context.getResources().getColor(R.color.medicine_future));
                            auxViewHolder.medicine_name_textView.setTextColor(context.getResources().getColor(R.color.medicine_future));
                            Utils.changeBtnBackgroundMedicine(auxViewHolder.medicine_btn_imageView, R.drawable.item_medicine_future_btn_back);
                            auxViewHolder.time_textView.setTextColor(context.getResources().getColor(R.color.medicine_future));
                            auxViewHolder.points_with_background_textView.setBackgroundResource(R.drawable.future_circle);
                        }
                    }

                    int padding = context.getResources().getDimensionPixelSize(R.dimen.points_padding);
                    int paddingTopWithExtra = context.getResources().getDimensionPixelSize(R.dimen.points_padding_top);

                    auxViewHolder.points_with_background_textView.setPadding(padding,paddingTopWithExtra,padding,padding);
                    auxViewHolder.points_with_background_textView.setTextColor(context.getResources().getColor(R.color.white));
                    auxViewHolder.points_with_backgorund_box.setVisibility(View.VISIBLE);
                }
            }



            auxViewHolder.medicine_btn_imageView.setTag(R.id.tag_animating, false);
            //auxViewHolder.medicine_btn_imageView.clearAnimation();
            if(animate){
                //Utils.animateBtnToTakeMedicine(context, auxViewHolder.medicine_btn_imageView);
                auxViewHolder.medicine_btn_imageView.setTag(R.id.tag_position,position);
                auxViewHolder.medicine_btn_imageView.setTag(R.id.tag_viewholder, auxViewHolder);
                auxViewHolder.medicine_btn_imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = (int) view.getTag(R.id.tag_position);

                        listener.takeMedicine(view, position, innerTimeline.get(position).getTimelineItem());
                    }
                });
            }else{
                auxViewHolder.medicine_btn_imageView.clearAnimation();
                auxViewHolder.medicine_btn_imageView.setOnClickListener(null);
            }
        }
    }

    private void showPopupNotes(int position, View viewIcon) {
        listener.notesPopUpOpen();

        InnerTimelineItem itemAux = innerTimeline.get(position);
        popUpAuxTimelineItem = itemAux.getTimelineItem();

        int[] iconLocation = new int[2];
        viewIcon.getLocationInWindow(iconLocation);

        View popUpView = LayoutInflater.from(context).inflate(R.layout.note_popup, null);

        if(popUpAuxTimelineItem.isSOS()){
            popUpView.findViewById(R.id.medicine_notes).setVisibility(View.GONE);

            String takeText = context.getString(R.string.no_note);

            if(popUpAuxTimelineItem.getMedicine().getNote()!=null && !popUpAuxTimelineItem.getMedicine().getNote().isEmpty()){
                takeText = popUpAuxTimelineItem.getMedicine().getNote();
            }

            ((TextView) popUpView.findViewById(R.id.take_note_textview)).setText(takeText);
        }else{
            String medicineText = context.getString(R.string.no_note);

            if(popUpAuxTimelineItem.getMedicine().getNote()!=null && !popUpAuxTimelineItem.getMedicine().getNote().isEmpty()){
                medicineText = popUpAuxTimelineItem.getMedicine().getNote();
            }

            ((TextView) popUpView.findViewById(R.id.medicine_note_textview)).setText(medicineText);

            String takeText = context.getString(R.string.no_note);
            int color = -1;

            if(popUpAuxTimelineItem.getTimeLineNote()!=null && !popUpAuxTimelineItem.getTimeLineNote().isEmpty()){
                takeText = popUpAuxTimelineItem.getTimeLineNote();
            }else{
                takeText = context.getString(R.string.add_note);
                color = context.getResources().getColor(R.color.colorPrimary);
            }

            ((TextView) popUpView.findViewById(R.id.take_note_textview)).setText(takeText);

            if(color != -1){
                ((TextView) popUpView.findViewById(R.id.take_note_textview)).setTextColor(color);
            }

            popUpView.findViewById(R.id.take_note_textview).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);

                    alert.setMessage(R.string.add_note_message);
                    //alert.setTitle(R.string.add_note);

                    final View addViewLayout = LayoutInflater.from(context).inflate(R.layout.add_note, null);
                    alert.setView(addViewLayout);

                    alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String takenNote = ((EditText) addViewLayout.findViewById(R.id.take_note_editText)).getText().toString();

                            if(takenNote.isEmpty()){
                                mpopup.dismiss();
                            }else{

                                if(AppController.getmInstance().getTimelineDataSource().updateTimelineNote(popUpAuxTimelineItem.getId(),takenNote)){
                                    popUpAuxTimelineItem.setTimeLineNote(takenNote);

                                    AppController.getmInstance().forceSyncManual();

                                    notifyDataSetChanged();

                                    mpopup.dismiss();
                                }else{
                                    Toast.makeText(context, R.string.error_adding_note, Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    });

                    alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });

                    alert.show();
                }
            });
        }

        popUpView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        int height=popUpView.getMeasuredHeight();
        int marginLeft = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);

        int margLeftIcon = context.getResources().getDimensionPixelSize(R.dimen.tendp);

        int popUpX = marginLeft-margLeftIcon;
        int popUpY = iconLocation[1]-height;

        if(popUpY < topLimit){
            popUpView.findViewById(R.id.down_triangle).setVisibility(View.GONE);
            popUpView.findViewById(R.id.top_triangle).setVisibility(View.VISIBLE);

            popUpY = iconLocation[1]+viewIcon.getHeight();
        }


        mpopup = new PopupWindow(popUpView, context.getResources().getDimensionPixelSize(R.dimen.popup_weight), ViewGroup.LayoutParams.WRAP_CONTENT, true); //Creation of popup
        mpopup.setAnimationStyle(android.R.style.Animation_Dialog);
        mpopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mpopup.setOutsideTouchable(true);



        mpopup.showAtLocation(popUpView, Gravity.NO_GRAVITY, popUpX, popUpY);    // Displaying popup

        mpopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popUpAuxTimelineItem = null;
                listener.notesPopUpClose();
            }
        });
    }

    public void animateTakeMedicine(RecyclerView.ViewHolder viewholderAux, boolean onTime, int points, BigDecimal multiplier, GregorianCalendar takenTime, Animation.AnimationListener listenerAnim) {
        if(viewholderAux instanceof ViewHolderMedicine) {


            ViewHolderMedicine viewholder = (ViewHolderMedicine) viewholderAux;

            viewholder.medicine_btn_imageView.clearAnimation();
            viewholder.medicine_btn_imageView.setOnClickListener(null);

            if (!onTime) {
                viewholder.done_late_img.setImageResource(R.drawable.check_yellow_icon);
                viewholder.points_without_background_textView.setText("" + points);
                viewholder.points_without_background_textView.setTextColor(context.getResources().getColor(R.color.medicine_late));


                viewholder.taken_text_textView.setText(R.string.medicine_late_msg);

                viewholder.multiplier_textView.setText("");
            } else {
                viewholder.done_late_img.setImageResource(R.drawable.check_green_icon);
                viewholder.points_without_background_textView.setText("" + points);
                viewholder.points_without_background_textView.setTextColor(context.getResources().getColor(R.color.medicine_normal_text_colro));

                viewholder.multiplier_textView.setText("x" + multiplier.toPlainString());
            }

            viewholder.time_textView.setText(context.getString(R.string.at_time,timeFormatter.format(takenTime.getTime())));

            Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            viewholder.multiplier_textView.startAnimation(anim);

            Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.rotate_fade_out);
            viewholder.points_with_background_textView.startAnimation(anim2);

            Animation anim3 = AnimationUtils.loadAnimation(context, R.anim.fade_in_fill_enable);
            anim3.setAnimationListener(listenerAnim);
            viewholder.points_without_background_box.startAnimation(anim3);
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if(holder instanceof ViewHolderMedicine){
            ViewHolderMedicine aux = (ViewHolderMedicine) holder;

            if((boolean) aux.medicine_btn_imageView.getTag(R.id.tag_animating)){
                Utils.animateBtnToTakeMedicine(context, aux.medicine_btn_imageView);
            }
        }else if(holder instanceof ViewHolderPoll){
            ViewHolderPoll aux = (ViewHolderPoll) holder;

            if((boolean) aux.medicine_btn_imageView.getTag(R.id.tag_animating)){
                Utils.animateBtnToTakeMedicine(context, aux.medicine_btn_imageView);
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    private void verifyMissedState(TimelineItem auxItem) {
        GregorianCalendar auxToday = new GregorianCalendar();

        if(!Utils.isToday(auxItem.getStartTime())){
            GregorianCalendar auxDate = new GregorianCalendar();
            auxDate.setTimeInMillis(auxItem.getEndTime().getTimeInMillis());
            auxDate.add(Calendar.HOUR_OF_DAY, 10);

            if(auxDate.before(auxToday)){
                if(AppController.getmInstance().getTimelineDataSource().updateTimelineState(auxItem.getId(), TimelineItem.STATE_MISSED)){
                    auxItem.setState(TimelineItem.STATE_MISSED);

                    if(AppController.getmInstance().getUserDataSource().resetActualBonus(AppController.getmInstance().getActiveUser().getId())){
                        AppController.getmInstance().getActiveUser().setActualBonus(new BigDecimal(1));
                    }

                    AppController.getmInstance().updateUserStats(true);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return innerTimeline.size();
    }

    public void updateTodayPos() {
        this.todayPos = -1;

        TimelineItem itemActual = AppController.getmInstance().getTimelineDataSource().getActiveTimelineItem(AppController.getmInstance().getActiveUser().getId());

        boolean enc = false;

        if(itemActual == null){
            enc = true;
        }

        for(TimelineWeek timelineWeek : timeline){
            if(timelineWeek.isOpen()){
                for(TimelineItem timelineItem : timelineWeek.getItems()) {
                    if(!enc){
                        if((timelineItem.getMedicine() !=null || timelineItem.getPoll() !=null) && timelineItem.getId().longValue() == itemActual.getId().longValue()){
                            enc = true;
                            todayPos+=1;
                        }else{
                            todayPos+=1;
                        }
                    }
                }
            }else{
                if(!enc){
                    todayPos += 1;
                }
            }
        }
    }

    public void updateInfoTakenTimelineItem(TimelineItem timelineItemUpdated) {
        for(InnerTimelineItem innerItem : innerTimeline){
            if(innerItem.getTimelineItem()!=null && innerItem.getTimelineItem().getId().longValue() == timelineItemUpdated.getId().longValue()){
                innerItem.getTimelineItem().markMedicineTaken(timelineItemUpdated.getPointWon(),timelineItemUpdated.getState(), timelineItemUpdated.getDateTaken());
                break;
            }
        }
    }

    public static class ViewHolderMedicine extends RecyclerView.ViewHolder {
        public ImageView note_btn;
        public TextView medicine_name_textView;
        public TextView medicine_podology_textView;
        public ImageView done_late_img;
        public TextView points_without_background_textView;
        public View points_without_background_box;
        public TextView points_with_background_textView;
        public View points_with_backgorund_box;
        public TextView time_textView;
        public TextView taken_text_textView;
        public View back_btn_view;
        public ImageView medicine_btn_imageView;
        public View layout_date_box;
        public TextView date_textView;

        public View medicine_back_linearLayout;
        public View top_line_view;

        public MorebiRoundedMediumTimerTextView timer_text_textView;

        public TextView multiplier_textView;

        public ViewHolderMedicine(View v) {
            super(v);

            note_btn = (ImageView) v.findViewById(R.id.note_btn);
            medicine_name_textView = (TextView) v.findViewById(R.id.medicine_name_textView);
            medicine_podology_textView = (TextView) v.findViewById(R.id.medicine_podology_textView);
            done_late_img = (ImageView) v.findViewById(R.id.done_late_img);
            points_without_background_textView = (TextView) v.findViewById(R.id.points_without_background_textView);
            points_without_background_box = v.findViewById(R.id.points_without_background_box);
            points_with_background_textView = (TextView) v.findViewById(R.id.points_with_background_textView);
            points_with_backgorund_box = v.findViewById(R.id.points_with_backgorund_box);
            time_textView = (TextView) v.findViewById(R.id.time_textView);
            taken_text_textView = (TextView) v.findViewById(R.id.taken_text_textView);
            back_btn_view = v.findViewById(R.id.back_btn_view);
            medicine_btn_imageView = (ImageView) v.findViewById(R.id.medicine_btn_imageView);
            layout_date_box = v.findViewById(R.id.layout_date_box);
            date_textView = (TextView) layout_date_box.findViewById(R.id.date_textView);

            medicine_back_linearLayout = v.findViewById(R.id.medicine_back_linearLayout);
            top_line_view = v.findViewById(R.id.top_line_view);

            timer_text_textView = (MorebiRoundedMediumTimerTextView) v.findViewById(R.id.timer_text_textView);

            multiplier_textView = (TextView) v.findViewById(R.id.multiplier_textView);

        }
    }

    public static class ViewHolderWeekResume extends RecyclerView.ViewHolder {
        public TextView week_textView;
        public TextView total_meds_textView;
        public TextView total_points_textView;
        public TextView total_ok_meds_textView;
        public TextView total_ok_meds_percent_textView;
        public TextView total_late_meds_textView;
        public TextView total_late_meds_percent_textView;
        public TextView total_failed_meds_textView;
        public TextView total_failed_meds_percent_textView;
        public View view_all_btn;
        public View top_line_view;

        public ViewHolderWeekResume(View v) {
            super(v);

            week_textView = (TextView) v.findViewById(R.id.week_textView);
            total_meds_textView = (TextView) v.findViewById(R.id.total_meds_textView);
            total_points_textView = (TextView) v.findViewById(R.id.total_points_textView);
            total_ok_meds_textView = (TextView) v.findViewById(R.id.total_ok_meds_textView);
            total_ok_meds_percent_textView = (TextView) v.findViewById(R.id.total_ok_meds_percent_textView);
            total_late_meds_textView = (TextView) v.findViewById(R.id.total_late_meds_textView);
            total_late_meds_percent_textView = (TextView) v.findViewById(R.id.total_late_meds_percent_textView);
            total_failed_meds_textView = (TextView) v.findViewById(R.id.total_failed_meds_textView);
            total_failed_meds_percent_textView = (TextView) v.findViewById(R.id.total_failed_meds_percent_textView);
            view_all_btn = v.findViewById(R.id.view_all_btn);
            top_line_view = v.findViewById(R.id.top_line_view);
        }
    }

    public static class ViewHolderPoll extends RecyclerView.ViewHolder {
        public TextView points_without_background_textView;
        public View points_without_background_box;
        //public TextView points_with_background_textView;
        //public View points_with_backgorund_box;
        public TextView time_textView;
        public TextView poll_textView;
        public View back_btn_view;
        public ImageView medicine_btn_imageView;
        public View layout_date_box;
        public TextView date_textView;

        public View top_line_view;

        public TextView multiplier_textView;

        public TextView poll_type_textView;

        public ViewHolderPoll(View v) {
            super(v);

            points_without_background_textView = (TextView) v.findViewById(R.id.points_without_background_textView);
            points_without_background_box = v.findViewById(R.id.points_without_background_box);
            //points_with_background_textView = (TextView) v.findViewById(R.id.points_with_background_textView);
            //points_with_backgorund_box = v.findViewById(R.id.points_with_backgorund_box);
            time_textView = (TextView) v.findViewById(R.id.time_textView);
            poll_textView = (TextView) v.findViewById(R.id.poll_textView);
            back_btn_view = v.findViewById(R.id.back_btn_view);
            medicine_btn_imageView = (ImageView) v.findViewById(R.id.medicine_btn_imageView);
            layout_date_box = v.findViewById(R.id.layout_date_box);
            date_textView = (TextView) layout_date_box.findViewById(R.id.date_textView);

            top_line_view = v.findViewById(R.id.top_line_view);

            multiplier_textView = (TextView) v.findViewById(R.id.multiplier_textView);

            poll_type_textView = (TextView) v.findViewById(R.id.poll_type_textView);
        }
    }

    public static class ViewHolderBadge extends RecyclerView.ViewHolder {
        public ImageView badge_icon;
        public View layout_date_box;
        public View top_line_view;
        public TextView date_textView;

        public ViewHolderBadge(View v) {
            super(v);

            badge_icon = (ImageView) v.findViewById(R.id.badge_icon);
            layout_date_box = v.findViewById(R.id.layout_date_box);
            top_line_view = v.findViewById(R.id.top_line_view);
            date_textView = (TextView) layout_date_box.findViewById(R.id.date_textView);
        }
    }

    private class InnerTimelineItem{
        private int type;
        private boolean showDate;
        private TimelineWeek timelineWeek;
        private TimelineItem timelineItem;

        public InnerTimelineItem(int type, boolean showDate, TimelineWeek timelineWeek, TimelineItem timelineItem) {
            this.type = type;
            this.showDate = showDate;
            this.timelineWeek = timelineWeek;
            this.timelineItem = timelineItem;
        }

        public int getType() {
            return type;
        }

        public TimelineWeek getTimelineWeek() {
            return timelineWeek;
        }

        public boolean isShowDate() {
            return showDate;
        }

        public TimelineItem getTimelineItem() {
            return timelineItem;
        }
    }

    public int getTodayPos() {
        return todayPos<= getItemCount()-1 && todayPos!=-1 ? todayPos : getItemCount();
    }

    public interface TimelineAdapterInterface{
        void updateActiveMedicinePos();
        void notesPopUpOpen();
        void notesPopUpClose();
        void takeMedicine(View view, int position, TimelineItem timelineItem);
    }
}
