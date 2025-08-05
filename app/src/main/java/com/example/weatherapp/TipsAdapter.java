package com.example.weatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;

import java.util.List;

public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.ViewHolder> {

    private final List<String> tips;

    public TipsAdapter(List<String> tips) {
        this.tips = tips;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tipTextView.setText((position + 1) + ". " + tips.get(position));
    }

    @Override
    public int getItemCount() {
        return tips.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tipTextView;

        public ViewHolder(View view) {
            super(view);
            tipTextView = view.findViewById(R.id.tipTextView);
        }
    }
}
