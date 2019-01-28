package com.crowdin.platform.example.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crowdin.platform.example.R;

public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.SampleViewHolder> {

    private String[] dataArray;

    public SampleAdapter(String[] array) {
        dataArray = array;
    }

    class SampleViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        SampleViewHolder(@NonNull TextView itemView) {
            super(itemView);
            textView = itemView;
        }
    }

    @NonNull
    @Override
    public SampleAdapter.SampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row,
                parent, false);
        return new SampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SampleViewHolder holder, int position) {
        holder.textView.setText(dataArray[position]);
    }

    @Override
    public int getItemCount() {
        return dataArray.length;
    }
}
