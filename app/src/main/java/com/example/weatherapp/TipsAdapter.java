package com.example.weatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;

import java.util.List;
/**
 * Adapter class for displaying a list of safety tips in a RecyclerView.
 * Each item in the list is a single tip string displayed with numbering.
 */
public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.ViewHolder> {

    private final List<String> tips;
    /**
     * Constructor for TipsAdapter.
     *
     * @param tips List of safety tips to display.
     */
    public TipsAdapter(List<String> tips) {
        this.tips = tips;
    }
    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type.
     *
     * @param parent   The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tip, parent, false);
        return new ViewHolder(view);
    }
    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder which should be updated.
     * @param position The position of the item within the data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tipTextView.setText((position + 1) + ". " + tips.get(position));
    }
    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The size of the tips list.
     */
    @Override
    public int getItemCount() {
        return tips.size();
    }
    /**
     * ViewHolder class that describes an item view and metadata about its place within the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tipTextView;
        /**
         * Constructor for ViewHolder.
         *
         * @param view The item view that will hold the tip text.
         */
        public ViewHolder(View view) {
            super(view);
            tipTextView = view.findViewById(R.id.tipTextView);
        }
    }
}
