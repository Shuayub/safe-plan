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

/**
 * Adapter class for binding a list of ReminderItem objects to a RecyclerView.
 * Displays each reminder's frequency and time using a simple two-line layout.
 * Handles edit interactions via a click listener.
 */
public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    /** List of reminders to display. */
    private final ArrayList<ReminderItem> reminders;

    /** Application context used for inflating layouts. */
    private final Context context;

    /**
     * Callback function triggered when a reminder is clicked.
     * The integer parameter represents the position of the clicked item.
     */
    private final Consumer<Integer> onEdit;

    /**
     * Constructor for the adapter.
     *
     * @param reminders List of ReminderItem objects to display.
     * @param context   Context for inflating views.
     * @param onEdit    Callback to handle edit action when an item is clicked.
     */
    public ReminderAdapter(ArrayList<ReminderItem> reminders, Context context, Consumer<Integer> onEdit) {
        this.reminders = reminders;
        this.context = context;
        this.onEdit = onEdit;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The view type (not used here).
     * @return A new ViewHolder with a two-line layout.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds reminder data to the ViewHolder.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the data in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReminderItem item = reminders.get(position);
        holder.text1.setText("Frequency: " + item.getFrequency());
        holder.text2.setText("Time: " + item.getTime());

        // Trigger edit callback on item click
        holder.itemView.setOnClickListener(v -> onEdit.accept(position));
    }

    /**
     * Returns the total number of reminders.
     *
     * @return Number of items in the reminder list.
     */
    @Override
    public int getItemCount() {
        return reminders.size();
    }

    /**
     * ViewHolder class that holds the TextViews for displaying frequency and time.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;

        /**
         * Initializes the TextViews from the default Android two-line layout.
         *
         * @param itemView The view for a single item in the list.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}