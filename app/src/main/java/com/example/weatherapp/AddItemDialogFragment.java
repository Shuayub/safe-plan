package com.example.weatherapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AddItemDialogFragment extends DialogFragment {

    private String category;

    public AddItemDialogFragment(String category) {
        this.category = category;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_item, null);

        TextView title = view.findViewById(R.id.add_item_title);
        EditText input = view.findViewById(R.id.input_field);
        Button submit = view.findViewById(R.id.submit_button);

        title.setText("Add " + category);

        submit.setOnClickListener(v -> {
            String value = input.getText().toString().trim();
            if (!value.isEmpty()) {
                // TODO: send to Firebase or local list
                dismiss();
            } else {
                input.setError("Required");
            }
        });

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss())
                .create();
    }
}