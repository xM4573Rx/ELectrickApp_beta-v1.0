package com.example.jorge.controlbt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnItemClickListener mListener;
    private ArrayList<Items> mExampleList;

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public RecyclerViewAdapter(ArrayList<Items> exampleList) {
        mExampleList = exampleList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_row, viewGroup, false);
        Item item = new Item(row, mListener);
        return item;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        Items currentItem = mExampleList.get(i);
        ((Item) viewHolder).textView.setText(currentItem.getText1());

    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public static class Item extends RecyclerView.ViewHolder {

        TextView textView;

        public Item(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
