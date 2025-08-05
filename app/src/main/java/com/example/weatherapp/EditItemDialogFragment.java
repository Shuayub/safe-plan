package com.example.weatherapp;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditItemDialogFragment extends DialogFragment {

    private final String category;
    private final String itemId;
    private final Map<String, String> currentData;
    private String uid;

    public EditItemDialogFragment(String category, String itemId, Map<String, String> currentData) {
        this.category = category;
        this.itemId = itemId;
        this.currentData = currentData;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view;
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        switch (category.toLowerCase()) {
            case "contact":
                view = inflater.inflate(R.layout.fragment_add_contact, null);
                setupContactForm(view);
                break;
            case "location":
                view = inflater.inflate(R.layout.fragment_add_location, null);
                setupLocationForm(view);
                break;
            case "medication":
                view = inflater.inflate(R.layout.fragment_add_medication, null);
                setupMedicationForm(view);
                break;
            default:
                throw new IllegalArgumentException("Unsupported category: " + category);
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        uid = prefs.getString("userUID", "xw5kWxTsebgdWJHHVOKfBIbxAwR2");

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .setNegativeButton("Cancel", (d, id) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            cancelButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
        });

        return dialog;
    }

    private void setupContactForm(View view) {
        EditText name = view.findViewById(R.id.contact_name);
        EditText relationship = view.findViewById(R.id.contact_relationship);
        EditText phone = view.findViewById(R.id.contact_phone);
        Button save = view.findViewById(R.id.save_button);

        name.setText(currentData.get("name"));
        relationship.setText(currentData.get("relationship"));
        phone.setText(currentData.get("phone"));

        save.setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            String r = relationship.getText().toString().trim();
            String p = phone.getText().toString().trim();

            if (n.isEmpty() || p.isEmpty()) {
                Toast.makeText(getContext(), "Name and phone are required", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference contactRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("contacts")
                    .child(itemId);

            Map<String, String> contact = new HashMap<>();
            contact.put("name", n);
            contact.put("relationship", r);
            contact.put("phone", p);

            contactRef.setValue(contact)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Contact updated", Toast.LENGTH_SHORT).show();
                        Bundle result = new Bundle();
                        result.putString("item_type", category); // "Contact", "Document", etc.
                        getParentFragmentManager().setFragmentResult("item_updated", result);
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void setupLocationForm(View view) {
        EditText address = view.findViewById(R.id.location_address);
        EditText notes = view.findViewById(R.id.location_notes);
        Button save = view.findViewById(R.id.save_button);

        address.setText(currentData.get("address"));
        notes.setText(currentData.get("notes"));

        save.setOnClickListener(v -> {
            String a = address.getText().toString().trim();
            String n = notes.getText().toString().trim();

            if (a.isEmpty()) {
                Toast.makeText(getContext(), "Address is required", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference locationRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("locations")
                    .child(itemId);

            Map<String, String> location = new HashMap<>();
            location.put("address", a);
            location.put("notes", n);

            locationRef.setValue(location)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Location updated", Toast.LENGTH_SHORT).show();
                        Bundle result = new Bundle();
                        result.putString("item_type", category); // "Contact", "Document", etc.
                        getParentFragmentManager().setFragmentResult("item_updated", result);
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void setupMedicationForm(View view) {
        EditText name = view.findViewById(R.id.medication_name);
        EditText dosage = view.findViewById(R.id.medication_dosage);
        Button save = view.findViewById(R.id.save_button);

        name.setText(currentData.get("name"));
        dosage.setText(currentData.get("dosage"));

        save.setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            String d = dosage.getText().toString().trim();

            if (n.isEmpty()) {
                Toast.makeText(getContext(), "Medication name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference medRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("medications")
                    .child(itemId);

            Map<String, String> medication = new HashMap<>();
            medication.put("name", n);
            medication.put("dosage", d);

            medRef.setValue(medication)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Medication updated", Toast.LENGTH_SHORT).show();
                        Bundle result = new Bundle();
                        result.putString("item_type", category); // "Contact", "Document", etc.
                        getParentFragmentManager().setFragmentResult("item_updated", result);
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}