package com.bloomidea.inspirers.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.customViews.MorebiRoundedTimerTextView;
import com.bloomidea.inspirers.model.Request;
import com.bloomidea.inspirers.model.Review;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.swipeLayout.SwipeLayout;
import com.bloomidea.inspirers.swipeLayout.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by michellobato on 06/04/17.
 */

public class WarriorsAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TITLE = 101;
    private static final int TYPE_USER_TO_ME = 102;
    private static final int TYPE_USER_BY_ME = 103;
    private static final int TYPE_USER_SUGGESTION = 104;

    private Activity context;

    private ArrayList<Request> toMeList;
    private ArrayList<Request> byMeList;
    private ArrayList<User> suggestedList;

    private ArrayList<InnerItem> listItems;

    private WarriorsAdapterListener listener;

    public WarriorsAdapter(Activity context, WarriorsAdapterListener listener) {
        this.context = context;
        this.toMeList = new ArrayList<>();
        this.byMeList = new ArrayList<>();
        this.suggestedList = new ArrayList<>();

        this.listener = listener;

        initInnerItems();
    }

    public int totalByMe(){
        return byMeList.size();
    }

    public int totalToMe(){
        return toMeList.size();
    }

    private void initInnerItems() {
        listItems = new ArrayList<>();

        if(!toMeList.isEmpty()){
            listItems.add(new InnerItem(TYPE_TITLE,context.getString(R.string.title_warriors_to_answer)));
            for(Request r : toMeList){
                listItems.add(new InnerItem(TYPE_USER_TO_ME, r));
            }
        }

        if(!byMeList.isEmpty()){
            listItems.add(new InnerItem(TYPE_TITLE,context.getString(R.string.title_warriors_asked)));
            for(Request r : byMeList){
                listItems.add(new InnerItem(TYPE_USER_BY_ME, r));
            }
        }

        if(!suggestedList.isEmpty()){
            listItems.add(new InnerItem(TYPE_TITLE,context.getString(R.string.title_warriors_to_ask)));
            for(User u : suggestedList){
                listItems.add(new InnerItem(TYPE_USER_SUGGESTION, u));
            }
        }
    }

    public void addAllSuggestedList(ArrayList<User> results) {
        if(toMeList != null && !toMeList.isEmpty()){
            for(Request r : toMeList){
                User remove = null;
                for(User u : results){
                    if(r.getProfile().getUid().equals(u.getUid())){
                        remove = u;
                        break;
                    }
                }

                if(remove!=null){
                    results.remove(remove);
                }
            }
        }

        if(byMeList != null && !byMeList.isEmpty()){
            for(Request r : byMeList){
                User remove = null;
                for(User u : results){
                    if(r.getProfile().getUid().equals(u.getUid())){
                        remove = u;
                        break;
                    }
                }

                if(remove!=null){
                    results.remove(remove);
                }
            }
        }

        suggestedList.addAll(results);

        initInnerItems();

        notifyDataSetChanged();
    }

    public void addAllToMeList(ArrayList<Request> results) {
        toMeList.addAll(results);

        initInnerItems();

        notifyDataSetChanged();
    }

    public void addAllByMeList(ArrayList<Request> results) {
        byMeList.addAll(results);

        initInnerItems();

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return listItems.get(position).getType();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        RecyclerView.ViewHolder viewHolder;

        if (viewType == TYPE_TITLE) {
            view = LayoutInflater.from(context).inflate(R.layout.item_title, parent, false);
            viewHolder = new ViewHolderTitle(view);
        } else if(viewType == TYPE_USER_TO_ME) {
            view = LayoutInflater.from(context).inflate(R.layout.item_user_warrior_accept_deny, parent, false);
            viewHolder = new ViewHolderUserToMe(view);
        } else if(viewType == TYPE_USER_BY_ME) {
            view = LayoutInflater.from(context).inflate(R.layout.item_user_warrior_request, parent, false);
            viewHolder = new ViewHolderUserByMe(view);
        } else{
            //TYPE_USER
            view = LayoutInflater.from(context).inflate(R.layout.item_user_warrior, parent, false);
            viewHolder = new ViewHolderUserInfoWarriors(view);
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        InnerItem item = listItems.get(position);

        if(holder instanceof ViewHolderTitle){
            ((ViewHolderTitle) holder).title_textView.setText(item.getTitle());
        }else if(holder instanceof ViewHolderUserToMe){
            final ViewHolderUserToMe auxHolder = (ViewHolderUserToMe) holder;

            auxHolder.setUserInfo(context,item.getRequest().getProfile());

            auxHolder.btnAccept.setTag(R.id.tag_position,position);
            auxHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag(R.id.tag_position);
                    Request request = listItems.get(pos).getRequest();

                    Log.d("accept",request.getProfile().getUserName());

                    listener.acceptBtnClick(request,pos);
                }
            });

            auxHolder.btnDeny.setTag(R.id.tag_position,position);
            auxHolder.btnDeny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag(R.id.tag_position);
                    Request request = listItems.get(pos).getRequest();

                    Log.d("deny",request.getProfile().getUserName());

                    listener.denyBtnClick(request,pos);
                }
            });
        }else if(holder instanceof ViewHolderUserByMe){
            final ViewHolderUserByMe auxHolder = (ViewHolderUserByMe) holder;
            auxHolder.setUserInfo(context,item.getRequest().getProfile());

            //auxHolder.swipeRevealLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            auxHolder.swipeRevealLayout.setDrag(SwipeLayout.DragEdge.Right, auxHolder.bottomWrapper);
            auxHolder.swipeRevealLayout.setTag(R.id.tag_viewholder,auxHolder);
            auxHolder.swipeRevealLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onClose(SwipeLayout layout) {
                    ViewHolderUserByMe auxHolder = (ViewHolderUserByMe) layout.getTag(R.id.tag_viewholder);
                    auxHolder.delete_btn.setClickable(false);
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                    //you are swiping.
                }

                @Override
                public void onStartOpen(SwipeLayout layout) {

                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    ViewHolderUserByMe auxHolder = (ViewHolderUserByMe) layout.getTag(R.id.tag_viewholder);
                    auxHolder.delete_btn.setClickable(true);
                }

                @Override
                public void onStartClose(SwipeLayout layout) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                    //when user's hand released.
                }
            });

            GregorianCalendar dateEnd = new GregorianCalendar();
            dateEnd.setTimeInMillis(item.getRequest().getCreated().getTimeInMillis());
            dateEnd.add(Calendar.DAY_OF_MONTH,1);

            if (dateEnd.getTimeInMillis() > (new GregorianCalendar()).getTimeInMillis()) {
                auxHolder.timer_textView.setTimerDate(dateEnd, item, new MorebiRoundedTimerTextView.TimerTextViewListener() {
                    @Override
                    public void onTimeEnd(Object obj) {
                        InnerItem aux = ((InnerItem) obj);

                        listItems.remove(aux);
                        byMeList.remove(aux.getRequest());

                        if (byMeList.isEmpty()) {
                            int posTitle = toMeList.size() + (toMeList.isEmpty() ? 0 : 1);

                            listItems.remove(posTitle);
                        }

                        notifyDataSetChanged();
                    }
                });
            }else{
                auxHolder.timer_textView.setTimerDate(null,null,null);
            }


            auxHolder.delete_btn.setTag(R.id.tag_position,position);
            auxHolder.delete_btn.setTag(R.id.tag_viewholder,auxHolder);
            auxHolder.delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag(R.id.tag_position);

                    ViewHolderUserByMe auxHolder = (ViewHolderUserByMe) view.getTag(R.id.tag_viewholder);
                    auxHolder.swipeRevealLayout.close();

                    Request request = listItems.get(pos).getRequest();

                    Log.d("delete",request.getProfile().getUserName());

                    listener.deleteBtnClick(request, pos);
                }
            });
            auxHolder.delete_btn.setClickable(false);
        }else{
            //ViewHolderUserInfoWarriors
            final ViewHolderUserInfoWarriors auxHolder = (ViewHolderUserInfoWarriors) holder;

            auxHolder.setUserInfo(context,item.getUser()==null?item.getRequest().getProfile():item.getUser());

            auxHolder.btnAsk.setTag(R.id.tag_position,position);
            auxHolder.btnAsk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag(R.id.tag_position);
                    User userToAsk = listItems.get(pos).getUser();

                    listener.askBtnClick(userToAsk,pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void warriorAccepted(int positionOnListItems) {
        InnerItem aux = listItems.get(positionOnListItems);

        if(aux!=null){
            listItems.remove(aux);
            toMeList.remove(aux.getRequest());

            if(toMeList.isEmpty()){
                listItems.remove(0);
            }

            notifyDataSetChanged();
        }
    }

    public void warriorDenied(int positionOnListItems) {
        InnerItem aux = listItems.get(positionOnListItems);

        if(aux!=null){
            listItems.remove(aux);
            toMeList.remove(aux.getRequest());

            suggestedList.add(aux.getRequest().getProfile());

            initInnerItems();

            notifyDataSetChanged();
        }
    }

    public void warriorDeleted(int positionOnListItems) {
        InnerItem aux = listItems.get(positionOnListItems);

        if(aux!=null){
            listItems.remove(aux);
            byMeList.remove(aux.getRequest());

            suggestedList.add(aux.getRequest().getProfile());

            initInnerItems();

            notifyDataSetChanged();
        }
    }

    public void warriorAsked(String nid, String status, String requestType, int positionOnListItems) {
        InnerItem aux = listItems.get(positionOnListItems);

        if(aux!=null){
            listItems.remove(aux);
            suggestedList.remove(aux.getUser());

            Request auxR = new Request(nid, requestType, status, aux.getUser(), new GregorianCalendar());


            byMeList.add(auxR);

            initInnerItems();

            notifyDataSetChanged();
        }
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_reveal_layout;
    }

    public void clearAll() {
        toMeList.clear();
        byMeList.clear();
        suggestedList.clear();
        listItems.clear();

        notifyDataSetChanged();
    }

    public static class ViewHolderTitle extends RecyclerView.ViewHolder {
        public TextView title_textView;

        public ViewHolderTitle(View v) {
            super(v);

            title_textView = (TextView) v.findViewById(R.id.title_textView);
        }
    }

    public static class ViewHolderUserInfoWarriors extends ViewHolderUserInfo {
        public View btnAsk;

        public ViewHolderUserInfoWarriors(View v) {
            super(v);

            btnAsk = v.findViewById(R.id.ask_btn);
        }
    }

    public static class ViewHolderUserToMe extends ViewHolderUserInfo {
        public View btnAccept;
        public View btnDeny;

        public ViewHolderUserToMe(View v) {
            super(v);

            btnAccept = v.findViewById(R.id.accept_imageView);
            btnDeny = v.findViewById(R.id.deny_imageView);
        }
    }

    public static class ViewHolderUserByMe extends ViewHolderUserInfo {
        public MorebiRoundedTimerTextView timer_textView;
        public View delete_btn;
        public SwipeLayout swipeRevealLayout;
        public View bottomWrapper;

        public ViewHolderUserByMe(View v) {
            super(v);

            timer_textView = (MorebiRoundedTimerTextView) v.findViewById(R.id.timer_textView);
            delete_btn = v.findViewById(R.id.delete_btn);
            swipeRevealLayout = (SwipeLayout) v.findViewById(R.id.swipe_reveal_layout);
            bottomWrapper = v.findViewById(R.id.bottom_wrapper);
        }
    }

    private class InnerItem{
        private int type;
        private String title;
        private Request request;
        private User user;

        public InnerItem(int type, String title) {
            this.type = type;
            this.title = title;
        }

        public InnerItem(int type, Request request) {
            this.type = type;
            this.request = request;
        }

        public InnerItem(int type, User user) {
            this.type = type;
            this.user = user;
        }

        public int getType() {
            return type;
        }

        public String getTitle() {
            return title;
        }

        public Request getRequest() {
            return request;
        }

        public User getUser() {
            return user;
        }
    }

    public void updateReviewsInfo(String uid, Review newReview) {
        if(listItems != null && listItems.isEmpty()){
            boolean enc = false;
            for(InnerItem innerItem : listItems){
                if(innerItem.getRequest()!=null){
                    if(innerItem.getRequest().getProfile()!=null) {
                        if (innerItem.getRequest().getProfile().getUid().equals(uid)) {
                            innerItem.getRequest().getProfile().updateReviewsInfo(newReview);
                            enc = true;
                        }
                    }
                }else if(innerItem.getUser()!=null){
                    if (innerItem.getUser().getUid().equals(uid)) {
                        innerItem.getUser().updateReviewsInfo(newReview);
                        enc = true;
                    }
                }
            }

            if(enc){
                notifyDataSetChanged();
            }
        }
    }

    public static interface WarriorsAdapterListener{
        void askBtnClick(User user, int positionOnListItems);
        void acceptBtnClick(Request request, int positionOnListItems);
        void denyBtnClick(Request request, int positionOnListItems);
        void deleteBtnClick(Request request, int positionOnListItems);
    }
}
