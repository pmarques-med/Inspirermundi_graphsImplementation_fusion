package com.bloomidea.inspirers.adapter;

import android.app.Activity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.customViews.MorebiRoundedTimerTextView;
import com.bloomidea.inspirers.model.Godchild;
import com.bloomidea.inspirers.model.Request;
import com.bloomidea.inspirers.model.Review;
import com.bloomidea.inspirers.model.User;
import com.bloomidea.inspirers.swipeLayout.SwipeLayout;
import com.bloomidea.inspirers.swipeLayout.adapters.RecyclerSwipeAdapter;
import com.bloomidea.inspirers.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by michellobato on 10/04/17.
 */

public class PeopleAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TITLE = 100;
    private static final int TYPE_SHARE_DOCTOR = 101;
    private static final int TYPE_SEARCH= 102;
    private static final int TYPE_USER_TO_ME = 103;
    private static final int TYPE_USER_BY_ME = 104;
    private static final int TYPE_USER_SUGGESTION = 105;
    private static final int TYPE_MY_SPONSER = 106;
    private static final int TYPE_MY_TEAM = 107 ;

    private Activity context;
    private boolean searchVisible;
    private ArrayList<Request> requestToMe;
    private ArrayList<Request> requestsByMe;
    private ArrayList<User> suggested;
    private User godfather;
    private ArrayList<Godchild> godchilds;

    private PeopleAdapterListener listener;

    private ArrayList<InnerItem> listItems;

    public PeopleAdapter(Activity context, PeopleAdapterListener listener) {
        this.context = context;

        this.searchVisible = true;
        this.requestToMe = new ArrayList<>();
        this.requestsByMe = new ArrayList<>();
        this.suggested = new ArrayList<>();
        this.godfather = null;
        this.godchilds = new ArrayList<>();

        this.listener = listener;

        initInnerItems();
    }

    private void initInnerItems() {
        listItems = new ArrayList<>();

        //listItems.add(new InnerItem(TYPE_SHARE_DOCTOR));

        if(searchVisible) {
            listItems.add(new InnerItem(TYPE_SEARCH));
        }

        if(!requestToMe.isEmpty()){
            listItems.add(new InnerItem(TYPE_TITLE,context.getString(R.string.title_godfather_to_answer)));
            for(Request r : requestToMe){
                listItems.add(new InnerItem(TYPE_USER_TO_ME, r));
            }
        }

        if(!requestsByMe.isEmpty()){
            listItems.add(new InnerItem(TYPE_TITLE,context.getString(R.string.title_godfather_asked)));
            for(Request r : requestsByMe){
                listItems.add(new InnerItem(TYPE_USER_BY_ME, r));
            }
        }

        if(!suggested.isEmpty()){
            listItems.add(new InnerItem(TYPE_TITLE,context.getString(R.string.title_godfather_to_ask)));
            for(User u : suggested){
                listItems.add(new InnerItem(TYPE_USER_SUGGESTION, u));
            }
        }

        if(godfather != null){
            listItems.add(new InnerItem(TYPE_TITLE,context.getString(R.string.title_godfather)));
            listItems.add(new InnerItem(TYPE_MY_SPONSER, godfather));
        }

        if(!godchilds.isEmpty()){
            listItems.add(new InnerItem(TYPE_TITLE,context.getString(R.string.title_my_team)));
            for(Godchild gc : godchilds){
                listItems.add(new InnerItem(TYPE_MY_TEAM, gc));
            }
        }
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
            viewHolder = new WarriorsAdapter.ViewHolderTitle(view);
        } else if (viewType == TYPE_SHARE_DOCTOR){
            view = LayoutInflater.from(context).inflate(R.layout.item_doctor_invite, parent, false);
            viewHolder = new ViewHolderShareDoctor(view);
        } else if (viewType == TYPE_SEARCH){
            view = LayoutInflater.from(context).inflate(R.layout.item_sponsor_search, parent, false);
            viewHolder = new ViewHolderSearch(view);
        } else if(viewType == TYPE_USER_TO_ME){
            view = LayoutInflater.from(context).inflate(R.layout.item_user_warrior_accept_deny, parent, false);
            viewHolder = new WarriorsAdapter.ViewHolderUserToMe(view);
        } else if(viewType == TYPE_USER_BY_ME){
            view = LayoutInflater.from(context).inflate(R.layout.item_user_warrior_request, parent, false);
            viewHolder = new WarriorsAdapter.ViewHolderUserByMe(view);
        } else if(viewType == TYPE_USER_SUGGESTION){
            view = LayoutInflater.from(context).inflate(R.layout.item_user_warrior, parent, false);
            viewHolder = new WarriorsAdapter.ViewHolderUserInfoWarriors(view);
        } else if(viewType == TYPE_MY_SPONSER){
            view = LayoutInflater.from(context).inflate(R.layout.item_user_godfather, parent, false);
            viewHolder = new ViewHolderUserGodfather(view);
        } else{
            //TYPE_MY_TEAM
            view = LayoutInflater.from(context).inflate(R.layout.item_user_godchild, parent, false);
            viewHolder = new ViewHolderUserGodchild(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
       // holder.setIsRecyclable(false);
        InnerItem item = listItems.get(position);

        if(holder instanceof WarriorsAdapter.ViewHolderTitle){
            ((WarriorsAdapter.ViewHolderTitle) holder).title_textView.setText(item.getTitle());
        } else if(holder instanceof ViewHolderShareDoctor){
            ((ViewHolderShareDoctor) holder).btnShareDoctor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.shareWithDoctorClicked();
                }
            });
        } else if(holder instanceof ViewHolderSearch){
            ViewHolderSearch auxViewHolder = ((ViewHolderSearch) holder);

            auxViewHolder.btnSearch.setTag(R.id.tag_viewholder, auxViewHolder);
            auxViewHolder.btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewHolderSearch auxViewHolder = ((ViewHolderSearch) view.getTag(R.id.tag_viewholder));
                    String text = auxViewHolder.search_editText.getText().toString();

                    listener.searchClicked(text);
                }
            });
        } else if(holder instanceof WarriorsAdapter.ViewHolderUserToMe){
            WarriorsAdapter.ViewHolderUserToMe auxHolder = (WarriorsAdapter.ViewHolderUserToMe) holder;

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
        } else if(holder instanceof WarriorsAdapter.ViewHolderUserByMe){
            WarriorsAdapter.ViewHolderUserByMe auxHolder = (WarriorsAdapter.ViewHolderUserByMe) holder;
            auxHolder.setUserInfo(context,item.getRequest().getProfile());

            //auxHolder.swipeRevealLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            auxHolder.swipeRevealLayout.setDrag(SwipeLayout.DragEdge.Right, auxHolder.bottomWrapper);

            auxHolder.swipeRevealLayout.setTag(R.id.tag_viewholder,auxHolder);
            auxHolder.swipeRevealLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onClose(SwipeLayout layout) {
                    WarriorsAdapter.ViewHolderUserByMe auxHolder = (WarriorsAdapter.ViewHolderUserByMe) layout.getTag(R.id.tag_viewholder);
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
                    WarriorsAdapter.ViewHolderUserByMe auxHolder = (WarriorsAdapter.ViewHolderUserByMe) layout.getTag(R.id.tag_viewholder);
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

                        //listItems.remove(aux);
                        requestsByMe.remove(aux.getRequest());
                    /*
                    if(requestsByMe.isEmpty()){
                        int posTitle = toMeList.size() + (toMeList.isEmpty()?0:1);

                        listItems.remove(posTitle);
                    }
                    */

                        initInnerItems();
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
                    WarriorsAdapter.ViewHolderUserByMe auxHolder = (WarriorsAdapter.ViewHolderUserByMe) view.getTag(R.id.tag_viewholder);
                    auxHolder.swipeRevealLayout.close();

                    Request request = listItems.get(pos).getRequest();

                    Log.d("delete",request.getProfile().getUserName());

                    listener.deleteBtnClick(request, pos);
                }
            });
            auxHolder.delete_btn.setClickable(false);
        } else if(holder instanceof WarriorsAdapter.ViewHolderUserInfoWarriors){
            WarriorsAdapter.ViewHolderUserInfoWarriors auxHolder = (WarriorsAdapter.ViewHolderUserInfoWarriors) holder;

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
        } else if(holder instanceof ViewHolderUserGodchild){
            ViewHolderUserGodchild auxHolder = (ViewHolderUserGodchild) holder;

            auxHolder.setUserInfo(context,item.getRequest().getProfile());

            //auxHolder.swipeRevealLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            auxHolder.swipeRevealLayout.setDrag(SwipeLayout.DragEdge.Right, auxHolder.bottomWrapper);
            auxHolder.swipeRevealLayout.setTag(R.id.tag_viewholder,auxHolder);
            auxHolder.swipeRevealLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onClose(SwipeLayout layout) {
                    ViewHolderUserGodchild auxHolder = (ViewHolderUserGodchild) layout.getTag(R.id.tag_viewholder);
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
                    ViewHolderUserGodchild auxHolder = (ViewHolderUserGodchild) layout.getTag(R.id.tag_viewholder);
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

            int buzzTotal = ((Godchild) item.getRequest()).getUserBuzz();

            auxHolder.btnWarnig.setTag(R.id.tag_position,position);

            auxHolder.warnigImg.setImageResource(buzzTotal==0?R.drawable.warning_normal:(buzzTotal==1?R.drawable.warning_yellow:R.drawable.warning_red));

            if(buzzTotal >=2) {
                auxHolder.swipeRevealLayout.setSwipeEnabled(true);

                auxHolder.btnWarnig.setOnClickListener(null);
            }else{
                auxHolder.swipeRevealLayout.setSwipeEnabled(false);

                auxHolder.btnWarnig.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = (int) view.getTag(R.id.tag_position);

                        Godchild godchild = (Godchild) listItems.get(pos).getRequest();

                        listener.sendBuzzBtnClick(godchild, pos);
                    }
                });
            }

            Utils.configureCounter(auxHolder.totalUnreadMessages, item.getRequest().getProfile().getUnreadMessages());

            auxHolder.btnMessage.setTag(R.id.tag_user_profile, item.getRequest().getProfile());
            auxHolder.btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User userProfile = (User) view.getTag(R.id.tag_user_profile);

                    listener.openMessages(userProfile);
                }
            });

            auxHolder.delete_btn.setTag(R.id.tag_position,position);
            auxHolder.delete_btn.setTag(R.id.tag_viewholder,auxHolder);
            auxHolder.delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag(R.id.tag_position);

                    ViewHolderUserGodchild auxHolder = (ViewHolderUserGodchild) view.getTag(R.id.tag_viewholder);
                    auxHolder.swipeRevealLayout.close();

                    Godchild godchild = (Godchild) listItems.get(pos).getRequest();

                    Log.d("delete",godchild.getProfile().getUserName());

                    listener.deleteGodchildBtnClick(godchild, pos);
                }
            });
            auxHolder.delete_btn.setClickable(false);
        } else{
            //ViewHolderUserGodfather

            ViewHolderUserGodfather auxHolder = (ViewHolderUserGodfather) holder;

            auxHolder.setUserInfo(context,item.getUser());

            Utils.configureCounter(auxHolder.totalUnreadMessages, item.getUser().getUnreadMessages());

            auxHolder.btnMessage.setTag(R.id.tag_user_profile, item.getUser());
            auxHolder.btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User userProfile = (User) view.getTag(R.id.tag_user_profile);

                    listener.openMessages(userProfile);
                }
            });

            //auxHolder.swipeRevealLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            auxHolder.swipeRevealLayout.setDrag(SwipeLayout.DragEdge.Right, auxHolder.bottomWrapper);
            auxHolder.swipeRevealLayout.setTag(R.id.tag_viewholder,auxHolder);
            auxHolder.swipeRevealLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onClose(SwipeLayout layout) {
                    ViewHolderUserGodfather auxHolder = (ViewHolderUserGodfather) layout.getTag(R.id.tag_viewholder);
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
                    ViewHolderUserGodfather auxHolder = (ViewHolderUserGodfather) layout.getTag(R.id.tag_viewholder);
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

            auxHolder.delete_btn.setTag(R.id.tag_position,position);
            auxHolder.delete_btn.setTag(R.id.tag_viewholder,auxHolder);
            auxHolder.delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag(R.id.tag_position);

                    ViewHolderUserGodfather auxHolder = (ViewHolderUserGodfather) view.getTag(R.id.tag_viewholder);
                    auxHolder.swipeRevealLayout.close();

                    User user = listItems.get(pos).getUser();

                    Log.d("delete",user.getUserName());

                    listener.deleteGodfatherBtnClick(user, pos);
                }
            });
            auxHolder.delete_btn.setClickable(false);
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void clearAll() {
        this.searchVisible = true;
        this.requestToMe = new ArrayList<>();
        this.requestsByMe = new ArrayList<>();
        this.suggested = new ArrayList<>();
        this.godfather = null;
        this.godchilds = new ArrayList<>();
    }

    public void initInfo(boolean searchVisible, ArrayList requestToMe, ArrayList requestsByMe, ArrayList suggested, User godfather, ArrayList godchilds){
        this.searchVisible = searchVisible;
        this.requestToMe = requestToMe;
        this.requestsByMe = requestsByMe;


        if(requestToMe != null && !requestToMe.isEmpty()){
            for(Request r : (ArrayList<Request>) requestToMe){
                if(suggested!=null && !suggested.isEmpty()) {
                    User remove = null;

                    for (User u : (ArrayList<User>) suggested) {
                        if (r.getProfile().getUid().equals(u.getUid())) {
                            remove = u;
                            break;
                        }
                    }

                    if (remove != null) {
                        suggested.remove(remove);
                    }
                }
            }
        }

        if(requestsByMe != null && !requestsByMe.isEmpty()){
            for(Request r : (ArrayList<Request>) requestsByMe){
                if(suggested!=null && !suggested.isEmpty()) {
                    User remove = null;

                    for (User u : (ArrayList<User>) suggested) {
                        if (r.getProfile().getUid().equals(u.getUid())) {
                            remove = u;
                            break;
                        }
                    }

                    if (remove != null) {
                        suggested.remove(remove);
                    }
                }
            }
        }

        this.suggested = suggested;
        this.godfather = godfather;
        this.godchilds = godchilds;

        initInnerItems();

        notifyDataSetChanged();
    }

    public void godfatherAsked(String nid, String status, String requestType, int positionOnListItems) {
        InnerItem aux = listItems.get(positionOnListItems);

        if(aux!=null){
            listItems.remove(aux);
            suggested.remove(aux.getUser());

            Request auxR = new Request(nid, requestType, status, aux.getUser(), new GregorianCalendar());


            requestsByMe.add(auxR);

            initInnerItems();

            notifyDataSetChanged();
        }
    }

    public void godfatherDenied(int positionOnListItems) {
        InnerItem aux = listItems.get(positionOnListItems);

        if(aux!=null){
            listItems.remove(aux);
            requestToMe.remove(aux.getRequest());

            suggested.add(aux.getRequest().getProfile());

            initInnerItems();

            notifyDataSetChanged();
        }
    }

    public void godfatherDeleted(int positionOnListItems) {
        InnerItem aux = listItems.get(positionOnListItems);

        if(aux!=null){
            listItems.remove(aux);
            requestsByMe.remove(aux.getRequest());

            suggested.add(aux.getRequest().getProfile());

            initInnerItems();

            notifyDataSetChanged();
        }
    }

    public void buzzSent(int positionOnListItems, int newUserBuzz, GregorianCalendar buzzdate) {
        InnerItem aux = listItems.get(positionOnListItems);

        if(aux!=null){
            ((Godchild) aux.getRequest()).setUserBuzz(newUserBuzz, buzzdate);

            for(Godchild g : godchilds){
                if(g.getNid().equals(aux.getRequest().getNid())){
                    g.setUserBuzz(newUserBuzz, buzzdate);
                    break;
                }
            }

            notifyDataSetChanged();
        }

    }

    public void godchilDeleted(Godchild godchild, int positionOnListItems) {
        InnerItem aux = listItems.get(positionOnListItems);

        if(aux!=null){
            listItems.remove(aux);
            godchilds.remove(aux.getRequest());

            initInnerItems();

            notifyDataSetChanged();
        }
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_reveal_layout;
    }

    public void resetCounter(String uid) {
        /*for(Request r : requestToMe){
            if(r.getProfile()!=null && r.getProfile().getUid().equals(uid)){
                r.getProfile().setUnreadMessages(0);
            }
        }

        for(Request r : requestsByMe){
            if(r.getProfile()!=null && r.getProfile().getUid().equals(uid)){
                r.getProfile().setUnreadMessages(0);
            }
        }

        for(User u : suggested){
            if(u.getUid().equals(uid)){
                u.setUnreadMessages(0);
            }
        }*/

        if(godfather!=null && godfather.getUid().equals(uid)){
            godfather.setUnreadMessages(0);
        }

        if(godchilds!=null) {
            for (Godchild g : godchilds) {
                if(g.getProfile()!=null && g.getProfile().getUid().equals(uid)){
                    g.getProfile().setUnreadMessages(0);
                    break;
                }
            }
        }

        initInnerItems();
        notifyDatasetChanged();
    }

    public void addMessageCounter(String uid, int value) {
        /*for(Request r : requestToMe){
            if(r.getProfile()!=null && r.getProfile().getUid().equals(uid)){
                r.getProfile().setUnreadMessages(0);
            }
        }

        for(Request r : requestsByMe){
            if(r.getProfile()!=null && r.getProfile().getUid().equals(uid)){
                r.getProfile().setUnreadMessages(0);
            }
        }

        for(User u : suggested){
            if(u.getUid().equals(uid)){
                u.setUnreadMessages(0);
            }
        }*/

        if(godfather!=null && godfather.getUid().equals(uid)){
            godfather.setUnreadMessages(godfather.getUnreadMessages()+value);
        }

        if(godchilds!=null) {
            for (Godchild g : godchilds) {
                if(g.getProfile()!=null && g.getProfile().getUid().equals(uid)){
                    g.getProfile().setUnreadMessages(g.getProfile().getUnreadMessages()+value);
                    break;
                }
            }
        }

        initInnerItems();
        notifyDatasetChanged();
    }

    private class InnerItem{
        private int type;
        private String title;
        private Request request;
        private User user;

        public InnerItem(int type) {
            this.type = type;
        }

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

    public static class ViewHolderShareDoctor extends RecyclerView.ViewHolder {
        private View btnShareDoctor;

        public ViewHolderShareDoctor(View v) {
            super(v);

            btnShareDoctor = v.findViewById(R.id.share_doctor_btn);
        }
    }

    public static class ViewHolderSearch extends RecyclerView.ViewHolder {
        private EditText search_editText;
        private View btnSearch;

        public ViewHolderSearch(View v) {
            super(v);

            search_editText = (EditText) v.findViewById(R.id.search_editText);
            btnSearch = v.findViewById(R.id.search_btn);
        }
    }

    public static class ViewHolderUserGodfather extends ViewHolderUserInfo {
        public View btnMessage;
        public TextView totalUnreadMessages;
        public SwipeLayout swipeRevealLayout;
        public View bottomWrapper;
        public View delete_btn;

        public ViewHolderUserGodfather(View v) {
            super(v);

            btnMessage = v.findViewById(R.id.msg_btn);
            totalUnreadMessages = (TextView) v.findViewById(R.id.messages_notif);
            swipeRevealLayout = (SwipeLayout) v.findViewById(R.id.swipe_reveal_layout);
            bottomWrapper = v.findViewById(R.id.bottom_wrapper);
            delete_btn = v.findViewById(R.id.delete_btn);
        }
    }

    public static class ViewHolderUserGodchild extends ViewHolderUserGodfather {
        private View btnWarnig;
        private ImageView warnigImg;

        public ViewHolderUserGodchild(View v) {
            super(v);

            btnWarnig = v.findViewById(R.id.warning_btn);
            warnigImg = (ImageView) v.findViewById(R.id.warning_imageView);
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

    public interface PeopleAdapterListener extends WarriorsAdapter.WarriorsAdapterListener{
        void shareWithDoctorClicked();
        void searchClicked(String textToSearch);
        void openMessages(User userProfile);
        void deleteGodchildBtnClick(Godchild godchild, int positionOnListItems);
        void deleteGodfatherBtnClick(User godfather, int positionOnListItems);
        void sendBuzzBtnClick(Godchild godchild, int positionOnListItems);
    }
}
