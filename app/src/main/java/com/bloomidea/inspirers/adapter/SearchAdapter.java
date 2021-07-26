package com.bloomidea.inspirers.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.model.User;

import java.util.ArrayList;

/**
 * Created by michellobato on 21/04/17.
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TITLE = 101;
    private static final int TYPE_USER_SUGGESTION = 102;

    private ArrayList<User> suggestedList;

    private Activity context;

    private SearchAdapterListener listener;

    public SearchAdapter(Activity context, SearchAdapterListener listener) {
        this.context = context;
        this.suggestedList = new ArrayList<>();

        this.listener = listener;
    }

    public void addAllSuggestedList(ArrayList<User> results) {
        suggestedList.addAll(results);

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        RecyclerView.ViewHolder viewHolder;

        if (viewType == TYPE_TITLE) {
            view = LayoutInflater.from(context).inflate(R.layout.item_title, parent, false);
            viewHolder = new ViewHolderTitle(view);
        } else{
            //TYPE_USER_SUGGESTION
            view = LayoutInflater.from(context).inflate(R.layout.item_user_warrior, parent, false);
            viewHolder = new ViewHolderUserSearch(view);
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolderTitle){
            ((ViewHolderTitle) holder).title_textView.setText(R.string.search_results);
        }else {
            ViewHolderUserSearch auxHolder = (ViewHolderUserSearch) holder;
            User item = suggestedList.get(position-1);

            auxHolder.setUserInfo(context,item);

            auxHolder.btnAsk.setTag(R.id.tag_position,position);
            auxHolder.btnAsk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag(R.id.tag_position);
                    User userToAsk = suggestedList.get(pos-1);

                    listener.askBtnClick(userToAsk,pos);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position==0?TYPE_TITLE:TYPE_USER_SUGGESTION;
    }

    @Override
    public int getItemCount() {
        return suggestedList.size()+1;
    }


    public static class ViewHolderTitle extends RecyclerView.ViewHolder {
        public TextView title_textView;

        public ViewHolderTitle(View v) {
            super(v);

            title_textView = (TextView) v.findViewById(R.id.title_textView);
        }
    }

    public static class ViewHolderUserSearch extends ViewHolderUserInfo {
        public View btnAsk;

        public ViewHolderUserSearch(View v) {
            super(v);

            btnAsk = v.findViewById(R.id.ask_btn);
        }
    }

    public static interface SearchAdapterListener{
        void askBtnClick(User user, int positionOnListItems);
    }
}
