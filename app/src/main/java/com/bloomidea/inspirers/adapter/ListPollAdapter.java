package com.bloomidea.inspirers.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bloomidea.inspirers.R;
import com.bloomidea.inspirers.model.ListPoll;
import com.bloomidea.inspirers.model.Poll;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by michellobato on 27/04/17.
 */

public class ListPollAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity context;
    private ArrayList<ListPoll> listPolls;
    private ListPollListener listener;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public ListPollAdapter(Activity context, ArrayList<ListPoll> listPolls, ListPollListener listener) {
        this.context = context;
        this.listPolls = listPolls;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_carat_list, parent, false);

        RecyclerView.ViewHolder viewHolder = new ViewHolderListPoll(view);;

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolderListPoll auxHolder = (ViewHolderListPoll) holder;
        ListPoll item = listPolls.get(position);

        auxHolder.date_textview.setText(dateFormat.format(item.getAsweredDate().getTime()));
        auxHolder.name_textView.setText(item.getPoll().getPoolType().equals(Poll.POLL_TYPE_CARAT)?R.string.carat_poll:R.string.simptoms_poll);

        auxHolder.itemView.setTag(R.id.tag_position,position);
        auxHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.openPoll(listPolls.get((int) view.getTag(R.id.tag_position)));
            }
        });
    }

    public static class ViewHolderListPoll extends RecyclerView.ViewHolder {
        public TextView name_textView;
        public TextView date_textview;

        public ViewHolderListPoll(View v) {
            super(v);

            name_textView = (TextView) v.findViewById(R.id.name_textView);
            date_textview = (TextView) v.findViewById(R.id.date_textview);
        }
    }

    public void setListPolls(ArrayList<ListPoll> newList){
        this.listPolls = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listPolls.size();
    }

    public interface ListPollListener{
        void openPoll(ListPoll poll);
    }
}
