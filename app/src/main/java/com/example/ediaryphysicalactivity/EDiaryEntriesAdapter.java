package com.example.ediaryphysicalactivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EDiaryEntriesAdapter extends RecyclerView.Adapter<EDiaryEntriesAdapter.EDiaryEntriesViewHolder> {

    private Context mCtx;

    private List<EDiaryEntry> eDiaryEntryList;

    public EDiaryEntriesAdapter(Context mCtx, List<EDiaryEntry> eDiaryEntryList) {
        this.mCtx = mCtx;
        this.eDiaryEntryList = eDiaryEntryList;
    }

    @Override
    public EDiaryEntriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //System.out.println("TaskViewHolder. viewType: " + viewType);

        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_ediary_entries, parent, false);
        return new EDiaryEntriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EDiaryEntriesViewHolder holder, int position) {

        //System.out.println("onBindViewHolder. position: " + position);

        EDiaryEntry e = eDiaryEntryList.get(position);
        holder.textViewCardFirst.setText(e.getDate_time_str());
        holder.textViewCardSecond.setText(e.getAttr_str_1());
    }

    @Override
    public int getItemCount() {

        //System.out.println("getItemCount. taskList.size: " + taskList.size());

        return eDiaryEntryList.size();
    }


    class EDiaryEntriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewCardFirst, textViewCardSecond;

        public EDiaryEntriesViewHolder(View itemView) {
            super(itemView);

            textViewCardFirst = itemView.findViewById(R.id.text_view_card_first);
            textViewCardSecond = itemView.findViewById(R.id.text_view_card_second);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            //System.out.println("TasksViewHolder.onClick. getAdapterPosition(): " + getAdapterPosition());

            EDiaryEntry entry = eDiaryEntryList.get(getAdapterPosition());

            Intent intent = new Intent(mCtx, UpdateEntryActivity.class);
            intent.putExtra("entry", entry);

            mCtx.startActivity(intent);
            ((ShowEntriesActivity) mCtx).finish();
        }
    }



}
