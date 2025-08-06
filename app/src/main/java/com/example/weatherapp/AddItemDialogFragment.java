package com.example.weatherapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddItemDialogFragment extends DialogFragment {

    private String category;
    private String uid;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private Uri selectedFileUri;
    public AddItemDialogFragment(String category) {
        this.category = category;
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
            case "document":
                view = inflater.inflate(R.layout.fragment_add_document, null);
                setupDocumentForm(view);
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
                view = inflater.inflate(R.layout.fragment_add_contact, null); // fallback
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        uid = prefs.getString("userUID", "xw5kWxTsebgdWJHHVOKfBIbxAwR2");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            uid = user.getUid();
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(view)
                .setNegativeButton("Cancel", (d, id) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            cancelButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
        });

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedFileUri = result.getData().getData();
                        Toast.makeText(getContext(), "File selected!", Toast.LENGTH_SHORT).show();

                        if (getView() != null) {
                            Button save = getView().findViewById(R.id.save_button);
                            if (save != null) {
                                save.setEnabled(true);
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "File selection cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        return dialog;
    }

    private void setupContactForm(View view) {
        EditText name = view.findViewById(R.id.contact_name);
        EditText relationship = view.findViewById(R.id.contact_relationship);
        EditText phone = view.findViewById(R.id.contact_phone);
        Button save = view.findViewById(R.id.save_button);
        save.setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            String r = relationship.getText().toString().trim();
            String p = phone.getText().toString().trim();
            if (n.isEmpty() || p.isEmpty()) {
                Toast.makeText(getContext(), "Name and phone are required", Toast.LENGTH_SHORT).show();
                return;
            }


            if (uid == null) {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }
            DatabaseReference contactRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("contacts")
                    .push();  // generates a unique ID

            Map<String, String> contact = new HashMap<>();
            contact.put("name", n);
            contact.put("relationship", r);
            contact.put("phone", p);

            contactRef.setValue(contact)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Contact saved", Toast.LENGTH_SHORT).show();
                        Bundle result = new Bundle();
                        result.putString("item_type", category); // "Contact", "Document", etc.
                        getParentFragmentManager().setFragmentResult("item_updated", result);
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void setupDocumentForm(View view) {
        EditText docName = view.findViewById(R.id.document_name);
        Button selectFile = view.findViewById(R.id.select_file_button);
        Button save = view.findViewById(R.id.save_button);

        selectFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            filePickerLauncher.launch(Intent.createChooser(intent, "Select a file"));
        });

        save.setOnClickListener(v -> {
            String name = docName.getText().toString().trim();

            if (name.isEmpty() || selectedFileUri == null) {
                Toast.makeText(getContext(), "File and name required", Toast.LENGTH_SHORT).show();
                return;
            }


            // Firebase Storage path: /documents/uid/filename
            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReference()
                    .child("documents")
                    .child(uid)
                    .child(System.currentTimeMillis() + "_" + name);

            // Upload the file
            storageRef.putFile(selectedFileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            // Save metadata to Realtime Database
                            DatabaseReference docRef = FirebaseDatabase.getInstance()
                                    .getReference("users")
                                    .child(uid)
                                    .child("documents")
                                    .push(); // auto-ID

                            Map<String, String> document = new HashMap<>();
                            document.put("name", name);
                            document.put("url", downloadUri.toString());

                            docRef.setValue(document)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Document saved", Toast.LENGTH_SHORT).show();
                                        Bundle result = new Bundle();
                                        result.putString("item_type", category); // "Contact", "Document", etc.
                                        getParentFragmentManager().setFragmentResult("item_updated", result);
                                        dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

    }

    private void setupLocationForm(View view) {
        EditText address = view.findViewById(R.id.location_address);
        EditText notes = view.findViewById(R.id.location_notes);
        Button save = view.findViewById(R.id.save_button);

        save.setOnClickListener(v -> {
            String a = address.getText().toString().trim();
            String n = notes.getText().toString().trim();

            if (a.isEmpty()) {
                Toast.makeText(getContext(), "Address is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (uid == null) {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference locationRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("locations")
                    .push();

            Map<String, String> location = new HashMap<>();
            location.put("address", a);
            location.put("notes", n);

            locationRef.setValue(location)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Location saved", Toast.LENGTH_SHORT).show();
                        Bundle result = new Bundle();
                        result.putString("item_type", category); // "Contact", "Document", etc.
                        getParentFragmentManager().setFragmentResult("item_updated", result);
                        dismiss();
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void setupMedicationForm(View view) {
        EditText name = view.findViewById(R.id.medication_name);
        EditText dosage = view.findViewById(R.id.medication_dosage);
        Button save = view.findViewById(R.id.save_button);

        save.setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            String d = dosage.getText().toString().trim();

            if (n.isEmpty()) {
                Toast.makeText(getContext(), "Medication name is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (uid == null) {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference medRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("medications")
                    .push();

            Map<String, String> medication = new HashMap<>();
            medication.put("name", n);
            medication.put("dosage", d);

            medRef.setValue(medication)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Medication saved", Toast.LENGTH_SHORT).show();
                        Bundle result = new Bundle();
                        result.putString("item_type", category); // "Contact", "Document", etc.
                        getParentFragmentManager().setFragmentResult("item_updated", result);
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}