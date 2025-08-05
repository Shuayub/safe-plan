package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    private final ArrayList<ReminderItem> reminders;
    private final Context context;
    private final Consumer<Integer> onEdit;

    public ReminderAdapter(ArrayList<ReminderItem> reminders, Context context, Consumer<Integer> onEdit) {
        this.reminders = reminders;
        this.context = context;
        this.onEdit = onEdit;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReminderItem item = reminders.get(position);
        holder.text1.setText("Frequency: " + item.getFrequency());
        holder.text2.setText("Time: " + item.getTime());
        holder.itemView.setOnClickListener(v -> onEdit.accept(position));
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}
