package com.nategreat13.teamactivitytracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BasicRecyclerViewAdapter extends RecyclerView.Adapter<BasicRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> list;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    // data is passed into the constructor
    BasicRecyclerViewAdapter(Context context, ArrayList<String> data) {
        this.inflater = LayoutInflater.from(context);
        this.list = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.roster_cell, parent, false);
        // Get the Layout Parameters for ListView Current Item View
        ViewGroup.LayoutParams params = view.getLayoutParams();

        // Set the height of the Item View
        params.height = 200;
        view.setLayoutParams(params);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = list.get(position);
        holder.myTextView.setText(name);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return list.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.nameTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return list.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setList(ArrayList<String> newList) {
        this.list = newList;
    }

    public ArrayList<String> getList() {
        return this.list;
    }
}